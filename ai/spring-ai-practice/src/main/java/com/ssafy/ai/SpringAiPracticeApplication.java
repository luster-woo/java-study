package com.ssafy.ai;

import org.springframework.ai.model.google.genai.autoconfigure.chat.GoogleGenAiChatAutoConfiguration;
import org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 직접 만든 빈이 있는 자동 설정은 제외
 *  - GoogleGenAiChatAutoConfiguration  : GMS base-url을 쓰려고 ChatModel을 직접 구성 (TODO 01, config/ChatModelConfig)
 *  - RedisVectorStoreAutoConfiguration : MetadataField를 추가하려고 VectorStore를 직접 구성 (TODO 08, config/VectorStoreConfig)
 */
@SpringBootApplication(exclude = {
        GoogleGenAiChatAutoConfiguration.class,
        RedisVectorStoreAutoConfiguration.class })
public class SpringAiPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiPracticeApplication.class, args);
    }
}
