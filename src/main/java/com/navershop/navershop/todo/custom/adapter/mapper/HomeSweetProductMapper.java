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
import java.util.Random;

import static org.apache.commons.lang3.StringUtils.truncate;

@Slf4j
@Component
@RequiredArgsConstructor
public class HomeSweetProductMapper implements ProductMapper<Product, ProductCategory, User> {

    private final ProductNameFactory productNameFactory;
    private final Random random = new Random();

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
                .description("네이버 쇼핑에서 수집된 상품입니다.")
                .shippingPrice(3000)
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
                .name(truncate(productName, 30))
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
