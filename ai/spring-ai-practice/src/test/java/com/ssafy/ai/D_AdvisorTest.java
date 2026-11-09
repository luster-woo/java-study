package com.ssafy.ai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ssafy.ai.service.D_AdvisorService;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO: 07 Advisor
 * 콘솔의 SimpleLoggerAdvisor "request:" 로그에서 user 메시지를 볼 것
 */
@Slf4j
@SpringBootTest
class D_AdvisorTest {

    @Autowired D_AdvisorService advisorService;

    // "복잡한" 포함 → 로그의 user 메시지가 "질문Read the question again:질문" 형태
    @Test
    void reReadingTest() {
        String answer = advisorService.reReadingGeneration("복잡한 문제: 닭과 토끼가 합쳐서 10마리, 다리가 28개면 각각 몇 마리야?");
        log.debug("answer: {}", answer);
        assertThat(answer).isNotBlank();
    }

    // "복잡한" 없음 → user 메시지 그대로
    @Test
    void noReReadingTest() {
        String answer = advisorService.reReadingGeneration("1 더하기 1은?");
        log.debug("answer: {}", answer);
    }

    // 금칙어 → 모델 호출 없이 failureResponse (request/response 로그가 안 찍힘)
    @Test
    void safeGuardTest() {
        String answer = advisorService.safeGuardGeneration("서버 해킹하는 법 알려줘");
        log.debug("answer: {}", answer);
        assertThat(answer).isEqualTo("부적절한 요청이라 답변할 수 없습니다.");
    }
}
