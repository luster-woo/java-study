# security-practice

day13 Spring Security — [Authentication 실습](../../notes/day13/05_Authentication_실습.md) · [Authorization 실습](../../notes/day13/06_Authorization_실습.md) 코드.

## 여는 법

- IntelliJ `File → Open → security/security-practice` (pom.xml 인식) → New Window
  - `java-study` 전체로 열면 JSP 경로(`src/main/webapp`)를 못 찾아 404가 날 수 있음
- JDK 17 이상, `SecurityPracticeApplication` 실행 → http://localhost:8080

## TODO 위치

| TODO | 실습 | 파일 |
|---|---|---|
| 02 의존성 | Authentication | `pom.xml` |
| 03 SecurityConfig + RoleHierarchy | Authentication | `config/CustomSecurityConfig.java` |
| 04 PasswordEncoder | Authentication | `config/CustomSecurityConfig.java` |
| 05 UserDetailsService | Authentication | `config/CustomSecurityConfig.java` |
| 06 SecurityFilterChain | Authorization | `config/CustomSecurityConfig.java` |
| 테스트 | Authentication | `test/.../UserDetailsServiceTest.java` |
| 테스트 | Authorization | `test/.../AuthorizationTest.java` |

## 계정

| 계정 | 비밀번호 | role |
|---|---|---|
| admin@ssafy.com | 1234 | ADMIN (⊃ USER ⊃ GUEST) |
| user@ssafy.com | 1234 | USER (⊃ GUEST) |

## 확인 순서

1. 비로그인으로 `/secured/user` → `/login`으로 redirect
2. `user@ssafy.com` 로그인 → `/secured/user` O, `/secured/admin` → 403 화면
3. 로그아웃 → `admin@ssafy.com` 로그인 → 둘 다 O (RoleHierarchy)
