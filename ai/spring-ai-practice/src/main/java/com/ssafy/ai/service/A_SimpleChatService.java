package com.ssafy.ai.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

/**
 * TODO: 03 ChatClient 구성 — 모델 직접 지정
 * ChatClient.builder(model) / 호출 시점의 .system(), .options()는 defaultXXX를 재정의
 */
@Service
public class A_SimpleChatService {

    private final ChatClient openAiClient;
    private final ChatClient geminiClient;

    public A_SimpleChatService(OpenAiChatModel openAi, GoogleGenAiChatModel gemini) {
        this.openAiClient = ChatClient.builder(openAi).defaultSystem("당신은 전문 번역가입니다.").build();
        this.geminiClient = ChatClient.builder(gemini).defaultSystem("당신은 요약 전문가입니다.").build();
    }

    public Map<String, String> handleDocument(String doc) {
        String result1 = openAiClient.prompt(doc + "를 처리해줘.")
                .options(ChatOptions.builder().temperature(0.1).build())   // 보수적
                .call().content();

        String result2 = geminiClient.prompt(doc + "를 처리해줘.")
                .system("당신은 동요 작사 전문가입니다.")                   // defaultSystem 재정의
                .options(ChatOptions.builder().temperature(2.0).build())   // 창의적
                .call().content();

        return Map.of("openai", result1, "gemini", result2);
    }
}
