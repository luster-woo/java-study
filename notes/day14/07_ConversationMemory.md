# ConversationMemory

> 교재: 15기_자바전공_06_ai_2_0611 (전공) — 2. ConversationMemory · 선택 강의

> [← Embedding과 VectorStore](06_Embedding과_VectorStore.md) · [day14 목록](../README.md) · [RAG ETL Pipeline →](08_RAG_ETL_Pipeline.md)

---

## ChatMemory

### 대화 맥락 유지의 필요성 (TODO: 10)

- 같은 서비스로 질문 3개를 연달아 보냄: 이름 알려 주기 → 그 이름으로 3행시 → 이름이 같은 유명인
- 서비스 메서드는 `user(question)`만 보내는 단순 호출

```java
public String simpleGeneration(String question) {
    return chatClient.prompt()
            .user(question)
            .call()
            .content();
}
```

```java
@Test
public void messageChatMemoryAdvisorTest() throws Exception {
    Object answer1 = service.simpleGeneration("내 이름은 홍길동이야.");
    Object answer2 = service.simpleGeneration("내 이름으로 3행시 지어줘.");
    Object answer3 = service.simpleGeneration("나랑 이름이 같은 사람 중 유명인 1사람만 이야기해줘.");
    log.debug("answer1: {}", answer1);
    log.debug("answer2: {}", answer2);
    log.debug("answer3: {}", answer3);
}
```

| 질문 | 응답 요약 |
|---|---|
| 내 이름은 홍길동이야. | 반갑게 인사함(이름 인식) |
| 내 이름으로 3행시 지어줘. | "어떤 이름으로 지어 드릴까요?" → **이름을 모름** |
| 이름이 같은 유명인 1사람 | "어떤 분야의 유명인을 찾으세요?" → **이름을 모름** |

- 두 번째 질문부터 앞의 대화를 전혀 기억하지 못함 → 대화 기억 장치가 필요

### 대화 기억

- **LLM은 기본적으로 상태를 저장하지 않음(stateless)** → 이전 대화를 기억하거나 그에 기반한 응답 생성 불가
- Spring AI의 대화 기억 두 가지

| 구분 | 범위 | 내용 |
|---|---|---|
| **Chat Memory** | 현재 세션 | LLM과 대화할 때 맥락 유지용 메시지 모음(UserMessage + AssistantMessage) |
| **Chat History** | 현재 + 과거 세션 | 주고받은 **모든** 메시지 |

### ChatMemoryRepository

- 채팅 대화 기록을 관리하고 대화 목록을 유지하는 **저장소** 객체
  - **저장**: 새 사용자 질문과 AI 답변을 **Conversation ID별로** 묶어 저장
  - **조회**: 다음 질문이 오면 그 사용자의 과거 대화 내역 조회
  - **관리**: 오래된 대화 삭제, 특정 대화 세션 초기화

```text
사용자 질문("삼행시!")
   │
   ▼
MessageChatMemoryAdvisor ──최근 내역 요청──▶ ChatMemory ──▶ ChatMemoryRepository(대화 기록)
                                                              ├─ InMemoryChatMemoryRepository
                                                              └─ JdbcChatMemoryRepository
```

### ChatMemory

- 대화 기록을 관리하는 **인터페이스**
- 자동 환경 설정된 빈을 **바로 주입**받아 사용 가능
  - 기본: conversationId별로 **메시지 20개** 기억
  - 필요하면 속성(최대 메시지 수 등) 재정의

```java
public interface ChatMemory {
    String CONVERSATION_ID = "chat_memory_conversation_id"; // context에서 사용자의 conversation id를 꺼내는 키

    void add(String conversationId, List<Message> messages); // 사용자 id와 함께 대화 내용 저장
    List<Message> get(String conversationId);                // 사용자 id로 저장된 대화 기억 검색
    void clear(String conversationId);                       // 사용자 id로 저장된 대화 기억 삭제
}
```

- 기본 구현체: **MessageWindowChatMemory** — 최근 N개 메시지만 유지하는 "창(window)"

```java
public final class MessageWindowChatMemory implements ChatMemory {
    private static final int DEFAULT_MAX_MESSAGES = 20;
    private final ChatMemoryRepository chatMemoryRepository;
    private final int maxMessages;
    // ...
}
```

- 활용

```java
@Autowired          // 기본 설정으로 구성된 ChatMemory 주입
ChatMemory chatMemory;
```

