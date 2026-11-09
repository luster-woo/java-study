# spring-ai-practice

day14 Spring AI 실습 완성본 (TODO 01~13 + ChatBot) — 노트: [notes/day14](../../notes/day14/)

## 준비

1. **JDK 21** (`Project Structure → SDK`), Lombok 플러그인 + `Enable annotation processing`
2. **API 키**: `src/main/resources/secret.properties`의 `ssafy.gms.api-key=` 뒤에 GMS 키 붙여 넣기
   - 이 파일은 `.gitignore` 처리 → 커밋되지 않음. **`application.properties`에 키를 직접 쓰지 말 것**
   - 키가 비어 있으면 앱이 시작하지 않음 (`OpenAI API key must be set`)
3. **Docker Desktop** 실행 → `src/main/resources/docker`에서 `docker compose up -d`
   - `redis-stack`: VectorStore (6379), Redis Insight GUI http://localhost:8001
   - `mysql`: 대화 기억 저장용 (3306, root / ssafy, DB `ssafylive`)
   - 로컬 MySQL이 이미 3306을 쓰고 있으면 compose의 mysql 서비스를 지우고 `application.properties`의 계정만 내 것으로 바꾸기
   - **Redis·MySQL이 안 떠 있으면 앱(테스트 포함)이 시작하지 않음** — VectorStore와 ChatMemory 테이블을 시작할 때 만들기 때문

## TODO 위치

| TODO | 내용 | 파일 |
|---|---|---|
| 01 | 의존성(BOM 1.1.5), 키·GMS 설정, Gemini ChatModel 직접 구성 | `pom.xml`, `application.properties`, `config/ChatModelConfig.java`, `SpringAiPracticeApplication.java`(exclude) |
| 02 | ChatModel 동작 확인 | `test/A_ChatModelTest` |
| 03 | ChatClient — 모델 직접 지정, 호출 시 system·options 재정의 | `service/A_SimpleChatService` |
| 04 | System/User Prompt, PromptTemplate 변수 | `service/B_PromptChatService`, `prompts/default_penpal_prompt.txt` |
| — | 모델별 ChatClient.Builder 빈 + SimpleLoggerAdvisor | `config/ChatClientConfig.java` |
| 05 | Structured Output (`BeanOutputConverter`, `entity()`) | `service/C_StructuredOutputService`, `dto/Joke` |
| 06 | 멀티모달 객체 탐지 | `service/E_MultiModalService`, `images/sample.png` |
| 07 | ReReadingAdvisor, SafeGuardAdvisor | `advisor/ReReadingAdvisor`, `service/D_AdvisorService` |
| 08 | Redis VectorStore 빈 (MetadataField) | `config/VectorStoreConfig.java` |
| 09 | 데이터 추가·filter 유사도 검색 | `test/E_VectorStoreTest` |
| 10 | ChatMemory(MySQL) + MessageChatMemoryAdvisor | `config/ChatMemoryConfig.java`, `scripts/chatmemoryrepo-mysql.sql`, `service/F_ConversationService` |
| 11 | 대화 기록 전용 VectorStore (`chat-history`) | `config/VectorStoreConfig.java` |
| 12 | VectorStoreChatMemoryAdvisor / ETL (TextReader·JsonReader·TokenTextSplitter) | `service/F_ConversationService`, `service/EtlService`, `data/` |
| 13 | RAG (QuestionAnswerAdvisor) | `service/G_RAGService` |
| ChatBot | `/chat` 분기 (이미지 / RAG / 대화 기억) | `controller/AiController`, `static/chat.html` |

## 확인 순서

테스트 클래스 이름 순서(A → H)대로 하나씩 실행하고 콘솔 로그(DEBUG)를 본다.

| 테스트 | 볼 것 |
|---|---|
| `A_ChatModelTest` | 모델 구현체·옵션·응답·토큰 사용량 |
| `B_ChatClientTest` | 번역가(0.1) vs 동요 작사가(2.0) / 펜팔 편지가 제주 이야기 + 지정 언어 / 응답이 `Joke` 객체로 |
| `C_MultiModalTest` | 그림 속 집·나무·해·사과를 찾아냄 |
| `D_AdvisorTest` | `request:` 로그에 질문이 두 번(`Read the question again:`) / 금칙어면 모델 호출 로그 없이 거절 문구 |
| `E_VectorStoreTest` | `score = 1 - distance`, filter·threshold 때문에 topK 3이어도 결과 2개 |
| `F_ConversationTest` | 기억 없음 → 이름 모름 / MySQL 기억 → 이름 기억 (`SELECT * FROM ssafylive.SPRING_AI_CHAT_MEMORY`) / Redis 기억 (`chat:*` 키) |
| `G_EtlTest` | Reader 결과·chunk 분할, 문서 적재 |
| `H_RagTest` | 가상 규정 문서를 근거로 "4회 → 퇴소" / 문서에 없는 질문은 "답할 수 없다" |

**ChatBot**: `SpringAiPracticeApplication` 실행 → http://localhost:8080/chat.html → `RAG 문서 적재` 버튼 한 번 → 대화

- 그냥 대화: 대화 기억(VectorStore) / `RAG 사용` 체크: 문서 검색 + 대화 기억 / 이미지 첨부: 멀티모달 (GMS는 **100KB 미만**만)

## 교재와 다르게 한 곳

| 어디 | 교재 | 여기 | 이유 |
|---|---|---|---|
| API 키 | `application.properties`에 직접 | `secret.properties` (gitignore) | 키 유출 방지 |
| ChatClient 자동 구성 | — | `spring.ai.chat.client.enabled=false` | ChatModel이 2개라 자동 Builder가 모델을 고를 수 없음 → 모델별 Builder 빈을 직접 씀 |
| MySQL | 로컬 MySQL | docker-compose에 mysql 추가, URL에 `createDatabaseIfNotExist=true` | 바로 실행되게 |
| 스키마 SQL | `CREATE TABLE` | `CREATE TABLE IF NOT EXISTS` | `initialize-schema=always`라 재시작마다 실행됨 |
| ETL 적재 | Document id 자동(UUID) | `ssafy-life-0`처럼 고정 | 여러 번 실행해도 중복 적재 안 되게 |
| `/chat` | `file`·`useRag` 필수 | `required = false`, `defaultValue = "false"` | 파일 없이 보내면 400 (노트 참고 항목) |
| `chatBotImageDescription` | `(resource, message, userId)` | `mime` 인자 추가 | 업로드 파일의 Content-Type을 그대로 넘기려고 |
| `/etl`, `chat.html` | 없음 | 추가 | 화면에서 바로 RAG 확인용 |
| RAG 문서 | 교재 제공 파일 | `data/ssafy_life.txt`(**가상의 예시 규정**), `data/books.json` | 교재 파일이 없어서 직접 만듦 — 실제 SSAFY 규정 아님 |

## 처음 실행할 때

이 프로젝트는 Maven 의존성을 받을 수 없는 환경에서 Spring AI 1.1 API 기준으로 작성해 **아직 컴파일·실행 확인을 못 했다.** 처음 빌드에서 클래스·패키지 이름 오류가 나면 그 메시지를 그대로 가져와서 고치면 된다. 특히 확인할 곳:

- `SpringAiPracticeApplication`의 exclude 두 클래스(자동 설정 클래스 패키지 경로)
- `ChatModelConfig`의 `GoogleGenAiChatModel.builder().genAiClient(...)`
