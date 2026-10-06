# REST 서비스 작성

> 교재: 15기_자바전공_10_rest_0520 (전공) — 2. REST 서비스 작성

> [← REST API](01_REST_API.md) · [day10 목록](../README.md) · [Swagger →](03_Swagger.md)

---

## @RestController 작성 (TODO: 02)

### 회원 가입 처리 — 기존 @Controller → @RestController

- 기존 `@Controller` 방식: 성공하면 flash 메시지 + redirect, 실패하면 폼 화면으로 돌아감
- REST에서는 **화면, 세션 이야기를 하지 않음** → 데이터만 돌려줌

```java
// 기존 @Controller - 화면 이동 중심
@PostMapping("/regist-member")
private String registMember(@ModelAttribute Member member, Model model, RedirectAttributes redirs) {
    try {
        mService.registMember(member);
        redirs.addFlashAttribute("alertMsg", "등록되었습니다. 로그인 후 사용해주세요.");
        return "redirect:/";
    } catch (DataAccessException e) {
        model.addAttribute("error", e.getMessage());
        return "member/member-regist-form";
    }
}
```

```java
// REST - 데이터만 반환
@RestController
@RequestMapping("/api/v1/members")
public class MemberRestController {
    private final MemberService mService;

    @PostMapping
    private Map<String, ?> registMember(@ModelAttribute Member member) {
        mService.registMember(member);
        return Map.of("member", member);   // JSON 으로 변환되어 응답
    }
}
```

| 비교 | @Controller | @RestController |
|---|---|---|
| URL | `/regist-member` (동사형) | `POST /api/v1/members` (자원 + 메서드) |
| 반환 | view 이름(`String`) | 데이터(`Map` → JSON) |
| 부가 정보 | Model, RedirectAttributes | 없음 |

### API Test — Talend API Tester

- 서버가 `@ModelAttribute`로 받으므로 요청 Content-Type은 **`application/x-www-form-urlencoded`**
- 응답은 200이지만, 자료 추가는 엄밀히 말하면 **201 Created** 가 맞음 → 상태 코드를 직접 지정할 방법 필요

```http
POST /api/v1/members
Content-Type: application/x-www-form-urlencoded

email=...&name=...&password=...
```

---

## ResponseEntity (TODO: 02)

- REST 서비스 결과를 **HTTP 상태 코드와 함께** 전송하기 위해 사용
- 파라미터를 `@RequestBody`로 바꿈 → 이제 요청은 JSON으로 보냄

```java
private ResponseEntity<?> registMember(@RequestBody Member member) {
    mService.registMember(member);
    return ResponseEntity.status(HttpStatus.CREATED)          // 201
                         .body(Map.of("member", member));
}
```

```http
POST /api/v1/members
Content-Type: application/json

{"email": "...", "name": "...", "password": "..."}
```

### API Test — 저장 실패 시

- `@RequestBody`로 바꾼 뒤 Talend API Tester로 이전과 같은 요청을 다시 보내 봄 → 자료 저장이 안 되는 게 정상
- 응답에서 어색한 점 찾기 → 오류가 났을 때 REST 클라이언트가 받을 응답을 정리해야 함 → **REST 전용 예외 처리**

---

## RestController에서 발생하는 예외 처리

- `@RestControllerAdvice(annotations = RestController.class)`: **`@RestController`가 붙은 클래스만** 대상
- `@Order(Ordered.HIGHEST_PRECEDENCE)`: 기존 `@ControllerAdvice`보다 **우선권** 부여
- 모든 API 예외를 여기서 **JSON(ProblemDetail)** 으로 응답
- **ProblemDetail**: 표준 오류 응답 객체(RFC 7807)

```java
@Slf4j
@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleRestException(Exception e) {
        log.error("[@RestController에서 발생한 오류] ", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
```

---

## REST 요청 — 실제 화면 작성 (TODO: 02)

- 화면에서는 JavaScript `fetch`로 JSON을 보내고 JSON을 받음
- `response.ok`: 2XX 성공 여부
- `catch`: 네트워크 에러, CORS 위배 등

```javascript
document.querySelector("#btnRegist").addEventListener("click", async () => {
  let url = "${root}/api/v1/members";
  let html;
  try {
    const response = await fetch(url, {
      method: "post",
      headers: { "Content-Type": "application/json" },     // 서버는 @RequestBody
      body: JSON.stringify({
        email: document.querySelector("#email").value,
        name: document.querySelector("#name").value,
        password: document.querySelector("#password").value,
      }),
    });
    console.log("성공 여부(2XX)? ", response.ok, response);
    const json = await response.json();
    html = JSON.stringify(json);
  } catch (err) {
    console.error("네트워크에러, CORS 위배 등", err);
    html = JSON.stringify(err);
  } finally {
    document.querySelector("#result").innerHTML = html;
  }
});
```

---

## 단위테스트 — @RequestBody (TODO: 02-06)

- **Slice Test**: `@WebMvcTest` — WebMvc 관련 컴포넌트만 로드해서 테스트 환경 구성
- 서비스는 `@MockitoBean`으로 가짜 객체 등록 → `when(...).thenReturn(...)`으로 동작 정의
- 서버가 `@RequestBody`이므로 `ObjectMapper`로 객체를 JSON 문자열로 바꿔 요청
- 응답 검증: 상태 코드 `isCreated()`(201), `jsonPath`로 JSON 값 확인

```java
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = { MemberRestController.class, AuthRestController.class })
public class MemberRestControllerTest {

    @Autowired
    MockMvc mock;

    @MockitoBean
    BasicMemberService mService;

    @Test
    public void registMemberTest() throws Exception {
        // given
        Member member = Member.builder().name("hong").email("mvctest@ssafy.com").password("pass").build();
        ObjectMapper mapper = new ObjectMapper();
        RequestBuilder builder = post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)        // JSON 형태로 요청 전송
                .content(mapper.writeValueAsString(member));

        // MockitoBean 으로 등록한 mService 의 동작 정의
        when(mService.registMember(member)).thenReturn(1);

        // when & then
        mock.perform(builder)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.member.email", equalTo(member.getEmail())));
    }
}
```

- 남은 테스트 4개(TODO 02-06의 나머지)는 같은 방식으로 작성

---

## 참고

> 참고: `@MockitoBean`은 Spring Framework 6.2(Spring Boot 3.4)부터 쓰는 이름. 이전 버전에서는 `@MockBean`(Boot 3.4부터 deprecated).

> 참고: RFC 7807은 2023년 RFC 9457로 개정됨. `ProblemDetail`의 JSON 형식(`type`, `title`, `status`, `detail`, `instance`)은 그대로.
