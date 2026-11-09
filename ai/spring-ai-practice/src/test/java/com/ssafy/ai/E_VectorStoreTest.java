package com.ssafy.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO: 08 임베딩 확인 · TODO: 09 데이터 추가 및 유사도 기반 검색
 * Redis Insight(http://localhost:8001)에서 embedding:* 키를 보면 저장된 모습 확인 가능
 */
@Slf4j
@SpringBootTest
class E_VectorStoreTest {

    @Autowired
    @Qualifier("customEmbeddingVector")
    VectorStore vStore;

    @Autowired
    EmbeddingModel emodel;

    List<Document> docs;

    @BeforeEach
    void setUp() {
        docs = generateSampleDocuments();
        vStore.add(docs);                              // content를 임베딩 후 저장
        log.info("테스트 데이터 {}개 로드 완료", docs.size());
    }

    @AfterEach
    void tearDown() {
        List<String> ids = docs.stream().map(Document::getId).toList();
        vStore.delete(ids);                            // 테스트 Document 모두 삭제
        log.info("테스트 데이터 {}개 삭제 완료", ids.size());
    }

    // TODO: 08 — 임베딩이 잘 되는지: 사용 모델과 벡터 차원 확인
    @Test
    void embeddingTest() {
        String message = "대한민국의 수도는?";
        EmbeddingResponse res = emodel.embedForResponse(List.of(message));
        var metaData = res.getMetadata();
        log.debug("모델 정보: {}", metaData.getModel());
        float[] vector = res.getResult().getOutput();
        log.debug("벡터 차원: {}, 앞 5개: {} {} {} {} {}", vector.length,
                vector[0], vector[1], vector[2], vector[3], vector[4]);
        assertThat(vector.length).isGreaterThan(0);
    }

    // 쿼리만으로 검색 (topK 기본 4)
    @Test
    void similaritySearchSimple() {
        List<Document> results = vStore.similaritySearch("데이터베이스 저장소");
        print(results);
    }

    // TODO: 09 — category == database && (meta_txt in [redis, vector] && meta_num >= 3)
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
        print(filteredResults);
        // Redis(meta_num 3), 벡터 DB(meta_num 10)만 조건 통과 → topK가 3이어도 최대 2개
        assertThat(filteredResults).hasSizeLessThanOrEqualTo(2);
    }

    // 같은 조건을 문자열 filter로
    @Test
    void similaritySearchUseFilter2() {
        SearchRequest filterRequest = SearchRequest.builder()
                .filterExpression("category == 'database' && meta_txt in ['redis', 'vector'] && meta_num >= 3")
                .query("데이터베이스 저장소")
                .topK(3)
                .similarityThreshold(0.5)
                .build();
        print(vStore.similaritySearch(filterRequest));
    }

    private void print(List<Document> results) {
        log.debug("=== 검색 결과: {} ===", results.size());
        for (Document d : results) {
            log.debug("text     {}", d.getText());
            log.debug("metadata {}", d.getMetadata());
            log.debug("score    {}", d.getScore());   // score = 1 - distance
        }
    }

    private List<Document> generateSampleDocuments() {
        return List.of(
                new Document("TEST-1", "Redis는 인메모리 데이터 구조 저장소로, 키-값 데이터베이스로 사용됩니다. 캐시와 메시지 브로커로도 활용됩니다.",
                        Map.of("category", "database", "meta_txt", "redis nosql", "meta_num", 3)),
                new Document("TEST-2", "벡터 데이터베이스는 임베딩 벡터를 저장하고 유사도 검색을 수행하는 특화된 데이터베이스입니다.",
                        Map.of("category", "database", "meta_txt", "vector database", "meta_num", 10)),
                new Document("TEST-3", "MySQL은 널리 쓰이는 관계형 데이터베이스로, SQL로 테이블 데이터를 다룹니다.",
                        Map.of("category", "database", "meta_txt", "mysql rdbms", "meta_num", 5)),
                new Document("TEST-4", "Redis 클러스터는 데이터를 여러 노드에 나눠 저장합니다.",
                        Map.of("category", "database", "meta_txt", "redis cluster", "meta_num", 1)),
                new Document("TEST-5", "인공지능은 인간의 지능을 모방하여 학습하고 추론하는 기술입니다.",
                        Map.of("category", "technology", "meta_txt", "ai", "meta_num", 7)),
                new Document("TEST-6", "김치찌개는 김치와 돼지고기를 넣고 끓인 한국의 대표 찌개입니다.",
                        Map.of("category", "food", "meta_txt", "korean food", "meta_num", 2)));
    }
}
