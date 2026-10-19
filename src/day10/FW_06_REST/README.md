# FW_06_REST — day10 REST API 예제

노트: [01 REST API](../../../notes/day10/01_REST_API.md) · [02 REST 서비스 작성](../../../notes/day10/02_REST_서비스_작성.md) · [03 Swagger](../../../notes/day10/03_Swagger.md) · [04 RestClient](../../../notes/day10/04_RestClient.md) · [05 Service Layer와 TX](../../../notes/day10/05_Service_Layer와_TX.md) · [06 @Transactional](../../../notes/day10/06_Transactional.md)

DB 없이 메모리(Map)에 회원을 저장하는 간단한 REST API. 서버를 재시작하면 데이터가 지워진다.
04~06 실습(RestClient, CORS, 트랜잭션)이 추가되어 있다. 트랜잭션 실습은 H2 메모리 DB(`schema.sql`)를 쓴다.

## 실행

1. IntelliJ에서 `FW_06_REST` 폴더를 열고 Gradle 로딩을 기다린다.
2. `Fw06RestApplication` 실행.
3. `requests.http`의 ▶ 로 요청을 보내거나, 브라우저에서 http://localhost:8080 (fetch 화면)을 연다.
4. Swagger 문서: http://localhost:8080/swagger-ui.html → 오른쪽 위에서 그룹 선택 → API 펼치기 → **Try it out** → **Execute**
   - JSON 명세 원본: http://localhost:8080/v3/api-docs

## 구조

| 파일 | 내용 |
|---|---|
| `controller/MemberRestController` | `@RestController`, `@RequestBody`, `@PathVariable`, `ResponseEntity`(201, 204), Swagger `@Tag` `@Operation` `@ApiResponse` |
| `controller/HelloController` | 비교용: `@Controller` + `@ResponseBody` |
| `dto/Member` | Swagger `@Schema`(필드 설명, 예시 값) |
| `service/MemberService` | 메모리 저장소 CRUD |
| `config/SwaggerConfig` | `@OpenAPIDefinition`(문서 제목), `GroupedOpenApi`(URL별 그룹) |
| `exception/RestExceptionHandler` | `@RestControllerAdvice` + `ProblemDetail` (404, 500) |
| `static/index.html` | `fetch`로 JSON 보내고 받기 |
| `test/.../MemberRestControllerTest` | `@WebMvcTest` + `@MockitoBean` + `jsonPath` |
| **[04]** `controller/EtcRestController` | RestClient 프록시: `UriComponentsBuilder`, `RestClient.create()/builder()`, `retrieve()`, `ParameterizedTypeReference` |
| **[04]** `config/CorsConfig` | CORS 전역 설정 (`WebMvcConfigurer.addCorsMappings`) |
| **[04]** `controller/HelloController` | `/hello-json` 에 `@CrossOrigin` |
| **[04]** `static/index.html` | 아래쪽 버튼 ①~⑥ 으로 SOP, CORS, 프록시 비교 |
| **[05]** `tx/TxMemberService` | 주소 → 회원 삭제: `@Transactional` 유무, Checked 예외, `rollbackFor`, self-invocation, `PlatformTransactionManager` 직접 사용 |
| **[05]** `tx/TxMemberDao` `TxAddressDao` `TxLogDao` | `JdbcTemplate` DAO (Connection 을 직접 다루지 않음) |
| **[06]** `tx/TxOuterService` → `tx/TxLogService` | 전파 속성 `REQUIRED`, `REQUIRES_NEW` (서로 다른 빈) |
| **[05, 06]** `tx/TxDemoController` | `/api/v1/tx/**` 실험 API, 응답의 `after` 로 결과 확인 |
| **[05, 06]** `test/.../tx/TxMemberServiceTest` `TxPropagationTest` | 각 상황에서 무엇이 남는지 검증하는 테스트 |

## API

| Method | URL | 요청 body | 응답 |
|---|---|---|---|
| GET | `/api/v1/members` | - | 200, 회원 배열 |
| GET | `/api/v1/members/{mno}` | - | 200, 회원 / 404 |
| POST | `/api/v1/members` | JSON | 201, `{"member": {...}}` |
| PUT | `/api/v1/members/{mno}` | JSON | 200, 수정된 회원 / 404 |
| DELETE | `/api/v1/members/{mno}` | - | 204 / 404 |

## [04] RestClient / CORS 실습

1. vworld 키가 있으면 `application.properties` 의 `key.vworld=` 에 넣는다. (없어도 ⑥번 연습 API 는 동작)
2. 브라우저에서 **http://localhost:8080** 을 열고 아래쪽 버튼 ①~⑥ 을 눌러 본다. F12 콘솔도 같이 본다.
   - ①② 성공 / ③ CORS 차단 / ④ SOP 차단 / ⑤ 프록시로 성공(키 필요) / ⑥ RestClient 연습
3. `CorsConfig` 의 `@Configuration` 을 주석 처리하고 재시작하면 ① 도 차단된다.

| Method | URL | 내용 |
|---|---|---|
| GET | `/api/v1/etc/sidos` | 내 서버가 vworld 시/도 목록을 대신 받아서 전달 |
| GET | `/api/v1/etc/members-by-restclient` | RestClient 로 `/api/v1/members` 호출 (키 없이 연습) |

## [05, 06] 트랜잭션 실습

- 가장 빠른 확인: `TxMemberServiceTest`, `TxPropagationTest` 실행 → 테스트 이름이 곧 결과
- 직접 해 보기: `requests.http` 아래쪽을 위에서부터 차례로 ▶ (또는 Swagger 의 "Transaction 실습" 그룹)
- 콘솔에 `Creating new transaction`, `Committing`, `Rolling back` 로그가 찍힌다.

| 상황 | 결과 |
|---|---|
| `@Transactional` 없음 + 중간 예외 | 주소만 삭제 (반만 성공) |
| `@Transactional` + RuntimeException | 전부 rollback |
| `@Transactional` + Checked 예외 | **commit** (주소만 삭제) |
| `rollbackFor` + Checked 예외 | rollback |
| 같은 클래스 안에서 호출 | `@Transactional` 무시 (주소만 삭제) |
| `PlatformTransactionManager` 직접 사용 | rollback |
| 전파 `REQUIRED`, 안쪽 실패 후 예외 잡음 | 전체 rollback + `UnexpectedRollbackException` |
| 전파 `REQUIRES_NEW`, 안쪽 실패 | 안쪽만 rollback, 바깥 commit |
| 전파 `REQUIRES_NEW` 성공 후 바깥 실패 | 바깥만 rollback, 안쪽(로그)은 남음 |
