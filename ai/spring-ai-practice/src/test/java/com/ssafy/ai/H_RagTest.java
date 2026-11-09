package com.ssafy.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ssafy.ai.service.EtlService;
import com.ssafy.ai.service.G_RAGService;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO: 13 RAG — 모델이 원래 모르는 (가상의) SSAFY 규정을 적재해 둔 문서에서 찾아 근거로 답함
 * 문서에 없는 내용이면 "답할 수 없다"고 응답 (QuestionAnswerAdvisor 기본 템플릿)
 */
@Slf4j
@SpringBootTest
class H_RagTest {

    @Autowired G_RAGService service;
    @Autowired EtlService etlService;

    @BeforeEach
    void load() {
        etlService.loadAll();   // id 고정 → 매번 실행해도 덮어쓰기만 함
    }

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

    // 문서에 없는 질문 → 답할 수 없다고 해야 정상
    @Test
    void notInContextTest() {
        String userInput = "SSAFY 수료식은 몇 월에 해?";
        var searchRequest = SearchRequest.builder()
                .query(userInput)
                .topK(3)
                .filterExpression("category == 'SSAFY_LIFE'")
                .build();
        log.debug("answer: {}", service.ragGeneration(userInput, searchRequest, "default_user_id"));
    }

    // category BOOK만 대상으로
    @Test
    void bookTest() {
        String userInput = "N+1 문제를 다루는 책 추천해줘";
        var searchRequest = SearchRequest.builder()
                .query(userInput)
                .topK(2)
                .filterExpression("category == 'BOOK'")
                .build();
        log.debug("answer: {}", service.ragGeneration(userInput, searchRequest, "book_user"));
    }
}
