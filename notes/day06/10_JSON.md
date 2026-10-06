# JSON

> 교재: 15기_자바_live_11_xml_0127_오후 (전공) — 4단원 JSON

> [← 보조스트림 활용](09_보조스트림_활용.md) · [day06 목록](../README.md) · [String →](11_String.md)

## 데이터의 형태

- 공공데이터(예: 영화진흥위원회 오픈API, kobis.or.kr)는 주로 CSV · XML · JSON 형태로 제공
- 같은 데이터를 세 형태로 쓰면

| 형태 | 특징 |
| --- | --- |
| CSV (comma separated value) | 용량이 작지만 구조적이지 못함 |
| XML (Extensible Markup Language) | 태그로 데이터 형식 정의. 구조적이지만 정확한 문법이 필요하고 용량이 큼 |
| JSON (JavaScript Object Notation) | 구조를 가지며, 객체로 다른 언어와 호환 |

```
연도,제조사,모델,설명,가격
1997,Ford,E350,"ac, abs, moon",3000.00
1999,Chevy,"Venture Edition","",4900.00
```

```json
[
  { "연도": 1997, "제조사": "Ford", "모델": "E350", "설명": "ac, abs, moon", "가격": 3000 },
  { "연도": 1999, "제조사": "Chevy", "모델": "Venture Edition", "설명": "", "가격": 4900 }
]
```

---

## JSON

### JSON이란

- **JavaScript Object Notation** — 자바스크립트에서의 객체 표현법
- 간결한 문법, 단순한 텍스트, 적은 용량 → 대부분의 언어·플랫폼에서 사용 가능
    - **이기종 간 데이터 교환**에 광범위하게 사용 (예: Open API 응답)
- 객체를 **key-value 쌍**으로 관리 → 자바에서 key-value 하면 **Map**
- 문법 정의: https://www.json.org/json-en.html

### JSON ↔ 자바 자료구조

- 박스오피스 Open API 의 JSON 응답 구조

```json
{
  "boxOfficeResult": {
    "boxofficeType": "일별 박스오피스",
    "showRange": "20210701~20210701",
    "dailyBoxOfficeList": [
      { "rnum": 1, "rank": 1, "movieNm": "발신제한", "openDt": "2021-06-23", "audiAcc": 499902 },
      { "rnum": 2, "rank": 2, "movieNm": "크루엘라", "openDt": "2021-05-26", "audiAcc": 1554649 }
    ]
  }
}
```

| JSON | 자바 |
| --- | --- |
| 객체 `{ "key": value, ... }` | `Map<String, Object>` |
| 배열 `[ ... ]` | `List<...>` |
| 문자열 · 숫자 · true/false · null | `String` · `Integer`/`Double` 등 · `Boolean` · `null` |

- 위 JSON 전체를 자바로 보면 → `Map<String, Map<String, Object>>`
    - 바깥 객체의 key `"boxOfficeResult"` 의 값이 또 객체(Map)
- `map.get("boxOfficeResult").get("dailyBoxOfficeList")` 의 타입은 → `List<Map<String, Object>>`
    - 배열 안의 각 영화 정보가 객체(Map)

```
Map<String, Map<String, Object>>
 └─ "boxOfficeResult" → Map<String, Object>
                         ├─ "boxofficeType"      → String
                         ├─ "showRange"          → String
                         └─ "dailyBoxOfficeList" → List<Map<String, Object>>
                                                    ├─ { rnum, rank, movieNm, openDt, audiAcc }
                                                    └─ ...
```

### 라이브러리를 이용한 간편한 사용

- JSON 을 직접 파싱하지 않고 라이브러리 사용
- **Jackson**: `jackson-databind`, `jackson-core`, `jackson-annotations`
    - 핵심 클래스: `ObjectMapper` (JSON ↔ 자바 객체 변환)

---

## JSON 파싱 및 결과 확인

### 앞 단원에서 이어지는 구성

- 교재는 SAX·DOM·JSON 파서가 같은 인터페이스를 구현하게 함 → 데이터 형태만 바꿔 같은 `List<BoxOffice>` 를 얻음

```java
import java.io.InputStream;
import java.util.List;

public interface BoxOfficeParser {
    List<BoxOffice> getBoxOffice(InputStream resource);
}
```

- **DTO(Data Transfer Object)**: 계층 간 데이터 교환용 객체 — 필요한 데이터만 private 필드로 캡슐화, getter/setter, 기본 생성자

### DTO — @JsonIgnoreProperties

- JSON 에는 `rnum`, `movieCd`, `salesAmt` ... 등 key 가 많지만 DTO 는 필요한 것(rank, movieNm, openDt, audiAcc)만 가짐
- DTO 에 없는 key 가 오면 Jackson 은 기본적으로 예외 발생 → **관리하지 않을 속성은 무시**

