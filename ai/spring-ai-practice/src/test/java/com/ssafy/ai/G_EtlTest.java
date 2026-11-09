package com.ssafy.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ssafy.ai.service.EtlService;

import lombok.extern.slf4j.Slf4j;

/** TODO: 12 ETL Pipeline — Reader 결과 확인 후 VectorStore에 적재 (RAG 테스트 전에 한 번 실행) */
@Slf4j
@SpringBootTest
class G_EtlTest {

    @Autowired EtlService etlService;

    @Test
    void textReaderTest() {
        List<Document> docs = etlService.readSsafyLife();
        docs.forEach(d -> log.debug("metadata: {}\n{}", d.getMetadata(), d.getText()));
        assertThat(docs.get(0).getMetadata()).containsEntry("category", "SSAFY_LIFE");

        // Transform: 작은 chunkSize로 나눠 보면 chunk가 여러 개로 쪼개지는 것이 보임
        List<Document> chunks = new TokenTextSplitter(100, 50, 5, 10000, true).apply(docs);
        log.debug("chunkSize 100 → chunk {}개", chunks.size());
        chunks.forEach(c -> log.debug("chunk: {}", c.getText()));
    }

    @Test
    void jsonReaderTest() {
        List<Document> books = etlService.readBooks();
        books.forEach(d -> log.debug("book: {}", d.getText()));
        assertThat(books).hasSize(4);

        List<Document> first = etlService.readFirstBook();   // JSON Pointer /store/books/0
        log.debug("first: {}", first.get(0).getText());
        assertThat(first).hasSize(1);
    }

    @Test
    void loadAllTest() {
        etlService.loadAll();   // id 고정이라 여러 번 실행해도 중복 적재 X
    }
}
