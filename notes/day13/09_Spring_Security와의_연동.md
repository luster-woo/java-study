# Spring Security와의 연동

> 교재: 15기_자바전공_03_JWT_0608 (전공) — 3. Spring Security와의 연동

> [← JWT 생성](08_JWT_생성.md) · [day13 목록](../README.md) · [refresh token →](10_refresh_token.md)

---

## Spring Security의 흐름과 JWT

### Spring Security의 전체적인 흐름

- 복습: 요청은 Servlet Filter Chain의 **DelegatingFilterProxy** → **Security Filter Chain**을 거쳐 Secured Resource에 도달

```
request → Servlet Filter Chain (Filter 0 → DelegatingFilterProxy → Filter 2)
                                   │
                    Security Filter Chain
                    ├─ ① AuthenticationFilter ── Authentication(P,C) 생성 → AuthenticationManager
                    │        ② ProviderManager → AuthenticationProvider
                    │        ③ UserDetailsService: P로 UserDetails 생성
                    │        ④ UserDetails의 비밀번호와 C 비교 → 실패 시 AuthenticationException
                    │        ⑤ 성공 → Authentication(P:UserDetails, Authorities)을 SecurityContext에 저장
                    └─ AuthorizationFilter
                             ⓐ SecurityContext에서 인증 정보 획득
                             ⓑ AuthorizationManager: 권한? NO → AccessDeniedException / YES → continue
                                                                   → Secured Resource
```

- 질문: JWT 방식이라면 새로 만들 **2개의 필터**는 이 흐름의 어디에 들어갈까?

### Spring Security의 인증 절차 — JWT가 적용되면

- 일반(form login) 절차: 세션에 인증 정보가 없으면 로그인 페이지 → `POST /login`(username, password) → 세션에 저장 → `GET /private`
- JWT에서는 두 지점이 바뀜

| 시점 | 기존 | JWT 적용 | 담당 필터 |
|---|---|---|---|
| 로그인 | 인증 후 세션에 저장 | 인증 후 **토큰 생성**·응답 | **JWTAuthenticationFilter** |
| 이후 요청 | 세션 검사 | **토큰 검증** | **JWTVerificationFilter** |

---

## JWTVerificationFilter

### OncePerRequestFilter

- 기존 Security Filter Chain에 없던 필터라 **새로 만듦** → `OncePerRequestFilter` 상속
- **OncePerRequestFilter**: Spring에서 Filter를 만들 때 쓰는 클래스
  - forward·include가 섞인 요청 처리 과정에서도 **한 번만** 실행됨을 보장
- JWTVerificationFilter의 일: 요청에서 토큰 추출 → 문제가 없으면 **SecurityContextHolder**에 사용자 정보 저장

```java
public class JWTVerificationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. 토큰 검증
        // 2. valid하면 신뢰 → claim으로 사용자 정보 조회
        // 3. 사용자 정보를 SecurityContextHolder에 저장
        filterChain.doFilter(request, response);
    }
}
```

### 토큰 추출 (TODO: 04)

- 요청의 **Authorization 헤더** 사용, 값은 `"Bearer "`로 시작
  - **Bearer**: OAuth 2.0에서 Basic, Digest 등 다른 인증 방식과 구분하기 위한 값
  - `"Bearer "` 뒤의 **공백**까지 포함해 7글자 → `substring(7)`

```http
GET /api/v1/members
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOi...
```

```java
private String extractToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring(7);
    }
    return null;   // 토큰 없음
}
```

### doFilterInternal (TODO: 04)

- 토큰이 없는 요청(login 등)은 인증하지 않고 다음 필터로 넘김
- 토큰이 있으면 claims 획득 → 실제 사용자 조회 → **Authentication 생성 후 SecurityContextHolder에 저장**(Controller 등에서 쓰기 위함)
- 토큰에 문제가 있으면 `getClaims()`가 `JwtException`을 던짐 → 아래 SecurityExceptionHandlingFilter가 처리

