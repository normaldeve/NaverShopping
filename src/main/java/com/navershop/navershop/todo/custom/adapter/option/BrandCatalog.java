package com.navershop.navershop.todo.custom.adapter.option;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 제품 브랜드 카탈로그
 *
 * @author junnukim1007gmail.com
 * @date 25. 11. 7.
 */
@Getter
@RequiredArgsConstructor
public enum BrandCatalog {

    FURNITURE(Arrays.asList(
            "에이스침대", "시몬스", "에몬스", "한샘", "동서가구",
            "보루네오", "아케아", "리바트", "일룸", "삼익가구",
            "베드리움", "까사미아", "파로마", "보니애가구", "레이디가구",
            "상일리베가구", "스칸디", "잉글랜더", "흄앤헬", "라클라우드",
            "베디스", "아메리카나베드", "지누스", "슬림퍼", "트메르침대", "그남자의가구",
            "장인가구", "노르웨이숲", "휴도", "핀란디아", "코웨이",
            "NOOER", "썸앤데코", "도담", "올쏘", "미라지",
            "착한데이블", "규수방", "제퍼슨가구", "카구고래", "크라운퍼니쳐",
            "하포스", "퍼니코", "모벨퍼니", "인홈", "아씨방", "데이멜로우", "골든스트릿",
            "금성침대"
    )),

    FABRIC(Arrays.asList(
            "무지", "실사 프린팅", "체크", "페르시안", "에스닉",
            "트로피컬", "카모플라쥬", "캐릭터", "지브라", "뱀피/스네이크",
            "스트라이프", "도트", "헤링본", "플라워", "페이즐리",
            "레터링", "기하학", "젯소", "호피/레오파드", "패턴믹스"
    )),

    ELECTRONICS(Arrays.asList(
            "삼성전자", "쿠쿠", "로보락", "쿠첸", "나비엔 매직",
            "아이닉", "제니퍼룸", "보아르", "마샬", "ASUS",
            "LG전자", "와이드뷰", "미닉스", "캐리어", "위닉스",
            "디베아", "린클", "모온", "캐치웰"
    )),

    KITCHEN(Arrays.asList(
            "오가든", "바겐슈타이거", "스타우브", "달팽이리빙", "한샘",
            "휘슬러", "트루쿡", "쓰임", "르난세", "땡스소윤",
            "시라쿠스", "테팔", "메종오브제", "락앤락", "AMT",
            "이브리엉", "타블도트", "무타공마켓", "네오플램", "글로벌나이프"
    )),

    DECOR_PLANT(Arrays.asList(
            "오가든", "헤트라스", "세잔느화실", "에이센트", "파워플랜트",
            "데팡스", "코코도르", "메이드모드", "테일러센츠", "데일리오브젝트",
            "에덴미술", "아트밀", "무아스", "더 아트", "스튜디오무드",
            "이너조깅", "레나에너지", "펫플랜트", "헬로프로그", "애시드로우"
    )),

    STORAGE(Arrays.asList(
            "상도가구", "MF매직하우스", "진심감성", "얼라이브즈", "네이쳐리빙",
            "레어로우", "퓨어셀", "아이리스코리아", "레트로하우스", "위즈홈",
            "스피드랙", "무타공마켓", "루시아이", "룸앤홈", "심플먼트",
            "이케아", "한샘", "씨데코", "에브리윅", "THE살림앤코"
    )),

    KIDS(Arrays.asList(
            "아이러브베베", "베베앙 아기물티슈", "베베숲", "베베앙", "한샘",
            "일룸", "아워매뉴얼", "레이디가구", "베이비브레짜", "티엔디자인",
            "스마트에코", "리바트", "뉴트리시아", "퍼니코", "아이엔지홈",
            "팸퍼스", "윌로우", "꿈꾸는요셉", "시디즈", "룸핏"
    )),

    LIVING(Arrays.asList(
            "코튼리빙", "무타공마켓", "오가든", "라다타", "리프홈",
            "프랑코", "바이칸", "에브리윅", "다룸", "브랜든",
            "매직캔", "송월타월", "하우스레시피", "닥터워터", "소프트터치",
            "달팽이리빙", "한샘", "클라우망스탠다드", "무아스", "반값리필"
    )),

    PET(Arrays.asList(
            "묵스", "핏펫", "ANF", "세이펫", "모든펫",
            "캐츠랑", "리포소", "가르르", "일룸", "아가드",
            "올리", "딩동펫", "펫토", "초코펫하우스", "오브바이포",
            "두잇", "텍미홈", "밀리언펫", "로하우스", "마루이"
    )),

