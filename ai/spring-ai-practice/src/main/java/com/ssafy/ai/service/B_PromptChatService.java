package com.ssafy.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * TODO: 04 SystemPrompt와 UserPrompt 활용
 * 시스템 프롬프트를 파일(Resource)로 빼고 PromptTemplate 변수({location}, {language}, {topic})를 채움
 *  - 기본 system : location  (build 시점)
 *  - 호출 system : language  (호출 시점 — 기본 system 텍스트를 재정의, param은 합쳐짐)
 *  - user        : topic
 */
@Service
public class B_PromptChatService {

    private final ChatClient openAiClient;
    private final Resource systemPrompt;

    public B_PromptChatService(OpenAiChatModel openAi,
            @Value("classpath:/prompts/default_penpal_prompt.txt") Resource systemPrompt) {
        this.systemPrompt = systemPrompt;
        this.openAiClient = ChatClient.builder(openAi)
                .defaultSystem(spec -> spec.text(systemPrompt).param("location", "제주"))
                .build();
    }

    public String chat(String language, String topic) {
        String user = "상대방과의 관계: {topic}에게 보낼 따뜻한 안부 편지를 5줄 내외로 작성해줘.";
        return openAiClient.prompt()
                .system(spec -> spec.text(systemPrompt).param("language", language))
                .user(spec -> spec.text(user).param("topic", topic))
                .call().content();
    }
}
