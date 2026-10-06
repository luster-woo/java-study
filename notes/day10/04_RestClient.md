# RestClient

> 교재: 15기_자바전공_10_rest_0520 (전공) — 4. RestClient

> [← Swagger](03_Swagger.md) · [day10 목록](../README.md) · [Service Layer와 TX →](05_Service_Layer와_TX.md)

---

## SOP와 CORS 처리

### SOP(Same Origin Policy, 동일 근원 정책)

- JavaScript에서 Ajax 사용 시 **현재 문서와 Same Origin으로만** 데이터 전송 허용
- **Same Origin**: **Protocol, domain, port**가 모두 같음
- 일반 요청(페이지 이동 등)은 문제없고, **AJAX 통신**이 막힘

```text
브라우저(문서: http://localhost:8080)
   ├─ AJAX → http://localhost:8080/...      : same origin → 허용
   └─ AJAX → http://127.0.0.1:8080/api/...  : domain 다름 → CORS 정책으로 차단
```

- 콘솔 오류 예(요약): `http://localhost:8080`에서 `http://127.0.0.1:8080/api/v1/members`로 보낸 fetch가 **CORS policy**로 막힘
  - preflight 요청에 대한 응답에 **`Access-Control-Allow-Origin` 헤더가 없음**

### CORS: 서로 다른 domain 간 리소스 공유 설정 (TODO: 08)

- 요청을 **받는 서버**가 "이 origin은 허용한다"고 응답 헤더로 알려 줘야 함

#### @CrossOrigin — 컨트롤러 별 설정

```java
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CrossOrigin {
    @AliasFor("value")
    String[] origins() default {};           // 허용할 origin 목록. 기본은 모든 출처 허용 ex) http://domain.com
    String[] originPatterns() default {};    // 패턴으로 origin 설정 {"http://*.domain.com"}
    String[] allowedHeaders() default {};    // 실제 요청에서 쓸 수 있는 헤더 목록, 생략 시 모든 헤더 허용
    RequestMethod[] methods() default {};    // 허용할 HTTP 메서드 {RequestMethod.GET}
    long maxAge() default -1;                // preflight 결과 캐시 시간, -1은 브라우저 기본값
}
```

#### WebMvcConfigurer — 전역 설정

```java
@Configuration
public class MvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
                .allowedOrigins("*");
    }
}
```

- 문제: `@CrossOrigin`이나 CORS 설정은 **내가 사용하려는(요청 받는) 서버**에 붙어야 함
  - 외부 서버가 허락해 주지 않으면? → 내 코드로는 해결 불가 → 다음 방법

### cross-domain request 처리 (TODO: 09)

- 문제는 **JavaScript를 통한 요청**이고 **일반 요청은 문제되지 않음**
- 해결: JavaScript는 **내 서버(same origin)** 로 요청 → 내 서버가 원격지로 **일반 요청** → 결과 전달
  - 내 서버가 **proxy 역할**을 함

```text
브라우저 JS ──AJAX(same origin)──> 내 서버(localhost:8080) ──일반 요청──> service domain(외부 API)
           <──────── JSON ───────                         <──── 응답 ────
                                     (proxy 역할)
```

- REST 엔드포인트 호출 작성 옵션(동기형)

| 클라이언트 | 형태 |
|---|---|
| **RestClient** | fluent API 형태의 클라이언트 |
| **RestTemplate** | template 메서드 형태의 클라이언트 |

---

## RestClient 프로그래밍

### URI 구성

- 단순한 문자열로 이어 붙이기

```java
StringBuilder strUri = new StringBuilder("https://api.vworld.kr/ned/data/admCodeList?")
        .append("format=json&")
        .append("numOfRows=10&domain=localhost&")
        .append("key=").append(vworldKey);
```

- **UriComponentsBuilder** API 활용
  - `queryParam`으로 파라미터 추가
  - `{numOfRows}`, `{key}` 같은 **템플릿 변수** → 필요 시 바인딩, 템플릿 형태로 재사용 가능
  - `encode()` 후 `buildAndExpand(값...)`로 변수에 순서대로 값 채움 → `toUri()`

