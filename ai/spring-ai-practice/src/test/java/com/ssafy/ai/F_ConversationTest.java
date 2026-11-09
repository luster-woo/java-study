package com.ssafy.ai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ssafy.ai.service.F_ConversationService;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO: 10 ~ 12 대화 기억 — 같은 질문 3개를 기억 장치만 바꿔서 보내 보기
 *   1. 내 이름은 홍길동이야.
 *   2. 내 이름으로 3행시 지어줘.
 *   3. 나랑 이름이 같은 사람 중 유명인 1사람만 이야기해줘.
 */
@Slf4j
@SpringBootTest
class F_ConversationTest {

    @Autowired F_ConversationService service;

    // TODO: 10 — 기억 없음: 2·3번에서 이름을 모름
    @Test
    void simpleGenerationTest() {
        Object answer1 = service.simpleGeneration("내 이름은 홍길동이야.");
        Object answer2 = service.simpleGeneration("내 이름으로 3행시 지어줘.");
        Object answer3 = service.simpleGeneration("나랑 이름이 같은 사람 중 유명인 1사람만 이야기해줘.");
        log.debug("answer1: {}", answer1);
        log.debug("answer2: {}", answer2);
        log.debug("answer3: {}", answer3);
    }

    // TODO: 10 — MessageChatMemoryAdvisor + MySQL: 이름을 기억함
    //   MySQL에서 SELECT * FROM ssafylive.SPRING_AI_CHAT_MEMORY; 로 저장된 대화 확인
    @Test
    void messageChatMemoryAdvisorTest() {
        String id = "memory_user";
        service.clearMemory(id);                           // 이전 실행 기억 지우고 시작
        Object answer1 = service.memoryGeneration("내 이름은 홍길동이야.", id);
        Object answer2 = service.memoryGeneration("내 이름으로 3행시 지어줘.", id);
        Object answer3 = service.memoryGeneration("나랑 이름이 같은 사람 중 유명인 1사람만 이야기해줘.", id);
        log.debug("answer1: {}", answer1);
        log.debug("answer2: {}", answer2);
        log.debug("answer3: {}", answer3);

        // 다른 conversationId는 기억을 공유하지 않음
        Object other = service.memoryGeneration("내 이름이 뭐라고 했지?", "other_user");
        log.debug("other_user: {}", other);
    }

    // TODO: 11·12 — VectorStoreChatMemoryAdvisor + Redis(chat-history): 유사한 과거 대화를 검색해서 씀
    //   Redis Insight에서 chat:* 키 확인
    @Test
    void vectorStoreChatMemoryAdvisorTest() {
        String id = "history_user";
        Object answer1 = service.historyGeneration("내 이름은 홍길동이야.", id);
        Object answer2 = service.historyGeneration("내 이름으로 3행시 지어줘.", id);
        Object answer3 = service.historyGeneration("나랑 이름이 같은 사람 중 유명인 1사람만 이야기해줘.", id);
        log.debug("answer1: {}", answer1);
        log.debug("answer2: {}", answer2);
        log.debug("answer3: {}", answer3);
    }
}
