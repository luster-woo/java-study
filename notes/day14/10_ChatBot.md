# ChatBot

> 교재: 15기_자바전공_06_ai_2_0611 (전공) — 5. ChatBot, 부록: 기타 모델 · 선택 강의

> [← RAG Advisor](09_RAG_Advisor.md) · [day14 목록](../README.md)

---

## ChatBot 서비스 구현

### 전체 구성

- 지금까지 만든 서비스를 **컨트롤러 하나(`/chat`)**에서 요청 내용에 따라 골라 부름

```text
POST /chat  (multipart/form-data: message, file, userId, useRag)
   │
   ├─ file 있음        → E_MultiModalService.chatBotImageDescription()  이미지 설명(멀티모달)
   ├─ useRag == true   → G_RAGService.ragGeneration()                  문서 검색 + 대화 기억
   └─ 그 밖            → F_ConversationService.historyGeneration()     대화 기억만
   │
   ▼
200 OK + 결과   /   예외 시 500 + "요청 처리 중 오류 발생 ..."
```

### AiController

- 파일 업로드가 있으므로 `consumes = MediaType.MULTIPART_FORM_DATA_VALUE`
- `userId`를 그대로 **conversationId**로 넘겨 사용자별 대화 기억을 분리

```java
private final E_MultiModalService multiModalService;
private final F_ConversationService conversationService;
private final G_RAGService ragService;

@PostMapping(value = "/chat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> chat(@RequestParam("message") String message,
                              @RequestParam(value = "file") MultipartFile file,
                              @RequestParam(value = "userId") String userId,
                              @RequestParam(value = "useRag") boolean useRag) {
    try {
        Object result;
        if (file != null && !file.isEmpty()) {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes());
            result = multiModalService.chatBotImageDescription(resource, message, userId);
        } else if (useRag) {
            SearchRequest searchRequest = SearchRequest.builder().query(message).topK(3).build();
            result = ragService.ragGeneration(message, searchRequest, userId);
        } else {
            result = conversationService.historyGeneration(message, userId);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("요청 처리 중 오류 발생 " + e.getMessage());
    }
}
```

