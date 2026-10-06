# Advisor API

> 교재: 15기_자바전공_05_ai_1_0610 (전공) — 5. Advisor API · 선택 강의

> [← Multimodal (2)](04_Multimodal_2.md) · [day14 목록](../README.md) · [Embedding과 VectorStore →](06_Embedding과_VectorStore.md)

---

## Advisor API

### 개요

- Spring 애플리케이션의 AI 상호작용을 **가로채고·수정하고·강화**하는 방법
  - 반복되는 **전/후 처리 로직을 캡슐화** → 재사용 가능한 구조
  - 여러 advisor가 **체인**을 이뤄 순서대로 동작
  - 어디서 들어본 이름 → AOP의 advice, Servlet Filter 체인과 같은 발상

| 시점 | 하는 일 |
|---|---|
| **전처리** | 프롬프트에 컨텍스트(대화 이력, 로컬 데이터 등) 추가 |
| **후처리** | LLM 응답 검사, 요구사항에 맞춘 형 변환, 대화 맥락 유지를 위한 대화 내용 저장 |

- 빌드 시 `defaultAdvisors(...)`로 등록하고, 호출 시 `advisors(...)`로 **런타임 파라미터**를 바꿀 수 있음

```java
ChatClient chatClient = ChatClient.builder(chatModel)
        .defaultAdvisors(
                new MessageChatMemoryAdvisor(chatMemory),                     // chat-memory advisor
                new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()) // RAG advisor
        ).build();

String response = chatClient.prompt()
        .advisors(advisor -> advisor.param("chat_memory_conversation_id", "678")  // 런타임에 파라미터 수정
                .param("chat_memory_response_size", 100))
        .user(userText)
        .call().content();
```

### 핵심 컴포넌트 — 요청/응답 상태 공유

- **`ChatClientRequest`, `ChatClientResponse`**
  - 각각 프롬프트 요청과 응답을 다루는 **record**
  - AdvisorChain 전체가 공유하는 **context**(요청/응답 상태)를 가짐
- advisor 설정 때 `param`으로 공유 context에 값을 넣을 수 있음

```java
chatClient.prompt()
        .system("주어진 질문에 한글로 친절히 답해줘.")
        .user(user -> user.text("넌센스 퀴즈 5개 알려줘."))
        .advisors(spec -> spec.param("some", "context"))   // 공유 context에 설정
        .call();
```

### 핵심 컴포넌트 — 순서와 종류

- **Ordered**
  - `getOrder()` 값이 **낮을수록 먼저** 실행 (`Ordered.HIGHEST_PRECEDENCE = Integer.MIN_VALUE`)
  - **같은 우선순위**끼리는 실행 순서를 보장하지 않음
  - `Ordered.LOWEST_PRECEDENCE`는 동작하지 않음 → 맨 뒤에 두고 싶으면 `LOWEST_PRECEDENCE - 1`

| 인터페이스 | 지원 API |
|---|---|
| **CallAdvisor** | 비 스트리밍 (`call()`) |
| **StreamAdvisor** | 스트리밍 (`stream()`) |
| **BaseAdvisor** | 둘 다 구현 + `before()` / `after()` 템플릿 제공 |

- 수업에서는 **비스트리밍** API 형태 사용

### 동작 절차

```text
Prompt
  └▶ ChatClientRequest (비어 있는 context: Map<String, Object> 생성)
       └▶ CallAdvisor order 1   ─ 요청 수정 가능
            └▶ CallAdvisor order 2   ─ 요청 수정 가능
                 └▶ CallAdvisor (Internal) ── Prompt ──▶ ChatModel
                 ◀─ ChatResponse ───────────────────────────┘
            ◀─ ChatClientResponse (응답 수정 가능)
       ◀─ ChatClientResponse
  ◀─ context에서 ChatCompletion 추출 ─▶ ChatResponse
```

- **order가 낮은 것이 우선순위가 높음** → 요청은 order 오름차순으로 지나가고, 응답은 **역순**으로 돌아옴
- 체인 맨 끝의 **내부(Internal) CallAdvisor**가 실제로 ChatModel을 호출

---

## SimpleLoggerAdvisor

### 요청·응답 로깅

