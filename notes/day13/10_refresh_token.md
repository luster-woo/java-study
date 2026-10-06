# refresh token

> 교재: 15기_자바전공_04_Vue_with_Jwt_0609 (전공) — 1. refresh token

> [← Spring Security와의 연동](09_Spring_Security와의_연동.md) · [day13 목록](../README.md) · [SPA의 인증 처리 →](11_SPA의_인증_처리.md)

---

## Refresh Token 활용

### 복습 — JWT 동작 절차와 필터 구성

- 로그인: `/api/v1/auth/login` → JWTVerificationFilter 통과 → **JWTAuthenticationFilter**가 인증 → 토큰 생성 → 클라이언트 저장
- API 요청: header(토큰) → **JWTVerificationFilter**가 검증 → SecurityContext → 권한 확인
- 인가 규칙: `/api/v1/etc/**`, `/api/v1/auth/**`는 `permitAll()`, `/api/v1/members` 등은 `authenticated()`
- API용 FilterChain: `LogoutFilter → SecurityExceptionHandlingFilter → JWTVerificationFilter → JWTAuthenticationFilter → RequestCacheAwareFilter → ...`

### What IF — Access Token이 탈취된다면?

- 토큰만 있으면 공격자도 **정상 사용자처럼 행세** 가능
  - 서버는 토큰의 소유자(**Bearer**)를 신뢰하고 **유효성만** 검사 → 누가 제시했는지는 따지지 않음
- 피해는 **토큰 만료 시까지** 지속
  - token은 만료시간까지 유효
  - JWT는 **stateless** → 서버 쪽 **logout 개념이 없음**
- 대응: Access Token 유효기간을 **5분~1시간**으로 매우 짧게 → 악용 시간 최소화
  - 하지만 너무 짧으면 사용자가 자주 로그인해야 함 → Access Token **재발급용 Refresh Token** 도입

| 항목 | Access Token | Refresh Token |
|---|---|---|
| 유효기간 | 짧음(수 분 ~ 1시간) | 김(수 일 ~ 수 주) |
| 목적 | API 호출 | Access Token 재발급 |
| 노출 정도 | API 호출 때마다 노출 | 토큰 재발급 때만 노출 |

### Refresh Token의 흐름

```
client                                              server
  │── POST /api/v1/auth/login ───────────────────────▶│ 토큰 생성(access 1h, refresh 1d)
  │◀──────────────── access, refresh token 전달 ──────│ refresh token 저장(DB)
  │ access, refresh 저장
  │
  │   ... 1시간 경과 → access token 만료 ...
  │── /api/v1/members/update (만료 access token) ────▶│
  │◀────────────────────────────── 401 오류 반환 ─────│
  │── POST /api/v1/auth/refresh (refresh token) ─────▶│ refresh token 검증
  │◀──────────────── new access, refresh token ───────│ new 토큰 발행, refresh 저장
  │ new access, refresh 저장
  │── /api/v1/members/update (new access token) ─────▶│ 정상 처리
```

---

## 서버 구현

### 로그인 성공 시 refresh token 추가 발급 (TODO: 08)

- `successfulAuthentication()`에서 access token과 함께 refresh token도 생성
- refresh token은 **회원 정보(DB)에도 저장** → 나중에 재발급 요청 때 대조

```java
@Override
protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain chain, Authentication authentication) {
    Map<String, String> result = new HashMap<>();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Member member = userDetails.getMember();

    String accessToken = jwtUtil.createAccessToken(member);
    result.put("accessToken", accessToken);

    String refreshToken = jwtUtil.createRefreshToken(member);   // refresh token 생성
    member.setRefresh(refreshToken);                            // member 정보 수정
    memberService.update(member);
    result.put("refreshToken", refreshToken);

    handleResult(response, result, HttpStatus.OK);
}
```

```json
// 결과(응답 body)
{"accessToken":"eyJ...","refreshToken":"eyJ..."}
```

### /api/v1/auth/refresh 처리 (TODO: 09)

- refresh token은 `Authorization`이 아닌 **`Refresh-Token` 커스텀 헤더**로 받음
- 처리 순서

| 단계 | 내용 |
|---|---|
| 1. refresh token 확인 | 없으면 400 |
| 2. 검증 및 정보 취득 | `jwtUtil.getClaims()` → email claim(없으면 `JwtException`) |
| 3. 정보 validation | DB의 회원 refresh 값과 **같은지** 비교 → 다르면 401 |
| 4. 신규 토큰 발행 | access·refresh 둘 다 새로 발급 |
| 5. DB 업데이트 | 새 refresh token 저장 → 응답 |

