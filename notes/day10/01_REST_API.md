# REST API

> 교재: 15기_자바전공_10_rest_0520 (전공) — 1. REST API

> [day10 목록](../README.md) · [REST 서비스 작성 →](02_REST_서비스_작성.md)

---

## REST API

### 서비스/애플리케이션 개발 흐름

- 멀티 플랫폼, 멀티 디바이스의 시대
- 예전: 플랫폼마다 API가 따로 있음 → **플랫폼 종속적**
- 지금: 모두가 공통으로 쓰는 **HTTP 프로토콜 기반**으로 기능 제공 → **플랫폼 독립적**

```text
Linux C    → Linux C Api     ┐ 플랫폼 종속적 (플랫폼마다 API가 다름)
Windows C  → Windows C Api   ┘
Linux Java / Windows Java → Java Api
Android ...               ┐
어떤 클라이언트든          ├→ Http Protocol → REST API   (플랫폼 독립적)
```

### REST(Representational State Transfer) API

| 단어 | 의미 |
|---|---|
| **Representational** | 자원의 표현 — 정보(상태)를 JSON, XML 등의 형식으로 표현 |
| **State** | 자원의 상태 |
| **Transfer** | 네트워크를 통해 상태를 전송 |

- 2000년 **로이 필딩(Roy Fielding)** 이 제안한 아키텍처 스타일
- 웹의 기존 기술과 HTTP 프로토콜을 그대로 활용하는 설계 원칙

### REST API의 주요 특징

- **자원 중심(resource-oriented)**: 모든 것은 자원으로 표현, 각 자원은 고유한 **URI**를 가짐
- **HTTP 메서드 활용**: GET / POST / PUT / DELETE 등으로 요청
- **자원의 표현**: JSON, XML 사용
- **무상태(Stateless)**: 각 요청은 이전 요청과 독립적, 서버는 클라이언트 상태를 저장하지 않음

### 기존 방식 vs REST 방식

| 특성 | 전통적인 방식(비 REST) | REST 방식 |
|---|---|---|
| URL 설계 | 행위 중심(동사형) `GET /get-user-info?id=123` | 자원 중심(명사형) `GET /users/123` |
| HTTP 메서드 | 주로 GET, POST만 | GET, POST, PUT, DELETE 등 |
| 상태 관리 | 서버에 세션 상태 유지 경향 | Stateless — 각 요청이 독립적 |
| 응답 형식 | HTML | 주로 JSON, XML 등 표준화된 형식 |
| 클라이언트-서버 분리 | 혼합되거나 밀접하게 결합 | 명확한 역할 분리 |
| 확장성 | 제한적(시스템 간 결합도 높음) | 높음(분산 시스템에 적합) |
| 버전 관리 | 명시적 버전 관리 부재 | URI에 버전 포함 가능 `/api/v1/users` |

### REST 서비스 URL 작성 권장 사항

| 권장 사항 | 내용 | 예 |
|---|---|---|
| 자원 중심 URL | 자원을 나타냄, 단수형보다 **복수형 명사** | `/members`, `/addresses` |
| HTTP 메서드 사용 | URL은 자원의 위치만, 작업 의미는 HTTP 메서드로 전달 | `GET /members` |
| `/`로 계층 구조 | 자원의 관계를 계층으로 표현, **맨 마지막에 `/` 넣지 않음** | `/members/{mno}/addresses` |
| 목록과 개별 자원 구분 | 목록 / 하나 | `/members`, `/members/{mno}` |
| 버전 관리 | API 버전을 URL에 포함 → 변경에 유연 | `/v1/members`, `/v2/members` |
| 소문자 사용 | 단어 구분은 하이픈(-) 또는 언더스코어(_) | `/member-profile` |
| 쿼리스트링 활용 | 필터링, 정렬 등 추가 정보 | `/members?key=email&word=01&page=3` |

### REST API URL의 예

| URL | Method | 동작 |
|---|---|---|
| `/api/members` | GET | 전체 리소스 조회 — 모든 회원 목록 |
| `/api/members?key=email&word=01&page=3` | GET | 전체 리소스 조회(필터링) — 조건에 맞는 회원 목록 |
| `/api/members/123` | GET | 특정 리소스 조회 — 123번 회원 |
| `/api/members` | POST | 새 리소스 생성 — 내용은 body로 |
| `/api/members/123` | PUT | 기존 리소스 **완전** 업데이트 — 내용은 body로 |
| `/api/members/123` | PATCH | 기존 리소스 **부분** 업데이트 — 내용은 body로 |
| `/api/members/123` | DELETE | 기존 리소스 삭제 — 123번 회원 |

```http
GET    http://example.com/api/members/123
POST   http://example.com/api/members
DELETE http://example.com/api/members/123
```

---

## REST API를 위한 Annotation

### @ResponseBody

- 일반 `@Controller`에서 REST 서비스를 위해 사용
- 뷰를 연동하지 않고 **데이터(문자열, JSON, XML)만 전송**
- **MessageConverter**(Jackson Data Bind 등)가 데이터를 JSON 등으로 가공해서 전달
- REST API의 **응답**에는 언제나 `@ResponseBody`

```java
@Target({ElementType.TYPE, ElementType.METHOD})   // 클래스, 메서드에 사용
public @interface ResponseBody {}
```

### @RequestBody

- **body로 전송된 JSON 데이터를 객체로 변환**
- 파라미터로 전달되는 데이터를 처리하는 `@ModelAttribute`와 비슷한 역할
- REST API라고 **요청**에 언제나 `@RequestBody`를 쓰는 것은 아님 → 요청의 Content-Type에 따라 고름

| 요청 Content-Type | 서버 쪽 |
|---|---|
| `application/json` | `@RequestBody` |
| `application/x-www-form-urlencoded` | `@ModelAttribute`, `@RequestParam` |

```java
@Target(ElementType.PARAMETER)
public @interface RequestBody {
    boolean required() default true;
}
```

### @RestController

- **`@Controller` 이면서 `@ResponseBody`**
- 해당 Controller의 모든 `@RequestMapping` 메서드 반환은 View가 아닌 **데이터**
  - 모든 요청 처리 메서드가 `@ResponseBody`로 처리됨

```java
@Target(ElementType.TYPE)
@Controller
@ResponseBody
public @interface RestController {
    @AliasFor(annotation = Controller.class)
    String value() default "";
}
```

### @PathVariable

- **URL 상의 변수**를 처리하기 위한 annotation
- `@RequestParam`처럼 자동 형 변환 등 지원

```java
@GetMapping("/members/{mno}")
@ResponseBody
public Map<String, Object> getMember(@PathVariable int mno) {
    // ...
}
```

```java
@Target(ElementType.PARAMETER)
public @interface PathVariable {
    @AliasFor("value")
    String name() default "";
    boolean required() default true;
}
```
