# jwt-practice

day13 JWT 실습 — [JWT 개요](../../notes/day13/07_JWT_개요.md) · [JWT 생성](../../notes/day13/08_JWT_생성.md) · [Spring Security와의 연동](../../notes/day13/09_Spring_Security와의_연동.md) · [refresh token](../../notes/day13/10_refresh_token.md)

`security-practice`(Authentication/Authorization 실습)를 복사해서 이어 만든 프로젝트. 웹 화면(form login)은 그대로 두고 `/api/**`만 JWT로 인증한다.

## 여는 법

- IntelliJ `File → Open → security/jwt-practice` → New Window, JDK 17 이상, Lombok 플러그인
- `JwtPracticeApplication` 실행 → http://localhost:8080/jwt.html
- DB는 H2 메모리(재시작하면 초기화), 시작할 때 계정 2개 자동 등록

| 계정 | 비밀번호 | role |
|---|---|---|
| admin@ssafy.com | 1234 | ADMIN |
| user@ssafy.com | 1234 | USER |

## TODO 위치

| TODO | 내용 | 파일 |
|---|---|---|
| 02 | JJWT 의존성, 설정값 | `pom.xml`, `application.properties` |
| 03 | 토큰 생성·claims 조회 | `jwt/JWTUtil.java` |
| 04 | 토큰 검증 필터 | `jwt/JWTVerificationFilter.java` |
| 05 | 로그인 필터, 예외 처리 필터 | `jwt/JWTAuthenticationFilter.java`, `jwt/SecurityExceptionHandlingFilter.java` |
| 06 | API용 SecurityFilterChain, CORS | `config/APISecurityConfig.java`, `config/CorsConfig.java`, `config/CustomSecurityConfig.java`(@Order(2)) |
| 07 | Authorization 헤더로 토큰 전달 | `static/jwt.html` |
| 08 | 로그인 시 refresh token 발급·저장 | `jwt/JWTAuthenticationFilter.java` |
| 09 | `/api/v1/auth/refresh` | `controller/AuthController.java` |
| 10 | refresh 요청 (클라이언트) | `static/jwt.html` |
| 11 | logout (서버·클라이언트) | `controller/AuthController.java`, `static/jwt.html` |

## API

| 요청 | 인증 | 설명 |
|---|---|---|
| `POST /api/v1/auth/login` (email, password — form) | X | 필터가 처리 → `{accessToken, refreshToken}` |
| `POST /api/v1/auth/refresh` (헤더 `Refresh-Token`) | X | 새 토큰 2개 발급 |
| `POST /api/v1/auth/logout` (헤더 `Refresh-Token`) | X | DB의 refresh token 삭제 |
| `GET /api/v1/etc/hello` | X | permitAll |
| `GET /api/v1/members`, `/api/v1/members/me` | `Authorization: Bearer {access}` | authenticated |

## 확인 순서 (jwt.html)

1. 토큰 없이 members → 401
2. login → 토큰 2개 표시, 토큰 포함 members → 200
3. 변조 토큰으로 호출 → 401 `TOKEN_ERROR`
4. 1분 기다리면 access 만료 → members 401 `TOKEN_ERROR` → refresh → 다시 200
5. logout → refresh 401 `INVALID_REFRESH_TOKEN` (access token은 만료 전까지 여전히 동작 = stateless)

토큰을 https://jwt.io 에 붙여 넣으면 payload 확인, secret(`application.properties`)을 넣으면 서명 검증도 된다.
