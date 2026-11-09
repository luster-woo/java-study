package com.ssafy.ai.config;

import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.genai.Client;
import com.google.genai.types.HttpOptions;

/**
 * TODO: 01 사용자 정의 ChatModel 구성
 * Google GenAI 자동 설정은 end point(base-url)를 지정하지 않음 → GMS를 쓰려고 직접 구성
 * (OpenAI는 application.properties만으로 자동 설정 → openAiChatModel 빈)
 */
@Configuration
public class ChatModelConfig {

    @Value("${spring.ai.google.genai.api-key}")
    private String genAiApiKey;

    @Value("${spring.ai.google.genai.base-url}")
    private String genAiBaseUrl;

    @Value("${spring.ai.google.genai.chat.options.model}")
    private String genAiModel;

    @Bean
    GoogleGenAiChatModel googleGenAiChatModel() {
        // 1. Low level API 클라이언트 생성
        Client client = Client.builder().apiKey(genAiApiKey)
                .httpOptions(HttpOptions.builder().baseUrl(genAiBaseUrl).build())  // GMS
                // .httpOptions(HttpOptions.builder().build())                     // 일반(Google 직접)
                .build();
        // 2. 필요한 옵션 재정의
        GoogleGenAiChatOptions options = GoogleGenAiChatOptions.builder()
                .model(genAiModel)
                .temperature(0.7)
                .maxOutputTokens(1000)
                .build();
        // 3. 모델 생성
        return GoogleGenAiChatModel.builder()
                .genAiClient(client)
                .defaultOptions(options)
                .build();
    }
}
