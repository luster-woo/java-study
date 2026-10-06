# Embedding과 VectorStore

> 교재: 15기_자바전공_06_ai_2_0611 (전공) — 1. Embedding과 VectorStore · 선택 강의

> [← Advisor API](05_Advisor_API.md) · [day14 목록](../README.md) · [ConversationMemory →](07_ConversationMemory.md)

---

## Embedding과 VectorStore

### Embedding

- 텍스트·이미지 등의 데이터를 **부동 소수점 값으로 이루어진 vector**로 바꾸는 과정
  - vector = 크기와 방향을 가진, **다차원 공간의 한 점**
- **코사인 유사도**: 크기·방향이 비슷한 vector일수록 원래 데이터끼리 유사도가 높다고 봄

```text
"Redis는 인메모리 저장소"  ──embedding──▶ [0.12, -0.03, 0.88, ...]  ┐
"벡터 DB는 임베딩 저장소"  ──embedding──▶ [0.10, -0.01, 0.91, ...]  ├─ 방향이 비슷 → 유사도 높음
"오늘 저녁 메뉴는?"        ──embedding──▶ [-0.70, 0.45, 0.02, ...]  ┘  방향이 다름 → 유사도 낮음
```

### VectorStore

- **벡터 값을 저장**하고 **벡터 유사도 검색**을 수행하는 데이터베이스
- 이 강의에서는 **Redis**를 VectorStore로 사용

---

## EmbeddingModel 및 RedisVectorStore 설정

### 의존성 (TODO: 08)

- 임베딩 모델 starter + Redis VectorStore starter
- OpenAI는 chat starter에 **임베딩 모델이 이미 포함**되어 있어 따로 추가할 필요 없음
  - Google GenAI처럼 다른 모델을 쓰면 해당 embedding starter 추가

```xml
<!-- OpenAI가 아닌 모델(예: Google GenAI)의 임베딩을 쓸 때 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-google-genai-embedding</artifactId>
</dependency>

<!-- Redis를 VectorStore로 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-redis</artifactId>
</dependency>
```

```properties
# 임베딩 모델을 위한 키 추가 필요(모델마다 속성 이름이 다름)
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.embedding.options.model=text-embedding-3-large
```

### VectorStore 빈 생성 (TODO: 08)

- RedisVectorStore도 **자동 환경 구성**이 되지만, 검색용 **MetadataField**를 추가하려고 커스텀 빈을 직접 만듦
  - 그래서 Redis VectorStore 자동 설정은 **exclude**

| MetadataField | 용도 |
|---|---|
| `MetadataField.tag("category")` | 태그(정확히 일치) 필터 |
| `MetadataField.numeric("meta_num")` | 숫자 비교(`>=` 등) 필터 |
| `MetadataField.text("meta_txt")` | 텍스트 필터 |

```java
// RedisVectorStore 자동 환경 설정은 제외
@SpringBootApplication(exclude = RedisVectorStoreAutoConfiguration.class)
public class SpringaiApplication { /* ... */ }
```

```java
@Bean
VectorStore vectorStore(EmbeddingModel embeddingModel) {
    return RedisVectorStore.builder(new JedisPooled("localhost", 6379), embeddingModel)
            .initializeSchema(true)                 // 선택: schema 초기화 여부(기본 false)
            .metadataFields(                        // 선택: filtering에 쓸 metadata field
                    MetadataField.tag("category"),
                    MetadataField.numeric("meta_num"),
                    MetadataField.text("meta_txt"))
            .build();
}
```

- 임베딩이 잘 되는지 테스트: `embedForResponse()`로 응답을 받고 메타데이터에서 사용 모델 확인

```java
@Test
void embeddingTest() {
    String message = "대한민국의 수도는?";
    EmbeddingResponse res = emodel.embedForResponse(List.of(message));
    var metaData = res.getMetadata();
    log.debug("모델 정보: {}", metaData.getModel());
}
```

---

## VectorStore 활용

### Document

