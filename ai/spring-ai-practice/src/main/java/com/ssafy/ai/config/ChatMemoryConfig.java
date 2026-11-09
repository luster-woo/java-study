package com.ssafy.ai.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO: 10 ChatMemory 구성
 * jdbc starter를 넣으면 JdbcChatMemoryRepository가 자동 구성됨 → 그걸로 id당 최근 10개만 유지하는 ChatMemory
 */
@Configuration
public class ChatMemoryConfig {

    @Bean
    ChatMemory chatMemory(ChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(10)
                .build();
    }
}