```java
@Component
@RequiredArgsConstructor
public class JWTVerificationFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final CustomUserDetailService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token == null) {                  // 인증 토큰이 없는 요청 → 그냥 통과
            filterChain.doFilter(request, response);
            return;
        }
        // 1. 토큰에서 claims 획득 - 예외 없이 통과하면 신뢰할 만한 사용자
        Claims claims = jwtUtil.getClaims(token);
        // 2. 실제 사용자 정보 조회
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.get("email").toString());
        // 3. Authentication 생성 → SecurityContextHolder에 저장
        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 4. 다음 filter 호출
        filterChain.doFilter(request, response);
    }
}
```

- `UsernamePasswordAuthenticationToken(principal, credentials, authorities)` — 권한 목록을 넘기는 3인자 생성자는 **인증 완료** 상태 토큰을 만듦

---

## JWTAuthenticationFilter

### UsernamePasswordAuthenticationFilter 확장 (TODO: 05)

- 편의상 **UsernamePasswordAuthenticationFilter**를 확장 → Spring Security의 인증 과정(AuthenticationManager → UserDetailsService)을 그대로 활용
- 생성자에서 조상 생성자에 `AuthenticationManager` 전달, 로그인 URL·파라미터 이름 지정
- 성공/실패 시 동작만 재정의

```java
@Component
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    public JWTAuthenticationFilter(AuthenticationManager authManager,
                                   MemberService memberService, JWTUtil jwtUtil) {
        super(authManager);                                // 조상 생성자 호출 필요
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
        this.setFilterProcessesUrl("/api/v1/auth/login");  // 로그인 처리 URL
        this.setUsernameParameter("email");                // email을 username으로 사용
    }

    @Override  // 로그인 성공: JWT 발행 → response로 json 전달
    protected void successfulAuthentication(...) { }

    @Override  // 로그인 실패: 실패 내용 전달
    protected void unsuccessfulAuthentication(...) { }
}
```

### 인증 성공 — successfulAuthentication

- 인증된 `Authentication`의 principal(`CustomUserDetails`)에서 Member를 꺼내 access token 발급
- 결과는 JSON으로 응답 → helper `handleResult()`

```java
@Override
protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain chain, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String accessToken = jwtUtil.createAccessToken(userDetails.getMember());

    Map<String, String> result = new HashMap<>();
    result.put("accessToken", accessToken);
    handleResult(response, result, HttpStatus.OK);
}

private void handleResult(HttpServletResponse response, Map<String, String> data, HttpStatus status) {
    response.setContentType("application/json;charset=UTF-8");
    try {
        String jsonResponse = new ObjectMapper().writeValueAsString(data);
        response.setStatus(status.value());
        response.getWriter().write(jsonResponse);
    } catch (IOException e) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
```

```json
// 결과(응답 body)
{"accessToken":"eyJhbGciOiJIUzI1NiJ9...."}
```

### 인증 실패 — unsuccessfulAuthentication

- 여기서 응답을 만들지 않고 예외를 **다시 던짐** → 예외 처리는 **한 곳**(SecurityExceptionHandlingFilter)에 모음
- 필터 체인의 흐름을 타도록 예외 처리 필터를 **앞쪽**에 둠

```java
@Override
protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                          AuthenticationException failed) {
    throw failed;   // 예외 처리는 한 곳에서
}
```

```
Security filter chain: [
  ...
  LogoutFilter
  SecurityExceptionHandlingFilter   ← 뒤쪽 필터들의 예외를 try-catch로 받음
  JWTVerificationFilter
  JWTAuthenticationFilter
  RequestCacheAwareFilter
  ...
]
```

---

## SecurityExceptionHandlingFilter (TODO: 05)

### 커스텀 필터들의 예외 집중 처리

- 예외를 `@ControllerAdvice`에서 처리하면 되지 않나? → 필터는 **DispatcherServlet 앞**에서 동작하므로 `@ControllerAdvice`가 잡지 못함 → 예외 처리 **필터**를 따로 둠
- `filterChain.doFilter()`를 try로 감싸 뒤쪽 필터에서 올라온 예외를 종류별로 응답

| 예외 | 상황 | 응답 |
|---|---|---|
| `JwtException` | 토큰 만료·형식 오류·변조 | 401, `"TOKEN_ERROR"` |
| `BadCredentialsException` | 로그인 실패 | 401, 예외 메시지 |
| 그 외 | 기타 | 500, 예외 메시지 |

