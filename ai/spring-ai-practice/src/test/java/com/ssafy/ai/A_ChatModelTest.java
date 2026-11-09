package com.ssafy.ai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO: 02 ChatModel 동작 확인
 * ChatModel 빈이 2개(openAiChatModel, googleGenAiChatModel) → @Qualifier로 골라 주입
 * getResult() → Generation, getOutput() → AssistantMessage, getText() → 답변 문자열
 */
@Slf4j
@SpringBootTest
class A_ChatModelTest {

    @Autowired
    @Qualifier("openAiChatModel")
    private ChatModel openAiChatModel;

    @Autowired
    @Qualifier("googleGenAiChatModel")
    private ChatModel googleGenAiChatModel;

    @Test
    void openAiChatClientInjectionTest() {
        assertThat(openAiChatModel).isNotNull();
        printModelInfo(openAiChatModel);
    }

    @Test
    void geminiChatClientInjectionTest() {
        assertThat(googleGenAiChatModel).isNotNull();
        printModelInfo(googleGenAiChatModel);
    }

    private void printModelInfo(ChatModel model) {
        log.debug("Model Implementation: {}", model.getClass().getSimpleName());
        var options = model.getDefaultOptions();
        log.debug("Model Name: {}", options.getModel());
        log.debug("Temperature: {}", options.getTemperature());
        log.debug("Full Options: {}", options);

        var response = model.call(new Prompt("안녕? 너는 어떤 모델이야?"));
        var gen = response.getResult();          // 첫 번째 Generation
        log.debug("response: {}", gen.getOutput().getText());
        log.debug("usage: {}", response.getMetadata().getUsage());   // 토큰 사용량
    }
}
