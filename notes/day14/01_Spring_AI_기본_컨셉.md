# Spring AI 기본 컨셉

> 교재: 15기_자바전공_05_ai_1_0610 (전공) — 1. Spring AI 기본 컨셉 · 선택 강의

> [day14 목록](../README.md) · [Chat Client API →](02_Chat_Client_API.md)

---

## Spring AI 기본 컨셉

### Spring AI란

- Spring의 **DI·AOP·PSA·POJO·Auto-config**를 그대로 AI 쪽으로 확장한 프레임워크
- AI 기능만이 아니라 **보안·트랜잭션·관측성**을 기존 인프라와 붙이는 비용이 적음
- **추상화** 덕분에 LLM 공급자를 쉽게 바꿀 수 있음 → LLM 시장이 바뀌어도 유연하게 대응
- 애플리케이션에 AI를 쉽게 넣도록 만든 **모듈형** 프레임워크
  - 기업 데이터·API와 여러 AI 모델을 효율적으로 연결
  - OpenAI·Anthropic·Google·MS 등 여러 공급자를 아우르는 **포터블 API**
  - chat, text→image, 임베딩 모델 지원
  - **동기식**과 **스트리밍** API 모두 지원

```text
            비즈니스 앱 (Security 연동 · @Transactional · Micrometer 메트릭 · 기존 서비스 재사용 · DI로 테스트 쉬움)
                                   │
 Spring Core ──확장──▶ Spring AI
 (IoC/DI, AOP,          ├─ ChatClient    (LLM 추상화)      ─▶ OpenAI / Anthropic / Ollama / Gemini / Azure ...
  Auto Configuration,   ├─ VectorStore   (벡터 DB 추상화)  ─▶ PgVector / Redis / Pinecone
  Security / Data)      ├─ RAG Pipeline
                        ├─ Tool Calling
                        ├─ AI Agent
                        ├─ Chat Memory                     ─▶ InMemory / Redis / JDBC
                        └─ Micrometer 관측
```

### Model

- **Model**: 정보를 처리·생성하도록 설계된 알고리즘
  - 대규모 데이터에서 패턴을 학습 → 추론, 텍스트·이미지 등 출력 생성
- **GPT**(Generative Pre-Trained Transformer)
  - 이미 **사전 학습**이 잘 되어 있어 범용 개발자 도구처럼 쓸 수 있음

### Prompt

- AI 모델이 특정 결과를 내도록 안내하는 **언어 기반 입력**
- 단순 문자열이 아니라 **역할(role)이 다른 여러 메시지의 배열**

| Role | 설명 |
|---|---|
| **System** | 모델의 행동 방식과 대화의 컨텍스트를 정함 |
| **User** | 사용자의 실제 입력 |
| **Assistant** | (암시적) 모델의 이전 응답 |

### PromptTemplate

- 프롬프트를 효과적으로 만들기 위해 템플릿 엔진 **StringTemplate**(오픈소스) 사용
- `{변수}` 자리에 값 맵을 넣어 렌더링 → Spring AI가 자동으로 해 줌

```java
// 1. 템플릿 정의
String template = "Tell me a {adjective} joke about {content}.";
// 2. 값 맵 준비 (Model 역할)
Map<String, String> userInput = Map.of("adjective", "funny", "content", "Java");
// 3. 렌더링 (Spring AI가 자동으로 처리)
// 결과: "Tell me a funny joke about Java."
// 4. 렌더링된 문장을 AI 모델에 전달
```

### Prompt Engineering

- AI가 원하는 결과를 내도록 **프롬프트를 설계·작성**하는 분야
- 프롬프트 ≠ 그냥 질문
  - **역할 + 문맥 + 형식**까지 담은 구조화된 명령어 세트
  - 같은 요청이라도 표현 방식에 따라 결과가 완전히 달라짐
- 연구 사례: 효과가 좋았던 프롬프트가 "Take a deep breath and work on this step by step"(깊게 숨 쉬고 단계별로)으로 시작했다는 결과가 있음

| 기법 | 설명 | 예시 |
|---|---|---|
| **Role-Based Prompting** | AI에게 역할 부여(예: 경험 많은 개발자) | System: `"You are an expert developer"` |
| **Step-by-Step (CoT)** | 단계별 사고 과정을 명시적으로 요청 | User: `"Explain your reasoning step by step"` |
| **Context Injection** | 배경 정보·규칙을 명확히 제공 | User: `"{context}를 고려해서 답변해줘"` |
| **Output Format** | 원하는 응답 형식을 구체적으로 지정 | User: `"JSON 형식으로 답변하세요"` |

