package com.navershop.navershop.todo.custom.adapter.mapper;

import com.navershop.navershop.todo.custom.adapter.naming.ProductNameFactory;
import com.navershop.navershop.todo.custom.adapter.option.BrandCatalog;
import com.navershop.navershop.todo.repository.product.Product;
import com.navershop.navershop.todo.repository.category.ProductCategory;
import com.navershop.navershop.todo.repository.product.ProductStatus;
import com.navershop.navershop.todo.repository.user.User;
import com.navershop.navershop.template.adapter.mapper.ProductMapper;
import com.navershop.navershop.core.dto.NaverShoppingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.commons.lang3.StringUtils.truncate;

@Slf4j
@Component
@RequiredArgsConstructor
public class HomeSweetProductMapper implements ProductMapper<Product, ProductCategory, User> {

    private final ProductNameFactory productNameFactory;
    private final Random random = new Random();

    private static final List<Integer> SHIPPING_PRICES = List.of(0, 3000, 5000);

    private static final List<String> DESCRIPTION_TEMPLATES = List.of(
            "합리적인 가격으로 만나는 %s의 프리미엄 %s — 공간의 분위기를 한층 더 높여줍니다.",
            "%s의 %s은 세련된 디자인과 실용성을 동시에 갖춘 베스트셀러 제품입니다.",
            "감각적인 인테리어를 완성하고 싶다면 %s의 %s을 추천드립니다. 품격이 다릅니다.",
            "%s 고객들이 사랑하는 %s — 오랜 시간 질리지 않는 디자인과 퀄리티를 자랑합니다.",
            "%s의 %s은 고급스러운 소재와 정교한 마감으로 완성된 완벽한 아이템입니다.",
            "하루의 피로를 편안하게 풀어주는 %s의 %s, 부드럽고 안정적인 사용감을 제공합니다.",
            "깔끔한 라인과 미니멀한 감각으로 완성된 %s의 %s, 어느 공간에도 자연스럽게 어울립니다.",
            "내추럴한 무드와 따뜻한 감성이 느껴지는 %s의 %s — 집 안에 편안함을 더해줍니다.",
            "디자인부터 내구성까지 신경 쓴 %s의 %s, 일상의 만족도를 한 단계 높여줍니다.",
            "오랜 시간 사용해도 질리지 않는 클래식한 감성, %s의 %s으로 공간을 완성해보세요.",
            "공간을 세련되게 연출하고 싶다면 %s의 %s으로 새로운 분위기를 만들어보세요.",
            "부드러운 곡선과 감각적인 컬러가 어우러진 %s의 %s, 집안에 생기를 불어넣습니다.",
            "디테일 하나하나가 돋보이는 %s의 %s — 실용성과 디자인 모두를 만족시킵니다.",
            "일상 속 작은 여유를 선물하는 %s의 %s, 감성적인 공간 연출에 꼭 필요한 아이템입니다.",
            "고급스러운 질감과 탄탄한 구조로 오래 사용할 수 있는 %s의 %s입니다.",
            "%s의 %s은 현대적인 감각과 클래식한 분위기가 조화를 이루는 완벽한 디자인입니다.",
            "편안함과 스타일을 모두 갖춘 %s의 %s — 매일 사용해도 새로운 만족감을 줍니다.",
            "트렌디하면서도 실용적인 %s의 %s, 누구나 만족할 수 있는 완성도 높은 제품입니다.",
            "심플한 디자인 속에 담긴 세심한 디테일, %s의 %s으로 공간의 품격을 더하세요.",
            "따뜻한 감성과 모던함이 공존하는 %s의 %s, 집안의 포인트 아이템으로 추천드립니다.",
            "모던한 감각과 내추럴한 소재가 어우러진 %s의 %s, 어디에 두어도 잘 어울립니다.",
            "고급스러운 컬러감과 섬세한 마감으로 완성된 %s의 %s은 오랫동안 사랑받는 이유가 있습니다.",
            "%s의 %s은 단순한 제품을 넘어 라이프스타일을 완성하는 디자인 오브젝트입니다.",
            "집 안 어디에 두어도 자연스러운 조화를 이루는 %s의 %s, 실용성과 감성의 완벽한 조합입니다.",
            "심플하지만 존재감 있는 디자인으로 공간을 채워주는 %s의 %s입니다.",
            "누구나 좋아할 수밖에 없는 감각적인 컬러와 디자인, %s의 %s으로 완성해보세요.",
            "섬세한 디테일과 안정적인 구조로 완성된 %s의 %s, 믿고 사용할 수 있는 품질입니다.",
            "공간에 자연스럽게 녹아드는 디자인, %s의 %s으로 따뜻한 분위기를 연출하세요.",
            "기능성과 디자인, 두 가지 모두를 만족시키는 %s의 %s입니다.",
            "감각적인 트렌드 컬러와 세련된 마감으로 완성된 %s의 %s, 공간에 생기를 더하세요.",
            "실용성과 디자인을 모두 고려한 %s의 %s, 일상에서 가장 많이 찾게 될 제품입니다.",
            "간결하면서도 세련된 실루엣으로 완성된 %s의 %s — 오래 봐도 질리지 않습니다.",
            "%s의 %s은 고급 소재를 사용해 내구성과 디자인 모두를 잡은 완성도 높은 제품입니다.",
            "내추럴한 분위기와 모던한 감각을 모두 담은 %s의 %s, 인테리어의 중심이 되어줍니다.",
            "매일의 일상 속에서 더 나은 편안함을 선사하는 %s의 %s입니다.",
            "감각적인 취향을 가진 사람들을 위한 %s의 %s — 작은 차이가 큰 만족을 만듭니다.",
            "%s의 %s은 공간의 균형과 조화를 고려해 디자인된 실용적인 제품입니다.",
            "오랜 시간 사용해도 변함없는 품질, %s의 %s으로 나만의 공간을 완성해보세요.",
            "디자인과 기능, 두 마리 토끼를 잡은 %s의 %s — 만족도 높은 아이템입니다.",
            "깔끔한 디자인과 부드러운 색감으로 인테리어 완성도를 높여주는 %s의 %s입니다.",
            "작은 디테일까지 세심하게 고려한 %s의 %s, 일상에 감성을 더해줍니다.",
            "합리적인 가격대에 높은 품질을 자랑하는 %s의 %s, 누구에게나 추천할 만한 제품입니다.",
            "집안의 중심이 되는 포인트 아이템, %s의 %s으로 새로운 분위기를 만들어보세요.",
            "트렌디한 디자인과 안정적인 구조를 모두 갖춘 %s의 %s, 매일의 만족을 선사합니다.",
            "편안함을 최우선으로 생각한 %s의 %s — 디자인까지 완벽한 조화를 이룹니다.",
            "모던하고 감각적인 디자인으로 완성된 %s의 %s, 공간의 품격을 한층 높여줍니다.",
            "%s의 %s은 깔끔한 라인과 실용적인 기능으로 꾸준히 사랑받는 제품입니다.",
            "오랜 시간 사용해도 변형 없는 견고함, %s의 %s으로 집 안의 완성도를 높이세요.",
            "세련된 컬러와 부드러운 촉감이 매력적인 %s의 %s, 어디에 두어도 잘 어울립니다.",
            "하루의 시작과 끝을 함께하는 %s의 %s, 당신의 공간을 더욱 특별하게 만들어줍니다.",
            "%s의 %s은 감각적인 디자인과 뛰어난 내구성으로 완성된 믿을 수 있는 선택입니다."
    );

