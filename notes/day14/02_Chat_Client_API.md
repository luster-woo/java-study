# Chat Client API

> 교재: 15기_자바전공_05_ai_1_0610 (전공) — 2. Chat Client API + 3. Structured Output · 선택 강의

> [← Spring AI 기본 컨셉](01_Spring_AI_기본_컨셉.md) · [day14 목록](../README.md) · [Multimodal →](03_Multimodal.md)

---

## 프로젝트 구성

### 프로젝트 설정 (TODO-01, 2026.05.03 기준)

- Java **21**, Spring AI **1.1.5**
- Spring AI는 아직 초창기라 **버전이 자주 바뀜** → 버전 확인 필수
- 공급자별 **starter**를 추가 (Google GenAI, OpenAI)

```xml
<properties>
    <java.version>21</java.version>
    <spring-ai.version>1.1.5</spring-ai.version>
</properties>

<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-google-genai</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```

### application.properties

- 사용할 모델의 **서비스 키** 설정 (SSAFY는 **GMS** 프록시 주소를 base-url로 씀)
- OpenAI는 **자동 설정** 사용, Gemini는 **수동 설정** 사용(아래 사용자 정의 ChatModel)

```properties
# 01-2. 사용하려는 모델의 서비스 키 — 키는 환경변수로 주입
ssafy.gms.api-key=${GMS_API_KEY}

## OpenAI 설정 예시 - 자동 설정 사용
spring.ai.openai.api-key=${ssafy.gms.api-key}
spring.ai.openai.base-url=https://gms.ssafy.io/gmsapi/api.openai.com/v1/
spring.ai.openai.chat.completions-path=chat/completions
spring.ai.openai.chat.options.model=gpt-4o-mini
spring.ai.openai.embedding.base-url=https://gms.ssafy.io/gmsapi/api.openai.com/
spring.ai.openai.embedding.options.model=text-embedding-3-large

## Google Gemini 설정 예시 - 수동 설정 사용
spring.ai.google.genai.api-key=${ssafy.gms.api-key}
spring.ai.google.genai.base-url=https://gms.ssafy.io/gmsapi/generativelanguage.googleapis.com
spring.ai.google.genai.chat.options.model=gemini-2.5-flash-lite
spring.ai.google.genai.embedding.api-key=${spring.ai.google.genai.api-key}
```

---

## Chat Model API

### Spring AI의 기본 모델 분류

| 종류 | 인터페이스 |
|---|---|
| Large Language Model | **ChatModel** |
| Image Generation Model | **ImageModel** |
| Text-To-Voice, Voice-To-Text | **SpeechModel** |
| Embedding Model | **EmbeddingModel** |

- 개발자는 특정 AI 모델에 묶이지 않고 **공통 방식**으로 사용

```text
Service 빈 ──▶ ChatModel ◀── application.properties (공급자·모델·옵션)
```

### ChatModel

- **텍스트 기반 대화형** 인터페이스

| 메서드 | 동작 |
|---|---|
| `call(...)` | LLM에 **동기** 요청 후 응답 처리 |
| `stream(...)` | LLM에 **비동기(스트리밍)** 요청 후 응답 처리 |

### Prompt와 Message

- **Prompt**: AI 출력·동작을 안내하는 지침 = **Message 목록 + ChatOptions**

```java
public class Prompt implements ModelRequest<List<Message>> {
    private final List<Message> messages;
    private ChatOptions chatOptions;
}
```

| 종류 (Role) | 특징 | 예 |
|---|---|---|
| **SystemMessage** (System) | 대화 시작 전 AI에게 지침 제공 — 행동·응답 스타일 안내 | "친절하게 단계별로 답하고 JSON으로 반환해줘" |
| **UserMessage** (User) | 사용자가 주는 질문 — 작업 수행·응답 생성에 필요한 정보 | "오늘 날씨 어때?", "한강 작가의 도서 목록 알려줘" |
| **AssistantMessage** (Assistant) | 사용자 입력에 대한 AI의 응답. 다음 질문에 다시 넣어 **맥락 있는 대화** 유지. 모델이 쓰려는 **Tool 호출 요청 정보**도 포함 | |
| **ToolResponseMessage** (Tool) | Tool 호출 **결과**로 만들어지는 메시지 → 추가 컨텍스트 | |