### Embeddings

- 단어·이미지 같은 **고차원 데이터를 저차원 벡터 공간으로 변환**하는 과정 → 입력 간의 관계를 잡아냄
  - 벡터는 원래 데이터의 **의미적 특성**을 보존
- 텍스트·이미지·비디오를 **숫자로 표현**해서 입력끼리의 관계를 나타냄
- 예: word embedding — 의미가 비슷한 단어는 벡터 공간에서 가까이 위치
- **RAG와 임베딩**

```text
[문서 저장]  외부 지식 문서 ──임베딩──▶ 벡터 DB
[질의]       사용자 질문    ──같은 임베딩 공간으로 변환──▶ 벡터 유사도 계산
                                                          └▶ 관련성 높은 문서 검색 ─▶ 응답 생성
```

### Tokens

- AI 모델이 동작하는 **기본 단위**
  - 입력(prompt)을 **토큰으로 바꿔서** 처리하고, 출력할 때 토큰을 다시 단어로 바꿈
- 영어 기준 토큰 1개 ≈ 단어의 75%
  - 예: 셰익스피어 전체 작품 약 90만 단어 → 약 120만 토큰
- 토큰 수가 곧 **비용**
- **컨텍스트 윈도우(Context Window)**
  - API 한 번 호출에서 처리할 수 있는 텍스트 양 → 넘는 부분은 처리되지 않음
  - 예: ChatGPT3 4K, Claude 100K, 메타 1M 등
  - 큰 데이터는 **조각으로 잘라** 윈도우 안에서 처리 → Spring AI가 이 작업을 도와줌

### Structured Output Converter

- AI 모델의 출력은 원래 그냥 `java.lang.String`
- "JSON으로 줘"라고 해도 결과는 **JSON 모양의 문자열** → 객체로 바꿔 줄 도구가 필요
- Structured Output Converter: AI 출력을 **POJO로 매핑** (자세한 사용은 [Chat Client API](02_Chat_Client_API.md#structured-output))

### Pre-Trained 특성 강화하기

- 사전 학습된 모델에 **내 데이터·기능**을 붙이는 방법

| 방법 | 내용 |
|---|---|
| **Fine Tuning** | 전통적 머신러닝 방식으로 모델의 **내부 가중치를 변경**. 난이도·리소스 소모가 매우 큼. 폐쇄형 모델은 아예 불가 |
| **Prompt Stuffing (RAG)** | 사용자 데이터를 **프롬프트 안에 넣어** 모델에 전달 |
| **Tool Calling** | LLM을 외부 시스템의 API(사용자 정의 서비스)에 연결 |

### RAG (Retrieval Augmented Generation, 검색 증강 생성)

- **정보 검색 + 텍스트 생성**을 묶어 질문에 맞는 답을 만듦
  - **정보 검색**: 질문과 관련된 정보를 담은 문서를 찾음 → 벡터 DB 활용
  - **텍스트 생성**: 찾은 정보를 바탕으로 새 텍스트 생성 → 자연어 처리 기술 활용
- 문서 수집 단계 = **ETL**

| 단계 | 의미 |
|---|---|
| **E**xtract | 추출 |
| **T**ransform | 변환 |
| **L**oad | 적재 |

### Tool Calling

- LLM은 **학습 시점까지의 지식**으로만 동작 → 실시간 정보 반영이 어려움
- LLM을 **외부 시스템 API와 연결**해 실시간 데이터를 주고 데이터 처리 작업도 하게 함
- Spring AI에서는 해당 함수를 **`@Tool`** 로 만들어 프롬프트 옵션으로 전달

---

## 참고

- PromptTemplate 렌더링은 `{키}` 자리를 값으로 바꾸는 것 → 키 이름이 템플릿과 다르면 자리표시자가 그대로 남음(순수 자바로 흉내 낸 확인 결과)

```java
// 템플릿: "Tell me a {adjective} joke about {content}."
render(template, Map.of("adjective", "funny", "content", "Java"));
// 결과: Tell me a funny joke about Java.
render(template, Map.of("adjactive", "funny", "content", "java"));   // 키 오타
// 결과: Tell me a {adjective} joke about java.
```

- 컨텍스트 윈도우 수치(4K·100K·1M)는 교재 출처(spring.io) 작성 당시 기준 → 지금 모델들은 대부분 훨씬 큼. 쓰는 모델의 문서에서 확인
- RAG의 ETL·VectorStore, Chat Memory는 같은 날 뒤 강의([Embedding과 VectorStore](06_Embedding과_VectorStore.md) 이후)에서 실습
