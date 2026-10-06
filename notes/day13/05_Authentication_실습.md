# Authentication 실습

> 교재: 15기_자바전공_11_security_1_(0521) (전공) — 5. Authentication 실습

> [← Authorization](04_Authorization.md) · [day13 목록](../README.md) · [Authorization 실습 →](06_Authorization_실습.md)

---

## 프로젝트 구성 및 기본 동작

### 실습 프로젝트가 만들 동작

| 경로 | 접근 조건 |
|---|---|
| `index`, `/member/**`, `/api/**` | 누구나 접근 가능 |
| `member/login-form` | 누구나 접근 가능 (로그인 화면) |
| `/auth/**` | 로그인한 사용자만 |
| `/secured/user/**` | user role 사용자만 |
| `/secured/admin/**` | admin role 사용자만 |
| `error/403` | 권한 부족(forbidden) 시 이동하는 화면 |

- ROLE 구성: **ADMIN ⊃ USER ⊃ GUEST** (아래 RoleHierarchy 설정)

```
사용자 요청
   │
   ▼
 인증? ── no ──▶ 인증 요청(member/login-form) ──▶ user role 획득 ──┐
   │ yes                                                          │
   ▼  ◀───────────────────────────────────────────────────────────┘
 인가? ── 권한 충족 ──▶ 요청한 화면
   └──── 권한 부족 ──▶ error/403
```

### Spring Security 관련 의존성 추가 (TODO: 02)

```xml
<!-- spring-security 기본 lib -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- spring-security test를 위한 편의 기능 제공 -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- JSP에서 spring-security 태그를 사용하기 위한 라이브러리 -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-taglibs</artifactId>
</dependency>
```

| 의존성 | 용도 |
|---|---|
| spring-boot-starter-security | Spring Security 기본 |
| spring-security-test | 테스트 편의 기능 (`@WithMockUser` 등) |
| spring-security-taglibs | JSP에서 security 태그 사용 |

- test 전용 라이브러리가 따로 있다 = **slice test용 도구**가 있다는 뜻

### 기본 동작

- 의존성만 추가해도 **모든 리소스에 인증 요구**
  - `index` 요청 → login 페이지로 redirect
- 기본 계정
  - username: `user`
  - password: 기동 로그의 `Using generated security password` 값 (실행할 때마다 바뀜)
- logout: `localhost:8080/logout`

```
Using generated security password: 3f1c...(실행마다 다른 값)
```

### 동작 근거 (기본 설정 내용)

- Boot가 기본으로 깔아 주는 설정을 단순화하면 아래와 같음 → **앞으로의 과정은 이 설정을 커스터마이징**하는 것

```java
@EnableWebSecurity
@Configuration
public class DefaultSecurityConfig {
    // Spring Security Filter Chain을 @Bean으로 등록

    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class) // 사용자 정의 UserDetailsService가 없을 때만 동작
    InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        // 기본 사용자 정보
        String generatedPassword = ...;
        return new InMemoryUserDetailsManager(
                User.withUsername("user").password(generatedPassword).roles("USER").build());
    }
    // ...
}
```

- 내가 `UserDetailsService` 빈을 만들면 기본 `user` 계정은 사라짐

---

## SecurityConfig

### 사용자 정의 설정 작성 (TODO: 03)

- `@EnableWebSecurity`: SecurityFilterChain 빈 등 Security 관련 빈을 노출해 Spring Security 구성
- 보통 커스터마이징하는 빈

| 빈 | 담당 |
|---|---|
| **UserDetailsService** | 인증에 쓸 사용자 설정 |
| **SecurityFilterChain** | 인증 절차 설정 — 적용 대상, 권한, 로그인, 로그아웃 형태 |
| **RoleHierarchy** | 계층적 권한 관리 |

```java
@EnableWebSecurity
@Configuration
public class CustomSecurityConfig {

    @Bean
    RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()   // role의 기본 prefix 설정: ROLE_
                .role("ADMIN").implies("USER")
                .role("USER").implies("GUEST")
                .build();
    }
}
```

---

## Authentication 절차