    @Override
    public Product map(NaverShoppingResponse.NaverShoppingItem item, ProductCategory category, User seller) {

        String brand = BrandCatalog.getRandomBrandByCategory(category.getName());

        // 2. 카테고리명 기반 상품명 생성
        String productName = productNameFactory.generateProductName(brand, category.getName());

        // 3. 가격 계산 (최저가, 최고가)
        long lprice = Long.parseLong(item.getLprice());
        long hprice = Long.parseLong(item.getHprice());
        PriceInfo priceInfo = calculatePrice(lprice, hprice);

        // 4. Product 엔티티 생성
        return Product.builder()
                .category(category)
                .seller(seller)
                .name(productName)
                .imageUrl(item.getImage())
                .brand(brand)
                .basePrice(priceInfo.basePrice)
                .discountRate(BigDecimal.valueOf(priceInfo.discountRate))
                .description(generateRandomDescription(brand, category.getName()))
                .shippingPrice(randomShippingPrice())
                .status(ProductStatus.ON_SALE)
                .build();
    }

    @Override
    public Product map(NaverShoppingResponse.NaverShoppingItem item,
                       ProductCategory category,
                       User seller,
                       String customBrand,
                       String customProductName) {
        return createProduct(item, category, seller, customBrand, customProductName);
    }

    private Product createProduct(NaverShoppingResponse.NaverShoppingItem item,
                                  ProductCategory category,
                                  User seller,
                                  String brand,
                                  String productName) {
        long lprice = Long.parseLong(item.getLprice());
        long hprice = Long.parseLong(item.getHprice());
        PriceInfo priceInfo = calculatePrice(lprice, hprice);

        return Product.builder()
                .category(category)
                .seller(seller)
                .name(productName)
                .imageUrl(item.getImage())
                .brand(brand)
                .basePrice(priceInfo.basePrice)
                .discountRate(BigDecimal.valueOf(priceInfo.discountRate))
                .description(generateRandomDescription(brand, category.getName()))
                .shippingPrice(randomShippingPrice())
                .status(ProductStatus.ON_SALE)
                .build();
    }

    private int randomShippingPrice() {
        return SHIPPING_PRICES.get(ThreadLocalRandom.current().nextInt(SHIPPING_PRICES.size()));
    }

    private String generateRandomDescription(String brand, String categoryName) {
        String template = DESCRIPTION_TEMPLATES.get(ThreadLocalRandom.current().nextInt(DESCRIPTION_TEMPLATES.size()));
        return String.format(template, brand, categoryName);
    }


    /**
     * 가격 계산 로직 (랜덤 할인율 + 가격 변동)
     */
    private PriceInfo calculatePrice(long lprice, long hprice) {
        // 랜덤 할인율 (10~30%)
        int randomDiscountRate = 10 + random.nextInt(21);

        // 랜덤 가격 변동 계수 (60~100%)
        double priceVariation = 0.6 + random.nextDouble() * 0.4;

        int basePrice = determineBasePrice(lprice, hprice, randomDiscountRate, priceVariation);

        return new PriceInfo(basePrice, randomDiscountRate);
    }

    /**
     * 기준 가격 결정 (랜덤 변동 포함)
     */
    private int determineBasePrice(long lprice, long hprice, int discountRate, double variation) {
        long referencePrice;

        if (hprice > 0) {
            referencePrice = hprice;
        } else if (lprice > 0) {
            referencePrice = (long) (lprice / (1 - discountRate / 100.0));
        } else {
            referencePrice = 50000;
        }

        // 변동 계수 적용
        int adjustedPrice = (int) (referencePrice * variation);

        // 최소 보정 (1만 원 이하 방지)
        return Math.max(adjustedPrice, 10000);
    }

    /**
     * 가격 정보 DTO
     */
    private static class PriceInfo {
        int basePrice;
        int discountRate;

        PriceInfo(int basePrice, int discountRate) {
            this.basePrice = basePrice;
            this.discountRate = discountRate;
        }
    }
}
