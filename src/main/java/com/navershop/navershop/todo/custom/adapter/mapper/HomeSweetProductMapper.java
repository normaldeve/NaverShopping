package com.navershop.navershop.todo.custom.adapter.mapper;

import com.navershop.navershop.config.RandomBrand;
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
import java.util.Random;

import static org.apache.commons.lang3.StringUtils.truncate;

@Slf4j
@Component
@RequiredArgsConstructor
public class HomeSweetProductMapper implements ProductMapper<Product, ProductCategory, User> {

    private final RandomBrand randomBrand;
    private final Random random = new Random();

    @Override
    public Product map(NaverShoppingResponse.NaverShoppingItem item, ProductCategory category, User seller) {
        // 1. 제목 정제
        String title = item.getTitle();

        // 2. 가격 계산 (최저가, 최고가)
        long lprice = Long.parseLong(item.getLprice());
        long hprice = Long.parseLong(item.getHprice());

        PriceInfo priceInfo = calculatePrice(lprice, hprice);

        // 3. 브랜드 처리
        String brand = randomBrand.getOrDefault(item.getBrand());

        // 4. Product 엔티티 생성
        return Product.builder()
                .category(category)
                .seller(seller)
                .name(truncate(title, 30))
                .imageUrl(item.getImage())
                .brand(truncate(brand, 20))
                .basePrice(priceInfo.basePrice)
                .discountRate(BigDecimal.valueOf(priceInfo.discountRate))
                .description("네이버 쇼핑에서 수집된 상품입니다.")
                .shippingPrice(3000)
                .status(ProductStatus.ON_SALE)
                .build();
    }

    /**
     * 가격 계산 로직 (랜덤 할인율 10 ~ 30 % 사이)
     */
    private PriceInfo calculatePrice(long lprice, long hprice) {
        int randomDiscountRate = 10 + random.nextInt(21); // 10~30%

        int basePrice = determineBasePrice(lprice, hprice, randomDiscountRate);

        return new PriceInfo(basePrice, randomDiscountRate);
    }

    /**
     * 기준 가격 결정
     */
    private int determineBasePrice(long lprice, long hprice, int discountRate) {
        if (hprice > 0) {
            // 최고가가 있으면 최고가 사용
            return (int) hprice;
        } else if (lprice > 0) {
            // 최저가만 있으면 역계산
            return (int) (lprice / (1 - discountRate / 100.0));
        } else {
            // 가격 정보 없음
            return 50000;
        }
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
