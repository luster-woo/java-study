# Authorization 실습

> 교재: 15기_자바전공_11_security_1_(0521) (전공) — 6. Authorization 실습

> [← Authentication 실습](05_Authentication_실습.md) · [day13 목록](../README.md) · [JWT 개요 →](07_JWT_개요.md)

---

## SecurityFilterChain 빈과 Authorization

- 전체 흐름([개요](01_Spring_Security_개요.md#인가authorization-흐름)) 중 **AuthorizationFilter → AuthorizationManager** 구간을 SecurityFilterChain 빈으로 설정
- 필터 13종 목록·역할은 [SecurityFilterChain](02_SecurityFilterChain.md#securityfilter의-종류) 표와 같음

### SecurityFilterChain 구성 (TODO: 06)

- Spring Security 동작 커스터마이징은 **대부분 여기서** 진행
- 파라미터로 받는 **HttpSecurity 객체의 메서드**로 각 Security Filter 동작 제어

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll()); // 이외의 경로는 그냥 통과
    // ...
    return http.build();
}
```

- 대표적인 필터 제어 메서드

| 메서드 | 용도 | 관련 필터 | 필터 역할 |
|---|---|---|---|
| `authorizeHttpRequests()` | URL별 권한 설정 | AuthorizationFilter | 최종 권한 검증, 접근 제어 |
| `formLogin()` | 폼 로그인 설정 | UsernamePasswordAuthenticationFilter | `POST /login` 처리, 인증 수행 |
| `logout()` | 로그아웃 설정 | LogoutFilter | 로그아웃 요청 처리, 세션/쿠키 제거 |
| `rememberMe()` | 자동 로그인 | RememberMeAuthenticationFilter | Remember-me 쿠키 검증, 자동 로그인 |
| `csrf()` | CSRF 보호 | CsrfFilter | CSRF 토큰 검증 |

### Authorization 필터 설정 — 경로별 권한

- 특정 경로를 쓸 수 있는 권한 지정

```java
authorizeManager.requestMatchers("/secured/user/**").hasRole("USER")
//                               └─── 대상 경로 ───┘ └ 필요 조건 ┘
```

- 경로 패턴: 일반 Ant 표현식 + Spring Security가 확장한 표현식

| 패턴 | 매칭 |
|---|---|
| `?` | 문자 1개 |
| `*` | 한 경로 세그먼트 안의 0개 이상 문자 |
| `**` | 0개 이상의 경로 세그먼트 |
| `{pattern1,pattern2}` | 여러 패턴 중 하나 |
| `[abc]` | a, b, c 중 문자 하나 |

```java
.requestMatchers("/secured/**").authenticated()
.requestMatchers("/admin/*").hasRole("ADMIN")
.requestMatchers("/api/v1/**/*.json").hasRole("API_USER")
.requestMatchers("/public/??/*.html").permitAll()
.requestMatchers("/images/*.{jpg,png,gif}").permitAll()
.requestMatchers("/static/[abc]/*.js").permitAll()
```

| 예 | 매칭되는 요청 예 |
|---|---|
| `/admin/*` | `/admin/list` (O), `/admin/a/b` (X — 세그먼트 1개만) |
| `/public/??/*.html` | `/public/ko/index.html` (두 글자 폴더) |
| `/secured/**` | `/secured`, `/secured/user`, `/secured/admin/page` |

- 정말 복잡한 조건은 **RegexRequestMatcher**로 정규 표현식 사용

### 자주 쓰는 role 설정 메서드

| 메서드 | 내용 |
|---|---|
| `hasRole(role)` | 부여된 권한이 그 역할과 일치하는지 확인 |
| `hasAnyRole(role...)` | 부여된 역할 중 **하나라도** 일치하는지 확인 — `hasAnyRole("ADMIN", "STAFF")` → ADMIN **이나** STAFF |
| `permitAll()` | 모두 접근 허가 |
| `denyAll()` | 모두 접근 금지 |
| `authenticated()` | 인증된 경우만 허용 |

### 경로와 ROLE 설정 시 주의 사항

- 설정한 **순서대로** 검사하다가 처음 매칭된 규칙에서 **멈춤**

```java
// 잘못된 순서
http.authorizeHttpRequests(authorize -> authorize
        .requestMatchers("/secured/**").authenticated()
        .requestMatchers("/secured/user/**").hasRole("USER")
        .requestMatchers("/secured/admin/**").hasRole("ADMIN")
        .anyRequest().permitAll());
```

- Q. `/secured/admin`에 필요한 ROLE은? → **없음**. 첫 줄 `/secured/**`에 먼저 걸려 **인증만 되면 통과** (USER도 admin 화면 접근 가능)

```java
// 올바른 순서
http.authorizeHttpRequests(authorize -> authorize
        .requestMatchers("/secured/user/**").hasRole("USER")    // USER ROLE 필요
        .requestMatchers("/secured/admin/**").hasRole("ADMIN")  // ADMIN ROLE 필요
        .requestMatchers("/secured/**").authenticated()         // secured/**는 인증만 받으면 OK
        .anyRequest().permitAll());
```

- **세부 경로 먼저**, 뭉뚱그린 경로는 **마지막**
- `anyRequest()` 뒤에는 다른 경로 설정 불가

### SecurityFilterChain 구성 완성 (TODO: 06)

- `HttpSecurity.authorizeHttpRequests()`로 경로별 접근 가능 role 지정
- **AuthorizationFilter**가 지정된 경로(resource) 사용 시 필요한 권한 확인
- 이전 실습에서 세션을 직접 확인하던 **SessionInterceptor는 이제 필요 없음**

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // Authorization 필터 설정
    http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/secured/user/**").hasRole("USER")    // USER role 필요
            .requestMatchers("/secured/admin/**").hasRole("ADMIN")  // ADMIN role 필요
            .requestMatchers("/auth/**").authenticated()            // auth는 인증만 필요
            .anyRequest().permitAll());                             // 이외의 경로는 그냥 통과

    // 기본 form login 활용 : UsernamePasswordAuthenticationFilter 설정
    http.formLogin(Customizer.withDefaults());
    // 기본 logout 활용 : LogoutFilter 설정
    http.logout(Customizer.withDefaults());

    return http.build();
}
```

- SecurityFilterChain 빈을 **직접 등록하면 Boot 기본 설정이 무력화**됨
  - `formLogin()`을 다시 켜지 않으면 **로그인 불가**
- RoleHierarchy(ADMIN ⊃ USER ⊃ GUEST)와 합치면

| 사용자 | `/` | `/auth/**` | `/secured/user/**` | `/secured/admin/**` |
|---|---|---|---|---|
| 비로그인 | O | 로그인 페이지로 redirect | 로그인 페이지로 redirect | 로그인 페이지로 redirect |
| USER | O | O | O | 403 |
| ADMIN | O | O | O (USER 포함) | O |

### 테스트 — 인증 상태에 따른 페이지 접근

```java
// 클래스: @SpringBootTest + @AutoConfigureMockMvc, MockMvc mockMvc 주입 (교재 슬라이드엔 메서드만 있음)
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Test
@DisplayName("인증 없는 상태에서 여러 엔드포인트 접근 테스트")
void notAuthenticatedTest() throws Exception {
    // given
    var req = MockMvcRequestBuilders.get("/");
    // when & then
    mockMvc.perform(req).andExpect(MockMvcResultMatchers.status().isOk());

    req = MockMvcRequestBuilders.get("/secured/user");
    mockMvc.perform(req).andExpect(MockMvcResultMatchers.status().is3xxRedirection());
}

@Test
@DisplayName("USER 권한이 있는 사용자가 권한별 페이지에 접근하는 경우를 테스트")
@WithMockUser(roles = "USER")   // 테스트용 권한의 사용자
void userRoleTest() throws Exception {
    mockMvc.perform(get("/secured/user")).andExpect(status().isOk());
    mockMvc.perform(get("/secured/admin")).andExpect(status().isForbidden());
}
```

| 사용자 | 요청 | 기대 결과 | 이유 |
|---|---|---|---|
| 비로그인 | `/` | 200 OK | `anyRequest().permitAll()` |
| 비로그인 | `/secured/user` | 3xx redirect | 인증 필요 → 로그인 페이지로 |
| `@WithMockUser(roles="USER")` | `/secured/user` | 200 OK | `hasRole("USER")` 충족 |
| `@WithMockUser(roles="USER")` | `/secured/admin` | 403 Forbidden | 인증은 됐지만 ADMIN 아님 |

- `@WithMockUser`: spring-security-test가 주는 기능 → 로그인 과정 없이 지정한 권한의 사용자로 요청
- 비로그인은 **redirect(인증 문제)**, 권한 부족은 **403(인가 문제)** → 둘을 구분

### 내일 수업을 위해서

- Spring Security 흐름을 그림으로 설명할 수 있게 정리
  - request → DelegatingFilterProxy → FilterChainProxy → SecurityFilterChain
  - 인증: AuthenticationFilter → AuthenticationManager(ProviderManager) → AuthenticationProvider → UserDetailsService·PasswordEncoder → SecurityContext
  - 인가: AuthorizationFilter → SecurityContext에서 인증 정보 → AuthorizationManager → 통과 / AccessDeniedException

---

## 참고

- 교재 role 메서드 표의 `hasRole('ADMIN', 'STAFF')` → "ADMIN과 STAFF 권한 필요"는 틀림. `hasRole()`은 role **하나만** 받음. 둘 다 필요하면 `access(...)`나 SpEL `hasRole('ADMIN') and hasRole('STAFF')`로 표현
- 교재 표는 `permitAll()`·`denyAll()`을 "모든 인증자"로 설명하지만, `permitAll()`은 **인증 안 된 사용자까지 포함한 모두**를 허용함
- 교재 슬라이드 제목 `authorizedHttpRequests`는 오타 → `authorizeHttpRequests`
- Spring Boot 3(Spring MVC 6)의 기본 경로 파서(PathPatternParser)에서는 `**`가 패턴 **끝**에만 올 수 있음 → `/api/v1/**/*.json`은 시작 시 예외가 남. `{jpg,png,gif}`, `[abc]`도 경로 변수·리터럴로 해석될 수 있으니, 이런 패턴은 직접 테스트하거나 `RegexRequestMatcher`를 쓰는 편이 안전
- 테스트의 `MockMvcRequestBuilders.get(...)`과 static import한 `get(...)`은 같은 메서드(교재가 두 형태를 섞어 씀)
