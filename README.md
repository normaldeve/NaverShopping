# 네이버 쇼핑 API 크롤링 프로젝트 (V1.0.1)

## 1. 설명
네이버 쇼핑 API를 활용하여 카테고리 이름에 해당하는 상품 데이터를 수집하고 DB에 저장하는 프로그램입니다.

## 2. 네이버 쇼핑 API 설정

### 2.1 애플리케이션 등록
1. [네이버 쇼핑 API 문서](https://developers.naver.com/docs/serviceapi/search/shopping/shopping.md) 접속
2. Naver Developers > Application > 애플리케이션 등록
    - **애플리케이션 이름**: [본인의 프로젝트명]
    - **사용 API**: 검색
    - **비로그인 오픈 API 서비스 환경**
        - 환경 추가 > WEB 설정 > `http://localhost` 입력
    - 등록하기 클릭
3. 애플리케이션 정보에서 **Client ID**, **Client Secret** 값 복사 (나중에 사용)

## 3. 프로젝트 설정

### 3.1 프로젝트 가져오기
1. Fork를 통해 프로젝트를 가져옵니다
2. 프로젝트 루트에 `.env` 파일을 생성합니다

### 3.2 환경 변수 설정
`.env` 파일에 다음 정보를 입력합니다:

```properties
# Database 설정
DB_URL=jdbc:mysql://localhost:3306/your_database
DB_USER_NAME=your_username
DB_PASSWORD=your_password
DB_DRIVER_NAME=com.mysql.cj.jdbc.Driver

# Naver API 설정
NAVER_CLIENT_ID=your_client_id
NAVER_CLIENT_SECRET=your_client_secret
```

## 4. 네이버 API 요청과 응답

### 4.1 요청 형식
```http
GET https://openapi.naver.com/v1/search/shop.xml?query=침대&display=10&start=1&sort=sim
Headers:
  X-Naver-Client-Id: {YOUR_CLIENT_ID}
  X-Naver-Client-Secret: {YOUR_CLIENT_SECRET}
```

**파라미터:**
- `query`: 검색 키워드 (본 프로젝트에서는 카테고리 이름 사용)
- `display`: 한 번에 가져올 상품 개수 (최대 100)
- `start`: 검색 시작 위치 (1부터 시작, 최대 1000)
- `sort`: 정렬 방식 (sim: 유사도순, date: 날짜순, asc/dsc: 가격 오름차순/내림차순)

### 4.2 응답 정보
API 응답에 포함되는 주요 정보:
- 상품명 (title)
- 상품 이미지 URL (image)
- 최저가 (lprice)
- 최고가 (hprice)
- 브랜드명 (brand)
- 제조사 (maker)
- 상품 ID (productId)
- 쇼핑몰명 (mallName)
- 카테고리 (category1~4)

## 5. 코드 작성하기

### 5.1 프로젝트 구조
```
src/main/java/com/navershop/navershop/
├── core/                          # 핵심 기능 (수정 금지)
│   ├── api/                      # 네이버 API 클라이언트
│   │   ├── NaverShoppingApiClient.java
│   │   └── ProductCrawlingController.java
│   └── dto/                      # API 응답 DTO
│       └── NaverShoppingResponse.java
│
├── template/                      # 템플릿 인터페이스 (수정 금지)
│   ├── adapter/
│   │   ├── mapper/              # 엔티티 매핑 인터페이스
│   │   │   └── ProductMapper.java
│   │   ├── option/              # 옵션 생성 인터페이스
│   │   │   └── OptionGenerator.java
│   │   └── provider/            # 데이터 제공 인터페이스
│   │       ├── category/
│   │       │   └── CategoryProvider.java
│   │       ├── product/
│   │       │   └── ProductProvider.java
│   │       └── user/
│   │           └── UserProvider.java
│   └── service/                 # 크롤링 서비스 기본 클래스
│       └── BaseCrawlingService.java
│
└── todo/                         # 사용자 커스텀 영역 ⭐
    ├── custom/                   # 인터페이스 구현체
    │   ├── adapter/
    │   │   ├── mapper/          # ProductMapper 구현
    │   │   ├── option/          # OptionGenerator 구현 (선택)
    │   │   └── provider/        # Provider 구현체들
    │   └── service/             # 크롤링 서비스 구현
    │       └── ProductCrawlingService.java
    │
    └── repository/               # 엔티티 및 레포지토리
        ├── category/            # Category 엔티티
        ├── product/             # Product 엔티티
        └── user/                # User 엔티티
```

### 5.2 엔티티 작성

#### 5.2.1 필수 엔티티
다음 엔티티들은 반드시 작성해야 합니다:

**Product (상품 정보)**
```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User seller;
    
    private String name;
    private String imageUrl;
    private String brand;
    private Integer basePrice;
    private BigDecimal discountRate;
    private String description;
    private Integer shippingPrice;
    // ... 기타 필드
}
```

**ProductCategory (카테고리 정보)**
- id, parentId, depth 값은 필수입니다
- depth가 없을 때도 처리 가능하도록 수정할 예정입니다
```java
@Entity
@Table(name = "product_category")
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private Long parentId;      // 부모 카테고리 ID (필수)
    
    @Column(nullable = false)
    private Integer depth;      // 카테고리 깊이 (필수)
    // ... 기타 필드
}
```

**User (판매자 정보)**
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String email;
    private String name;
    private String address;
    // ... 기타 필드
}
```

#### 5.2.2 선택 엔티티 (옵션 기능 사용 시)
옵션 기능이 필요한 경우에만 작성합니다:

- **ProductOptionGroup**: 옵션 그룹 (예: 색상, 사이즈)
- **ProductOptionValue**: 옵션 값 (예: 빨강, 파랑, S, M, L)
- **Sku**: 재고 관리 단위
- **ProductSkuOption**: SKU와 옵션 연결 테이블

### 5.3 인터페이스 구현

#### 5.3.1 CategoryProvider 구현 (필수)
카테고리 데이터를 제공하는 Provider를 구현합니다.
- 메서드들은 모두 실제 사용되는 메서드입니다

```java
@Component
@RequiredArgsConstructor
public class HomeSweetCategoryProvider implements CategoryProvider<ProductCategory> {
    