```java
@Component
@Slf4j
public class SecurityExceptionHandlingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            if (e instanceof JwtException) {                    // JWT 관련
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, "TOKEN_ERROR");
            } else if (e instanceof BadCredentialsException) {  // 로그인 실패
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            } else {                                            // 기타
                setErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }

    private void setErrorResponse(HttpServletResponse response, HttpStatus status, String message)
            throws IOException {
        log.debug("커스텀 필터에서 발생한 예외 처리: {}, {}", status, message);
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        Map<String, Object> errorDetails = Map.of("status", status.value(), "message", message);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorDetails));
    }
}
```

```json
// 결과(만료된 토큰으로 요청 시)
{"status":401,"message":"TOKEN_ERROR"}
```

---

## SpringSecurity와의 연결

### JWT 동작 절차 (다시 보기)

```
[로그인]  POST /api/v1/auth/login (토큰 X)
          → JWTVerificationFilter: 검사 대상 아님 → 통과
          → JWTAuthenticationFilter: AuthenticationManager → UserDetailsService로 인증
          → JWTUtil로 토큰 생성 → 클라이언트가 저장
[API]     /api/v1/** + header(토큰)
          → JWTVerificationFilter: JWTUtil로 검증 → SecurityContext 저장 → 권한 확인 → Secured Resource
```

### Multiple SecurityFilterChain

- **FilterChainProxy**는 경로 기반으로 **여러 SecurityFilterChain**을 가질 수 있음
  - 순서대로 경로를 확인 → **처음 맞는 chain 하나**에서 처리
- 아이디어: `/api/**`는 인증 정보를 **토큰**으로, 나머지는 **session**으로 관리

```
FilterChainProxy
 ├─ SecurityFilterChain 0 : /api/**   ← /api/message/ 요청
 └─ SecurityFilterChain n : /**       ← /messages/ 요청
```

```java
@Bean
@Order(1)   // FilterChain(빈)의 순서
public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/**");   // API 경로에만 이 설정 적용
    // 사용할 filter들 설정
    return http.build();
}

@Bean
@Order(2)   // 웹 애플리케이션용
public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
    // 사용할 filter들 설정
    return http.build();
}
```

### SecurityFilterChain 구성 비교

| 일반 웹 요청용 FilterChain | API 처리용 FilterChain |
|---|---|
| ... | ... |
| LogoutFilter | LogoutFilter |
| | **SecurityExceptionHandlingFilter** (신규) |
| | **JWTVerificationFilter** (신규) |
| UsernamePasswordAuthenticationFilter | **JWTAuthenticationFilter** (대체) |
| RequestCacheAwareFilter | RequestCacheAwareFilter |
| SecurityContextHolderAwareRequestFilter | SecurityContextHolderAwareRequestFilter |
| AnonymousAuthenticationFilter | AnonymousAuthenticationFilter |
| ... | ... |

### APISecurityConfig (TODO: 06)

- 기존 `CustomSecurityConfig`(웹용)는 `@Order(2)`로 낮추고, API용 `APISecurityConfig`를 `@Order(1)`로 먼저 적용
- `@Order`: 값이 **낮을수록 우선순위 높음**, 생략하면 가장 낮음

```java
@Configuration
public class CustomSecurityConfig {
    @Bean
    @Order(2)   // APISecurityConfig를 먼저 적용하려고 우선순위 조정
    SecurityFilterChain securityFilterChain(HttpSecurity http,
            @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfig) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfig));
        // ... 기존 form login 설정
        return http.build();
    }
}
```

- API chain 설정 ①: 범위·CORS·UserDetailsService·CSRF·세션

| 설정 | 의미 |
|---|---|
| `securityMatcher("/api/**")` | 이 SecurityFilterChain의 동작 범위 |
| `cors(..)` | 명시적 CORS 설정 사용 |
| `userDetailsService(..)` | 사용할 UserDetailsService |
| `csrf(csrf -> csrf.disable())` | CSRF 무력화(세션·쿠키를 안 쓰므로) |
| `sessionCreationPolicy(STATELESS)` | 세션 사용 안 함 |