### ChatOptions

- LLM과의 **대화 방식**을 정하는 옵션

| 속성 | 설명 |
|---|---|
| `model` | 사용할 AI 모델명 (예: gpt-4, claude-3) |
| `frequencyPenalty` | 같은 단어 반복을 줄이는 패널티(-2.0 ~ 2.0), 클수록 반복 감소 |
| `maxTokens` | 생성할 최대 토큰 수(응답 길이 제한) |
| `presencePenalty` | 새 주제로 전환을 유도하는 패널티(-2.0 ~ 2.0), 클수록 다양한 응답 |
| `stopSequences` | 생성을 멈출 문자열 목록 |
| `temperature` | 응답의 무작위성·창의성(0.0 ~ 2.0) |
| `topK` | 상위 K개 토큰 안에서 선택(샘플링 범위 제한) |
| `topP` | 누적 확률 P까지의 토큰 안에서 선택(0.0 ~ 1.0) |

- 기본값·지원 여부는 **모델마다 다름** → 쓰기 전에 확인
- `application.properties`에 설정하거나 **코드에서 동적으로** 설정

### ChatResponse

- LLM의 출력(**Generation**)을 담는 객체
  - topK·topP·temperature 등에 따라 **여러 답변**이 올 수 있어 `List<Generation>`

```java
public class ChatResponse implements ModelResponse<Generation> {
    private final ChatResponseMetadata chatResponseMetadata;
    private final List<Generation> generations;
}

public class Generation implements ModelResult<AssistantMessage> {
    private final AssistantMessage assistantMessage;
    private ChatGenerationMetadata chatGenerationMetadata;
}
```

### 사용자 정의 ChatModel 구성

- 필요하면 ChatModel 빈을 **직접 구성**
  - Google GenAI 자동 설정은 end point(base-url)를 지정하지 않음 → **GMS를 쓰려면 수정 필요**

```java
@Bean
ChatModel googleGenAiChatModel() {
    // 1. Low level API 클라이언트 생성
    Client client = Client.builder().apiKey(genAiApiKey)
            .httpOptions(HttpOptions.builder().baseUrl(genAiBaseUrl).build())  // GMS
            // .httpOptions(HttpOptions.builder().build())                     // 일반
            .build();
    // 2. 필요한 옵션 재정의
    GoogleGenAiChatOptions options = GoogleGenAiChatOptions.builder()
            .model(genAiModel)
            .temperature(0.7)
            .maxOutputTokens(1000)
            .build();
    // 3. 모델 생성
    return GoogleGenAiChatModel.builder()
            .genAiClient(client)
            .defaultOptions(options)
            .build();
}
```

- 직접 만들었으면 **기본(자동 설정) 빈은 제외**

```java
@SpringBootApplication(exclude = GoogleGenAiChatAutoConfiguration.class)
public class FWA03AIApplication { ... }
```

### ChatModel 동작 확인 (TODO: 02)

```java
@Slf4j
@SpringBootTest
public class ChatClientTest {

    @Autowired
    @Qualifier("openAiChatModel")
    private ChatModel openAiChatModel;

    @Test
    void openAiChatClientInjectionTest() {
        assertThat(openAiChatModel).isNotNull();
        printModelInfo(openAiChatModel);
    }

    private void printModelInfo(ChatModel model) {
        log.debug("Model Implementation: {}", model.getClass().getSimpleName());
        var options = model.getDefaultOptions();
        log.debug("Model Name: {}", options.getModel());
        log.debug("Temperature: {}", options.getTemperature());
        log.debug("Full Options: {}", options);

        var response = model.call(new Prompt("안녕? 너는 어떤 모델이야?"));
        var gen = response.getResult();          // 첫 번째 Generation
        log.debug("response: {}", gen.getOutput().getText());
    }
}
```