- ChatClient의 **요청과 응답 데이터를 기록**하는 advisor → AI 모델과의 상호작용 디버깅·모니터링
- `nextCall()` 앞에서 요청, 뒤에서 응답을 찍음

```java
public class SimpleLoggerAdvisor implements CallAdvisor, StreamAdvisor {

    public SimpleLoggerAdvisor(int order) {
        this(DEFAULT_REQUEST_TO_STRING, DEFAULT_RESPONSE_TO_STRING, order);
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        logRequest(chatClientRequest);                                                // 다음 체인 호출 전 로깅
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest); // 다음 체인 호출
        logResponse(chatClientResponse);                                              // 다음 체인 호출 후 로깅
        return chatClientResponse;
    }

    private void logRequest(ChatClientRequest request) {
        logger.debug("request: {}", this.requestToString.apply(request));
    }

    private void logResponse(ChatClientResponse chatClientResponse) {
        logger.debug("response: {}", this.responseToString.apply(chatClientResponse.chatResponse()));
    }
}
```

### Advisor 적용

- **ChatClient 생성 시** `defaultAdvisors`로 적용 → 모든 호출에 적용
  - 다른 advisor가 강화한 **최종 요청**을 보고 싶으니 로거는 **나중에** 동작하게(`LOWEST_PRECEDENCE - 1`)

```java
@Bean
ChatClient chatClient(ChatClient.Builder builder) {
    return builder.defaultSystem("...")
            .defaultAdvisors(simpleLoggerAdvisor)
            .build();
}
```

- **Prompt 생성 시** 개별 적용 → 그 호출에만 적용

```java
@GetMapping("/quiz")
public Optional<Quiz> quiz(@RequestParam String category) {
    return chatClient.prompt()
            // (교재 예제는 user 메시지 부분 생략)
            .advisors(simpleLoggerAdvisor)
            .call()
            .entity(new ParameterizedTypeReference<Optional<Quiz>>() {});
}
```

- 제네릭 타입(`Optional<Quiz>`, `List<...>`)은 `Class`로 표현할 수 없어 **`ParameterizedTypeReference`** 사용
- 로그가 보이려면 **logging level** 설정 필요 (DEBUG로 찍기 때문)

```properties
logging.level.org.springframework.ai.chat.client.advisor=debug
```

---

## 사용자 정의 Advisor 작성

### ReReadingAdvisor (TODO: 07)

- **Re2(재 읽기) 기법**: 질문을 한 번 더 읽게 해서 LLM의 **추론 능력을 높임**
  - 공식 문서: `https://docs.spring.io/spring-ai/reference/api/advisors.html#_re_reading_re2_advisor`
- 조건: 질문에 **"복잡한"** 이 들어가거나 **1000글자**를 넘으면 다시 읽게 함
- **BaseAdvisor** 구현 → `before()`에서 요청 수정, `after()`는 그대로 반환

```java
public class ReReadingAdvisor implements BaseAdvisor {

    private static final String DEFAULT_RE2_ADVISE_TEMPLATE =
            "{re2_input_query}Read the question again:{re2_input_query}";

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String message = chatClientRequest.prompt().getUserMessage().getText();
        if (message.length() > 1_000 || message.contains("복잡한")) {
            String augmentedUserText = PromptTemplate.builder()
                    .template(DEFAULT_RE2_ADVISE_TEMPLATE)
                    .variables(Map.of("re2_input_query", message))
                    .build()
                    .render();
            // 요청은 불변 → mutate()로 user 메시지만 바꾼 새 요청 생성
            return chatClientRequest.mutate()
                    .prompt(chatClientRequest.prompt().augmentUserMessage(augmentedUserText))
                    .build();
        } else {
            return chatClientRequest;
        }
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
```

- 렌더링 결과: `질문Read the question again:질문` → 같은 질문이 두 번 들어감

---

## Spring AI Built-in Advisors

- 제대로 써 보는 것은 다음 강의들([ConversationMemory](07_ConversationMemory.md) 등)에서