    private final ProductCategoryRepository categoryRepository;
    
    @Override
    public List<ProductCategory> findAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Override
    public Long getCategoryId(ProductCategory category) {
        return category.getId();
    }
    
    @Override
    public String getCategoryName(ProductCategory category) {
        return category.getName();
    }
    
    @Override
    public Integer getCategoryDepth(ProductCategory category) {
        return category.getDepth();
    }
    
    @Override
    public Long getParentCategoryId(ProductCategory category) {
        return category.getParentId();
    }
}
```

#### 5.3.2 UserProvider 구현 (필수)
판매자 정보를 제공하는 Provider를 구현합니다.
- 메서드들은 모두 실제 사용되는 메서드입니다

```java
@Component
@RequiredArgsConstructor
public class HomeSweetUserProvider implements UserProvider<User> {
    
    private final UserRepository userRepository;
    
    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + userId));
    }
}
```

#### 5.3.3 ProductProvider 구현 (필수)
상품을 저장하는 Provider를 구현합니다.
- 메서드들은 모두 실제 사용되는 메서드입니다

```java
@Component
@RequiredArgsConstructor
public class HomeSweetProductProvider implements ProductProvider<Product> {
    
    private final ProductRepository productRepository;
    
    @Override
    public boolean isDuplicate(Product product) {
        // 중복 체크 로직 (예: 같은 판매자의 같은 상품명)
        return productRepository.existsBySellerAndName(
            product.getSeller(), 
            product.getName()
        );
    }
    
    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }
}
```

#### 5.3.4 ProductMapper 구현 (필수)
네이버 API 응답을 본인의 Product 엔티티로 변환하는 Mapper를 구현합니다.
- 해당 클래스에서 네이버 응답 DTO에서 본인이 저장하고자 하는 값을 커스텀할 수 있습니다
- 예를 들어 아래 코드처럼 최저가, 최고가 조합으로 특정 가격 정보를 만들 수 있으며, 원한다면 그대로 사용하셔도 좋습니다
- 네이버 응답 DTO 구조를 확인하세요!

```java
@Component
@RequiredArgsConstructor
public class HomeSweetProductMapper implements ProductMapper<Product, ProductCategory, User> {
    