```java
UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://api.vworld.kr/ned/data/admCodeList")
        .queryParam("format", "json")
        .queryParam("domain", "localhost")
        .queryParam("numOfRows", "{numOfRows}")   // 필요 시 바인딩 처리 가능
        .queryParam("key", "{key}")
        .encode();
URI uri = builder.buildAndExpand(10, vworldKey).toUri();
// https://api.vworld.kr/ned/data/admCodeList?format=json&domain=localhost&numOfRows=10&key=...
```

### RestClient 구성

- `RestClient.create(...)`(static create method) 또는 `RestClient.builder()` 활용

```java
RestClient defaultClient = RestClient.create(uri);              // 간단한 요청 처리

RestClient customClient = RestClient.builder()
        .baseUrl("https://example.com")                         // 기본 URI
        .defaultUriVariables(Map.of("variable", "foo"))         // path variable 전달
        .defaultHeader("My-Header", "Foo")                      // 헤더 설정
        .defaultCookie("My-Cookie", "Bar")                      // 쿠키 전달
        .requestInterceptor(myCustomInterceptor)                // interceptor 등록
        .build();
```

### RestClient 사용

- `RestClient#method(HttpMethod)` 또는 `get()`, `post()` 등 편의 메서드
- 흐름: **HTTP 메서드 선택 → `retrieve()`(요청) → `body(...)`(결과 형식 지정)**
- 제네릭 타입(`Map<String, Object>`)으로 받을 때는 `ParameterizedTypeReference` 사용

```java
RestClient client = RestClient.create(uri);
Map<String, Object> resultMap = client.get()          // Http Method 별 처리
        .retrieve()                                   // 요청
        .body(new ParameterizedTypeReference<Map<String, Object>>() {});   // 결과 포맷
```

### RestClient 활용 (TODO: 09)

- 국토교통부 디지털트윈국토(vworld) API에서 **시/도 정보**를 조회해 그대로 돌려주는 proxy API
- API 키는 설정 파일에 두고 `@Value("${key.vworld}")`로 주입

```java
@RestController
@RequestMapping("/api/v1/etc")
@RequiredArgsConstructor
@Tag(name = "EtcRestController", description = "기타 유틸리티성 기능 제공")
public class EtcRestController implements RestControllerHelper {

    private final RestTemplate restTemplate;

    @Value("${key.vworld}")
    private String vworldKey;

    @GetMapping("/sidos")
    @Operation(summary = "시/도조회", description = "국토교통부 디지털트윈 국토에서 시/도 정보 조회")
    private ResponseEntity<?> getSido() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://api.vworld.kr/ned/data/admCodeList")
                .queryParam("format", "json").queryParam("domain", "localhost")
                .queryParam("numOfRows", "{numOfRows}").queryParam("key", "{key}").encode();
        URI uri = builder.buildAndExpand(10, vworldKey).toUri();

        RestClient client = RestClient.create(uri);
        Map<String, Object> resultMap = client.get().retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        return handleSuccess(resultMap);
    }
}
```

```properties
# application.properties - 키 값은 각자 발급
key.vworld=발급받은_키
```

```text
브라우저 → GET /api/v1/etc/sidos (same origin)
         → EtcRestController → RestClient → api.vworld.kr
         ← JSON(시/도 목록)
```

---

## 참고

> 참고: 교재 `EtcRestController`의 `RestTemplate restTemplate` 필드는 이 메서드에서 쓰이지 않음(요청은 `RestClient.create(uri)`로 보냄). 또 Spring Boot는 `RestTemplate` 빈을 자동 등록하지 않으므로(`RestTemplateBuilder`만 제공) 이 필드를 남기려면 `@Bean`으로 직접 등록해야 함.

> 참고: `RestClient`는 Spring Framework 6.1(Spring Boot 3.2)부터 제공.