- API chain 설정 ②: 인가 규칙과 필터 배치
  - `/api/v1/etc/**`, `/api/v1/auth/**`는 바로 통과(`permitAll`), 나머지는 `authenticated`
  - login 처리는 DispatcherServlet을 탈까? → **아니오**, JWTAuthenticationFilter가 필터 단계에서 처리하고 응답함(컨트롤러 불필요)

```java
@Configuration
public class APISecurityConfig {
    @Bean
    @Order(1)   // 낮을수록 우선순위 높음
    public SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http,
            @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfig,
            CustomUserDetailService userDetailsService,
            JWTAuthenticationFilter authFilter,
            JWTVerificationFilter jwtVerifyFilter,
            SecurityExceptionHandlingFilter exceptionFilter) throws Exception {

        http.securityMatcher("/api/**")
            .cors(cors -> cors.configurationSource(corsConfig))
            .userDetailsService(userDetailsService)
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/v1/etc/**", "/api/v1/auth/**").permitAll()  // 바로 통과
                .anyRequest().authenticated());

        http.addFilterBefore(jwtVerifyFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(authFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(exceptionFilter, JWTVerificationFilter.class);
        return http.build();
    }
}
```

```
UsernamePasswordAuthenticationFilter 자리 기준으로
  SecurityExceptionHandlingFilter → JWTVerificationFilter → JWTAuthenticationFilter
  (addFilterBefore)                 (addFilterBefore)        (addFilterAt)
```

### CORS 설정

- Spring Security는 **filter**에서 동작 → CORS도 **filter 단에서** 처리 권장
  - `WebMvcConfigurer`의 CORS 설정은 `@Controller`(DispatcherServlet 이후)에서 처리됨 → 필터에서 막힌 요청엔 적용 안 됨
- `CorsConfigurationSource` 빈을 만들어 경로별로 등록

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);
    source.registerCorsConfiguration("/member/checkEmail", configuration);
    return source;
}
```

---

## 클라이언트의 토큰 전달

### Authorization header 활용 (TODO: 07)

- 로그인 응답으로 받은 accessToken을 요청마다 `Authorization: Bearer {토큰}` 헤더에 담아 보냄

```js
document.querySelector("#list2").addEventListener("click", async () => {
  try {
    const response = await fetch("/api/v1/members", {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });
    const json = await response.json();
    document.querySelector("#result").innerHTML = JSON.stringify(json);
  } catch (error) {
    console.error("Error:", error);
  }
});
```

---

## 참고

> 참고: 교재 `extractToken()`의 else 분기는 `throw null;`로 적혀 있으나, `doFilterInternal()`이 `token == null`로 "토큰 없음"을 판단하므로 **`return null;`**이 맞음(`throw null`은 NullPointerException을 던짐).

> 참고: 교재 `successfulAuthentication()`은 `jwtUtil.createAccessToken(accessExpMin, ...)`으로 받은 변수 `jwt`를 만들고 `accessToken`을 put하고 있음. JWTUtil의 `createAccessToken(Member)`에 맞춰 노트에서는 `createAccessToken(userDetails.getMember())` → `accessToken` 변수로 통일함.

- 교재의 `doFilterInternal`은 `throws` 없이 적힌 쪽이 있으나, `filterChain.doFilter()`가 `IOException`·`ServletException`을 던지므로 노트처럼 선언해야 컴파일됨
- `JWTAuthenticationFilter` 생성자가 `AuthenticationManager`를 주입받으려면 그 타입의 **빈이 있어야 함** — 보통 `AuthenticationConfiguration.getAuthenticationManager()`로 빈을 등록함
- 필터를 `@Component`로 빈 등록하면 Spring Boot가 이를 **일반 서블릿 필터로도 자동 등록**함 → Security 체인 밖에서도 실행될 수 있음. 막으려면 `FilterRegistrationBean`에서 `setEnabled(false)`
- `setAllowedOrigins("*")`는 `setAllowCredentials(true)`와 함께 쓸 수 없음(쿠키를 쓰지 않는 토큰 방식이라 여기선 문제없음). 자격 증명이 필요하면 출처를 명시하거나 `setAllowedOriginPatterns` 사용