    private final Random random = new Random();
    
    @Override
    public Product map(NaverShoppingResponse.NaverShoppingItem item, 
                      ProductCategory category, 
                      User seller) {
        
        // 1. 가격 정보 추출
        long lprice = Long.parseLong(item.getLprice());
        long hprice = Long.parseLong(item.getHprice());
        
        // 2. 본인의 비즈니스 로직에 맞게 가격 계산
        int randomDiscountRate = 10 + random.nextInt(21); // 10~30%
        int basePrice = calculateBasePrice(lprice, hprice, randomDiscountRate);
        
        // 3. Product 엔티티 생성
        return Product.builder()
            .category(category)
            .seller(seller)
            .name(item.getTitle())
            .imageUrl(item.getImage())
            .brand(item.getBrand() != null ? item.getBrand() : "Unknown")
            .basePrice(basePrice)
            .discountRate(BigDecimal.valueOf(randomDiscountRate))
            .description("네이버 쇼핑에서 수집된 상품입니다.")
            .shippingPrice(3000)
            .status(ProductStatus.ON_SALE)
            .build();
    }
    
    /**
     * 기준 가격 결정
     */
    private int calculateBasePrice(long lprice, long hprice, int discountRate) {
        if (hprice > 0) {
            // 최고가가 있으면 최고가 사용
            return (int) hprice;
        } else if (lprice > 0) {
            // 최저가만 있으면 역계산 (할인 전 가격 추정)
            return (int) (lprice / (1 - discountRate / 100.0));
        } else {
            // 가격 정보 없음 - 기본값
            return 50000;
        }
    }
}
```

#### 5.3.5 OptionGenerator 구현 (선택)
옵션 기능이 필요한 경우에만 구현합니다.

```java
@Component
@RequiredArgsConstructor
public class HomeSweetOptionGenerator implements OptionGenerator<Product> {
    
    @Override
    public void generateAndAddOptions(Product product, String categoryName) {
        // 카테고리별로 옵션 생성
        if (categoryName.contains("의류") || categoryName.contains("옷")) {
            // 색상, 사이즈 옵션 생성
            addClothingOptions(product);
        } else if (categoryName.contains("가전") || categoryName.contains("전자")) {
            // 용량, 색상 옵션 생성
            addElectronicsOptions(product);
        }
        // ... 기타 카테고리별 로직
    }
    
    @Override
    public boolean needsOptions(String categoryName) {
        // 옵션이 필요한 카테고리인지 판단
        return true; // 또는 특정 조건
    }
    
    private void addClothingOptions(Product product) {
        // 옵션 그룹 및 SKU 생성 로직
    }
}
```

### 5.4 크롤링 서비스 구현
마지막으로 `BaseCrawlingService`를 상속받는 서비스를 작성합니다.
- 나의 Product, Category, User와 관련된 Entity들을 넣어주시면 끝입니다

```java
@Service
public class ProductCrawlingService extends BaseCrawlingService<Product, ProductCategory, User> {
    
    public ProductCrawlingService(
            NaverShoppingApiClient apiClient,
            ProductMapper<Product, ProductCategory, User> productMapper,
            ProductProvider<Product> productProvider,
            CategoryProvider<ProductCategory> categoryProvider,
            UserProvider<User> userProvider,
            @Autowired(required = false) OptionGenerator<Product> optionGenerator) {
        super(apiClient, productMapper, productProvider, categoryProvider, userProvider, optionGenerator);
    }
}
```

> **참고**: `@Autowired(required = false)`를 사용하여 OptionGenerator는 선택적으로 주입됩니다.

## 6. 사용 방법

### 6.1 카테고리 데이터 준비
먼저 DB에 카테고리 데이터를 입력해야 합니다.

```sql
-- Depth 1 (대분류)
INSERT INTO product_category (name, parent_id, depth, created_at, updated_at) 
VALUES 
    ('가구', NULL, 1, NOW(), NOW()),
    ('침구', NULL, 1, NOW(), NOW()),
    ('주방용품', NULL, 1, NOW(), NOW());