- **임베딩될 문서의 조각**
  - 원본 문서 전체는 토큰 양이 너무 크기 때문에 **작게 나눈 것**
- `org.springframework.ai.document.Document`의 구성

| 필드 | 내용 |
|---|---|
| **content** | 주된 내용(문자열) |
| **metadata** | 데이터 검색에 쓰는 부가 데이터 → VectorStore의 **metadata field**와 연결 |
| **media** | 멀티모달 형태의 입력 정보 |

```java
new Document(
        "ID-1",                                                    // id
        "인공지능은 인간의 지능을 모방하여 학습하고 추론하는 기술이다.", // content
        Map.of("category", "technology",                           // metadata
               "language", "ko"));
```

### VectorStore 인터페이스

- `DocumentWriter`를 상속 → ETL의 Load 단계 저장소 역할도 함([ETL Pipeline](08_RAG_ETL_Pipeline.md))

```java
public interface VectorStore extends DocumentWriter {

    // Document의 text content를 embedding해서 저장
    void add(List<Document> documents);

    // 저장된 vector를 대상으로 유사도 검사 → 결과를 Document로 반환
    @Nullable
    List<Document> similaritySearch(SearchRequest request);

    @Nullable
    default List<Document> similaritySearch(String query) {
        return this.similaritySearch(SearchRequest.builder().query(query).build());
    }

    // id 목록 또는 filter로 삭제
    void delete(List<String> idList);
    void delete(Filter.Expression filter);
}
```

| 메서드 | 하는 일 |
|---|---|
| `add(docs)` | content를 **임베딩 후 저장** |
| `similaritySearch(request)` | 유사도 검색 → `List<Document>` |
| `similaritySearch(query)` | 쿼리 문자열만으로 검색(나머지는 기본값) |
| `delete(ids)` / `delete(filter)` | id 목록 / filter 조건으로 삭제 |

### 테스트 데이터 넣고 지우기

- `@BeforeEach`에서 샘플 Document를 `add()`, `@AfterEach`에서 그 id들로 `delete()` → 테스트마다 깨끗한 상태

```java
@SpringBootTest
public class VectorStoreTest {
    @Autowired(required = false)
    VectorStore vStore;

    @Autowired(required = false)
    OpenAiEmbeddingModel emodel;

    List<Document> docs;

    @BeforeEach
    void setUp() {
        docs = generateSampleDocuments();
        vStore.add(docs);                              // VectorStore에 추가
        log.info("테스트 데이터 {}개 로드 완료", docs.size());
    }

    @AfterEach
    void tearDown() {
        List<String> ids = docs.stream().map(Document::getId).toList();
        vStore.delete(ids);                            // 테스트 Document 모두 삭제
        log.info("테스트 데이터 {}개 삭제 완료", ids.size());
    }
}
```

### SearchRequest

- 임베딩된 데이터에 대한 **유사도 검색 조건** 객체, builder로 만듦

| 속성 | 의미 | 기본값 / 제약 |
|---|---|---|
| `query` | 문자열 형태의 쿼리 | `""`, null 불가 |
| `topK` | 조회 대상 개수(K nearest neighbors) | `4`, 음수 불가 |
| `similarityThreshold` | 검색 대상 **최소 유사도** | `0.0`, 범위 [0, 1] |
| `filterExpression` | metadata 기반 filter | `Filter.Expression` 또는 문자열 |

```java
SearchRequest request = SearchRequest.builder()
        .query("데이터베이스 저장소")
        .topK(3)
        .similarityThreshold(0.5)
        .filterExpression("category == 'database'")   // 문자열 filter
        .build();
```

### Filter.Expression

- VectorStore를 만들 때 등록한 **MetadataField**로 filtering
- 작성법 1: **FilterExpressionBuilder**

```java
FilterExpressionBuilder b = new FilterExpressionBuilder();
Expression exp1 = b.eq("country", "BG").build();
Expression exp2 = b.and(b.eq("genre", "drama"), b.gte("year", 2020)).build();
Expression exp3 = b.in("genre", "comedy", "documentary", "drama").build();
```

