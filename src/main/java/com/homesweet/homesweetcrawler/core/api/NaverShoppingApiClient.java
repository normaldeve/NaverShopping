package com.homesweet.homesweetcrawler.core.api;

import com.homesweet.homesweetcrawler.core.dto.NaverShoppingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NaverShoppingApiClient {

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    @Value("${naver.api.request-delay:100}")
    private int requestDelay;

    public NaverShoppingResponse searchProducts(String keyword, int display, int start, String sort) {
        try {
            // URL 인코딩
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

            // XML 응답 사용
            String apiURL = String.format(
                    "https://openapi.naver.com/v1/search/shop.xml?query=%s&display=%d&start=%d&sort=%s",
                    encodedKeyword, display, start, sort
            );

            log.info("'{}' 검색 중...", keyword);
            log.info("API URL: {}", apiURL);

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            int responseCode = con.getResponseCode();
            log.info("응답 코드: {}", responseCode);

            // 에러 처리
            if (responseCode == 401) {
                log.error("인증 오류 (401): Client ID 또는 Client Secret이 잘못되었습니다.");
                return null;
            } else if (responseCode == 403) {
                log.error("접근 거부 (403): API 사용 권한이 없거나 일일 호출 한도를 초과했습니다.");
                return null;
            } else if (responseCode == 429) {
                log.error("요청 제한 (429): 너무 많은 요청을 보냈습니다.");
                return null;
            }

            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8));
                StringBuilder errorResponse = new StringBuilder();
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    errorResponse.append(inputLine);
                }
                br.close();
                log.error("API 에러: {}", errorResponse.toString());
                return null;
            }

            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            String xmlResponse = response.toString();
            log.debug("XML 응답: {}", xmlResponse);

            // XML 파싱
            NaverShoppingResponse result = parseXmlResponse(xmlResponse);

            if (result != null) {
                log.info("전체 검색 결과: {}개", result.getTotal());
                log.info("{}개의 상품을 찾았습니다.",
                        result.getItems() != null ? result.getItems().size() : 0);
            }

            return result;

        } catch (Exception e) {
            log.error("API 호출 오류: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * XML 응답 파싱
     */
    private NaverShoppingResponse parseXmlResponse(String xmlString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));

            doc.getDocumentElement().normalize();

            // channel 찾기
            NodeList channelList = doc.getElementsByTagName("channel");
            if (channelList.getLength() == 0) {
                log.error("XML 응답 구조가 올바르지 않습니다.");
                return null;
            }

            Element channel = (Element) channelList.item(0);

            // total 추출
            String totalText = getTagValue("total", channel);
            Integer total = totalText != null ? Integer.parseInt(totalText) : 0;

            String startText = getTagValue("start", channel);
            Integer start = startText != null ? Integer.parseInt(startText) : 1;

            String displayText = getTagValue("display", channel);
            Integer display = displayText != null ? Integer.parseInt(displayText) : 0;

            // items 추출
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
            return null;
        }
    }

    /**
     * XML item 파싱
     */
    private NaverShoppingResponse.NaverShoppingItem parseXmlItem(Element item) {
        NaverShoppingResponse.NaverShoppingItem product = new NaverShoppingResponse.NaverShoppingItem();

        // HTML 태그 제거
        String title = removeHtmlTags(getTagValue("title", item));
        product.setTitle(title);

        // 가격 정보
        String lpriceText = getTagValue("lprice", item);
        String hpriceText = getTagValue("hprice", item);

        product.setLprice(lpriceText != null ? lpriceText : "0");
        product.setHprice(hpriceText != null ? hpriceText : "0");

        // 브랜드
        String brand = getTagValue("brand", item);
        product.setBrand(brand);

        // 이미지 및 기타 정보
        product.setImage(getTagValue("image", item));
        product.setProductId(getTagValue("productId", item));

        return product;
    }

    /**
     * XML 태그 값 추출
     */
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
            log.debug("태그 추출 실패: {}", tag);
        }
        return null;
    }

    /**
     * HTML 태그 제거
     */
    private String removeHtmlTags(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("<[^>]*>", "");
    }

    /**
     * 여러 페이지 검색
     */
    public NaverShoppingResponse searchMultiplePages(String keyword, int totalCount, int display, String sort) {
        log.info("🔍 '{}'로 {}개 상품 수집 시작", keyword, totalCount);

        NaverShoppingResponse combinedResponse = new NaverShoppingResponse();
        combinedResponse.setItems(new ArrayList<>());

        int pages = (totalCount + display - 1) / display;

        for (int page = 0; page < pages; page++) {
            int start = page * display + 1;

            // 1000개 제한 체크
            if (start > 1000) {
                log.warn("네이버 API는 최대 1000개까지만 조회 가능합니다.");
                break;
            }

            log.info("페이지 {}/{} (start={})", page + 1, pages, start);

            NaverShoppingResponse response = searchProducts(keyword, display, start, sort);

            if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                combinedResponse.getItems().addAll(response.getItems());

                if (combinedResponse.getTotal() == null) {
                    combinedResponse.setTotal(response.getTotal());
                }

                log.info("현재까지 수집: {}개", combinedResponse.getItems().size());

                // 목표 개수 도달 시 종료
                if (combinedResponse.getItems().size() >= totalCount) {
                    break;
                }
            }

            // API 호출 간격
            try {
                Thread.sleep(requestDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        log.info("총 {}개의 상품 정보를 수집했습니다.", combinedResponse.getItems().size());

        return combinedResponse;
    }
}