```java
@Bean               // 최대 메시지 개수 변경
ChatMemory chatMemory() {
    return MessageWindowChatMemory.builder().maxMessages(20).build();
}
```

---

## ChatMemoryAdvisor

### 맥락 유지를 위한 Advisor 3종

- ChatMemory(또는 VectorStore)에 저장된 대화 기록을 **prompt에 넣어 주는** Advisor

| 구분 | MessageChatMemoryAdvisor | PromptChatMemoryAdvisor | VectorStoreChatMemoryAdvisor |
|---|---|---|---|
| 메모리 저장소 | ChatMemory(DB/메모리) | ChatMemory(DB/메모리) | VectorStore(벡터 DB) |
| 검색 방식 | 시간순(최근 N개) | 시간순(최근 N개) | **유사도 기반 검색** |
| 포함 방식 | **메시지 모음**으로 프롬프트에 포함 | **시스템 프롬프트에 텍스트**로 포함 | 시스템 프롬프트에 텍스트로 포함 |
| 토큰 사용량 | 구조적 정보(메시지 형식)까지 포함 → 증가 | 중간 | 낮음~중간(관련 내용만) |
| 정확도 | 높음(전체 맥락) | 중간 | 높음(관련 맥락) |
| 성능 | 빠름 | 빠름 | 느림(벡터 검색) |
| 특징 | **일반적으로 사용 권장** | 토큰 절약 등 특별한 경우 | 대화량이 늘어도 성능 유지(우수) |

---

## JdbcChatMemoryRepository

### 왜 DB에 저장하나 (TODO: 10)

- 기본 저장소(InMemory)는 메모리 → 사용자가 많거나 시스템이 불안정하면 기록 유실·부담
- **JDBC로 DB를 저장소로 쓰는** ChatMemoryRepository

### 의존성

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-chat-memory-repository-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### application.properties

```properties
# table이 없을 때 초기화 여부: 개발 중엔 always, 운영에선 never
spring.ai.chat.memory.repository.jdbc.initialize-schema=always
# schema 파일 위치
spring.ai.chat.memory.repository.jdbc.schema=classpath:scripts/chatmemoryrepo-mysql.sql

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/ssafylive?serverTimezone=UTC
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

### table schema

- 테이블만 있으면 **CRUD는 기본 제공**

```sql
CREATE TABLE SPRING_AI_CHAT_MEMORY (
    conversation_id VARCHAR(255) NOT NULL,
    type            VARCHAR(50)  NOT NULL,   -- USER / ASSISTANT 등 메시지 종류
    content         TEXT         NOT NULL,
    metadata        JSON,
    timestamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_conversation_id (conversation_id)
);
```

### 빈 구성

- `spring-ai-starter-model-chat-memory-repository-jdbc`를 추가하면 **ChatMemoryRepository가 자동 구성**됨
- 그 Repository로 ChatMemory를 직접 구성 — id당 **10개만 유지**

```java
@Bean
ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
    return MessageWindowChatMemory.builder()
            .chatMemoryRepository(jdbcChatMemoryRepository)
            .maxMessages(10)
            .build();
}
```

### ChatMemory와 MessageChatMemoryAdvisor (TODO: 10)

- 요청마다 `conversationId`를 넘겨 **사용자별로 대화를 분리**

```java
// ChatClientService.java
private final ChatMemory chatMemory;

public String memoryGeneration(String userInput, String conversationId) {
    return chatClient.prompt()
            .user(userInput)
            .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                    .conversationId(conversationId)
                    .build())
            .call()
            .content();
}
```

- Advisor 실행 순서(order 값이 **작을수록 먼저**)

```text
Integer.MIN_VALUE                                                   Integer.MAX_VALUE
(Ordered.HIGHEST_PRECEDENCE)                                     (Ordered.LOWEST_PRECEDENCE)
   │──────────────────────────────────────────────────────────────────────│
          ▲                                              ▲
   MessageChatMemoryAdvisor                       SimpleLoggerAdvisor
   기본 order = HIGHEST_PRECEDENCE + 1000
