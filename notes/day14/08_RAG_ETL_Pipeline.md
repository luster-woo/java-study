# RAG ETL Pipeline

> 교재: 15기_자바전공_06_ai_2_0611 (전공) — 3. RAG - ETL Pipeline · 선택 강의

> [← ConversationMemory](07_ConversationMemory.md) · [day14 목록](../README.md) · [RAG Advisor →](09_RAG_Advisor.md)

---

## RAG

### Retrieval Augmented Generation(검색 증강 생성)

- 모델의 **학습 데이터에 없는 외부 데이터**를
- 실시간으로 **검색(Retrieval)** → 질문에 **보강(Augmented)** → 답변 **생성(Generation)**
- Spring AI는 RAG를 쉽게 쓰도록 **Advisor API** 지원 → **QuestionAnswerAdvisor**
  - Vector Database에 부가 데이터를 미리 저장
  - 사용자 질문이 오면 QuestionAnswerAdvisor가 Vector DB에서 **관련 내용 검색**(vector 기반 cos 유사도)
  - 검색 내용이 **Context**로 LLM에 전달되어 응답 생성에 활용

### RAG 진행 절차

```text
[문서 수집 단계 — ETL]                         [질의 단계 — 다음 강의 RAG Advisor]
원본 문서(PDF·JSON·Markdown·Text)              사용자 질문
   │ Extract  : 추출                              │
   ▼                                              ▼
Document 목록                                 VectorStore 유사도 검색 (Retrieval)
   │ Transform: 변환(작은 chunk로 분할)            │
   ▼                                              ▼
chunk Document 목록                           검색 결과를 프롬프트에 보강 (Augment)
   │ Load     : 적재(임베딩 후 저장)               │
   ▼                                              ▼
VectorStore  ─────────────────────────────▶   LLM 응답 생성 (Generation)
```

---

## ETL Pipeline

### 역할

- **Extract, Transform, Load**는 RAG를 위한 데이터 처리의 중추
- 원시 데이터를 모델이 **검색하기 좋은 형태**로 유지
- ETL Pipeline으로 Document를 **생성 → 변환 → 저장**

### 구성 요소 — 함수형 인터페이스 3개

| 구성 요소 | 구현하는 인터페이스 | 하는 일 |
|---|---|---|
| **DocumentReader** | `Supplier<List<Document>>` | JSON·Markdown·PDF·Text 등에서 데이터 **수집** |
| **DocumentTransformer** | `Function<List<Document>, List<Document>>` | 수집한 Document를 원하는 형태로 **변환** |
| **DocumentWriter** | `Consumer<List<Document>>` | VectorStore·File 등에 **저장** |

- 셋이 Supplier → Function → Consumer라서 한 줄로 이어 붙일 수 있음

```java
// reader.get() → splitter.apply(...) → vectorStore.accept(...)
vectorStore.accept(new TokenTextSplitter().apply(reader.get()));
```

