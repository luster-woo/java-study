package day06.i_json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * 박스오피스 Open API 응답(JSON) 파싱 - 강의 예제 (같은 폴더의 boxoffice.json 사용)
 *   ※ boxoffice.json : 1·2위는 교재 값, 3~5위와 movieCd·salesAmt는 연습용으로 넣은 임의 값
 *
 *  { "boxOfficeResult" : { ..., "dailyBoxOfficeList" : [ {영화}, {영화}, ... ] } }
 *     └ Map<String, Map<String, Object>>
 *                              └ "dailyBoxOfficeList" → List<Map<String, Object>>
 *
 *  방법 1 (강의) : 전체를 Map으로 → 배열 꺼내서 List로 형변환 → 하나씩 convertValue로 DTO 변환
 *  방법 2 (참고) : readTree로 트리(JsonNode)로 읽고 경로로 찾아 들어가기 → 형변환 없이 깔끔
 *
 *  - JSON에는 rnum, movieCd, salesAmt ... 등 key가 많지만 DTO는 필요한 4개만 가짐
 *    → @JsonIgnoreProperties(ignoreUnknown = true)  없으면 UnrecognizedPropertyException
 *  - JSON에서 "1" 처럼 문자열이어도 DTO 필드가 Integer면 Jackson이 알아서 숫자로 바꿔줌
 *
 * [실행 결과]
 * --- 방법 1. Map → List → DTO (강의 방식) ---
 * BoxOffice[rank=1, movieNm=발신제한, openDt=2021-06-23, audiAcc=499902]
 * BoxOffice[rank=2, movieNm=크루엘라, openDt=2021-05-26, audiAcc=1554649]
 * BoxOffice[rank=3, movieNm=콰이어트 플레이스 2, openDt=2021-06-16, audiAcc=632197]
 * BoxOffice[rank=4, movieNm=루카, openDt=2021-06-17, audiAcc=336184]
 * BoxOffice[rank=5, movieNm=킬러의 보디가드 2, openDt=2021-06-16, audiAcc=306051]
 * --- 방법 2. JsonNode 트리로 ---
 * 결과 개수 : 5, 방법 1과 1등 같음? true
 * --- 활용 : 누적 관객 50만 이상 ---
 * 2위 크루엘라 : 1,554,649명
 * 3위 콰이어트 플레이스 2 : 632,197명
 */

@JsonIgnoreProperties(ignoreUnknown = true)
class BoxOffice {
    private Integer rank;
    private String movieNm;
    private String openDt;
    private Integer audiAcc;

    public BoxOffice() {}

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
    public String getMovieNm() { return movieNm; }
    public void setMovieNm(String movieNm) { this.movieNm = movieNm; }
    public String getOpenDt() { return openDt; }
    public void setOpenDt(String openDt) { this.openDt = openDt; }
    public Integer getAudiAcc() { return audiAcc; }
    public void setAudiAcc(Integer audiAcc) { this.audiAcc = audiAcc; }

    @Override
    public String toString() {
        return "BoxOffice[rank=" + rank + ", movieNm=" + movieNm + ", openDt=" + openDt + ", audiAcc=" + audiAcc + "]";
    }
}

public class BoxOfficeTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        System.out.println("--- 방법 1. Map → List → DTO (강의 방식) ---");
        List<BoxOffice> list1 = parseWithMap(open());
        list1.forEach(System.out::println);

        System.out.println("--- 방법 2. JsonNode 트리로 ---");
        List<BoxOffice> list2 = parseWithTree(open());
        System.out.println("결과 개수 : " + list2.size() + ", 방법 1과 1등 같음? "
                + list1.get(0).getMovieNm().equals(list2.get(0).getMovieNm()));

        System.out.println("--- 활용 : 누적 관객 50만 이상 ---");
        list1.stream()
                .filter(b -> b.getAudiAcc() >= 500_000)
                .forEach(b -> System.out.printf("%d위 %s : %,d명%n", b.getRank(), b.getMovieNm(), b.getAudiAcc()));
    }

    // 클래스와 같은 패키지에 있는 boxoffice.json을 InputStream으로 연다 (노드 스트림!)
    private static InputStream open() {
        return BoxOfficeTest.class.getResourceAsStream("boxoffice.json");
    }

    @SuppressWarnings("unchecked")
    private static List<BoxOffice> parseWithMap(InputStream resource) throws IOException {
        // 1. JSON 객체 { } → Map
        Map<String, Map<String, Object>> result = mapper.readValue(resource, new TypeReference<>() {});
        // 2. 배열 [ ] → List  (get 결과가 Object라서 형변환 필요)
        List<Map<String, Object>> items =
                (List<Map<String, Object>>) result.get("boxOfficeResult").get("dailyBoxOfficeList");
        // 3. Map 하나하나 → BoxOffice
        List<BoxOffice> list = new ArrayList<>();
        for (Map<String, Object> item : items) {
            list.add(mapper.convertValue(item, BoxOffice.class));
        }
        return list;
    }

    private static List<BoxOffice> parseWithTree(InputStream resource) throws IOException {
        JsonNode root = mapper.readTree(resource);
        JsonNode items = root.path("boxOfficeResult").path("dailyBoxOfficeList");   // 경로로 바로 찾아가기
        List<BoxOffice> list = new ArrayList<>();
        for (JsonNode item : items) {
            list.add(mapper.treeToValue(item, BoxOffice.class));
        }
        return list;
    }
}