```

- MessageChatMemoryAdvisor가 앞에서 기억을 붙인 뒤 → 뒤쪽 SimpleLoggerAdvisor가 **기억이 합쳐진 프롬프트**를 로그로 남김

---

## VectorStoreChatMemoryAdvisor 활용

### 특징 (TODO: 11)

- 대화 기억을 **VectorStore에 저장**
- 현재 대화와 **유사한 이전 대화 기록**을 검색해서 활용
- 대화량이 많을 때 **현재 대화와 관련된 기억만** 골라 쓰기 좋음
- 단, 다른 방식보다 **느릴 수 있음**

### 전용 VectorStore 구성

- Redis에는 table 개념이 없음 → 대화 기록용 **인덱스·prefix를 따로** 둔 VectorStore 빈을 하나 더 만듦
- 검색에 필요한 `conversationId`를 tag 필드로 등록

```java
@Bean
RedisVectorStore chatHistoryVectorStore(OpenAiEmbeddingModel eModel, JedisPooled jedisPooled) {
    return RedisVectorStore.builder(jedisPooled, eModel)
            .indexName("chat-history")                       // Chat 전용 인덱스
            .prefix("chat:")
            .metadataFields(
                    MetadataField.tag("conversationId"),     // 검색에 필요한 ID
                    MetadataField.text("timestamp"))
            .initializeSchema(true)
            .build();
}
```

### 서비스 메서드 (TODO: 12)

```java
public String historyGeneration(String userInput, String conversationId) {
    return chatClient.prompt()
            .user(userInput)                                     // user 메시지 전달
            .advisors(VectorStoreChatMemoryAdvisor.builder(historyVectorStore)
                    .conversationId(conversationId)
                    .build())
            .call()                                              // model 호출
            .content();
}
```

### MessageChatMemoryAdvisor vs VectorStoreChatMemoryAdvisor 동작 차이

| | MessageChatMemoryAdvisor | VectorStoreChatMemoryAdvisor |
|---|---|---|
| 꺼내는 기억 | 그 conversationId의 **최근 N개** 메시지 | 현재 질문과 **유사한** 과거 대화 |
| 오래된 대화 | window 밖으로 밀려나면 사라짐 | 오래돼도 관련 있으면 다시 검색됨 |
| 프롬프트에 넣는 방식 | 메시지 목록 그대로 | 시스템 프롬프트에 텍스트로 |

---

## 참고

- Advisor 3종 비교표는 교재 표의 칸 배치를 텍스트로 옮기며 재구성한 것 — 행·칸 짝이 애매한 곳(토큰 사용량·정확도·특징)은 교재 화면으로 한 번 더 확인
- 교재 오류 정정: 테스트는 `service.simpleChat(...)`을 부르는데 서비스 메서드 이름은 `simpleGeneration` → 위에서는 `simpleGeneration`으로 통일
- 교재 예제의 DB 계정은 로컬 실습용 값 — 노트에서는 `${DB_USERNAME}`, `${DB_PASSWORD}` 자리표시자로 바꿈
- MessageWindowChatMemory의 "최근 N개 유지" 동작을 순수 자바로 흉내(대화 id별 분리, 최대 4개)

```java
import java.util.*;

public class Window {
    static class WindowMemory {
        private final Map<String, Deque<String>> store = new HashMap<>();
        private final int maxMessages;
        WindowMemory(int maxMessages) { this.maxMessages = maxMessages; }

        void add(String conversationId, String message) {
            Deque<String> q = store.computeIfAbsent(conversationId, k -> new ArrayDeque<>());
            q.addLast(message);
            while (q.size() > maxMessages) q.removeFirst();   // 오래된 것부터 버림
        }
        List<String> get(String conversationId) {
            return new ArrayList<>(store.getOrDefault(conversationId, new ArrayDeque<>()));
        }
        void clear(String conversationId) { store.remove(conversationId); }
    }

    public static void main(String[] args) {
        WindowMemory memory = new WindowMemory(4);
        memory.add("user1", "U: 내 이름은 홍길동이야.");
        memory.add("user1", "A: 반가워요, 홍길동 님!");
        memory.add("user1", "U: 내 이름으로 3행시 지어줘.");
        memory.add("user1", "A: 홍: ... 길: ... 동: ...");
        memory.add("user1", "U: 유명인 1사람만 이야기해줘.");
        memory.add("user2", "U: 안녕?");
        System.out.println(memory.get("user1"));
        System.out.println(memory.get("user2"));
        memory.clear("user1");
        System.out.println(memory.get("user1"));
    }
}
// 결과
// [A: 반가워요, 홍길동 님!, U: 내 이름으로 3행시 지어줘., A: 홍: ... 길: ... 동: ..., U: 유명인 1사람만 이야기해줘.]
// [U: 안녕?]
// []
```

- 위 예처럼 window가 작으면 **첫 메시지(이름)가 밀려나** 다시 이름을 잊을 수 있음 → maxMessages는 대화 길이에 맞게 정함