-- Depth 2 (중분류)
INSERT INTO product_category (name, parent_id, depth, created_at, updated_at) 
VALUES 
    ('침대', 1, 2, NOW(), NOW()),
    ('소파', 1, 2, NOW(), NOW()),
    ('책상', 1, 2, NOW(), NOW()),
    ('이불', 2, 2, NOW(), NOW()),
    ('베개', 2, 2, NOW(), NOW());
```

### 6.2 판매자 데이터 준비
```sql
INSERT INTO users (email, name, address, created_at, updated_at) 
VALUES ('admin@example.com', 'Admin User', 'Seoul, Korea', NOW(), NOW());
```

### 6.3 API 호출

#### 엔드포인트
```
POST /api/crawling/products
```

#### 파라미터
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| userId | Long | X | 1 | DB에 등록된 판매자 ID |
| productsPerCategory | int | X | 5 | 카테고리당 수집할 상품 개수 (1~1000) |

#### 예시
```bash
# 기본값 사용 (userId=1, 카테고리당 5개)
curl -X POST "http://localhost:8080/api/crawling/products"

# 카테고리당 10개씩 수집
curl -X POST "http://localhost:8080/api/crawling/products?userId=1&productsPerCategory=10"

# 카테고리당 50개씩 수집
curl -X POST "http://localhost:8080/api/crawling/products?userId=1&productsPerCategory=50"
```

#### 응답 예시
```json
{
  "totalCategories": 7,
  "successCategories": 7,
  "failedCategories": 0,
  "totalProducts": 70,
  "categoryResults": [
    {
      "categoryId": 4,
      "categoryName": "침대",
      "depth": 2,
      "productCount": 10,
      "status": "SUCCESS",
      "error": null
    },
    {
      "categoryId": 5,
      "categoryName": "소파",
      "depth": 2,
      "productCount": 10,
      "status": "SUCCESS",
      "error": null
    },
    {
      "categoryId": 1,
      "categoryName": "가구",
      "depth": 1,
      "productCount": 0,
      "status": "NO_RESULTS",
      "error": null
    }
  ]
}
```

## 7. 동작 원리

### 7.1 크롤링 프로세스
1. **카테고리 선택**
    - Depth 1 카테고리의 하위 카테고리(Depth 2)를 우선적으로 선택
    - Depth 2가 없으면 Depth 1 카테고리 사용

2. **API 호출**
    - 카테고리 이름을 키워드로 네이버 쇼핑 API 호출
    - 여러 페이지가 필요한 경우 자동으로 페이지네이션 처리

3. **데이터 변환**
    - ProductMapper를 통해 API 응답을 본인의 Product 엔티티로 변환

4. **옵션 생성** (선택사항)
    - OptionGenerator가 구현되어 있으면 옵션 자동 생성

5. **중복 체크**
    - ProductProvider의 `isDuplicate()` 메서드로 중복 확인
    - 기본 로직: 동일한 판매자의 동일한 상품명이 있는지 확인

6. **DB 저장**
    - 중복이 아닌 경우에만 저장

### 7.2 카테고리 선택 로직
```
예시:
├── 가구 (Depth 1, ID: 1)
│   ├── 침대 (Depth 2, ID: 4)     ← 선택됨
│   ├── 소파 (Depth 2, ID: 5)     ← 선택됨
│   └── 책상 (Depth 2, ID: 6)     ← 선택됨
│
├── 침구 (Depth 1, ID: 2)
│   ├── 이불 (Depth 2, ID: 7)     ← 선택됨
│   └── 베개 (Depth 2, ID: 8)     ← 선택됨
│
└── 주방용품 (Depth 1, ID: 3)     ← Depth 2가 없으므로 선택됨
```

### 7.3 네이버 API 제약사항
- **최대 조회 개수**: 한 키워드당 최대 1000개까지만 조회 가능 (start 파라미터 제한)
- **호출 간격**: API 호출 간 기본 100ms 지연 (application.properties에서 설정 가능)
- **일일 호출 한도**: 네이버 API 정책에 따라 제한됨 (무료 플랜 기준)

## 8. 설정 옵션

### 8.1 application.properties 설정
```properties
# 데이터베이스 설정
spring:
    application:
        name: HomeSweetCrawler
    config:
        import: optional:file:.env[.properties]
    datasource:
        url: ${DB_URL}
        username: ${DB_USER_NAME}
        password: ${DB_PASSWORD}
        driver-class-name: ${DB_DRIVER_NAME}
    jpa:
        hibernate:
            ddl-auto: none
        show-sql: true
        properties:
            hibernate:
                format_sql: true

