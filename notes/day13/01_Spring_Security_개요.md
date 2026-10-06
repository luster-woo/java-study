# Spring Security 개요

> 교재: 15기_자바전공_11_security_1_(0521) (전공) — 1. Spring Security 개요

> [day13 목록](../README.md) · [SecurityFilterChain →](02_SecurityFilterChain.md)

---

## Security?

### Security의 주요 용어와 개념

| 용어 | 뜻 | 예 |
|---|---|---|
| **Secured Resource**(보호된 자원) | 보안이 걸리는 대상 리소스 | URL, API end point, 파일 |
| **Principal** | 인증된 사용자 또는 그 사용자를 식별하는 정보 → 행위의 주체 | 사용자 계정, 디바이스, 시스템 |
| **Authentication**(인증) | Principal을 믿을 수 있는지 확인함 | 보통 ID/PASS 검사 |
| **Authorization**(인가·권한) | 인증을 마친 Principal이 Secured Resource를 쓸 권한이 있는지 확인함 | ADMIN, MEMBER, GUEST 같은 **role** 기준으로 접근 가능한 자원을 거름 |

- 인증 = "누구냐", 인가 = "그 사람이 이걸 써도 되냐"
- 인가는 반드시 인증 **뒤**에 옴

### 용어로 정리한 Security 그림

```
                         WAS
 GUEST  ──────────────────────────────────────▶ non Secured Resource   (인증 없이 접근)

 MEMBER ──▶ [authentication] ──▶ [authorization] ──▶ Secured Resource (MEMBER용)

 ADMIN  ──▶ [authentication] ──▶ [authorization] ──▶ Secured Resource (ADMIN용)
```

- principal이 WAS의 자원에 접근할 때 보호되지 않은 자원은 그냥 통과
- 보호된 자원은 authentication(신원 확인) → authorization(role 확인)을 거쳐야 접근 가능

---

## Spring의 Security

### Spring Security의 특징

- Principal이 Secured Resource에 접근할 때 **Authentication으로 신원 확인**, **Authorization으로 접근 권한 검증**을 해 주는 보안 프레임워크
- 스프링 기반 애플리케이션 보호에 사실상 필수
  - 세션을 써서 인증을 직접 구현하지 말고 Spring Security에 맡김
- **컨테이너에 무관**하게 적용
  - WAR, EAR로 배포하거나 standalone으로 실행 가능
  - Servlet 애플리케이션에서는 **Filter 기반**으로 동작 → Spring 애플리케이션이 아니어도 적용 가능
- **관점의 분리**
  - `@Transactional`이 트랜잭션을 비즈니스 로직에서 떼어내듯, 보안도 로직과 분리 (`@Secured`)
- **유연성**
  - 인증 방법 선택 가능: basic, form, cookies 등
  - 사용자 정보 저장 방식을 설정으로 고름: properties file, 메모리, RDBMS, LDAP …

| 관심사 | 비즈니스 로직에서 떼어내는 수단 |
|---|---|
| 트랜잭션 | `@Transactional` |
| 보안(권한 확인) | `@Secured` |

---

## Spring Security의 전체적인 흐름

### Web Application에서 Authentication과 Authorization 절차

- 교재 2~6단원 시작마다 반복해서 나오는 그림 → 이 흐름을 머릿속에 그릴 수 있어야 함

```
request
  │
  ▼
┌ Servlet Filter Chain ──────────────────────────────────────────────────────┐
│  Filter 0 ──▶ DelegatingFilterProxy ──▶ Filter 2 ──▶ ...                   │
└──────────────────────│─────────────────────────────────────────────────────┘
                       │ 위임
                       ▼
┌ Security Filter Chain ─────────────────────────────────────────────────────┐
│  Security Filter 0 ──▶ AuthenticationFilter ──▶ ... ──▶ AuthorizationFilter ──▶ Security Filter n ──▶ Secured Resource
└──────────────────────────────│─────────────────────────────│───────────────┘
                               │ (인증)                      │ (인가)
                               ▼                             ▼
                     AuthenticationManager           AuthorizationManager
                               │                             ▲
                               ▼                             │ 인증 정보 획득
                          SecurityContext ───────────────────┘
                     (인증·인가 정보 관리)
```

### 인증(Authentication) 흐름

| 단계 | 하는 일 |
|---|---|
| 1 | **AuthenticationFilter**가 요청의 username(**P**rincipal)·password(**C**redential)로 `Authentication(P, C)`를 만들어 **AuthenticationManager**에 전달 |
| 2 | AuthenticationManager(구현체 **ProviderManager**)가 **AuthenticationProvider**에게 인증을 맡김 |
| 3 | AuthenticationProvider가 **UserDetailsService** 구현체로 사용자 정보를 찾아 **UserDetails** 생성 (P 활용) |
| 4 | UserDetails의 비밀번호와 C를 비교 → 인증? |
| 4-1 | 실패 → `AuthenticationException` |
| 4-2 | 성공 → `Authentication(P: UserDetails, Authorities)` 반환 |
| 5 | 인증된 Authentication을 **SecurityContext**에 저장 (인증·인가 정보 관리) |

### 인가(Authorization) 흐름

| 단계 | 하는 일 |
|---|---|
| ⓐ | **AuthorizationFilter**가 SecurityContext에서 인증 정보 획득 |
| ⓑ | **AuthorizationManager**가 권한 판단 → 권한? |
| YES | continue → Secured Resource 도달 |
| NO | `AccessDeniedException` |

### 구성 요소 한눈에

| 구성 요소 | 역할 | 자세히 |
|---|---|---|
| DelegatingFilterProxy | 서블릿 Filter → 스프링 빈(Security Filter Chain)으로 위임 | [SecurityFilterChain](02_SecurityFilterChain.md) |
| Security Filter Chain | 보안 필터들의 묶음 | [SecurityFilterChain](02_SecurityFilterChain.md) |
| AuthenticationFilter / AuthenticationManager / AuthenticationProvider / UserDetailsService | 인증 처리 | [Authentication](03_Authentication.md) |
| SecurityContext | 인증 결과 보관 | [Authentication](03_Authentication.md) |
| AuthorizationFilter / AuthorizationManager | 인가 처리 | [Authorization](04_Authorization.md) |

---

## 참고

- 교재에 적힌 프로젝트 주소 `http://projects.spring.io/spring-security/`는 현재 `https://spring.io/projects/spring-security`로 연결됨
- 교재 그림의 ①~⑤·ⓐⓑ 번호는 위 표에서 흐름 순서대로 다시 매김(그림 속 번호 위치와 1:1로 같지 않을 수 있음)
- 이 노트의 코드·설정은 Spring Boot 3.x + Spring Security 6.x 기준
