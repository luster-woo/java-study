# Interceptor 실습

> 교재: 15기_자바비전공_Framework(Back)_07_0512_Interceptor (비전공) — Interceptor 실습 · 선택 강의

> [← Interceptor](05_Interceptor.md) · [extra 목록](../README.md) · [멀티스레드 →](07_멀티스레드.md)

## 학습 목표

- Interceptor의 개념을 이해하고 활용할 수 있음

---

## Interceptor 실습

### 실습 7-A — LoggingInterceptor로 요청 시간 측정

- 모든 요청의 URI와 처리 시간을 콘솔에 기록
- `preHandle` → 시작 시각을 **request 속성**에 저장
- `afterCompletion` → 저장한 시각으로 경과 시간 계산
- 핸들러 처리 시간 측정은 성능 모니터링·디버깅의 1차 도구

```text
// ① preHandle()
//     request.setAttribute("startTime", 현재시간ms)
//     "[요청 시작] " + URI 콘솔 출력
//     return true
// ② afterCompletion()
//     startTime 속성 꺼내기
//     경과 시간 계산 후 콘솔 출력
//     "[요청 완료] URI → ___ms"
```

```java
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        System.out.println("[요청 시작] " + request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            Exception ex) throws Exception {
        long start = (Long) request.getAttribute("startTime");
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("[요청 완료] " + request.getRequestURI()
                + " → " + elapsed + "ms");
    }
}
```

- 왜 request 속성에 저장하나: 인터셉터는 **싱글톤 Bean** → 필드에 시작 시각을 두면 동시에 들어온 요청끼리 값이 섞임. request 속성은 요청마다 따로 있음
- `getAttribute()`는 `Object`를 돌려주므로 `(Long)`으로 형변환(언박싱되어 `long`에 대입)

### 실습 7-B — LoginInterceptor로 세션 검사

- `/board/**` 경로에 접근하면 로그인 여부 확인
- 세션에 `loginUser`가 없으면 `/user/login`으로 **redirect**
- 있으면 `return true`로 통과

```java
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        // 세션 가져오기 (없으면 null)
        HttpSession session = request.getSession(false);

        // 세션 없음 → 로그인 페이지로
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return false;
        }
        // loginUser 속성 확인
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return false;
        }
        // 로그인 상태 → 통과
        return true;
    }
}
```

```java
// UserDto.java
public class UserDto {
    private String id;
    private String name;
    private String password;
    private String role;   // "USER" or "ADMIN"

    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }
    // getter / setter 생략
}
```

| 코드 | 의미 |
|---|---|
| `getSession(false)` | 세션이 있으면 반환, **없으면 새로 만들지 않고 `null`** (`getSession()`/`getSession(true)`는 없으면 새로 만듦) |
| `sendRedirect(...)` 후 `return false` | 로그인 페이지로 보내고 컨트롤러 실행은 막음 |
| `"ADMIN".equals(this.role)` | 상수를 앞에 두어 `role`이 `null`이어도 NPE 없이 `false` |

### 실습 7-C — AdminInterceptor

- 세션의 `loginUser.role`이 `"ADMIN"`인지 확인
- 관리자가 아니면 **403(`SC_FORBIDDEN`)** 에러 반환
- `/board/delete/**` 경로에만 적용

```java
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req,
            HttpServletResponse res, Object handler) throws Exception {
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.sendRedirect("/user/login");
            return false;
        }
        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null || !user.isAdmin()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자만 가능");
            return false;
        }
        return true;
    }
}
```

| 응답 | 언제 | 의미 |
|---|---|---|
| `sendRedirect()` (302) | 로그인 안 함 | "로그인부터 하세요" → 로그인 페이지로 이동 |
| `sendError(403)` | 로그인했지만 관리자 아님 | 인증은 됐지만 **권한(인가) 없음** |

### 실습 7-D — 3개 인터셉터 다중 등록