| 분기 | 호출 서비스 | 관련 강의 |
|---|---|---|
| 이미지 첨부 | `chatBotImageDescription(resource, message, userId)` | [Multimodal](03_Multimodal.md) |
| `useRag` | `ragGeneration(message, searchRequest, userId)` | [RAG Advisor](09_RAG_Advisor.md#questionansweradvisor-활용-todo-13) |
| 일반 대화 | `historyGeneration(message, userId)` | [ConversationMemory](07_ConversationMemory.md#서비스-메서드-todo-12) |

- RAG 분기는 filter 없이 topK 3만 지정 → 적재된 **모든 문서**를 대상으로 검색

---

## 내일 수업을 위해서

### langfuse.com — LLM 호출 추적 서비스

- 가입: [start free] → 3단계 설문 참여
- **조직·프로젝트 생성**: [+ New Organization] → 프로젝트 생성(팀원 추가 가능)
- **키 생성**: Project Settings > API Keys > [+ Create new API keys] → PUBLIC KEY, SECRET KEY 발급
- **Authorization Header 만들기**: `공개키:비밀키`를 base64로 인코딩

```bash
echo -n "${LANGFUSE_PUBLIC_KEY}:${LANGFUSE_SECRET_KEY}" | base64
```

- Spring AI 연동 방법: https://langfuse.com/integrations/frameworks/spring-ai

### 프로젝트 준비

- `spring-ai-project_lab.zip` 프로젝트 가져오기
- `application.properties`에 필요한 key 등록

| 속성 | 발급처 | 용도 |
|---|---|---|
| `api.openweather.key` | https://home.openweathermap.org/ | 날씨 정보 |
| `api.serp.key` | https://serpapi.com/ | 인터넷 검색 |
| `management.otlp.tracing.headers.Authorization` | langfuse(위에서 만든 값) | langfuse 연결용 Authorization Header |

```properties
api.openweather.key=${OPENWEATHER_API_KEY}
api.serp.key=${SERP_API_KEY}
management.otlp.tracing.headers.Authorization=${LANGFUSE_AUTH_HEADER}
```

---

## 부록: 기타 모델

### 이미지 모델

- 사용자가 입력한 **텍스트 프롬프트로 새 이미지를 생성**하는 모델
- Spring AI가 모든 이미지 Model을 지원하지는 않음(1.1.2 기준)
  - Gemini는 Gemini API 쪽 예제를 참조

```text
List<ImageMessage> + ImageOptions
            │
            ▼
        ImagePrompt ──▶ ImageModel.call() ──▶ ImageResponse
                                                  └─ List<ImageGeneration>
                                                        └─ Image(+ 메타데이터)
```

#### ImageModel

- 이미지 생성형 모델의 **클라이언트**

```java
@FunctionalInterface
public interface ImageModel extends Model<ImagePrompt, ImageResponse> {
    ImageResponse call(ImagePrompt request);   // 이미지 생성 요청
}
```

#### ImagePrompt

- 이미지 생성 모델의 **입력 프롬프트** = ImageMessage 목록 + ImageOptions

```java
public class ImagePrompt implements ModelRequest<List<ImageMessage>> {
    private final List<ImageMessage> messages;
    private ImageOptions imageModelOptions;

    @Override
    public List<ImageMessage> getInstructions() { /* ... */ }

    @Override
    public ImageOptions getOptions() { /* ... */ }
}
```

#### ImageMessage

```java
public class ImageMessage {
    private String text;    // 이미지 생성에 쓰는 텍스트
    private Float weight;   // 텍스트가 이미지 생성에 미치는 영향력(양수/음수)
    public String getText() { /* ... */ }
    public Float getWeight() { /* ... */ }
}
```

```java
new ImageMessage("고양이가 쥐를 쫓는 모습", 0.8f)
```

#### ImageOptions

```java
public interface ImageOptions extends ModelOptions {
    Integer getN();               // 이미지 생성 개수
    String getModel();            // 사용할 모델 이름
    Integer getWidth();           // 이미지 폭
    Integer getHeight();          // 이미지 높이
    String getResponseFormat();   // openai: url 또는 base64 / stability ai: byte[] 또는 base64
}
```

- 옵션 상세(OpenAI): https://platform.openai.com/docs/api-reference/images/create

#### ImageResponse / ImageGeneration

- **ImageResponse**: 생성형 모델의 출력을 담는 클래스

```java
public class ImageResponse implements ModelResponse<ImageGeneration> {
    private final ImageResponseMetadata imageResponseMetadata;
    private final List<ImageGeneration> imageGenerations;

    @Override
    public ImageGeneration getResult() { /* 첫 번째 결과 */ }
    @Override
    public List<ImageGeneration> getResults() { /* ... */ }
    @Override
    public ImageResponseMetadata getMetadata() { /* ... */ }
}
```

- **ImageGeneration**: 생성된 이미지와 그 메타데이터

```java
public class ImageGeneration implements ModelResult<Image> {
    private ImageGenerationMetadata imageGenerationMetadata;
    private Image image;
}
```

### 음성 모델

| API | 방향 | Spring AI 모델 |
|---|---|---|
| **TTS**(Text-To-Speech) | 텍스트 → 음성 | `TextToSpeechModel` |
| **Transcription**(STT) | 음성 → 텍스트 | `TranscriptionModel` |

- GMS로 audio를 쓸 때는 **path는 설정하지 않고 base-url만** 설정

```properties
spring.ai.openai.audio.speech.base-url=https://gms.ssafy.io/gmsapi/api.openai.com/
spring.ai.openai.audio.transcription.base-url=https://gms.ssafy.io/gmsapi/api.openai.com/
```

#### TTS

- 문서: https://docs.spring.io/spring-ai/reference/api/audio/speech/openai-speech.html
- 옵션(모델·목소리·파일 형식·속도) → `TextToSpeechPrompt` → `speechModel.call()` → 결과는 **byte[] 음성 데이터**

```java
private final TextToSpeechModel speechModel;

public String generateTTS(String text) throws IOException {
    OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
            .model("gpt-4o-mini-tts")
            .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
            .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
            .speed(1.0)
            .build();
    TextToSpeechPrompt speechPrompt = new TextToSpeechPrompt(text, speechOptions);
    TextToSpeechResponse response = speechModel.call(speechPrompt);
    byte[] audioData = response.getResult().getOutput();
    File file = toFile(audioData, "tts_output", "mp3");   // byte[] → 파일 저장(도우미 메서드)
    return file.getCanonicalPath();
}
```

#### STT

- 문서: https://docs.spring.io/spring-ai/reference/api/audio/transcriptions/openai-transcriptions.html
- 언어 `ko`, temperature 0(가장 확정적), 결과 형식 **VTT**(자막 형식, 시간 정보 포함)

```java
private final TranscriptionModel transcriptionModel;

public String generateSTT(Resource audioFile) {
    OpenAiAudioApi.TranscriptResponseFormat responseFormat = OpenAiAudioApi.TranscriptResponseFormat.VTT;
    OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
            .language("ko")
            .temperature(0f)
            .responseFormat(responseFormat)
            .build();
    return transcriptionModel.transcribe(audioFile, transcriptionOptions);
}
```

---

## 참고

- `@RequestParam`은 기본이 **`required = true`** → 교재 코드처럼 `file`을 필수로 두면 파일 없이 보낸 요청은 컨트롤러에 오기 전에 **400 Bad Request**. 코드가 `file != null`을 검사하려면 `@RequestParam(value = "file", required = false) MultipartFile file`로 둬야 함(`useRag`도 생략 가능하게 하려면 `defaultValue = "false"`)
- 교재 오류 정정
  - ImageMessage 주석의 "양수/응수"는 "양수/음수"
  - ImageOptions 코드에 `extends ModelOptions {`가 줄바꿈으로 떨어져 있어 한 줄로 복원
- `toFile(...)`은 교재에 본문이 없는 도우미 메서드(byte[]를 임시 파일로 쓰는 용도)
- langfuse의 OTLP Authorization 값은 보통 `Basic <base64 문자열>` 형태 — 정확한 값은 위 연동 문서 기준으로 넣음
- 키(OpenAI·OpenWeather·SerpApi·langfuse)는 `application.properties`에 직접 쓰지 말고 환경 변수 자리표시자로 두고 git에 올리지 않음
- 이 노트의 코드는 API 키가 필요해 실행하지 않음