```java
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)   // DTO 에 없는 key 는 무시
public class BoxOffice {
    private Integer rank;      // 등수
    private String movieNm;    // 영화 제목
    private String openDt;     // 개봉일
    private Integer audiAcc;   // 누적 관객 수

    public BoxOffice() {}      // Jackson 이 객체를 만들 때 사용

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
        return "BoxOffice[rank=" + rank + ", movieNm=" + movieNm
                + ", openDt=" + openDt + ", audiAcc=" + audiAcc + "]";
    }
}
```

### JSON 파서 — ObjectMapper

| 단계 | 코드 | 결과 타입 |
| --- | --- | --- |
| 1. JSON 전체 읽기 (기본은 Map 구조) | `mapper.readValue(resource, new TypeReference<>() {})` | `Map<String, Map<String, Object>>` |
| 2. 배열 꺼내기 (배열은 List) | `result.get("boxOfficeResult").get("dailyBoxOfficeList")` | `List<Map<String, Object>>` |
| 3. Map 하나하나를 DTO 로 | `mapper.convertValue(item, BoxOffice.class)` | `BoxOffice` |

```java
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BoxOfficeJsonParser implements BoxOfficeParser {
    private List<BoxOffice> list = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public List<BoxOffice> getBoxOffice(InputStream resource) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // 1. JSON 객체 { } → Map
            Map<String, Map<String, Object>> result =
                    mapper.readValue(resource, new TypeReference<>() {});
            // 2. JSON 배열 [ ] → List
            List<Map<String, Object>> boxofficeList =
                    (List<Map<String, Object>>) result.get("boxOfficeResult").get("dailyBoxOfficeList");
            // 3. Map → BoxOffice
            list = boxofficeList.stream()
                    .map(item -> mapper.convertValue(item, BoxOffice.class))
                    .collect(Collectors.toList());
        } catch (IOException e) {          // readValue 는 IOException(checked)을 던짐
            e.printStackTrace();
        }
        return list;
    }
}
```

- `new TypeReference<>() {}` — 익명 클래스로 제네릭 타입 정보(`Map<String, Map<String, Object>>`)를 Jackson 에 넘김
    - 그냥 `Map.class` 로는 안쪽 제네릭 타입을 전달할 수 없음
- `get(...)` 의 결과는 `Object` 이므로 List 로 형변환 필요

### 사용

```java
import java.io.InputStream;
import java.util.List;

public class BoxOfficeCLI {
    public static void main(String[] args) {
        InputStream resource = BoxOfficeCLI.class.getResourceAsStream("/boxoffice.json");
        BoxOfficeParser parser = new BoxOfficeJsonParser();
        List<BoxOffice> list = parser.getBoxOffice(resource);
        list.forEach(System.out::println);
    }
}
/* 교재 결과 (SAX·DOM 과 같은 결과)
BoxOffice[rank=1, movieNm=발신제한, openDt=2021-06-23, audiAcc=499902]
BoxOffice[rank=2, movieNm=크루엘라, openDt=2021-05-26, audiAcc=1554649]
...
*/
```

- 실제 API 는 `new URL("http://kobis.or.kr/kobisopenapi/...").openStream()` 으로 받을 수 있음 (교재: 요청이 몰리면 서버가 다운되니 수업 중에는 파일로, 나중에 개별적으로 해 보기)

> 참고: 교재 코드는 `mapper.readValue(...)` 를 try 없이 호출한다. readValue 는 checked 예외(IOException)를 던지고 인터페이스 메서드 `getBoxOffice` 에는 throws 가 없으므로 그대로는 컴파일되지 않는다(`unreported exception IOException`). 위 코드는 try-catch 로 감싼 것.

---

## 참고

### JSON 값의 종류 (json.org 요약)

| 값 | 예 |
| --- | --- |
| 객체 | `{ "addr": { "city": "seoul" } }` |
| 배열 | `["Java", "Python"]` |
| 문자열 (큰따옴표만) | `"홍길동"` |
| 숫자 | `25`, `175.5` |
| 불리언 | `true`, `false` |
| null | `null` |

- key 는 반드시 큰따옴표 문자열
- 마지막 항목 뒤에 쉼표(`,`)를 붙이면 안 됨
### Jackson 의존성

- 교재 슬라이드의 `jackson-annotation` 은 실제 아티팩트명 `jackson-annotations`
- Maven/Gradle 에서는 `com.fasterxml.jackson.core:jackson-databind` 하나만 추가하면 core·annotations 가 함께 들어옴