- **등록된 순서가 실행 순서**
- 등록 순서: 로깅 → 로그인 체크 → 관리자 체크

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired LoggingInterceptor loggingInterceptor;
    @Autowired LoginInterceptor   loginInterceptor;
    @Autowired AdminInterceptor   adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ① 로깅 – 모든 요청
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/resources/**", "/css/**", "/js/**");

        // ② 로그인 체크 – 게시판
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/board/**")
                .excludePathPatterns("/");

        // ③ 관리자 체크 – 삭제만
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/board/delete/**");
    }
}
```

- 경로별로 거치는 인터셉터

| 요청 | Logging | Login | Admin |
|---|---|---|---|
| `/` | O | - | - |
| `/css/style.css` | - | - | - |
| `/board/list` | O | O | - |
| `/board/delete/3` | O | O | O |

### 단계별 구현 체크리스트

| Step | 내용 | 체크 항목 |
|---|---|---|
| 1 | **Filter 구현** | `LoggingFilter implements Filter` / `doFilter()` — 전처리·후처리 로깅 / `FilterRegistrationBean`으로 등록 |
| 2 | **Interceptor 구현** | `LoggingInterceptor` — 시간 측정 / `LoginInterceptor` — 세션 체크 / `AdminInterceptor` — 역할 체크 |
| 3 | **WebMvcConfig 등록** | `addInterceptors()` 구현 / `addPathPatterns`·`excludePathPatterns` / 실행 순서(order) 확인 |

### 완성 기준 확인

- 기능 확인
  - `/board/**`에 비로그인 접근 → 로그인 페이지로 이동
  - 로그인 후 `/board/list` 접근 → 정상 표시
  - `/board/delete/**`에 일반 사용자 접근 → 403 에러
  - 관리자 로그인 후 삭제 → 정상 처리
- 콘솔 로그 확인
  - 요청마다 `[요청 시작] URI` 출력
  - 응답 후 `[요청 완료] URI + 처리 시간 ms` 출력
  - 다중 인터셉터 실행 순서를 로그로 확인

### Interceptor 요청 흐름 정리

- 아래 그림을 보고 요청 하나가 어디를 거치는지 말로 설명하는 연습

```text
[SERVLET]      Filter(필터)
                  │
[SPRING]       DispatcherServlet (Front Controller)
                  │
[SPRING]       HandlerMapping (핸들러 매핑)  ── 어떤 컨트롤러가 처리할지 찾음
                  │
[SPRING]       Interceptor (인터셉터)  ── preHandle
                  │
[SPRING]       HandlerAdapter (핸들러 어댑터)  ── 찾은 컨트롤러를 실제로 호출
                  │
[BUSINESS]     Controller (컨트롤러)
                  │
[PROXY]        AOP Proxy (@Transactional)
                  │
[BUSINESS]     Service (서비스)
                  │
[DATA]         DAO (Data Access Object) ──▶ 데이터베이스
                  
               ... 결과가 돌아오면

[VIEW]         ViewResolver (뷰 리졸버) ──▶ View (뷰)
```

---

## 참고

- 7-C의 `res.sendRedirect("/user/login")`은 7-B와 달리 `getContextPath()`가 빠져 있음 → 컨텍스트 경로가 `/`가 아닌 앱에서는 잘못된 주소로 이동. 7-B처럼 `req.getContextPath() + "/user/login"`으로 맞추는 것이 안전
- 7-D의 `loginInterceptor`에 붙은 `.excludePathPatterns("/")`는 `/`가 애초에 `/board/**`에 해당하지 않아 **실제로 하는 일이 없음**(지워도 동작 같음)
- 7-D는 순서상 Login 인터셉터가 Admin보다 먼저라, 비로그인 사용자가 `/board/delete/**`에 오면 Admin까지 가지 않고 Login에서 먼저 redirect됨
- 필드 주입(`@Autowired` 필드) 대신 `final` 필드 + 생성자 주입으로 바꿔도 동작은 같음(테스트·불변성 면에서 생성자 주입 권장)
