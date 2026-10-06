# file upload

> 교재: 15기_자바전공_06_springmvc_2_0513 (전공) — 4. file upload

> [← 예외 처리](06_예외_처리.md) · [day09 목록](../README.md)

---

## File Upload

### 서버 설정 — application.properties

```properties
# 파일 저장 위치
spring.servlet.multipart.location=${user.home}/uploads
# 여러 파일 업로드 시 요청 전체의 최대 크기
spring.servlet.multipart.max-request-size=50MB
# 파일 하나당 최대 크기
spring.servlet.multipart.max-file-size=10MB
# 메모리에 저장하는 최대 크기(넘으면 디스크 임시 파일로)
spring.servlet.multipart.file-size-threshold=10MB
```

| 속성 | 의미 |
|---|---|
| `location` | 업로드 파일 저장 위치 |
| `max-request-size` | 한 요청(여러 파일 합계) 최대 크기 |
| `max-file-size` | 파일당 최대 크기 |
| `file-size-threshold` | 메모리에 두는 최대 크기 |

### 서버 설정 — Controller

- **`MultipartFile`**: 웹 애플리케이션에서 업로드된 파일을 다루는 객체
- 파일도 `@RequestParam`으로 받음

```java
// SimpleController
@PostMapping("/fileupload")
public String fileUpload(@RequestParam MultipartFile file,
                         @RequestParam(required = false) String desc,
                         Model model) throws IllegalStateException, IOException {
    model.addAllAttributes(service.uploadFile(file, desc));
    return "index";
}
```

```java
// SimpleService
public Map<String, Object> uploadFile(MultipartFile file, String desc) throws IOException {
    String fileName = file.getOriginalFilename();   // 사용자가 올린 원래 파일 이름
    File localFile = new File(filePath, fileName);  // filePath: 저장 위치
    file.transferTo(localFile);                     // 실제 저장
    return Map.of(
            "fileName", fileName,
            "desc", desc != null ? desc : "");
}
```

- 실제로는 이 외에도 신경 쓸 게 많음(아래 `참고`)

### 클라이언트 처리 — form 전송

- 파일은 크기가 크므로 **반드시 POST**로 보내고 **`enctype="multipart/form-data"`** 속성이 꼭 필요

```html
<form action="${root}/fileupload" method="post" enctype="multipart/form-data">
    <input type="file" name="file" accept="image/*">
    <input type="text" name="desc">
    <button>전송</button>
</form>
```

- `name="file"` → `@RequestParam MultipartFile file`, `name="desc"` → `@RequestParam String desc`

### File download — 정적 리소스로 확인

- `<a>`의 **`download`** 속성으로 링크 클릭 시 다운로드

```jsp
<div>
    <h4>일반</h4>
    <span>desc: ${desc}</span> <br>
    <a href="${root}/upload/${fileName}" download="${fileName}">
        <img src="${root}/upload/${fileName}" style="width: 100px" />
    </a>
</div>
```

- 문제: 업로드 파일의 물리적 위치는 `${user.home}/uploads` — 웹 경로가 아님
- **`WebMvcConfigurer`** 설정으로 화면 경로와 물리 경로를 연결

```java
@Value("${spring.servlet.multipart.location}")
String filePath;

@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/upload/**")                 // 화면에서 쓰는 경로
            .addResourceLocations("file:" + filePath + "/");  // 물리적인 경로
}
```

### Base64 encoding 활용

- **Base64**: 텍스트나 이진 데이터를 ASCII 문자열로 바꾸는 방법

```java
String mimeType = file.getContentType();
String base64 = "data:%s;base64,%s".formatted(mimeType,
        Base64.getEncoder().encodeToString(file.getBytes()));
```

- **Data URI Scheme**: `<img>`의 `src`에 파일 URL 대신 Base64 데이터를 직접 넣음

```html
<img src="data:image/png;base64,BASE64로 인코딩된 데이터" alt="base64">
```

| 장점 | 단점 |
|---|---|
| **HTTP 요청 감소** — 이미지가 페이지에 직접 들어가 별도 요청 불필요 | **데이터 크기 증가** — 인코딩 결과가 약 33% 커짐 → 큰 이미지엔 부적절 |
| **파일 경로 관리 불필요** — 파일 서버 없이 RDB에 저장 가능 | **캐싱 어려움** — 같은 이미지라도 매번 다시 로드 |

- 결론: **작은 이미지는 Base64로 간단히, 큰 이미지는 전통적인 방법(파일 저장 + 경로)**
- 크기 증가 확인(순수 자바, JDK 11이라 `formatted` 대신 `String.format`)

```java
import java.util.Base64;

public class Base64Demo {
    public static void main(String[] args) {
        byte[] data = new byte[300];          // 300바이트짜리 가짜 파일 내용
        String mimeType = "image/png";
        String encoded = Base64.getEncoder().encodeToString(data);
        String dataUri = String.format("data:%s;base64,%s", mimeType, encoded);

        System.out.println(data.length);      // 300
        System.out.println(encoded.length()); // 400
        System.out.println(dataUri.substring(0, 30));
    }
}
// 결과
// 300
// 400
// data:image/png;base64,AAAAAAAA
```

- 3바이트 → 4문자로 바뀌므로 300 → 400(약 33% 증가)

### Ajax 활용 — FormData

- Ajax로 파일을 올릴 때는 **`FormData`** 객체 사용
- 서버(`/simple/fileupload-ajax`)는 JSON으로 응답하고, 그 안의 `base64` 값을 `<img>`의 `src`에 바로 넣음

```javascript
document.querySelector("#btn-upload").addEventListener("click", async () => {
    const formData = new FormData();
    const file = document.querySelector("#file").files[0];
    const desc = document.querySelector("#desc").value;
    formData.append("file", file);
    formData.append("desc", desc);
    try {
        const response = await fetch("${root}/simple/fileupload-ajax", { method: "post", body: formData });
        const json = await response.json();
        console.log("profile upload success", json);
        document.querySelector("#ajax-img").src = json.base64;
    } catch (e) {
        console.log("profile upload fail", e);
    }
});
```

- `FormData`를 `body`로 넘기면 브라우저가 `multipart/form-data` 헤더를 자동으로 붙임 → `Content-Type`을 직접 지정하지 않음

---

## 참고

> 참고: `SimpleController`는 클래스에 `@RequestMapping("/simple")`이 붙어 있으므로, 교재 form의 `action="${root}/fileupload"`는 `${root}/simple/fileupload`로 써야 매핑됨(Ajax 예제는 `/simple/fileupload-ajax`로 되어 있음).

- 업로드 시 더 신경 쓸 것: 같은 이름 파일 덮어쓰기(UUID 등으로 저장 이름 변경), 파일 확장자·MIME 검사, 저장 폴더가 없을 때 생성
- `String.formatted()`는 Java 15+ 메서드 — Spring Boot 3.x(Java 17+)에서는 교재 코드 그대로 사용 가능
- 다음 수업 준비(교재 마지막): 실습 프로젝트 `FW_05_mvc_lab.zip`을 받아 workspace에 import, `application.properties`에 Kakao JavaScript 키 등록