| 분류 | Advisor | 하는 일 |
|---|---|---|
| **Chat Memory** — ChatMemory에 대화를 저장해 맥락 유지 | `MessageChatMemoryAdvisor` | **대화 이력 전달** — prompt와 completion을 관리해 대화 맥락·흐름 유지 |
| | `PromptChatMemoryAdvisor` | **프롬프트 조작** — user 메시지와 agent 메시지(completion)로 새 prompt 생성 |
| | `VectorStoreChatMemoryAdvisor` | PromptChatMemoryAdvisor 기능을 **VectorStore 기반**으로(대화를 저장 → 검색 후 사용) |
| **Question Answer** | `QuestionAnswerAdvisor` | VectorStore로 질문-답변 제공, **RAG 패턴** 구현 |
| **Content Safety** (TODO: 06) | `SafeGuardAdvisor` | 모델이 **유해·부적절한 콘텐츠**를 만들지 않게 막음 |

```java
public SafeGuardAdvisor(List<String> sensitiveWords, String failureResponse, int order)
```

- `sensitiveWords`(금칙어 목록)가 들어오면 모델을 호출하지 않고 `failureResponse`를 돌려줌

---

## 다음 수업 준비 — Redis

### Redis 설치·실행

- Docker Desktop 설치 → 실행
- `src/main/resources/docker/docker-compose.yml` 확인 후 실행

```yaml
version: "3.8"
services:
  redis-stack:
    image: redis/redis-stack:latest
    container_name: redis-vectorstore
    ports:
      - "6379:6379"   # Redis 기본 포트
      - "8001:8001"   # Redis Insight (GUI 접속용 포트)
    volumes:
      - redis_data:/data   # 데이터를 로컬에 보관 → 컨테이너 재시작 후에도 유지
    restart: always
volumes:
  redis_data:
```

```bash
docker-compose up -d
```

### Redis Client

- `http://localhost:8001/` (Redis Insight) → 좌측 하단 **`>_ CLI`** 로 명령창 열기

| 동작 | 명령 |
|---|---|
| 추가/수정 | `set [key] [value]`, `JSON.SET [key] $ [JSON 문자열]` |
| 조회 | `get [key]`, `JSON.GET [key] $` |
| 삭제 | `del [key]` |
| 전체 키 확인 | `keys *` |
| 전체 키 삭제 | `flushall` |
| 타입 확인 | `type [key]` |

---

## 참고

- 개요의 `new MessageChatMemoryAdvisor(chatMemory)`, `SearchRequest.defaults()` 예제는 Spring AI 1.0 이전 문서의 코드 → 교재 버전(1.1.x)에서는 **builder** 사용

```java
ChatClient chatClient = ChatClient.builder(chatModel)
        .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                QuestionAnswerAdvisor.builder(vectorStore).build())
        .build();

chatClient.prompt()
        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "678"))   // 키 상수 사용
        .user(userText)
        .call().content();
```

- 교재 ReReadingAdvisor 코드에는 `getOrder()`가 없지만 `BaseAdvisor`는 `Ordered`를 상속하므로 구현해야 컴파일됨 → 노트에 추가
- 교재는 `logging.level[org.springframework.ai.chat.client.advisor]=debug`처럼 대괄호 표기 — 점이 든 Map 키를 그대로 묶는 Spring Boot 표기라 위의 점 표기와 같은 뜻
- 교재 표기 `LOWEST_PERCEDENCE`는 `LOWEST_PRECEDENCE` 오타
- `LOWEST_PRECEDENCE - 1`을 쓰는 이유: 동작 절차 그림처럼 체인 맨 끝에서 ChatModel을 부르는 내부 advisor가 있으므로, 로거는 그 **바로 앞**에 둬야 다른 advisor가 바꾼 최종 요청과 원래 응답을 볼 수 있음
- 체인 순서를 순수 자바로 흉내 낸 결과(order 0인 ReReading, `MAX_VALUE - 1`인 Logger, 맨 끝 ChatModel 호출) — 요청은 order 순, 응답은 역순

```java
// 결과
// ReReading before: prompt
// Logger before: prompt +ReReading
// ChatModel 호출: prompt +ReReading +Logger
// Logger after : answer
// ReReading after : answer
```

- 교재에서 TODO: 06 번호가 Multimodal 객체 탐지와 SafeGuardAdvisor 두 곳에 붙어 있음 → 실습 프로젝트의 TODO 번호를 직접 확인
