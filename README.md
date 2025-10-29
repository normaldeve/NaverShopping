# 프로젝트 사용 방법

## 1. 설명
- 네이버 쇼핑 API를 활용하여 카테고리 이름에 해당하는 데이터를 수집하고 DB에 저장하는 프로그램입니다

## 2. 네이버 쇼핑 API
- 아래 링크에 접속해주세요 (네이버 쇼핑 API와 관련된 정보는 아래 링크를 통해 확인할 수 있습니다)
- https://developers.naver.com/docs/serviceapi/search/shopping/shopping.md
- Naver Developers > Application > 애플리케이션 등록을 통해 애플리케이션 등록 과정을 진행해주세요
  - 애플리케이션 이름 : [나의 프로젝트명]
  - 사용 API : 검색
  - 비로그인 오픈 API 서비스 환경
    - 환경 추가 > WEB 설정 > http://localhost 입력
  - 등록하기 -> 완료
- 이후 애플리케이션 정보 > Client ID, Client Secret 값을 메모장에 임시 저장해주세요 (이후 언제든 확인 가능합니다)

## 3. 프로젝트 가져오기
- Fork를 통해 프로젝트를 가져와주세요
- 이후 .env 파일을 생성하고 다음과 같은 정보를 입력해야 합니다
  - DB_URL : 사용하고 있는 DB URL을 입력합니다
  - DB_USER_NAME : 사용하고 있는 DB 사용자 이름
  - DB_PASSWORD : DB 비밀번호
  - DB_DRIVER_NAME : DB 드라이버를 입력해주세요 (MySQL, PostgreSQL 등)
  - NAVER_CLIENT_ID : 이곳에 이전에 메모장에 저장했던 Client ID 값을 넣어주세요
  - NAVER_CLIENT_SECRET : 이곳에 이전에 메모장에 저장했던 Client Secret 값을 넣어주세요

## 4. Naver API 요청과 응답 살펴보기
### 요청
```http
GET https://openapi.naver.com/v1/search/shop.xml?query=침대&display=10&start=1&sort=sim
Headers:
  X-Naver-Client-Id: {YOUR_CLIENT_ID}
  X-Naver-Client-Secret: {YOUR_CLIENT_SECRET}
```
- query에 검색할 키워드를 입력하면 해당 키워드 중심의 검색을 할 수 있습니다
- 위 프로그램은 키워드에 최하위 카테고리 이름을 넣습니다 (해당 카테고리에 해당하는 상품 검색)

### 응답
- 응답으로는 아래와 같은 값이 포함됩니다
- 상품명, 상품 상세 URL, 상품 이미지 URL, 최저가, 최고가, 쇼핑볼명, 상품 ID, 상품군 타입, 브랜드명, 제조사, 카테고리 (대분류, 중분류, 소분류, 세분류)
- 위 프로젝트에서 위 응답 중 필요한 값을 선택하여 DB에 저장합니다
- 응답에 대한 자세한 내용을 알고 싶다면 네이버 쇼핑 API를 참고해주세요


## 5. 코드 작성하기
- 루트에서 custom 폴더를 생성해주세요
- custom 하위에 entity 폴더를 만들고 그곳에 Product, Category, User와 관련된 코드를 모두 넣어주세요
  - 본인이 작성한 엔티티, JPA repository 코드를 모두 넣어주시면 됩니다. (상품과 관련된)
- 이후 custom 하위에 adapter, service 폴더를 생성해주세요
- 

