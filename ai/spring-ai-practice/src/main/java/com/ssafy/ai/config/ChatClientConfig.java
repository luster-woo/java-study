package com.ssafy.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;

/**
 * 모델별 ChatClient.Builder 빈 — 공통 system 프롬프트 + 로깅 advisor를 한 번에 적용
 * 서비스에서는 builder.clone() 해서 자기 설정만 덮어씀 (공용 빌더를 직접 바꾸면 다른 서비스에 영향)
 */
@Configuration
public class ChatClientConfig {

    @Value("classpath:/prompts/default_system_prompt.txt")
    private Resource systemPrompt;

    @Bean
    SimpleLoggerAdvisor simpleLoggerAdvisor() {
        // 다른 advisor가 바꾼 "최종 요청"을 보려고 맨 뒤(내부 advisor 바로 앞)
        return new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1);
    }

    @Bean
    ChatClient.Builder geminiChatClientBuilder(GoogleGenAiChatModel genAiChatModel, SimpleLoggerAdvisor sLogAdvisor) {
        return ChatClient.builder(genAiChatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(sLogAdvisor);
    }

    @Bean
    ChatClient.Builder openAiChatClientBuilder(OpenAiChatModel openAiChatModel, SimpleLoggerAdvisor sLogAdvisor) {
        return ChatClient.builder(openAiChatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(sLogAdvisor);
    }
}
