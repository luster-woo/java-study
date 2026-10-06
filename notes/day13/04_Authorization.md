# Authorization

> 교재: 15기_자바전공_11_security_1_(0521) (전공) — 4. Authorization

> [← Authentication](03_Authentication.md) · [day13 목록](../README.md) · [Authentication 실습 →](05_Authentication_실습.md)

---

## Servlet Authorization Architecture

- 전체 흐름([개요](01_Spring_Security_개요.md#인가authorization-흐름)) 중 **ⓐ AuthorizationFilter가 SecurityContext에서 인증 정보 획득 → ⓑ AuthorizationManager가 권한 판단** 구간

### 인가 관련 처리

- **Authorization**: Authentication 객체에 들어 있는 **GrantedAuthority 목록**으로 수행하는 과정
  - **GrantedAuthority**: 사용자에게 부여된 권한 → 특정 작업을 할 수 있는지에 대한 정보
  - 권한 이름은 보통 **`ROLE_` 접두사**로 관리 (`ROLE_ADMIN`, `ROLE_USER`, …)
- **AuthorizationManager**: GrantedAuthority를 보고 두 가지를 결정
  - Secured resource를 **호출할 수 있는지** 점검
  - Secured resource에서 **값을 반환받을 수 있는지** 결정
  - 선언적으로 처리할 때는 `@PreAuthorize`, `@PostAuthorize` 사용

| 어노테이션 | 판단 시점 | 판단 내용 |
|---|---|---|
| `@PreAuthorize` | 메서드 호출 **전** | 호출해도 되나 |
| `@PostAuthorize` | 메서드 호출 **후** | 결과값을 돌려받아도 되나 |

```
Authentication ── GrantedAuthority 목록 (ROLE_USER, ...)
        │
        ▼
AuthorizationManager ── 권한?
        ├ YES → continue → Secured Resource
        └ NO  → AccessDeniedException
```

### 계층적 role 관리

- 특정 role이 다른 role을 **자동으로 포함**해야 할 때 **RoleHierarchy** 빈 등록
- 계층이 없으면 선언할 필요 없음
- 같은 계층을 두 방식으로 표현 가능

```java
// 1) 빌더 방식
@Bean
RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withDefaultRolePrefix()   // ROLE_ 접두사 자동
            .role("ADMIN").implies("STAFF")
            .role("STAFF").implies("USER")
            .build();
}
```

```java
// 2) 문자열 방식 (text block)
@Bean
RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy("""
            ROLE_ADMIN > ROLE_STAFF
            ROLE_STAFF > ROLE_USER
            """);
}
```

| role | 실제로 갖는 ROLE 구성 |
|---|---|
| ADMIN | ADMIN, STAFF, USER |
| STAFF | STAFF, USER |
| USER | USER |

- `A > B` = A는 B의 권한까지 포함
- 빌더 방식은 role 이름만 쓰면 접두사가 붙고, 문자열 방식은 `ROLE_`까지 직접 씀

---

## 참고

- `RoleHierarchyImpl.withDefaultRolePrefix()`·`fromHierarchy()`는 Spring Security 6.3 이상(Spring Boot 3.3 이상) API. 그 이전 버전은 `new RoleHierarchyImpl()` 후 `setHierarchy("ROLE_ADMIN > ROLE_STAFF\nROLE_STAFF > ROLE_USER")` 형태
- 문자열 방식의 `"""` text block은 Java 15 이상(Java 17 OK)
- `@PreAuthorize`·`@PostAuthorize`를 쓰려면 설정 클래스에 `@EnableMethodSecurity`가 필요(Spring Security 6)