```java
@PostMapping("/refresh")
public ResponseEntity<?> refreshAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
    if (refreshToken == null || refreshToken.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Refresh token is required"));
    }
    Map<String, Object> claims = jwtUtil.getClaims(refreshToken);   // 검증 + 정보 취득
    String email = (String) claims.get("email");
    if (email == null) {
        throw new JwtException("Invalid refresh token: email claim missing");
    }

    Member selected = memberService.selectDetail(email);
    if (selected == null || selected.getRefresh() == null || !selected.getRefresh().equals(refreshToken)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Refresh token is required"));
    }

    String newAccessToken = jwtUtil.createAccessToken(selected);     // 신규 토큰 발행
    String newRefreshToken = jwtUtil.createRefreshToken(selected);
    selected.setRefresh(newRefreshToken);                            // DB 업데이트
    memberService.update(selected);
    return ResponseEntity.ok(Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken));
}
```

### 토큰 갱신 요청 — 클라이언트 (TODO: 10)

- `/api/v1/auth/refresh` 요청에는 **Authorization 헤더를 쓰지 않음** → JWTVerificationFilter의 검증을 타지 않게
  - 만료된 access token을 Authorization에 실으면 검증 단계에서 바로 `TOKEN_ERROR`

```
/api/v1/auth/refresh
  → JWTVerificationFilter : Authorization Header? NO → 통과 (YES면 검증)
  → JWTAuthenticationFilter: login URL 아님 → 통과
  → permitAll → Controller에서 Refresh-Token 확인
```

```js
document.querySelector("#refresh").addEventListener("click", async () => {
  try {
    const response = await fetch("/api/v1/auth/refresh", {
      method: "POST",
      headers: { "Refresh-Token": refreshToken },
    });
    const json = await response.json();
    updateTokens(json);   // 받은 새 토큰들로 교체
    document.querySelector("#result").innerText = JSON.stringify(json, null, 2);
  } catch (error) {
    document.querySelector("#result").innerText = "Refresh Error: " + error;
  }
});
```

---

## 로그아웃 처리

### refresh token을 이용한 logout — 서버 (TODO: 11)

- DB에 저장된 **refresh token 정보 삭제**(`null`) → 이후 재발급 불가
- 즉시 클라이언트를 차단할 수 있을까? → **아니오**. 이미 발급된 access token은 만료 전까지 여전히 유효함(stateless) → access token을 짧게 두는 이유

```java
@PostMapping("/logout")
public ResponseEntity<?> logout(@RequestHeader("Refresh-Token") String refreshToken) {
    if (refreshToken == null || refreshToken.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Refresh token is required"));
    }
    Map<String, Object> claims = jwtUtil.getClaims(refreshToken);
    String email = (String) claims.get("email");   // refresh token 생성 시 넣은 claim 키
    if (email == null) {
        throw new JwtException("Invalid refresh token: email claim missing");
    }
    Member member = memberService.selectDetail(email);
    if (member != null) {
        member.setRefresh(null);        // 저장된 refresh token 삭제
        memberService.update(member);
    }
    return ResponseEntity.ok(Map.of("accessToken", "", "refreshToken", ""));
}
```

### refresh token을 이용한 logout — 클라이언트 (TODO: 11)

- 서버에 logout 요청 후 **클라이언트에 저장된 토큰도 삭제**

```js
document.querySelector("#logout").addEventListener("click", async () => {
  try {
    const response = await fetch("/api/v1/auth/logout", {
      method: "POST",
      headers: { "Refresh-Token": refreshToken },
    });
    const json = await response.json();
    updateTokens({ accessToken: null, refreshToken: null });
    document.querySelector("#result").innerText = "Logout Success";
  } catch (error) {
    document.querySelector("#result").innerText = "Logout Error: " + error;
  }
});
```

---

## 참고

> 참고: 교재 TODO 08 코드는 `userDetails`를 선언하기 전에 `userDetails.getMember()`를 쓰고 `Member member`를 두 번 선언함 → 컴파일 오류. 노트처럼 `userDetails` → `member` 순으로 한 번만 선언.

- `@RequestHeader("Refresh-Token")`은 기본이 `required = true` → 헤더가 아예 없으면 메서드에 들어오기 전에 **400**(MissingRequestHeaderException). 메서드 안의 `null` 검사까지 가게 하려면 `required = false`
- 컨트롤러에서 던진 `JwtException`(만료·변조된 refresh token)은 필터가 아니라 DispatcherServlet 안에서 발생 → SecurityExceptionHandlingFilter에 도달할 때는 다른 예외로 감싸질 수 있으므로, `@RestControllerAdvice`에서 따로 401로 바꿔 주는 처리가 필요할 수 있음
- refresh 때 refresh token도 새로 발급하고 DB 값을 교체하는 방식을 **Refresh Token Rotation**이라 부름 — 예전 refresh token은 DB 값과 달라 재사용 불가
