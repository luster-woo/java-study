package com.ssafy.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * TODO: 13 RAG — QuestionAnswerAdvisor
 * 질문이 오면 VectorStore에서 관련 문서를 검색(Retrieval) → 질문에 붙여(Augment) → 답변 생성(Generation)
 *
 * | VectorStore                          | 담긴 것             | 쓰는 Advisor                  |
 * | eVectorStore (customEmbeddingVector) | ETL로 적재한 문서   | QuestionAnswerAdvisor         |
 * | hVectorStore (chatHistoryVectorStore)| 대화 기록           | VectorStoreChatMemoryAdvisor  |
 */
@Service
public class G_RAGService {

    private final ChatClient chatClient;
    private final VectorStore eVectorStore;
    private final VectorStore hVectorStore;

    public G_RAGService(@Qualifier("openAiChatClientBuilder") ChatClient.Builder builder,
                        @Qualifier("customEmbeddingVector") VectorStore eVectorStore,
                        @Qualifier("chatHistoryVectorStore") VectorStore hVectorStore) {
        this.chatClient = builder.clone().build();
        this.eVectorStore = eVectorStore;
        this.hVectorStore = hVectorStore;
    }

    public String ragGeneration(String userInput, SearchRequest searchRequest, String conversationId) {
        var qaAdvisor = QuestionAnswerAdvisor.builder(eVectorStore)
                .searchRequest(searchRequest)
                .build();
        return chatClient.prompt()
                .user(userInput)
                .advisors(qaAdvisor,
                          VectorStoreChatMemoryAdvisor.builder(hVectorStore)
                                  .conversationId(conversationId)
                                  .build())
                .call()
                .content();
    }
}
