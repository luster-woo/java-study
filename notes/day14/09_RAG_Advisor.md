# RAG Advisor

> 교재: 15기_자바전공_06_ai_2_0611 (전공) — 4. RAG - Advisor · 선택 강의

> [← RAG ETL Pipeline](08_RAG_ETL_Pipeline.md) · [day14 목록](../README.md) · [ChatBot →](10_ChatBot.md)

---

## RAG 진행 절차

- 앞 강의에서 ETL(Extract → Transform → Load)로 **문서를 VectorStore에 적재**함([RAG 진행 절차](08_RAG_ETL_Pipeline.md#rag-진행-절차))
- 이번 강의는 그 뒤, **질문이 들어왔을 때 검색해서 보강하는 단계**를 Advisor로 처리

---

## RAG와 QuestionAnswerAdvisor

### Spring AI의 RAG (TODO: 13)

- RAG를 위한 **모듈화된 아키텍처** 제공
  - Advisor로 기본 RAG 처리 모듈 제공 → **QuestionAnswerAdvisor**
  - 사용자 정의 RAG 처리 모듈도 추가 가능

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-advisors-vector-store</artifactId>
</dependency>
```

### QuestionAnswerAdvisor 동작

- 사용자 질문이 model에 도달하면 → QuestionAnswerAdvisor가 **VectorStore 검색**
- VectorStore의 응답을 **사용자 텍스트에 추가** → model이 응답을 만들 **컨텍스트** 제공

```text
요청(user 질문)
   │
   ▼
QuestionAnswerAdvisor ──retrieve──▶ VectorStore      (기본: 저장된 모든 데이터를 대상으로 검색)
   │  ◀──검색된 Document──┘
   │ Augment: 질문 + 검색 결과로 prompt 구성
   ▼
모델 ── Generation ──▶ 응답
```

- 기본 속성만으로 쓰기

```java
String response = ChatClient.builder(chatModel).build().prompt()
        .advisors(new QuestionAnswerAdvisor(vectorStore))
        .user(userText)
        .call()
        .content();
```

### 기본 PromptTemplate

- `{query}` 자리에 사용자 질문, `{question_answer_context}` 자리에 **검색해 온 context** → 결국 이 prompt가 모델로 감
- "context에 답이 없으면 답할 수 없다고 알려라"가 들어 있음 → 모델이 **사전 지식으로 지어내지 않게** 함

```text
{query}

Context information is below, surrounded by ---------------------

---------------------
{question_answer_context}
---------------------

Given the context and provided history information and not prior knowledge,
reply to the user comment. If the answer is not in the context, inform
the user that you can't answer the question.
```

### QuestionAnswerAdvisor 구조

- `BaseAdvisor` 구현

```java
public class QuestionAnswerAdvisor implements BaseAdvisor {
    private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate("""
            {query}
            ... (위 기본 템플릿)
            """);

    private final VectorStore vectorStore;       // vector store: 필수
    private final PromptTemplate promptTemplate; // prompt template 구성
    private final SearchRequest searchRequest;   // 검색 조건
    private final Scheduler scheduler;           // 비동기 방식에서 사용
    private final int order;                     // advisor 적용 순위

    public static Builder builder(VectorStore vectorStore) {
        return new Builder(vectorStore);
    }
}
```

| 필드 | 의미 |
|---|---|
| `vectorStore` | 검색 대상 저장소(**필수** → builder 인자) |
| `promptTemplate` | context를 끼워 넣을 템플릿(바꿀 수 있음) |
| `searchRequest` | topK·similarityThreshold·filter 등 [검색 조건](06_Embedding과_VectorStore.md#searchrequest) |
| `scheduler` | 비동기(stream) 방식에서 사용 |
| `order` | Advisor 적용 순위 |

### QuestionAnswerAdvisor 활용 (TODO: 13)

- RAG용 VectorStore는 `@Qualifier("customEmbeddingVector")`로 골라 주입(대화 기록용 VectorStore와 구분)
- 요청마다 **SearchRequest를 받아** QuestionAnswerAdvisor를 만들고, **VectorStoreChatMemoryAdvisor**와 함께 등록 → 문서 검색 + 대화 기억

```java
@Autowired(required = false)
public void setRagVectorStore(@Qualifier("customEmbeddingVector") VectorStore eVectorStore) {
    this.eVectorStore = eVectorStore;
}

public String ragGeneration(String userInput, SearchRequest searchRequest, String conversationId) {
    var qaAdvisor = QuestionAnswerAdvisor.builder(eVectorStore)
            .searchRequest(searchRequest)
            .build();
    return chatClient.prompt()
            .user(userInput)
            .advisors(qaAdvisor,
                      VectorStoreChatMemoryAdvisor.builder(hVectorStore)
                              .conversationId(conversationId)
                              .build())
            .call()
            .content();
}
```

| VectorStore | 담긴 것 | 쓰는 Advisor |
|---|---|---|
| `eVectorStore`(customEmbeddingVector) | ETL로 적재한 **문서 chunk** | QuestionAnswerAdvisor |
| `hVectorStore`(chat-history) | **대화 기록** | VectorStoreChatMemoryAdvisor([ConversationMemory](07_ConversationMemory.md#vectorstorechatmemoryadvisor-활용)) |

- 테스트: SSAFY 생활 규정 문서(category `SSAFY_LIFE`)만 대상으로 검색

```java
@Test
void ssafyLifeTest() {
    String userInput = "임의 결석을 4회 했을 때 수강생의 운명은 어떻게 될까?";
    var searchRequest = SearchRequest.builder()
            .query(userInput)
            .topK(3)
            .filterExpression("category == 'SSAFY_LIFE'")
            .similarityThreshold(0.5)
            .build();
    String result1 = service.ragGeneration(userInput, searchRequest, "default_user_id");
    log.debug("과연 그는: {}", result1);
}
```

- 모델이 원래 모르는 SSAFY 규정을, 적재해 둔 문서에서 찾아 근거로 답함 → 문서에 없으면 "답할 수 없다"고 응답(기본 템플릿)

---

## 참고

- 교재 오류 정정: `.filterExpression("category=='SSAFY_LIFE’”)`는 따옴표가 둥근 따옴표(`’`, `”`)로 바뀌어 컴파일 오류 → `"category == 'SSAFY_LIFE'"`로 씀
- 기본 템플릿의 구분선(`----`)은 교재 텍스트에서 줄바꿈이 깨져 있어 줄을 나눠 복원함 — 문구는 그대로
- PromptTemplate의 `"""` 텍스트 블록은 **Java 15+** 문법(Spring Boot 3.x의 Java 17에서 사용 가능)
- 이 노트의 코드는 OpenAI 키·Redis·문서 적재가 필요해 실행하지 않음