- `getResult()` → `Generation`, `getOutput()` → `AssistantMessage`, `getText()` → 답변 문자열

---

## ChatClient

### ChatModel vs ChatClient

- ChatModel은 **질문 하나에 답 하나**에 적합 → 복잡한 대화 흐름은 다루기 어려움
- 보통은 **ChatClient** 사용

| 항목 | ChatModel | ChatClient |
|---|---|---|
| 계층 | 저수준(Low-level) | 고수준(High-level) |
| 역할 | LLM API와 직접 통신 | ChatModel을 감싼 사용자 친화 인터페이스 |
| 책임 | 토큰화·포맷팅, API 호출, 응답 파싱 | 프롬프트 템플릿, Advisors(RAG·메모리), 재시도 로직 |
| 추상화 수준 | 벤더 레벨(OpenAI, Ollama 등) | 비즈니스 로직 레벨 |
| 설정 | 비교적 복잡(메시지 직접 구성) | 간단(선언적 API) |

```text
Service 빈 ──▶ ChatClient ──▶ ChatModel ◀── application.properties
```

### ChatClient 구성 — Builder 주입

- 일반 Spring 빈은 stateless(singleton)지만, ChatClient는 **서비스마다 다른 설정**을 품게 됨
  - 번역 서비스: defaultSystem "당신은 전문 번역가입니다"
  - 요약 서비스: defaultSystem "당신은 요약 전문가입니다" + 특정 Advisor
- 그래서 **Service 생성자에서 `ChatClient.Builder`를 주입받아 build** 하는 방식을 추천
  - LLM을 하나만 쓰면 `ChatClient.Builder` 주입으로 충분
  - 공용 빌더를 **`clone()`** 해서 내 설정만 덮어씀 → 다른 서비스에 영향 없음
- 기본 설정 메서드: `defaultSystem`, `defaultUser`, `defaultOptions`, `defaultAdvisors`, `defaultTools` 등

```java
@Service
public class TranslationService {
    private final ChatClient chatClient;

    public TranslationService(ChatClient.Builder builder) {
        // 공용 빌더를 복제해서 이 서비스만의 설정 적용
        this.chatClient = builder.clone()
                .defaultSystem("당신은 전문 번역가입니다.")
                .defaultOptions(ChatOptions.builder()
                        .model("gpt-4o-mini")
                        .temperature(0.7)
                        .maxTokens(1000)
                        .build())
                .defaultAdvisors(SimpleLoggerAdvisor.builder()
                        .order(Ordered.LOWEST_PRECEDENCE - 1)
                        .build())
                .build();
    }
}
```

### ChatClient 구성 — 모델 직접 지정 (TODO: 03)

- 모델을 골라야 하면 **`ChatClient.builder(model)`**
- 호출 시점의 `.system()`, `.options()`는 `defaultXXX`를 **재정의**

```java
@Service
public class SimpleChatService {
    private final ChatClient openAiClient;
    private final ChatClient geminiClient;

    public SimpleChatService(OpenAiChatModel openAi, GoogleGenAiChatModel gemini) {
        this.openAiClient = ChatClient.builder(openAi).defaultSystem("당신은 전문 번역가입니다.").build();
        this.geminiClient = ChatClient.builder(gemini).defaultSystem("당신은 요약 전문가입니다.").build();
    }

    public Map<String, String> handleDocument(String doc) {
        String result1 = openAiClient.prompt(doc + "를 처리해줘.")
                .options(ChatOptions.builder().temperature(0.1).build())   // 보수적
                .call().content();

        String result2 = geminiClient.prompt(doc + "를 처리해줘.")
                .system("당신은 동요 작사 전문가입니다.")                   // defaultSystem 재정의
                .options(ChatOptions.builder().temperature(2.0).build())   // 창의적
                .call().content();

        return Map.of("openai", result1, "gemini", result2);
    }
}
```