- VectorStore는 `DocumentWriter`를 상속 → `accept()`가 곧 임베딩 후 저장([VectorStore](06_Embedding과_VectorStore.md#vectorstore-인터페이스))

### DocumentReader (TODO: 12)

- 문서 포맷마다 reader가 따로 있음 → 필요한 것만 의존성 추가

```xml
<!-- PDF -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-pdf-document-reader</artifactId>
</dependency>
<!-- Markdown -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-markdown-document-reader</artifactId>
</dependency>
<!-- Tika: Word·PPT·HTML 등 여러 포맷 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-tika-document-reader</artifactId>
</dependency>
```

### DocumentTransformer

- `Function<List<Document>, List<Document>>`
- Document를 **더 작은 Document(chunk)로 분할**
- 대표 구현: **TokenTextSplitter** (`extends TextSplitter`) — 긴 문장을 chunk 단위로 나눔

| 옵션 | 설명 | 기본값 |
|---|---|---|
| `chunkSize` | 임시 chunk로 나눌 때 기준 **토큰 수** | 800 |
| `minChunkSizeChars` | 확정 chunk의 **최소 문자 수** | 350 |
| `keepSeparator` | 개행 문자를 chunk에 포함할지(문장 경계를 분명히 할 때 필요) | true |
| `minChunkLengthToEmbed` | 남은 text가 확정 chunk가 되기 위한 최소 문자 수(너무 짧으면 임베딩 효율이 떨어져 제외) | 5 |
| `maxNumChunks` | 확정 chunk 최대 수(넘으면 나머지 무시 → 임베딩 비용 절감) | 10000 |

### TokenTextSplitter 동작 흐름

```text
입력 문자열
   │ 토큰화, chunkSize(800) 기준으로 자름
   ▼
임시 청크   예) 2000 token → 800 / 800 / 400
   │ 청크별로 다시 문자열로
   ▼
문자열
   │ minChunkSizeChars보다 큰 덩어리를 [. ? ! \n] 단위로 분리
   ▼
분리된 문자열
   │ 앞뒤 공백 제거, keepSeparator 적용
   ▼
정리된 문자열
   │ minChunkLengthToEmbed 기준으로 불필요한 자투리 제거
   ▼
확정 청크
```

---

## Reader별 처리

### 단순 Text 처리 — TextReader (TODO: 12)

```java
public TextReader(String resourceUrl) { }
public TextReader(Resource resource) { }
```

- 데이터 관리를 위해 Document에 **category 속성(metadata)**을 추가 → 나중에 filter 검색에 사용

```java
TextReader reader = new TextReader(resource);
reader.getCustomMetadata().put("category", "SSAFY_LIFE");     // 이 reader가 만드는 Document 모두에 붙음
List<Document> docs = reader.get();
```

### JSON 처리 — JsonReader (TODO: 12)

- JSON 파일을 읽어 **객체마다 Document**로 만듦

```java
public JsonReader(Resource resource) { }
// JSON 객체에서 사용할 항목(key)만 지정
public JsonReader(Resource resource, String... jsonKeysToUse) { }
// JsonMetadataGenerator: JSON 정보로 metadata를 만들어 붙이는 객체
public JsonReader(Resource resource, JsonMetadataGenerator jsonMetadataGenerator, String... jsonKeysToUse) { }
```

- **JSON Pointer**: 문서의 특정 지점에서만 데이터를 가져옴 → `List<Document> get(String pointer)`

```java
// resource에서 description 항목만 사용 — 조회 대상 설정
JsonReader jsonReader = new JsonReader(resource, "description");
// /store/books/0번째 요소만 — JsonReader 안에서 경로 탐색
List<Document> documents = jsonReader.get("/store/books/0");
```

| 설정 | 무엇을 고르나 |
|---|---|
| `jsonKeysToUse` | 각 객체 **안의 어느 key**를 content로 쓸지 |
| JSON Pointer | JSON 트리 **어느 위치**의 객체를 읽을지 |

### PDF 처리 — PagePdfDocumentReader

- PDF를 텍스트로 나눠 Document로 구성

```java
public PagePdfDocumentReader(String resourceUrl) { }
public PagePdfDocumentReader(Resource pdfResource) { }
public PagePdfDocumentReader(String resourceUrl, PdfDocumentReaderConfig config) { }
public PagePdfDocumentReader(Resource pdfResource, PdfDocumentReaderConfig config) { }
```

```java
PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(simplePdf,
        PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)          // 페이지 상단 여백
                .withPagesPerDocument(1)       // Document 하나에 담을 PDF 페이지 수
                .build());
```

---

## 참고

- 교재 오류 정정
  - 교재는 PagePdfDocumentReader를 "PDF 카탈로그 정보로 텍스트 단락을 나눠 단락당 Document 하나"라고 설명하지만, 그건 **ParagraphPdfDocumentReader**의 설명. PagePdfDocumentReader는 **페이지 단위**로 나눔(`withPagesPerDocument`로 몇 쪽씩 묶을지 정함)
  - 슬라이드 제목의 `JSONReader`는 실제 클래스 이름 `JsonReader`
  - JSON Pointer 예제의 `this.jsonReader.get(...)`은 바로 위에서 만든 지역 변수를 쓰는 것이므로 `jsonReader.get(...)`
- TokenTextSplitter의 "[. ? ! \n] 단위로 분리"는 정확히는 임시 청크 안에서 **minChunkSizeChars 이후의 마지막 문장부호** 위치에서 자르고, 잘린 뒷부분은 다음 청크로 넘기는 방식 → 그래서 "800/800/400"은 대략적인 그림
- TextReader 예제의 `"SSAFY_LIFE"`는 다음 강의 RAG 검색에서 쓰는 category 값([RAG Advisor](09_RAG_Advisor.md))
- ETL을 순수 자바 함수형 인터페이스로 흉내(Document 대신 String, 마침표 기준 분할)

```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Etl {
    public static void main(String[] args) {
        // DocumentReader 역할
        Supplier<List<String>> reader = () -> List.of("SSAFY는 출석이 중요하다. 임의 결석이 누적되면 불이익이 있다.");
        // DocumentTransformer 역할 - 문장 단위 분할
        Function<List<String>, List<String>> splitter = docs -> docs.stream()
                .flatMap(d -> Arrays.stream(d.split("(?<=\\.)\\s*")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        // DocumentWriter 역할
        List<String> store = new ArrayList<>();
        Consumer<List<String>> writer = store::addAll;

        writer.accept(splitter.apply(reader.get()));
        store.forEach(System.out::println);
    }
}
// 결과
// SSAFY는 출석이 중요하다.
// 임의 결석이 누적되면 불이익이 있다.
```