- 지원 연산자

```java
public enum ExpressionType {
    AND, OR, EQ, NE, GT, GTE, LT, LTE, IN, NIN, NOT
}
```

- 작성법 2: **문자열(Filtering String)**로 조건식 작성

| Builder | 문자열 |
|---|---|
| `b.eq("country", "BG")` | `"country == 'BG'"` |
| `b.and(b.eq("genre","drama"), b.gte("year",2020))` | `"genre == 'drama' && year >= 2020"` |
| `b.in("genre", "comedy", "documentary", "drama")` | `"genre in ['comedy', 'documentary', 'drama']"` |

### 데이터 추가 및 유사도 기반 검색 (TODO: 09)

- 조건: `category == database` **그리고** (`meta_txt`가 redis·vector 중 하나 **그리고** `meta_num >= 3`)
- 쿼리 "데이터베이스 저장소", topK 3, 최소 유사도 0.5

```java
@Test
void similaritySearchUseFilter1() {
    FilterExpressionBuilder builder = new FilterExpressionBuilder();
    Expression expression = builder.and(
            builder.eq("category", "database"),
            builder.and(builder.in("meta_txt", "redis", "vector"),
                        builder.gte("meta_num", 3))
    ).build();

    SearchRequest filterRequest = SearchRequest.builder()
            .filterExpression(expression)
            .query("데이터베이스 저장소")
            .topK(3)
            .similarityThreshold(0.5)
            .build();

    List<Document> filteredResults = vStore.similaritySearch(filterRequest);
    // ... 결과 출력
}
```

```text
=== 검색 결과: 2 ===
text     Redis는 인메모리 데이터 구조 저장소로, 키-값 데이터베이스로 사용됩니다. ...
metadata {vector_score=0.27692682, distance=0.27692682, category=database, meta_txt=redis nosql, meta_num=3}
score    0.7230731844902039

text     벡터 데이터베이스는 임베딩 벡터를 저장하고 유사도 검색을 수행하는 특화된 데이터베이스입니다.
metadata {vector_score=0.28996307, distance=0.28996307, category=database, meta_txt=vector database, meta_num=10}
score    0.7100369334220886
```

- **similarityThreshold의 의미** 확인
  - 결과의 `score` = **1 − distance** (0.7231 = 1 − 0.2769)
  - threshold 0.5 → score가 **0.5 이상**인 Document만 남음
  - topK가 3이어도 filter·threshold를 통과한 것이 2개라 **결과는 2개**
- `meta_txt`는 text 필드라 "redis nosql", "vector database"처럼 **단어가 포함**되어 있으면 `in` 조건에 걸림

---

## 참고

- 교재 오류 정정
  - Filter 예제의 `"comery"`는 `"comedy"` 오타
  - 테스트 메서드·말풍선의 `simularitySearch…`, `simularityThreahold`는 `similaritySearch…`, `similarityThreshold` 오타 → 위 코드는 바로잡은 이름으로 씀
- `List.toList()`(Stream)는 **Java 16+**. Spring Boot 3.x는 Java 17 이상이라 문제없음
- 코사인 유사도를 순수 자바로 계산해 보기(벡터 값은 임의)

```java
public class Cosine {
    static double cosine(double[] a, double[] b) {
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }

    public static void main(String[] args) {
        double[] redis   = {0.9, 0.1, 0.3};
        double[] vector  = {0.8, 0.2, 0.4};
        double[] cooking = {0.1, 0.9, 0.0};
        System.out.printf("redis-vector  : %.3f%n", cosine(redis, vector));
        System.out.printf("redis-cooking : %.3f%n", cosine(redis, cooking));
        double distance = 0.27692682;                       // 교재 검색 결과의 distance
        System.out.printf("score = 1 - distance = %.4f%n", 1 - distance);
    }
}
// 결과
// redis-vector  : 0.984
// redis-cooking : 0.208
// score = 1 - distance = 0.7231
```