| 단계 | 메서드 | 의미 |
|---|---|---|
| 요청 시작 | `prompt()` / `prompt(String)` | 요청 스펙 시작(문자열이면 user 메시지) |
| 요청 구성 | `.system()` `.user()` `.options()` `.advisors()` | 이번 호출만의 설정 |
| 실행 | `.call()` / `.stream()` | 동기 / 스트리밍 |
| 응답 꺼내기 | `.content()` 등 | 아래 Structured Output 참고 |

---

## Message와 Prompt

### Spring AI Message API

- Message = 역할별 메시지(System·User·Assistant·Tool)의 공통 타입
- 생각해 볼 점: **SystemMessage와 UserMessage가 서로 상충**하면 모델은 어떻게 할까?
  - 예: System "JSON으로만 답해" ↔ User "자유롭게 길게 써줘" → 결과를 직접 확인해 볼 것

### Prompt 작성 및 적용

- ChatClient에 **기본 Prompt**를 넣어 두고, 대화할 때 필요하면 **재정의**
- `defaultUser`·`defaultSystem` 오버로드 — 모두 `Builder` 반환

```java
Builder defaultUser(String text);
Builder defaultUser(Resource text, Charset charset);
Builder defaultUser(Resource text);
Builder defaultUser(Consumer<PromptUserSpec> userSpecConsumer);

Builder defaultSystem(String text);
Builder defaultSystem(Resource text, Charset charset);
Builder defaultSystem(Resource text);
Builder defaultSystem(Consumer<PromptSystemSpec> systemSpecConsumer);
```

- **PromptTemplate** 활용 — `Consumer` 버전에서 `text()` + `param()/params()`로 `{변수}` 채움

```java
// systemPrompt = "Tell me a {adjective} joke about {content}."
.defaultSystem(spec -> spec.text(systemPrompt)
        .params(Map.of("adjective", "funny", "content", "java")))
```

### SystemPrompt와 UserPrompt 활용 예 (TODO: 04)

- 시스템 프롬프트를 **파일(Resource)** 로 빼고 `@Value("classpath:...")`로 주입
- 기본 system에는 `location`, 호출 시 system에는 `language`, user에는 `topic`을 채움

```java
@Service
public class PromptChatService {
    private final ChatClient openAiClient;
    private final Resource systemPrompt;

    public PromptChatService(OpenAiChatModel openAi,
            @Value("classpath:/prompts/default_penpal_prompt.txt") Resource systemPrompt) {
        this.systemPrompt = systemPrompt;
        this.openAiClient = ChatClient.builder(openAi)
                .defaultSystem(spec -> spec.text(systemPrompt).param("location", "제주"))
                .build();
    }

    public String chat(String language, String topic) {
        String user = "상대방과의 관계: {topic}에게 보낼 따뜻한 안부 편지를 5줄 내외로 작성해줘.";
        return openAiClient.prompt()
                .system(spec -> spec.text(systemPrompt).param("language", language))
                .user(spec -> spec.text(user).param("topic", topic))
                .call().content();
    }
}
```

- 프롬프트가 **정말 의도대로 들어가는지** 궁금 → 로그로 확인 (→ [Advisor API](05_Advisor_API.md)의 SimpleLoggerAdvisor)

### ChatClient.Builder 빈 구성

- 모델별 `ChatClient.Builder`를 **빈으로 등록** → 공통 system 프롬프트·로깅 advisor를 한 번에 적용

```java
@Configuration
public class ChatClientConfig {

    @Value("classpath:/prompts/default_system_prompt.txt")
    private Resource systemPrompt;

    @Bean
    SimpleLoggerAdvisor simpleLoggerAdvisor() {
        return new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1);
    }

    @Bean
    ChatClient.Builder geminiChatClientBuilder(GoogleGenAiChatModel genAiChatModel, SimpleLoggerAdvisor sLogAdvisor) {
        return ChatClient.builder(genAiChatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(sLogAdvisor);
    }

    @Bean
    ChatClient.Builder openAiChatClientBuilder(OpenAiChatModel openAiChatModel, SimpleLoggerAdvisor sLogAdvisor) {
        return ChatClient.builder(openAiChatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(sLogAdvisor);
    }
}
```

