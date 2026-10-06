# Multimodal (2)

> 교재: 15기_자바전공_05_ai_1_0610 (전공) — 4. Multimodal (뒷부분: MimeType·Resource 구성) · 선택 강의 · 4단원을 둘로 나눔 — 경계는 추정

> [← Multimodal](03_Multimodal.md) · [day14 목록](../README.md) · [Advisor API →](05_Advisor_API.md)

---

## 미디어 입력 구성

### 다양한 방식으로 MimeType 구성

- `spec.media(mime, resource)`의 첫 인자 = **MIME 타입**
- 자주 쓰는 값은 Spring의 **`MimeTypeUtils`** 에 상수로 정의되어 있음

```java
// MimeTypeUtils.java (일부)
ALL                      = new MimeType(MimeType.WILDCARD_TYPE, MimeType.WILDCARD_TYPE);
APPLICATION_GRAPHQL      = new MimeType("application", "graphql+json");
APPLICATION_JSON         = new MimeType("application", "json");
APPLICATION_OCTET_STREAM = new MimeType("application", "octet-stream");
APPLICATION_XML          = new MimeType("application", "xml");
IMAGE_GIF                = new MimeType("image", "gif");
IMAGE_JPEG               = new MimeType("image", "jpeg");
IMAGE_PNG                = new MimeType("image", "png");
TEXT_HTML                = new MimeType("text", "html");
TEXT_PLAIN               = new MimeType("text", "plain");
TEXT_XML                 = new MimeType("text", "xml");
```

- 상수에 없으면 `new MimeType("type", "subtype")`으로 직접 만듦
- 이미지면 `IMAGE_PNG`, `IMAGE_JPEG`, `IMAGE_GIF` 중 파일 형식에 맞는 것을 씀

### 다양한 방식으로 Resource 획득

- 두 번째 인자 = **Resource** → 이미지가 어디 있든 같은 인터페이스로 넘김

| 구현체 | 이미지 위치 |
|---|---|
| `UrlResource` | URL(웹 이미지) |
| `FileSystemResource` | 로컬 파일 경로 |
| `ClassPathResource` | 프로젝트 클래스패스(`src/main/resources`) |
| `ByteArrayResource` | 메모리의 byte 배열 |
| `InputStreamResource` | InputStream (예: 업로드된 파일) |

```java
// 같은 메서드에 출처만 바꿔서 전달
String a1 = imageDescGeneration("무엇이 보여?", MimeTypeUtils.IMAGE_PNG,
        new ClassPathResource("images/sample.png"));
String a2 = imageDescGeneration("무엇이 보여?", MimeTypeUtils.IMAGE_JPEG,
        new UrlResource("https://example.com/cat.jpg"));
String a3 = imageDescGeneration("무엇이 보여?", MimeTypeUtils.IMAGE_PNG,
        new ByteArrayResource(uploadedFile.getBytes()));   // MultipartFile 등
```

### GMS 제한

- 현재 **GMS는 100KB 미만**의 이미지 리소스만 지원
  - 실습할 때 이미지를 작게 줄여서 사용

---

## 참고

- 위 `a1`~`a3` 호출 예는 Resource 구현체별 사용법을 보이려고 만든 예시(교재에는 구현체 목록만 있음). `UrlResource` 생성자는 `MalformedURLException`, `getBytes()`는 `IOException`을 던질 수 있음
- 업로드 파일이면 `MultipartFile#getContentType()`으로 `MimeTypeUtils.parseMimeType(...)` 해서 MIME 타입을 그대로 넘길 수도 있음
