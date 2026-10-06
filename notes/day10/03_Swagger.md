# Swagger

> 교재: 15기_자바전공_10_rest_0520 (전공) — 3. Swagger

> [← REST 서비스 작성](02_REST_서비스_작성.md) · [day10 목록](../README.md) · [RestClient →](04_RestClient.md)

---

## Swagger란? (TODO-07)

- **REST API를 설계, 빌드, 문서화**하기 위한 오픈소스 프레임워크
- **SpringDoc**: Spring Boot에서 Swagger 문서를 자동으로 생성해 주는 library — https://springdoc.org/

### 주요 특징

- **API 문서화**: Annotation을 기반으로 API 문서 자동 생성
- **웹 기반**: 문서화된 API 화면에서 직접 요청을 실행하고 응답 확인 가능

### 의존성 추가

| Spring Boot | springdoc-openapi |
|---|---|
| 3.x | 2.x |
| 4.x | 3.x |

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>${springdoc-openapi-starter-webmvc-ui-version}</version>
</dependency>
```

### 기본 문서 경로

```text
http://server:port/context-path/swagger-ui.html
```

---

## Swagger를 이용한 Document 작성

### 빈 설정 (TODO-07)

- `@OpenAPIDefinition(info = @Info(...))`: 문서 전체에 대한 설명(제목, 설명, 버전)
- `GroupedOpenApi`: URL 패턴별로 API를 **그룹**으로 묶어 문서를 나눔

```java
@Configuration
@OpenAPIDefinition(info = @Info(title = "SSAFY HRM API 명세서",
                                description = "API 명세서 테스트 입니다.",
                                version = "v1"))
public class SwaggerConfig {

    @Bean
    GroupedOpenApi memberOpenApi() {
        String[] paths = { "/api/v1/members/**" };
        return GroupedOpenApi.builder().group("Member 관련 API")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    GroupedOpenApi otherOpenApi() {
        String[] paths = { "/api/v1/etc/**" };
        return GroupedOpenApi.builder().group("기타 API")
                .pathsToMatch(paths)
                .build();
    }
}
```

### Controller 설정 (TODO-07)

| Annotation | 위치 | 역할 |
|---|---|---|
| `@Tag` | 클래스 | 컨트롤러(API 묶음)의 이름과 설명 |
| `@Operation` | 메서드 | API 하나의 요약(summary)과 설명(description) |
| `@ApiResponses` / `@ApiResponse` | 메서드 | 응답 코드별 설명 |

```java
@RestController
@RequestMapping("/api/v1/members")
@Tag(name = "AuthRestController", description = "회원 관리를 위한 기능 제공")
public class AuthRestController implements RestControllerHelper {

    @GetMapping
    @Operation(summary = "회원 목록 조회", description = "모든 회원의 정보를 반환한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "회원 목록 조회 실패"),
    })
    private ResponseEntity<?> memberList(@ModelAttribute SearchCondition condition, Model model) {
        try {
            Page<Member> page = mService.search(condition);
            return handleSuccess(page);
        } catch (DataAccessException e) {
            return handleFail(e);
        }
    }
}
```

- `handleSuccess`, `handleFail`: 실습 프로젝트의 `RestControllerHelper` 인터페이스가 제공하는 응답 처리 메서드

### DTO 설정 (TODO-07)

- `@Schema`: 모델(DTO)과 필드에 대한 설명
  - `title`, `description`: 이름과 설명
  - `requiredMode`: 필수 여부 `RequiredMode.REQUIRED` / `RequiredMode.NOT_REQUIRED`
  - `example`: 문서 화면에 보여 줄 예시 값

```java
@Schema(title = "Address(주소)", description = "회원의 주소 정보")
public class Address {
    @Schema(description = "주소번호로 저장 시 자동 채번", requiredMode = RequiredMode.NOT_REQUIRED)
    private int ano;

    @Schema(description = "회원번호", requiredMode = RequiredMode.REQUIRED)
    private int mno;

    @Schema(description = "타이틀(분류기준)", requiredMode = RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "기본 주소", requiredMode = RequiredMode.REQUIRED, example = "서울특별시 강남구 테헤란로 21")
    private String address;

    @Schema(description = "상세 주소", requiredMode = RequiredMode.REQUIRED, example = "멀티캠퍼스")
    private String detailAddress;

    @Schema(description = "x 좌표", requiredMode = RequiredMode.NOT_REQUIRED, example = "지점의 x 좌표")
    private String x;

    @Schema(description = "y 좌표", requiredMode = RequiredMode.NOT_REQUIRED, example = "지점의 y 좌표")
    private String y;

    @Schema(description = "회원 정보", requiredMode = RequiredMode.NOT_REQUIRED)
    private Member member;
}
```

### 실제 API 테스트

- `swagger-ui.html` 화면에서 그룹 선택 → API 펼치기 → 값 입력 후 실행하면 실제 요청이 나가고 응답 확인 가능