---

## Structured Output

### 응답의 형태

- `call()` 뒤에 붙는 메서드로 **응답 형태**를 고름

| 메서드 | 반환 |
|---|---|
| `String content()` | 단순 문자열 |
| `ChatResponse chatResponse()` | content + 응답 **metadata**(토큰 정보 등) |
| `ChatClientResponse chatClientResponse()` | ChatResponse + ChatClient **실행 컨텍스트**(RAG를 위해 추가된 내용) |
| `T entity(Class<T> type)` 등 | T 타입 **자바 객체**(변환 가능한 타입 지정 필요) |

- 토큰 정보는 매번 꺼내기보다 **Advisor**로 처리해 보자 (→ [Advisor API](05_Advisor_API.md))

### Structured Output API

- 데이터를 **JSON 같은 구조화된 형식**으로 출력하게 하는 것
- **FormatProvider**: LLM 출력이 T로 바뀔 수 있도록 **출력 형식 지침** 제공
- **Converter**: 지침대로 나온 출력(Raw Output)을 **T 객체로 변환**

```text
사용자 입력 + 출력 포맷 가이드(FormatProvider) ──▶ LLM ──▶ 형식 지침대로 나온 Raw Output ──Converter──▶ T
```

### 사용 방법

- Spring AI는 여러 converter 제공(Bean·Map·List 등)

| 구분 | 방법 |
|---|---|
| **저수준** | 변환기(`BeanOutputConverter`)를 **직접 생성** → 형식 지침 넣기 → 변환 |
| **고수준** | ChatClient의 **`entity()`** 메서드 |

### T 형태로 반환 (TODO: 05)

```java
public record Joke(String content, String reason) { }

// 저수준: BeanOutputConverter 직접 사용
public Joke generateRecordJoke1(String topic) {
    String user = "{topic} 과 관련된 농담과 해설을 하나만 만들어줘. \n {format}";
    BeanOutputConverter<Joke> converter = new BeanOutputConverter<>(Joke.class);

    String response = genAiClient.prompt()
            .user(spec -> spec.text(user)
                    .param("topic", topic)
                    .param("format", converter.getFormat()))   // 포맷 지시어 주입
            .call().content();
    return converter.convert(response);                         // 수동 변환
}

// 고수준: entity()
public Joke generateRecordJoke2(String topic) {
    String user = "{topic} 과 관련된 농담과 해설을 하나만 만들어줘.";
    return genAiClient.prompt()
            .user(spec -> spec.text(user).param("topic", topic))
            .call().entity(Joke.class);
}
```

- 사실 `entity()`도 내부에서 **같은 일**(포맷 지시어를 프롬프트에 붙이고 → 응답을 변환)을 해 줌
  - `getFormat()`이 돌려주는 것 = "이 JSON 스키마에 맞춰 답하라"는 **지시 문장**

---

## 참고

- Spring AI 의존성은 버전을 starter마다 적지 않고 **BOM**으로 맞추는 것이 일반적

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>${spring-ai.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

- 교재의 `ssafy.gms.api-key=YOUR_KEY_HERE` 자리에 실제 키를 적어 커밋하지 않도록 노트에서는 `${GMS_API_KEY}`(환경변수)로 바꿔 적음
- 교재 PromptTemplate 예의 `params(Map.of("adjactive", ...))`는 키 오타 → 템플릿의 `{adjective}`와 키가 달라 값이 채워지지 않으므로 노트에서는 `adjective`로 고침
- `record`는 Java 16+ 문법 — 교재 프로젝트는 Java 21이라 문제없음
- `prompt(String)`은 그 문자열을 **user 메시지**로 씀 → `prompt().user(String)`과 같은 효과
