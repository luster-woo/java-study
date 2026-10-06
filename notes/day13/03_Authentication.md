# Authentication

> 교재: 15기_자바전공_11_security_1_(0521) (전공) — 3. Authentication

> [← SecurityFilterChain](02_SecurityFilterChain.md) · [day13 목록](../README.md) · [Authorization →](04_Authorization.md)

---

## Servlet Authentication Architecture

- 전체 흐름([개요](01_Spring_Security_개요.md#인증authentication-흐름)) 중 **인증 1~5단계**: AuthenticationFilter → AuthenticationManager → AuthenticationProvider → UserDetailsService → SecurityContext

### 인증 요청 처리

- **AbstractAuthenticationProcessingFilter**: 사용자 요청을 가로채 **Authentication 객체 생성**
  - 대표 하위 클래스: **UsernamePasswordAuthenticationFilter**
- 만든 Authentication은 **AuthenticationManager**에게 넘어가 인증 처리됨

```java
// 요청에서 꺼낸 username, password로 인증 전 Authentication 생성
Authentication authRequest =
        new UsernamePasswordAuthenticationToken(username, password);
```

```
HttpServletRequest
  ─▶ AbstractAuthenticationProcessingFilter (UsernamePasswordAuthenticationFilter)
        ├ username, password 꺼내서 UsernamePasswordAuthenticationToken 생성
        └ AuthenticationManager에 전달 ─▶ 인증 처리
               ├ 실패 → AuthenticationException
               └ 성공 → 인증된 Authentication → SecurityContextHolder
```

### Authentication

- 인증 정보를 담는 객체, 대표 구현체는 **UsernamePasswordAuthenticationToken**
- 세 가지로 구성

| 구성 | 뜻 | 예 |
|---|---|---|
| **Principal** | 사용자 식별 정보 | username / `UserDetails` 타입 객체 |
| **Credentials** | 자격 증명 | password (또는 토큰, 인증서 등) |
| **Authorities** | 사용자가 받은 권한 | `ROLE_ADMIN`, `ROLE_USER` |

- 인증 전과 후에 담긴 내용이 다름

| 시점 | 누가 만드나 | Principal | Credentials | Authorities |
|---|---|---|---|---|
| 인증 전 | 사용자 요청 → AuthenticationManager에 제공 | username | password | - |
| 인증 후 | AuthenticationManager가 인증 완료 후 반환 | `UserDetails` | **보안을 위해 삭제** | 부여된 권한 |

### SecurityContext

- 현재 사용자의 **Authentication을 세션에 보관**하는 객체
- **SecurityContextHolder**를 통해 접근
- **ThreadLocal**에 저장 → 같은 스레드 안에서는 어디서든 현재 사용자 정보 꺼낼 수 있음

```
SecurityContextHolder
  └ SecurityContext
      └ Authentication
          ├ Principal
          ├ Credentials
          └ Authorities
```

### AuthenticationManager

- 인증을 **어떻게 수행할지 정의한 인터페이스**, 구현체는 **ProviderManager**
- **ProviderManager**: 여러 **AuthenticationProvider**에게 인증을 맡김
  - 순서대로 인증 요청 → 끝까지 아무도 처리 못 하면 `ProviderNotFoundException`
- 반환된 인증 정보는 **SecurityContextHolder**에서 관리
  - Session scope에 `SPRING_SECURITY_CONTEXT`라는 이름으로 등록

```
<<interface>> AuthenticationManager
        ▲ 구현
   ProviderManager
        ├ AuthenticationProvider ── DaoAuthenticationProvider
        ├ AuthenticationProvider ── RememberMeAuthenticationProvider
        ├ ...
        └ AuthenticationProvider ── AnonymousAuthenticationProvider
```

### AuthenticationProvider

- **특정 유형**의 인증을 처리

| Provider | 담당 |
|---|---|
| **DaoAuthenticationProvider** | username/password 기반 인증 |
| RememberMeAuthenticationProvider | RememberMe 기능용 인증 |
| AnonymousAuthenticationProvider | 익명 사용자 인증 |

### DaoAuthenticationProvider

- **UserDetailsService**를 이용해 인증 처리

```
DaoAuthenticationProvider
  ├ has a ─▶ <<interface>> UserDetailsService   (UserDetails를 이용해 인증 처리)
  │               ▲
  │          <<interface>> UserDetailsManager
  │               ▲
  │       ┌───────┴─────────────────┐
  │  InMemoryUserDetailsManager   JdbcUserDetailsManager
  │  메모리에서 인증·권한 정보 관리   DB에서 인증·권한 정보 관리
  │
  └ has a ─▶ <<interface>> PasswordEncoder
```

| 구현체 | 사용자 정보 저장 위치 |
|---|---|
| InMemoryUserDetailsManager | 메모리 |
| JdbcUserDetailsManager | 데이터베이스 |

- 실습에서 PasswordEncoder·UserDetailsService 빈을 직접 등록 → [Authentication 실습](05_Authentication_실습.md)

---

## 참고

- 인증 후 Credentials 삭제는 `ProviderManager`가 기본으로 해 줌(`eraseCredentialsAfterAuthentication` 기본값 `true`)
- 현재 로그인한 사용자를 코드에서 꺼내는 기본 형태

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
```