    CAMPING(Arrays.asList(
            "제백", "로티캠프", "위드퍼니처", "체어팩토리", "아트웨이",
            "영가구", "오버더", "인블루가구", "코지앤코", "체어센스",
            "지라프", "마이크로킥보드", "깃든", "에이비퍼니쳐", "레토",
            "캠핑칸", "손리", "브리즈문", "캉거", "아베나키"
    )),

    RENTAL(Arrays.asList(
            "컴프로", "누하스", "세라젬", "헨지디자인", "퍼니챗",
            "다이슨", "미닉스", "HP", "쿠쿠", "린클",
            "대림케어", "플레이스테이션", "유버스", "레이잉", "SK브로드밴드",
            "한국갤러리", "캐리어", "메가스터디교육", "비상교육", "하이얼"
    )),

    SHOPPING(Arrays.asList(
            "헤드앤숄더", "코튼리빙", "진심감성", "송월타월", "동서식품",
            "무타공마켓", "BAS", "얼라이브즈", "닥터워터", "리프홈",
            "오가든", "베베숲", "뉴트리시아", "베베앙", "바겐슈타이거",
            "락앤락", "아이러브베베", "팸퍼스", "라다타", "살림의기술"
    )),

    TOOL(Arrays.asList(
            "프로메이드", "스윗홈", "파워존", "디월트", "SANRO",
            "밀레시스템", "자취생활연구소", "아이정", "직방 스마트 홈", "몬세라믹",
            "보쉬(BOSCH)", "러그박스", "올웨이즈홈", "게이트맨", "도튼",
            "주식회사 새로고침", "써지오", "매직픽스", "46month", "에코너"
    )),

    LIGHTING(Arrays.asList(
            "라이트온", "루미엘", "브릴란테", "하이노트라이트", "조이조명",
            "노르딕라이트", "비트라라이트", "디밍하우스", "라디언홈", "클라우드조명",
            "엘리시아라이트", "펜던트리빙", "모노라이트", "레트로램프", "라이트리브",
            "룸라이트", "아르떼조명", "브라이트코드", "캔들앤라이트", "모던일루미네이트"
    ));

    private final List<String> brands;
    private static final Random RANDOM = new Random();

    public static List<String> getBrandsByCategory(ProductCategory category) {
        return Arrays.stream(values())
                .filter(c -> c.name().equals(category.name()))
                .findFirst()
                .map(BrandCatalog::getBrands)
                .orElse(Collections.emptyList());
    }
    public String getRandomBrand() {
        if (brands.isEmpty()) {
            return "기본브랜드";
        }
        return brands.get(RANDOM.nextInt(brands.size()));
    }

    /**
     * 카테고리명으로 BrandCatalog 찾기
     */
    public static BrandCatalog fromCategoryName(String categoryName) {
        // "가구" 카테고리면 FURNITURE 반환
        if (categoryName != null) {
            if (categoryName.contains("가구")) {
                return FURNITURE;
            }
            if (categoryName.contains("패브릭") || categoryName.contains("커튼")) {
                return FABRIC;
            }
            if (categoryName.contains("가전") || categoryName.contains("디지털")) {
                return ELECTRONICS;
            }
            if (categoryName.contains("주방")) {
                return KITCHEN;
            }
            if (categoryName.contains("데코") || categoryName.contains("식물")) {
                return DECOR_PLANT;
            }
            if (categoryName.contains("수납") || categoryName.contains("정리")) {
                return STORAGE;
            }
            if (categoryName.contains("유아") || categoryName.contains("아동")) {
                return KIDS;
            }
            if (categoryName.contains("생활")) {
                return LIVING;
            }
            if (categoryName.contains("반려") || categoryName.contains("펫")) {
                return PET;
            }
            if (categoryName.contains("캠핑") || categoryName.contains("레저")) {
                return CAMPING;
            }
            if (categoryName.contains("렌탈")) {
                return RENTAL;
            }
            if (categoryName.contains("장보기")) {
                return SHOPPING;
            }
            if (categoryName.contains("공구")) {
                return TOOL;
            }
            if (categoryName.contains("조명")) {
                return LIGHTING;
            }
        }

        // 기본값: FURNITURE
        return FURNITURE;
    }

    /**
     * 카테고리명에 맞는 랜덤 브랜드 반환
     */
    public static String getRandomBrandByCategory(String categoryName) {
        BrandCatalog catalog = fromCategoryName(categoryName);
        return catalog.getRandomBrand();
    }
}