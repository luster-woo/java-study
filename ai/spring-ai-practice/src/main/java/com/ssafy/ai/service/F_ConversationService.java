package com.ssafy.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * TODO: 10 ~ 12 대화 기억
 *
 * | 메서드             | 기억 장치                                         | 꺼내는 기억                       |
 * | simpleGeneration   | 없음                                              | 없음 → 두 번째 질문부터 이름을 모름 |
 * | memoryGeneration   | MessageChatMemoryAdvisor + ChatMemory(MySQL)      | conversationId의 최근 10개        |
 * | historyGeneration  | VectorStoreChatMemoryAdvisor + Redis(chat-history)| 현재 질문과 유사한 과거 대화       |
 */
@Service
public class F_ConversationService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final VectorStore historyVectorStore;

    public F_ConversationService(@Qualifier("openAiChatClientBuilder") ChatClient.Builder builder,
                                 ChatMemory chatMemory,
                                 @Qualifier("chatHistoryVectorStore") VectorStore historyVectorStore) {
        this.chatClient = builder.clone().build();
        this.chatMemory = chatMemory;
        this.historyVectorStore = historyVectorStore;
    }

    // TODO: 10 대화 맥락 유지의 필요성 — 기억 없이 user 메시지만 보냄
    public String simpleGeneration(String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    // TODO: 10 ChatMemory와 MessageChatMemoryAdvisor — conversationId로 사용자별 대화 분리
    public String memoryGeneration(String userInput, String conversationId) {
        return chatClient.prompt()
                .user(userInput)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(conversationId)
                        .build())
                .call()
                .content();
    }

    // TODO: 12 VectorStoreChatMemoryAdvisor — 대화 기억을 VectorStore에 저장하고 유사도로 검색
    public String historyGeneration(String userInput, String conversationId) {
        return chatClient.prompt()
                .user(userInput)                                     // user 메시지 전달
                .advisors(VectorStoreChatMemoryAdvisor.builder(historyVectorStore)
                        .conversationId(conversationId)
                        .build())
                .call()                                              // model 호출
                .content();
    }

    /** 테스트 반복 실행용 — MySQL에 저장된 대화 기억 지우기 */
    public void clearMemory(String conversationId) {
        chatMemory.clear(conversationId);
    }
}
