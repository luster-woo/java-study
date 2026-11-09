package com.ssafy.ai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ssafy.ai.dto.Joke;
import com.ssafy.ai.service.A_SimpleChatService;
import com.ssafy.ai.service.B_PromptChatService;
import com.ssafy.ai.service.C_StructuredOutputService;

import lombok.extern.slf4j.Slf4j;

/** TODO: 03 ChatClient · TODO: 04 Prompt · TODO: 05 Structured Output */
@Slf4j
@SpringBootTest
class B_ChatClientTest {

    @Autowired A_SimpleChatService simpleChatService;
    @Autowired B_PromptChatService promptChatService;
    @Autowired C_StructuredOutputService structuredOutputService;

    // TODO: 03 — 같은 문장을 번역가(OpenAI, temperature 0.1) / 동요 작사가(Gemini, temperature 2.0)가 처리
    @Test
    void handleDocumentTest() {
        var result = simpleChatService.handleDocument("봄이 오면 산과 들에 꽃이 핀다");
        log.debug("openai: {}", result.get("openai"));
        log.debug("gemini: {}", result.get("gemini"));
        assertThat(result).containsKeys("openai", "gemini");
    }

    // TODO: 04 — 로그(SimpleLoggerAdvisor는 없음, 이 서비스는 직접 builder)로 결과만 확인
    @Test
    void promptChatTest() {
        String letter = promptChatService.chat("영어", "대학 동기");
        log.debug("letter:\n{}", letter);
        assertThat(letter).isNotBlank();
    }

    // TODO: 05 — 저수준(BeanOutputConverter) / 고수준(entity) 결과가 둘 다 Joke 객체
    @Test
    void structuredOutputTest() {
        Joke joke1 = structuredOutputService.generateRecordJoke1("자바");
        Joke joke2 = structuredOutputService.generateRecordJoke2("스프링");
        log.debug("joke1: {}", joke1);
        log.debug("joke2: {}", joke2);
        assertThat(joke1.content()).isNotBlank();
        assertThat(joke2.reason()).isNotBlank();

        var jokes = structuredOutputService.generateJokeList("JPA", 3);
        jokes.forEach(j -> log.debug("list: {}", j));
        assertThat(jokes).hasSize(3);
    }
}
