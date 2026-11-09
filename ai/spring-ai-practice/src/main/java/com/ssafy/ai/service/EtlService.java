package com.ssafy.ai.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO: 12 ETL Pipeline — DocumentReader(Extract) → TokenTextSplitter(Transform) → VectorStore(Load)
 * Supplier → Function → Consumer 라서 vectorStore.accept(splitter.apply(reader.get()))로 이어 붙일 수 있음
 */
@Slf4j
@Service
public class EtlService {

    private final VectorStore eVectorStore;

    @Value("classpath:/data/ssafy_life.txt")
    private Resource ssafyLife;

    @Value("classpath:/data/books.json")
    private Resource books;

    public EtlService(@Qualifier("customEmbeddingVector") VectorStore eVectorStore) {
        this.eVectorStore = eVectorStore;
    }

    // ---------- Extract ----------

    /** 단순 Text 처리 — TextReader + category metadata (RAG filter 검색에 사용) */
    public List<Document> readSsafyLife() {
        TextReader reader = new TextReader(ssafyLife);
        reader.getCustomMetadata().put("category", "SSAFY_LIFE");   // 이 reader가 만드는 Document 모두에 붙음
        return reader.get();
    }

    /** JSON 처리 — 객체마다 Document, description 항목만 content로 */
    public List<Document> readBooks() {
        JsonReader jsonReader = new JsonReader(books, "description");
        return jsonReader.get("/store/books");                       // JSON Pointer: books 배열 위치
    }

    /** JSON Pointer로 특정 요소 하나만 */
    public List<Document> readFirstBook() {
        JsonReader jsonReader = new JsonReader(books, "description");
        return jsonReader.get("/store/books/0");
    }

    // ---------- Transform + Load ----------

    /**
     * 분할 후 적재. 같은 문서를 여러 번 적재해도 중복되지 않게 id를 "prefix-순번"으로 고정
     * (Redis 키 = prefix + id → 같은 id면 덮어씀)
     */
    public int load(List<Document> docs, String idPrefix, String category) {
        List<Document> chunks = new TokenTextSplitter().apply(docs);  // Transform: chunk로 분할

        List<Document> fixed = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            Document chunk = chunks.get(i);
            var metadata = new HashMap<>(chunk.getMetadata());
            metadata.put("category", category);
            fixed.add(new Document(idPrefix + "-" + i, chunk.getText(), metadata));
        }
        eVectorStore.accept(fixed);                                   // Load: 임베딩 후 저장
        log.debug("[ETL] {} → chunk {}개 적재", idPrefix, fixed.size());
        return fixed.size();
    }

    /** RAG 실습 데이터 전부 적재 */
    public void loadAll() {
        load(readSsafyLife(), "ssafy-life", "SSAFY_LIFE");
        load(readBooks(), "books", "BOOK");
    }
}