naver:
api:
client-id: ${NAVER_CLIENT_ID}
client-secret: ${NAVER_CLIENT_SECRET}
base-url: https://openapi.naver.com/v1/search/shop.xml
request-delay: 100

server:
port: 8888
```

## 9. 주의사항

### 9.1 코드 수정 규칙
- ❌ `core`, `template` 패키지의 코드는 **절대 수정하지 마세요**
- ✅ 모든 커스텀 코드는 `todo` 패키지 하위에 작성해야 합니다
- ✅ CategoryProvider 구현 시 `depth`와 `parentId` 필드는 **필수**입니다
- ✅ 옵션 기능은 선택사항이며, 필요 없으면 OptionGenerator를 구현하지 않아도 됩니다

### 9.2 데이터 품질
- 네이버 API에서 가져온 데이터는 완벽하지 않을 수 있습니다
- 가격 정보가 없는 경우를 대비한 기본값 처리가 필요합니다
- HTML 태그가 포함된 상품명은 자동으로 제거됩니다

## 10. 트러블슈팅

### 10.1 401 에러 (인증 오류)
**원인**: Client ID 또는 Client Secret이 잘못되었습니다

**해결방법**:
1. .env 파일의 NAVER_CLIENT_ID와 NAVER_CLIENT_SECRET 값 확인
2. 네이버 개발자센터에서 애플리케이션 정보 재확인
3. 공백이나 특수문자가 잘못 입력되지 않았는지 확인

### 10.2 403 에러 (접근 거부)
**원인**: API 사용 권한이 없거나 일일 호출 한도를 초과했습니다

**해결방법**:
1. 네이버 개발자센터에서 검색 API가 활성화되어 있는지 확인
2. 일일 호출 한도를 확인하고, 초과한 경우 다음 날까지 대기
3. WEB 환경에 `http://localhost`가 등록되어 있는지 확인

### 10.3 429 에러 (요청 제한)
**원인**: 너무 많은 요청을 짧은 시간에 보냈습니다

**해결방법**:
1. `application.properties`에서 `naver.api.request-delay` 값을 늘립니다
   ```properties
   naver.api.request-delay=200  # 100 → 200으로 증가
   ```
2. 한 번에 수집하는 상품 개수를 줄입니다

### 10.4 중복 데이터 저장
**원인**: ProductProvider의 중복 체크 로직이 제대로 작동하지 않습니다

**해결방법**:
1. `existsBySellerAndName` 메서드가 제대로 구현되었는지 확인
2. 필요시 추가적인 중복 체크 조건 추가 (예: 브랜드, 가격 등)

### 10.5 옵션 생성 실패
**원인**: OptionGenerator 구현에 문제가 있거나 필수 엔티티가 누락되었습니다

**해결방법**:
1. ProductOptionGroup, ProductOptionValue, Sku 엔티티가 올바르게 작성되었는지 확인
2. 연관관계 매핑이 올바른지 확인
3. 옵션 기능이 필요 없다면 OptionGenerator를 구현하지 않아도 됩니다

## 11. 예제 프로젝트
`homesweet-naver` 브랜치에서 실제 구현 예제를 확인할 수 있습니다.

```bash
# homesweet-naver 브랜치로 전환
git checkout homesweet-naver
```

해당 브랜치에는 다음이 포함되어 있습니다:
- 완전히 구현된 모든 Provider, Mapper
- 옵션 생성 로직 예제 (주석 처리됨)
- 실제 프로젝트에 적용 가능한 엔티티 구조

## 12. 참고 자료
- [네이버 쇼핑 API 공식 문서](https://developers.naver.com/docs/serviceapi/search/shopping/shopping.md)
- [네이버 개발자센터](https://developers.naver.com/)
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)

**문의사항이 있으시면 이슈를 등록해주세요.**