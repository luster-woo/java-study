# JWT 생성

> 교재: 15기_자바전공_03_JWT_0608 (전공) — 2. JWT 생성

> [← JWT 개요](07_JWT_개요.md) · [day13 목록](../README.md) · [Spring Security와의 연동 →](09_Spring_Security와의_연동.md)

---

## 기본 설정

### JJWT 의존성 (TODO: 02)

- 자바용 JWT 라이브러리 **JJWT** — `api`는 컴파일용, `impl`·`jackson`(JSON 직렬화)은 `runtime` 스코프

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.13.0</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.13.0</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.13.0</version>
    <scope>runtime</scope>
</dependency>
```

### application.properties

- 토큰 만료 시간(분 단위)과 서명 키 문자열을 설정으로 뺌
- 실습에서는 만료를 바로 확인하려고 access 1분, refresh 2분으로 아주 짧게 둠

```properties
# access token 만료 시간: 일반적으로 1h~24h, 높은 보안이 필요하면 5m~1h
ssafy.jwt.access-expmin=1
# refresh token 만료 시간: 일반적으로 1일~몇 달
ssafy.jwt.refresh-expmin=2
# 임의의 문자열로 키 생성
ssafy.jwt.secret-string=your-very-long-and-secure-random-secret-key-here-12345678
```

- 만료 시간은 서비스 상황에 따라 정함

---

## JWT 동작 절차

### JWT를 활용한 Security 동작 절차

- 로그인 요청과 일반 API 요청이 서로 다른 흐름을 탐

```
[로그인]  1. POST /api/v1/auth/login (header에 토큰 X)
            ├─ JWTVerificationFilter   2. 검사 대상 아님 → 그냥 통과
            └─ JWTAuthenticationFilter 3. 인증 처리(AuthenticationManager → UserDetailsService)
                                       4. 인증 성공 → JWTUtil로 토큰 생성
                                       5. 클라이언트가 토큰 저장

[API]     a. /api/v1/** 요청 + header(토큰) 전달
            ├─ JWTVerificationFilter   b. JWTUtil로 토큰 검증 → SecurityContext에 인증 정보 저장
            └─ 인가 처리               c. 권한 확인 → Secured Resource
          (/api/v1/etc/**, POST /api/v1/members(회원가입) 등은 토큰 없이 통과)
```

| 구성 요소 | 역할 |
|---|---|
| **JWTUtil** | 토큰 생성·검증(claim 조회) |
| **JWTAuthenticationFilter** | 로그인 처리 → 성공 시 토큰 발급 |
| **JWTVerificationFilter** | 요청마다 토큰 검증 → SecurityContext에 저장 |

---

## JWTUtil

### SecretKey 생성

- 설정값을 생성자에서 `@Value`로 주입받음
- `Keys.hmacShaKeyFor(byte[])` → 문자열로부터 HMAC-SHA용 **SecretKey** 생성

```java
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
@Slf4j
public class JWTUtil {
    private final long accessExpMin;
    private final long refreshExpMin;
    private final SecretKey key;

    public JWTUtil(@Value("${ssafy.jwt.access-expmin}") long accessExpMin,
                   @Value("${ssafy.jwt.refresh-expmin}") long refreshExpMin,
                   @Value("${ssafy.jwt.secret-string}") String secretKeyString) {
        this.accessExpMin = accessExpMin;
        this.refreshExpMin = refreshExpMin;
        this.key = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        log.debug("jwt secret key: {}", Arrays.toString(key.getEncoded()));
    }
    // ...
}
```

### JWT 생성 (TODO: 03)

- access token과 refresh token은 **만료 시간과 담는 claim만 다름** → 공통 `create()` 사용

| 토큰 | subject | 만료 | claims |
|---|---|---|---|
| access token | `"accessToken"` | `accessExpMin` | email, name, role |
| refresh token | `"refreshToken"` | `refreshExpMin` | email |

- `Jwts.builder()` 체인

| 메서드 | 역할 |
|---|---|
| `subject(..)` | 제목(`sub`) 설정 |
| `claims(Map)` | claim(key-value 쌍) 추가 |
| `expiration(Date)` | 만료일(`exp`) 설정 |
| `signWith(key)` | 서명 키 지정(키에 맞는 알고리즘 선택) |
| `compact()` | `header.payload.signature` 문자열로 직렬화 |

```java
public String createAccessToken(Member member) {
    if (member.getRole() == null) {   // null 값은 넣을 수 없음
        member.setRole("USER");
    }
    return create("accessToken", accessExpMin,
            Map.of("email", member.getEmail(), "name", member.getName(), "role", member.getRole()));
}

public String createRefreshToken(Member member) {
    return create("refreshToken", refreshExpMin, Map.of("email", member.getEmail()));
}

public String create(String subject, long expireMin, Map<String, Object> claims) {
    Date expireDate = new Date(System.currentTimeMillis() + 1000 * 60 * expireMin);
    return Jwts.builder()
            .subject(subject)        // 제목
            .claims(claims)          // claim 추가
            .expiration(expireDate)  // 만료일
            .signWith(key)           // 서명
            .compact();              // 직렬화
}
```

### Claims 조회 (TODO: 03)

- `Jwts.parser().verifyWith(key).build()` → 같은 키로 **다시 서명해 비교**하는 파서
- `parseSignedClaims(jwt)` → **JWS**(JSON Web Signature: JSON 데이터에 대한 디지털 서명) 파싱 → `getPayload()`로 Claims 획득
- 문제가 있으면 **예외**로 알려 줌

```java
public Claims getClaims(String jwt) {
    JwtParser parser = Jwts.parser().verifyWith(key).build();
    Jws<Claims> jws = parser.parseSignedClaims(jwt);
    return jws.getPayload();
}
```

- 발생 가능한 예외(모두 `JwtException`의 하위)

| 예외 | 상황 |
|---|---|
| **ExpiredJwtException** | 형식은 맞지만 유효기간이 지난 토큰 |
| **MalformedJwtException** | 형식이 잘못된 토큰 |
| **SignatureException** | 훼손(변조)된 토큰 — 서명 불일치 |

```
JwtException
 ├─ ExpiredJwtException
 ├─ MalformedJwtException
 └─ SignatureException
```

---

## token 검증

### jwt.io (TODO: 03)

- 생성한 토큰을 jwt.io에 붙여 넣어 Header·Payload(`sub`, `email`, `name`, `role`, `exp`) 확인
- secret 문자열을 넣으면 서명 검증 결과도 확인 가능

---

## 참고

> 참고: 교재 예외 목록의 "ExpireJwtException"은 **`ExpiredJwtException`**이 정확한 클래스명. JJWT 0.12 이상에서 `SignatureException`은 `io.jsonwebtoken.security.SignatureException`을 씀(`io.jsonwebtoken.SignatureException`은 deprecated).

- `Map.of(..)`는 **null 값을 허용하지 않음**(NullPointerException) → `createAccessToken`에서 role이 null이면 기본값 `"USER"`를 넣는 이유
- `Keys.hmacShaKeyFor`는 키가 **256비트(32바이트) 미만**이면 `WeakKeyException` → secret 문자열은 충분히 길게
- `subject()`·`claims()`·`expiration()`·`verifyWith()`·`parseSignedClaims()`는 JJWT **0.12 이후** API. 0.11 이하 자료의 `setSubject()`·`setClaims()`·`parseClaimsJws()`·`setSigningKey()`는 deprecated
- `getBytes()`는 기본 문자셋에 따라 결과가 달라질 수 있어 노트에서는 `StandardCharsets.UTF_8`을 명시함
