# SecurityFilterChain

> 교재: 15기_자바전공_11_security_1_(0521) (전공) — 2. SecurityFilterChain

> [← Spring Security 개요](01_Spring_Security_개요.md) · [day13 목록](../README.md) · [Authentication →](03_Authentication.md)

---

## Filter와 SecurityFilterChain

- 전체 흐름([개요](01_Spring_Security_개요.md#spring-security의-전체적인-흐름)) 중 **request → Servlet Filter Chain → DelegatingFilterProxy → Security Filter Chain** 구간

### Servlet Application에서 Spring Security

- Spring MVC뿐 아니라 일반 Servlet 애플리케이션도 지원하려고 **Servlet Filter 레벨**에서 동작
  - Filter는 Servlet이 요청을 처리하기 **전에** 동작
  - Spring MVC는 Filter를 다 거친 뒤 `DispatcherServlet`에서 시작
- 문제: Filter(`@WebFilter`)는 서블릿 컨테이너가 관리 → 기본적으로 **스프링의 빈·설정과 연결 불가** ("빈이 뭐야?")

```
[Servlet Container]
 request ─▶ Filter(@WebFilter) ─▶ DispatcherServlet ─▶ Interceptor ─▶ @Controller ─▶ @Service ─▶ Secured Resource
                 │               └──────────────── Spring Application Context ────────────────┘
                 │                           (SpringSecurity 관련 빈들도 여기 있음)
                 └─ 컨테이너 소속이라 Context 안의 빈을 모름
```

### DelegatingFilterProxy

- `org.springframework.web.filter.DelegatingFilterProxy` — Spring이 제공하는 **Filter**
- 서블릿 컨테이너의 라이프사이클 ↔ Spring `WebApplicationContext` 사이의 **브릿지**
- ApplicationContext에 선언된 Filter(빈) 목록을 **Lazy**하게 가져와 실행
- Spring Security에서는 Bean Filter인 **FilterChainProxy**를 로딩해 일을 넘김

```
Servlet Filter Chain
  Filter 0 ─▶ DelegatingFilterProxy ─▶ Filter 2 ─▶ ... ─▶ Servlet
                    │ 위임 (빈을 lazy하게 찾아 호출)
                    ▼
  Spring ApplicationContext
    FilterChainProxy (Bean Filter) ── Security 관련 빈들
```

### FilterChainProxy와 SecurityFilterChain

- `org.springframework.security.web.FilterChainProxy`
  - DelegatingFilterProxy에게 작업을 위임받은 **실제 Filter**
  - **SecurityFilterChain을 목록으로** 관리하고 보안 작업을 그 chain에 맡김
- **SecurityFilterChain**
  - 보안 관련 Filter들의 chain
  - 인증 필터, CSRF 필터, 로그아웃 필터 등 **기능별 필터가 연결**됨

```
DelegatingFilterProxy ─▶ FilterChainProxy ─▶ SecurityFilterChain
                                               ├ Security Filter 0
                                               ├ Security Filter 1
                                               ├ ...
                                               └ Security Filter n
```

| 구성 | 소속 | 역할 |
|---|---|---|
| DelegatingFilterProxy | 서블릿 컨테이너(Filter로 등록) | 컨테이너 → 스프링 빈으로 위임 |
| FilterChainProxy | 스프링 빈 | 여러 SecurityFilterChain 관리, 알맞은 chain에 위임 |
| SecurityFilterChain | 스프링 빈 | 보안 필터들의 실제 chain |

### SecurityFilter의 종류

- 기본 chain에 아래 순서로 필터가 걸림

| # | 필터 | 역할 |
|---|---|---|
| 1 | DisableEncodeUrlFilter | URL 인코딩(세션 ID를 URL에 붙이기) 비활성화 — 보안상 이유 |
| 2 | WebAsyncManagerIntegrationFilter | 비동기 웹 요청에서도 SecurityContext 통합 관리 |
| 3 | SecurityContextHolderFilter | SecurityContext를 스레드 로컬 저장소에 설정 |
| 4 | HeaderWriterFilter | 보안 HTTP 응답 헤더 추가 (X-Frame-Options 등) |
| 5 | CsrfFilter | CSRF(Cross-Site Request Forgery) 공격 방지 |
| 6 | LogoutFilter | 로그아웃 처리, 관련 핸들러 실행 |
| 7 | UsernamePasswordAuthenticationFilter | 사용자명/비밀번호 기반 인증 |
| 8 | RequestCacheAwareFilter | 인증 전에 요청했던 URL 캐싱, 인증 후 리다이렉트 |
| 9 | SecurityContextHolderAwareRequestFilter | HttpServletRequest에 보안 관련 메서드 추가 |
| 10 | RememberMeAuthenticationFilter | 'Remember Me' 토큰으로 자동 로그인 |
| 11 | AnonymousAuthenticationFilter | 인증 안 된 사용자에게 익명 인증 객체 제공 |
| 12 | ExceptionTranslationFilter | 보안 예외(인증/인가 실패) 처리 |
| 13 | AuthorizationFilter | 최종 요청 권한 검증, 접근 제어 |

- 필터 구성은 **SecurityFilterChain 빈**에서 설정

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(Customizer.withDefaults())                 // CsrfFilter 기본 설정
        .formLogin(Customizer.withDefaults())            // UsernamePasswordAuthenticationFilter 기본 설정
        .authorizeHttpRequests(authorize ->
                authorize.anyRequest().authenticated()); // AuthorizationFilter: 모든 요청에 인증 필요
    return http.build();
}
```

- `HttpSecurity`의 메서드 하나가 필터 하나(또는 몇 개)의 동작을 켜고 설정함
- `Customizer.withDefaults()` → 그 필터를 기본값으로 사용

### Multiple SecurityFilterChain

- FilterChainProxy는 **경로 기준으로 여러 SecurityFilterChain**을 가질 수 있음
- 경로를 순서대로 확인 → **처음 매칭된 chain 하나**에서만 처리
- 예: "`/api/**`는 인증 정보를 토큰으로, 나머지는 session으로 관리하자"

| 요청 | 처리하는 chain |
|---|---|
| `/api/message/` | SecurityFilterChain 0 (`/api/**`) |
| `/messages/` | SecurityFilterChain n (나머지) |

```java
@Bean                        // API endpoint용
@Order(1)                    // 빈, 즉 FilterChain의 순서
public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/**");   // API 경로에만 이 설정 적용
    // 사용할 filter들에 대한 설정
    return http.build();
}

@Bean                        // 웹 애플리케이션용 보안 설정
@Order(2)
public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
    // 사용할 filter들에 대한 설정
    return http.build();
}
```

- `@Order` 값이 작은 chain부터 매칭 확인
- `securityMatcher()`가 없는 chain은 모든 요청에 매칭 → 순서상 마지막에 둠

---

## 참고

- 교재의 Multiple SecurityFilterChain 코드는 메서드에 `throws Exception`이 빠져 있음 → `http.build()`가 `Exception`을 던지므로 붙여야 컴파일됨(위 코드는 붙인 형태)
- `securityMatcher()` 없는 chain을 앞 순서에 두면 모든 요청을 먼저 가져가서 뒤 chain이 쓰이지 않음 → "구체적인 chain이 작은 `@Order`"
- 필터 목록은 버전마다 조금씩 다름(교재는 Spring Security 6.x 기본 목록)
