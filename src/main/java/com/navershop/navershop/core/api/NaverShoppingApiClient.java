package com.navershop.navershop.core.api;

import com.navershop.navershop.config.NaverApiLimiterConfig;
import com.navershop.navershop.core.dto.NaverShoppingResponse;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * WebClient를 사용하는 네이버 쇼핑 API 클라이언트
 *
 *  성능 개선:
 * - Non-blocking I/O
 * - 커넥션 풀 재사용
 * - 병렬 API 호출 최적화
 */
@Slf4j
@Service
public class NaverShoppingApiClient {

    private final WebClient webClient;
    private final RateLimiter rateLimiter;

    @Value("${naver.api.request-delay:100}")
    private int requestDelay;

    public NaverShoppingApiClient(WebClient webClient, RateLimiter rateLimiter) {
        this.webClient = webClient;
        this.rateLimiter = rateLimiter;
    }

    /**
     * 단일 페이지 검색 (비동기 방식 - Reactive)
     */
    public Mono<NaverShoppingResponse> searchProductsReactive(
            String keyword, int display, int start, String sort) {

        log.info("비동기 검색: {} (start={})", keyword, start);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search/shop.xml")
                        .queryParam("query", keyword)
                        .queryParam("display", display)
                        .queryParam("start", start)
                        .queryParam("sort", sort)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .map(this::parseXmlResponse)
                .doOnError(WebClientResponseException.class, e -> {
                    int status = e.getStatusCode().value();
                    if (status == 429) {
                        log.warn("⚠️ 429 Too Many Requests 발생 — 대기 후 재시도 예정");
                    } else {
                        log.error("API 에러 {}: {}", status, e.getResponseBodyAsString());
                    }
                    throw e;
                })
                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                                .filter(e -> e instanceof WebClientResponseException)
                                .doBeforeRetry(sig ->
                                        log.warn("🔁 재시도 {}/3 (이전 에러: {})",
                                                sig.totalRetries() + 1,
                                                sig.failure().getMessage()))
                )
                .onErrorResume(e -> {
                    log.warn("🚨 3회 재시도 후 실패: {}", e.getMessage());
                    return Mono.just(new NaverShoppingResponse()); // fallback
                });
    }

    /**
     * 여러 페이지 병렬 검색 (Reactive - 최고 성능)
     *
     * 특징:
     * - 모든 페이지를 동시에 요청
     * - Non-blocking I/O로 스레드 효율적 사용
     * - 자동 재시도 및 에러 핸들링
     */
    public NaverShoppingResponse searchMultiplePagesReactive(
            String keyword, int totalCount, int display, String sort) {

        int pages = (totalCount + display - 1) / display;
        int maxPages = Math.min(pages, 1000 / display);

        int concurrency = 3; // 병렬 처리 개수 제한

        log.info("🚀 Reactive 병렬 검색 시작: '{}' (pages={}, concurrency={})",
                keyword, maxPages, concurrency);

        List<NaverShoppingResponse> responses = Flux.range(0, maxPages)
                .flatMap(page ->
                                Mono.fromCallable(() -> {
                                            RateLimiter.waitForPermission(rateLimiter);
                                            return page;
                                        })
                                        .flatMap(p -> {
                                            int start = p * display + 1;
                                            log.info("🟢 요청 시작: {} (page={}, start={})", keyword, p, start);
                                            return searchProductsReactive(keyword, display, start, sort);
                                        })
                                        .subscribeOn(Schedulers.parallel())
                        , concurrency)
                .collectList()
                .block();

        NaverShoppingResponse result = mergeResponses(responses, totalCount);
        log.info("✅ Reactive 검색 완료: {}개 수집 (keyword={})",
                result.getItems() != null ? result.getItems().size() : 0, keyword);

        return result;
    }

    /**
     * 응답 병합
     */
    private NaverShoppingResponse mergeResponses(
            List<NaverShoppingResponse> responses, int totalCount) {

        NaverShoppingResponse combinedResponse = new NaverShoppingResponse();
        combinedResponse.setItems(new ArrayList<>());

        for (NaverShoppingResponse response : responses) {
            if (response != null && response.getItems() != null) {
                combinedResponse.getItems().addAll(response.getItems());

                if (combinedResponse.getTotal() == null) {
                    combinedResponse.setTotal(response.getTotal());
                }

                if (combinedResponse.getItems().size() >= totalCount) {
                    break;
                }
            }
        }

        // 필요한 개수만큼만 자르기
        if (combinedResponse.getItems().size() > totalCount) {
            combinedResponse.setItems(
                    combinedResponse.getItems().subList(0, totalCount)
            );
        }

        return combinedResponse;
    }

    /**
     * WebClient 에러 핸들링
     */
    private void handleWebClientError(WebClientResponseException e) {
        int statusCode = e.getStatusCode().value();

        switch (statusCode) {
            case 401 -> log.error("인증 오류 (401): Client ID 또는 Client Secret이 잘못되었습니다.");
            case 403 -> log.error("접근 거부 (403): API 사용 권한이 없거나 일일 호출 한도를 초과했습니다.");
            case 429 -> log.error("요청 제한 (429): 너무 많은 요청을 보냈습니다.");
            default -> log.error("API 에러 ({}): {}", statusCode, e.getResponseBodyAsString());
        }
    }

    /**
     * XML 응답 파싱
     */
    private NaverShoppingResponse parseXmlResponse(String xmlString) {
        if (xmlString == null || xmlString.isEmpty()) {
            return new NaverShoppingResponse();
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(
                    new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));

            doc.getDocumentElement().normalize();

            NodeList channelList = doc.getElementsByTagName("channel");
            if (channelList.getLength() == 0) {
                log.error("XML 응답 구조가 올바르지 않습니다.");
                return new NaverShoppingResponse();
            }

            Element channel = (Element) channelList.item(0);

            String totalText = getTagValue("total", channel);
            Integer total = totalText != null ? Integer.parseInt(totalText) : 0;

            String startText = getTagValue("start", channel);
            Integer start = startText != null ? Integer.parseInt(startText) : 1;

            String displayText = getTagValue("display", channel);
            Integer display = displayText != null ? Integer.parseInt(displayText) : 0;

            NodeList itemList = channel.getElementsByTagName("item");
            List<NaverShoppingResponse.NaverShoppingItem> items = new ArrayList<>();

            for (int i = 0; i < itemList.getLength(); i++) {
                Node itemNode = itemList.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) itemNode;
                    NaverShoppingResponse.NaverShoppingItem item = parseXmlItem(itemElement);
                    items.add(item);
                }
            }

            NaverShoppingResponse response = new NaverShoppingResponse();
            response.setTotal(total);
            response.setStart(start);
            response.setDisplay(display);
            response.setItems(items);

            return response;

        } catch (Exception e) {
            log.error("XML 파싱 오류", e);
            return new NaverShoppingResponse();
        }
    }

    private NaverShoppingResponse.NaverShoppingItem parseXmlItem(Element item) {
        NaverShoppingResponse.NaverShoppingItem product =
                new NaverShoppingResponse.NaverShoppingItem();

        String title = removeHtmlTags(getTagValue("title", item));
        product.setTitle(title);

        String lpriceText = getTagValue("lprice", item);
        String hpriceText = getTagValue("hprice", item);

        product.setLprice(lpriceText != null ? lpriceText : "0");
        product.setHprice(hpriceText != null ? hpriceText : "0");

        String brand = getTagValue("brand", item);
        product.setBrand(brand);

        product.setImage(getTagValue("image", item));
        product.setProductId(getTagValue("productId", item));

        return product;
    }

    private String getTagValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag);
            if (nodeList.getLength() > 0) {
                Node node = nodeList.item(0);
                if (node != null && node.getFirstChild() != null) {
                    String value = node.getFirstChild().getNodeValue();
                    return value != null && !value.trim().isEmpty() ? value : null;
                }
            }
        } catch (Exception e) {
            log.info("태그 추출 실패: {}", tag);
        }
        return null;
    }

    private String removeHtmlTags(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("<[^>]*>", "");
    }
}