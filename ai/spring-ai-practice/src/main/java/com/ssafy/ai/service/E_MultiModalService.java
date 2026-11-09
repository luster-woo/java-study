package com.ssafy.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

/**
 * TODO: 06 객체 탐지 — 이미지 + 질문으로 요청하기 (gpt-4o-mini는 이미지 입력 지원)
 * user 스펙에서 media(mime, resource)로 이미지, text(question)으로 질문을 같이 넣음
 * ※ GMS는 100KB 미만 이미지만 지원
 */
@Service
public class E_MultiModalService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public E_MultiModalService(@Qualifier("openAiChatClientBuilder") ChatClient.Builder builder,
                               ChatMemory chatMemory) {
        this.chatClient = builder.clone().build();
        this.chatMemory = chatMemory;
    }

    public String imageDescGeneration(String question, MimeType mime, Resource resource) {
        return chatClient.prompt()
                .user(spec -> spec.media(mime, resource).text(question))
                .call()
                .content();
    }

    /** ChatBot용 — 사용자별 대화 기억(MessageChatMemoryAdvisor)까지 붙임 */
    public String chatBotImageDescription(Resource resource, MimeType mime, String message, String userId) {
        String question = (message == null || message.isBlank()) ? "이 이미지에 무엇이 있는지 설명해줘." : message;
        return chatClient.prompt()
                .user(spec -> spec.media(mime, resource).text(question))
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(userId)
                        .build())
                .call()
                .content();
    }
}
