# Multimodal

> 교재: 15기_자바전공_05_ai_1_0610 (전공) — 4. Multimodal (앞부분: 개요·객체 탐지 요청) · 선택 강의 · 4단원을 둘로 나눔 — 경계는 추정

> [← Chat Client API](02_Chat_Client_API.md) · [day14 목록](../README.md) · [Multimodal (2) →](04_Multimodal_2.md)

---

## Multi Modal 지원

### Multi Modal LLM 지원

- **Multimodal**: 텍스트만이 아니라 **이미지·오디오·비디오** 등 여러 형태의 입력을 함께 처리
- Spring AI의 Message는 텍스트와 함께 **media**(이미지 등)를 실어 보낼 수 있음

```text
UserMessage
 ├─ text  : "이 사진에 뭐가 있어?"
 └─ media : [ (image/png, Resource) , ... ]
            ──▶ ChatModel(멀티모달 지원 모델) ──▶ 텍스트 응답
```

### 모델별 지원 여부

- **모델마다** 처리할 수 있는 modal이 다름 → 쓰기 전에 확인
  - 요즘 상용 모델은 대부분 지원하지만, 일부 오픈소스 모델은 **텍스트만** 지원
- 정리된 표: Spring AI 문서 `https://docs.spring.io/spring-ai/reference/api/multimodality.html`
- 크기 제한도 공급자·프록시마다 있음 (→ [Multimodal (2)](04_Multimodal_2.md)의 GMS 제한)

---

## 객체 탐지 (TODO: 06)

### 이미지 + 질문으로 요청하기

- user 메시지 스펙에서 **`media(mime, resource)`** 로 이미지를, **`text(question)`** 으로 질문을 같이 넣음
- 나머지 흐름(`call()` → `content()`)은 텍스트 대화와 같음

```java
public String imageDescGeneration(String question, MimeType mime, Resource resource) {
    String answer = chatClient.prompt()
            .user(spec -> spec.media(mime, resource).text(question))
            .call()
            .content();
    return answer;
}
```

| 인자 | 역할 | 예 |
|---|---|---|
| `question` | 이미지에 대해 묻는 문장 | "사진 속 물체를 모두 찾아 설명해줘" |
| `mime` | 이미지 형식(`MimeType`) | `MimeTypeUtils.IMAGE_PNG` |
| `resource` | 이미지 데이터(`Resource`) | `new ClassPathResource("images/sample.png")` |

- `MimeType`과 `Resource`를 만드는 여러 방법은 다음 강의에서 정리

---

## 참고

- `spec.media(...)`는 `MimeType` + `Resource` 조합 외에 `Media` 객체를 직접 넘기는 오버로드도 있음(공식 문서의 multimodality 절 참고)
- 표의 예 값(`question` 문장, `images/sample.png` 경로)은 이해를 돕기 위한 예시로, 교재 실습 파일명과 다를 수 있음
