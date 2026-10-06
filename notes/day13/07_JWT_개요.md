# JWT 개요

> 교재: 15기_자바전공_03_JWT_0608 (전공) — 1. JWT 개요

> [← Authorization 실습](06_Authorization_실습.md) · [day13 목록](../README.md) · [JWT 생성 →](08_JWT_생성.md)

---

## Frontend Framework와 JWT

### Session 기반 인증의 한계 1 — 다양한 클라이언트

- 전통적인 **MPA** 웹: 서버의 Template Engine이 화면을 만들고, **Cookie 기반 Session**으로 인증
- **SPA(Vue)·모바일 앱·마이크로서비스** 등 클라이언트가 다양해지면서 쿠키 방식이 불편해짐
  - Vue 클라이언트가 AJAX로 API를 부르면 쿠키를 다루려고 특별한 처리가 필요 → 쿠키 관리 번거로움
  - 스크립트에서 쿠키를 다루려면 **HttpOnly**를 쓸 수 없음 → 보안 위험
  - **네이티브 앱**은 쿠키 관리 자체가 어려움
  - **CORS** 정책 때문에 설정이 복잡해짐

```
                       ┌──────────── BackEnd Server ────────────┐
 브라우저(MPA) ─cookie─▶ │ Template Engine   BackEnd Service       │
 Vue Client  ──AJAX──▶  │ BackEnd API                             │
 Mobile App  ───────▶   │                                         │
 Micro Service ─────▶   └─────────────────────────────────────────┘
          → 쿠키 하나로 모든 클라이언트를 맞추기 어려움
```

### Session 기반 인증의 한계 2 — 서버 확장성(stateful)

- 세션은 서버가 상태를 가지는 **stateful** 방식
- **scale-out**(서버 여러 대 + load balancer)하면 서버끼리 인증 정보를 공유하기 어려움

```
1-1 login  ──▶ LB ──▶ 1번 서버 : 로그인 성공(세션 생성)
1-2 write  ──▶ LB ──▶ 2번 서버 : "글쓰려면 login!"   ← 2번 서버엔 세션 없음
```

- 해결책으로 **sticky session**, **session clustering**, **session store** 등이 있지만 기술이 점점 복잡해지고 유지보수가 어려움 → 대안이 **토큰(JWT)**

### Session vs JWT 동작 비교

```
[Session]                                   [JWT]
1. id/pass ───────────▶ WAS                 1. id/pass ───────────▶ WAS
2.          WAS ─ 유효 사용자? ─▶ DB          2.          WAS ─ 유효 사용자? ─▶ DB
3. ◀─── session cookie                      3. ◀─── 서명된 Token
4. write + session cookie ──▶ WAS           4. write + Header(Token) ──▶ WAS
5.     WAS ─ 유효 세션? ─▶ Session store     5.     WAS : 토큰 서명 검증 (저장소 불필요)
6. 요청 처리                                 6. 요청 처리
```

- 토큰은 **HTTP 헤더**로 전달 → 간단한 설정만 있으면 됨

| 항목 | session cookie | token |
|---|---|---|
| 인증 정보 저장 위치 | 클라이언트 + 서버(session store) | 클라이언트 |
| 클라이언트가 보내는 것 | session id가 담긴 쿠키 | header에 token이 담긴 요청 |
| 서버의 인증 확인 | session store에 session id의 사용자 정보가 있는지 확인 | token이 올바르게 서명되었는지 판단(session store 불필요) |
| 서버가 인증 정보 수정·로그아웃 처리 | 가능 | **불가** |
| 공격 가능성 | 쿠키 탈취에 의한 CSRF 공격 우려 | 토큰 탈취, 비밀 키 유출 우려 |

- 토큰이 더 **안전해서** 쓰는 것이 아님 → 굳이 말하면 **편의성과 확장성** 때문

---

## JWT

### JWT(JSON Web Token)

- **RFC 7519**에 정의된 개방형 업계 표준
- JSON 기반 → 대부분의 언어·플랫폼에서 독립적으로 사용 가능
- **암호화가 아니라 서명**만 함
- 구성: **Header . Payload . Signature** — 세 부분을 `.`으로 연결, 각 부분은 Base64(URL)로 인코딩

```
xxxxx.yyyyy.zzzzz
Header . Payload . Signature
```

| 부분 | 내용 |
|---|---|
| **Header** | 토큰 메타데이터 — 토큰 유형(`typ`), 서명 알고리즘(`alg`) |
| **Payload** | 전달할 **클레임(claim)** — 사용자 정보(ID, 권한, 이름), 토큰 추가 데이터 등 key-value 쌍 |
| **Signature** | Header와 Payload를 `alg`와 서버의 **secret key**로 서명한 뒤 인코딩한 값 |

```
Signature = HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secretKey)
```

- Payload는 인코딩일 뿐 암호화가 아님 → **누구나 읽을 수 있음** → 비밀번호 등 민감 정보 넣지 말 것
- 서버 검증: 받은 토큰의 Header·Payload를 **다시 서명** → Signature와 비교

### JWT를 활용한 인증 구현

- JWT는 단순한 **토큰 포맷** → JWT로 인증하는 **표준 구현 방식은 없음**
- 구현 전략이 다양함

| 결정할 것 | 선택지 |
|---|---|
| 토큰 저장(전달) 위치 | Authorization Header(`Bearer`), 쿠키, 커스텀 헤더, ... |
| 토큰 갱신 전략 | Refresh Token 사용, Access Token만 사용, ... |
| 토큰 검증 방식 | 단순 서명 검증, DB 블랙리스트 확인, 외부 API 호출, ... |

### jwt.io

- **jwt.io**: JWT **Decoder**와 **Encoder** 제공 → 토큰을 붙여 넣으면 Header·Payload 확인, secret을 넣으면 서명 검증 가능

---

## 참고

> 참고: 교재에 "RTC7519"로 적혀 있으나 **RFC 7519**가 맞음. 또 JWT의 각 부분은 일반 Base64가 아니라 **Base64URL**(`+`·`/` 대신 `-`·`_`, 패딩 `=` 없음)로 인코딩함 — 서명 공식의 `base64UrlEncode`가 이것.

- 서명 원리 확인용 순수 자바 예제(라이브러리 없이 HMAC-SHA256으로 직접 서명)

```java
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtSignDemo {
    static final Base64.Encoder ENC = Base64.getUrlEncoder().withoutPadding();

    static String b64(String s) {
        return ENC.encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    static String sign(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return ENC.encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    public static void main(String[] args) throws Exception {
        String secret = "your-very-long-and-secure-random-secret-key-here-12345678";
        String header = b64("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = b64("{\"sub\":\"accessToken\",\"email\":\"hong@ssafy.com\",\"role\":\"USER\"}");
        String jwt = header + "." + payload + "." + sign(header + "." + payload, secret);

        String[] parts = jwt.split("\\.");
        // payload는 서명만 되었을 뿐 누구나 디코딩 가능
        System.out.println(new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8));
        // 결과: {"sub":"accessToken","email":"hong@ssafy.com","role":"USER"}

        // 서버 검증: header.payload를 다시 서명해서 signature와 비교
        System.out.println(sign(parts[0] + "." + parts[1], secret).equals(parts[2]));  // 결과: true

        // payload를 조작(role → ADMIN)하면 서명이 맞지 않음
        String forged = b64("{\"sub\":\"accessToken\",\"email\":\"hong@ssafy.com\",\"role\":\"ADMIN\"}");
        System.out.println(sign(parts[0] + "." + forged, secret).equals(parts[2]));    // 결과: false
    }
}
```
