# Controller 단위테스트

> 교재: 15기_자바전공_06_springmvc_2_0513 (전공) — 1. @Controller 단위테스트

> [← Controller](03_Controller.md) · [day09 목록](../README.md) · [요청 처리 메서드의 파라미터 →](05_요청_처리_메서드의_파라미터.md)

---

## @Controller 단위테스트

### Spring Test MVC — 왜 필요한가

- 기존 테스트: 테스트 대상을 **직접 호출**하는 구조
  - `@Service`는 `selectDetail(email)`처럼 바로 호출해서 `Member`를 받아 보면 됨
  - `@Controller`는? 요청 처리 메서드가 `HttpServletRequest`·`HttpServletResponse`를 써야 하는데 이건 WAS가 만들어 줌
- 해결: **Mock 환경** — WAS가 Spring@MVC와 주고받는 부분을 **가상으로** 제공

```text
[실제]  Client ─► WAS ─ HttpServletRequest/Response ─► DispatcherServlet ─► @Controller
[테스트] 테스트 코드 ─► MockMvc ─ MockRequest/MockResponse ─► @Controller
                        (서버를 띄우지 않고 가상의 요청·응답으로 처리)
```

### 테스트 클래스 작성

| 요소 | 역할 |
|---|---|
| **`@WebMvcTest`** | 웹 계층만 띄우는 **web slice test** 애너테이션 — 대상 Controller 지정 |
| **`MockMvc`** | 가상의 웹 환경을 위한 객체 — 요청을 보내고 결과를 검증 |
| **`@MockitoBean`** | 실제 빈의 동작을 흉내내는 **가짜 객체** 등록 |

- `@WebMvcTest`는 Controller 관련 빈만 올림 → Service는 빈으로 없음 → 가짜(Mock)로 채움
- 통합 테스트(전체 빈 + MockMvc)를 하려면 `@SpringBootTest` + `@AutoConfigureMockMvc`

```java
@WebMvcTest(value = SimpleController.class)   // 테스트할 대상 Controller
@Slf4j
public class SimpleControllerTest {

    @Autowired
    MockMvc mock;                             // 가상의 웹 환경을 위한 객체

    @MockitoBean
    SimpleService service;                    // 실제 서비스를 테스트할 계획은 없음 → 가짜

    @BeforeEach
    public void setupMockBean() {
        when(service.echo("Hello")).thenReturn("echo: Hello");   // Mock 객체의 동작 정의
    }
}
```

### MockMvc 테스트 구조

```text
요청 만들기  ──►  perform(실행)  ──►  andExpect(검증)
(RequestBuilder)   (ResultActions)     (ResultMatcher)
```

### 1. 요청 만들기

- `MockMvcRequestBuilders`의 static 메서드 `get` / `post` / `put` / `delete` / `fileUpload` 등 사용
- ServletRequest를 구성하는 메서드들

| 메서드 | 설명 |
|---|---|
| `param` / `params` | 요청 파라미터 설정 |
| `header` / `headers` | 요청 헤더 설정 |
| `cookie` | 쿠키 설정 |
| `requestAttr` | request scope에 attribute 설정 |
| `flashAttr` | flash scope에 attribute 설정 |
| `sessionAttr` | session scope에 attribute 설정 |
| `contentType` | `MediaType`(Enum)으로 요청 컨텐트 타입 설정 |
| `file` | 파일 업로드용 빌더로 만든 경우 업로드 파일 지정 |

```java
MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/add")
        .param("a", "4.5").param("b", "3")
        .cookie(new Cookie("name", "홍길동"))
        .sessionAttr("loginUser", member);
ResultActions actions = mock.perform(request);   // 2. 실행
```

### 2·3. perform 결과 검증

- `perform()`의 결과인 **`ResultActions`**의 `andExpect()`에 **`ResultMatcher`** 전달 → `andExpect`가 Assertion 역할
- `ResultMatcher`는 `MockMvcResultMatchers`의 static 메서드로 얻음

| 메서드 | 검증 대상 |
|---|---|
| `handler` | 요청에 매핑된 컨트롤러(타입·메서드 이름) |
| `request` | 요청 후의 상태 — `request().attribute(..)`, `request().sessionAttribute(..)` |
| `header` | 응답 헤더 값 |
| `cookie` | 응답으로 전달된 쿠키 |
| `content` | 응답 본문 내용 |
| `view` | handler 메서드가 리턴한 view 이름 |
| `model` | model에 담긴 attribute 값 |
| `flash` | flash scope에 담긴 attribute 값 |
| `forwardedUrl` / `forwardedUrlPattern` | forward로 이동하는 대상 경로 |
| `redirectedUrl` / `redirectedUrlPattern` | redirect로 이동하는 대상 경로 |
| `status` | HTTP 상태 코드 |

```java
actions.andExpect(MockMvcResultMatchers.handler().handlerType(SimpleController.class))
       .andExpect(MockMvcResultMatchers.forwardedUrl("/WEB-INF/views/mvc/simple.jsp"))
       .andExpect(view().name("mvc/simple"))
       .andExpect(model().attribute("data", "Hello"))
       .andExpect(status().is(200));
```

- view 이름 `mvc/simple` → ViewResolver의 prefix·suffix가 붙어 forward 경로 `/WEB-INF/views/mvc/simple.jsp`

### @ResponseBody 테스트

- 응답 Content type이 `application/json`인 경우
- JSON 내용은 **`MockMvcResultMatchers.jsonPath()`**로 검증

| 표현식 | 의미 |
|---|---|
| `$` | 받은 JSON 객체의 root element |
| `$.name` | root의 `name` 속성 |
| `$.hobby[2]` | root의 `hobby` 배열에서 2번 index 요소 |
| `$.hobby[2].name` | `hobby`의 2번 index 요소의 `name` 속성 |
| `$.hobby.length()` | `hobby` 배열의 길이 |

```java
// 대상: GET /json → Map.of("name", "hong gil dong", "age", 10)
actions.andExpect(MockMvcResultMatchers.handler().methodName("json"))
       .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
       .andExpect(status().is(200))
       .andExpect(jsonPath("$.name", equalTo("hong gil dong")))
       .andExpect(jsonPath("$.age", equalTo(10)));
```

---

## 참고

> 참고: 교재 코드에서 Mock 필드는 `sService`로 선언하고 `when(service.echo(...))`로 써서 이름이 맞지 않음 — 위 코드는 `service`로 통일함.

> 참고: 교재의 `RequestActions actions = mock.perform(request);`에서 타입 이름은 `ResultActions`가 맞음.

- `@MockitoBean`은 Spring Framework 6.2(Spring Boot 3.4)부터 제공. 이전 버전은 `@MockBean`(3.4부터 deprecated)
- Spring Boot 4부터 `@WebMvcTest` 패키지가 바뀜 → `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest` (의존성 `spring-boot-starter-webmvc-test`). 3.x는 `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest`
- 실습 코드: `src/day09/FW_05_MVC/src/test/java/com/ssafy/fw_05_mvc/controller/SimpleControllerTest.java`
- 파일 업로드 요청은 최신 버전에서 `MockMvcRequestBuilders.multipart()` 사용(`fileUpload()`는 deprecated)
- 자주 쓰는 static import: `MockMvcRequestBuilders.*`, `MockMvcResultMatchers.*`, `org.mockito.Mockito.when`, `org.hamcrest.Matchers.equalTo`