- 전체 흐름([개요](01_Spring_Security_개요.md#인증authentication-흐름)) 중 이번 실습이 손대는 곳: **AuthenticationProvider(DaoAuthenticationProvider)가 쓰는 UserDetailsService와 PasswordEncoder**

## DaoAuthenticationProvider

### 구조

```
DaoAuthenticationProvider
  ├ has a ─▶ <<interface>> UserDetailsService ◀── UserDetailsManager ◀── InMemoryUserDetailsManager / JdbcUserDetailsManager
  └ has a ─▶ <<interface>> PasswordEncoder
```

- Spring Security에서 **Credential(비밀번호)은 암호화 필수** → PasswordEncoder 빈이 필요

### PasswordEncoder (TODO: 04)

- Spring Security가 제공하는 **비밀번호 암호화 인터페이스**
- 암호화 방식별 구현체

| Encoder | 강도 | 속도 | 권장 |
|---|---|---|---|
| Argon2PasswordEncoder | 매우 강력 | 느림 | 최신 프로젝트 |
| **BCryptPasswordEncoder** | 강력 | 보통 | **가장 많이 씀** |
| Pbkdf2PasswordEncoder | 보통 | 빠름 | 레거시 호환 |
| NoOpPasswordEncoder | 약함 | 즉시 | 테스트 전용 |

- 보통 제공되는 Encoder를 **빈으로 등록**해서 사용

```java
@Bean
PasswordEncoder passEncoder() {
    // 암호문 앞에 암호 형식을 붙임 - 기본 encoder는 bcrypt
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    // return new Argon2PasswordEncoder();
}
```

- `createDelegatingPasswordEncoder()` → 저장되는 값이 `{bcrypt}$2a$10$...`처럼 **`{형식}` 접두사 + 암호문**
  - 접두사를 보고 비교할 encoder를 고름 → 나중에 방식을 바꿔도 옛 암호문 검증 가능

### 사용자 인증을 위한 UserDetailsService (TODO: 05)

- **메모리 기반**으로 인증 정보 관리 → Principal, Credential, Authority 설정
- `username`은 로그인 id → **유일**해야 함

```java
@Bean
@ConditionalOnMissingBean(UserDetailsService.class)
UserDetailsService userDetailsService(PasswordEncoder pe) {
    UserDetails admin = User.builder()
            .username("admin@ssafy.com")      // Principal
            .password(pe.encode("1234"))      // Credential (암호화해서 저장)
            .roles("ADMIN")                   // Authority → ROLE_ADMIN
            .build();

    UserDetails user = User.builder()
            .username("user@ssafy.com")
            .password(pe.encode("1234"))
            .roles("USER")
            .build();

    return new InMemoryUserDetailsManager(user, admin);
}
```

| 계정 | 비밀번호 | role |
|---|---|---|
| `admin@ssafy.com` | 1234 | ADMIN (계층상 USER, GUEST 포함) |
| `user@ssafy.com` | 1234 | USER (계층상 GUEST 포함) |

- `roles("ADMIN")` → 실제 권한 이름은 **`ROLE_ADMIN`** (접두사 자동)

### UserDetailsService 테스트

- 이제 login 되는지 확인

```java
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class UserDetailsServiceTest {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    PasswordEncoder passEncoder;

    @Test
    void 사용자생성_테스트() {
        UserDetails user = userDetailsService.loadUserByUsername("admin@ssafy.com");
        Assertions.assertNotNull(user);
        // 평문 "1234"와 저장된 암호문 비교
        Assertions.assertTrue(passEncoder.matches("1234", user.getPassword()));
        // roles("ADMIN") → ROLE_ADMIN
        Assertions.assertTrue(user.getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}
```

| 검증 | 확인하는 것 |
|---|---|
| `assertNotNull(user)` | username으로 사용자 조회됨 |
| `passEncoder.matches("1234", 암호문)` | 저장된 비밀번호가 암호화돼 있고 평문과 맞음 |
| `anyMatch(... "ROLE_ADMIN")` | `roles("ADMIN")`이 `ROLE_ADMIN` 권한이 됨 |

---

## 참고

- 교재는 기본 동작(로그인 페이지로 redirect)을 "basic 인증 방식"이라 부르지만, 브라우저가 로그인 **페이지**로 이동하는 것은 **form 로그인**. Boot 기본 chain은 form 로그인과 HTTP Basic을 둘 다 켜 둠(브라우저 요청은 form, 그 외 클라이언트는 Basic 응답)
- `DefaultSecurityConfig`는 설명용 이름. 실제로는 Boot 자동 설정 `SpringBootWebSecurityConfiguration`(기본 SecurityFilterChain)과 `UserDetailsServiceAutoConfiguration`(기본 `user` 계정)이 담당
- 교재 슬라이드는 `RoleHierachy`, `roleHierachy()`로 철자가 틀림 → 타입은 `RoleHierarchy`(메서드 이름은 동작에 영향 없음)
- `@ConditionalOnMissingBean`은 Spring Boot 문서상 자동 설정 클래스용. 직접 만든 설정 클래스에서는 빈 등록 순서에 따라 기대대로 동작하지 않을 수 있어 보통 생략함
