package com.ssafy.ai.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore.MetadataField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPooled;

/**
 * TODO: 08 VectorStore 빈 생성 / TODO: 11 대화 기록 전용 VectorStore
 * Redis에는 table 개념이 없음 → 용도별로 인덱스·prefix를 따로 둔 VectorStore 2개
 *
 *  | 빈 이름                | 인덱스(기본값)   | 담는 것             | 쓰는 곳                       |
 *  | customEmbeddingVector  | spring-ai-index  | ETL로 적재한 문서   | QuestionAnswerAdvisor (RAG)   |
 *  | chatHistoryVectorStore | chat-history     | 대화 기록           | VectorStoreChatMemoryAdvisor  |
 *
 * 둘 다 VectorStore 타입 → 주입할 때 @Qualifier로 골라야 함
 */
@Configuration
public class VectorStoreConfig {

    @Bean
    JedisPooled jedisPooled(@Value("${ssafy.redis.host}") String host,
                            @Value("${ssafy.redis.port}") int port) {
        return new JedisPooled(host, port);
    }

    @Bean
    RedisVectorStore customEmbeddingVector(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .initializeSchema(true)                 // 선택: schema 초기화 여부(기본 false)
                .metadataFields(                        // 선택: filtering에 쓸 metadata field
                        MetadataField.tag("category"),
                        MetadataField.numeric("meta_num"),
                        MetadataField.text("meta_txt"))
                .build();
    }

    @Bean
    RedisVectorStore chatHistoryVectorStore(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName("chat-history")              // Chat 전용 인덱스
                .prefix("chat:")
                .metadataFields(
                        MetadataField.tag("conversationId"),   // 검색에 필요한 ID
                        MetadataField.text("timestamp"))
                .initializeSchema(true)
                .build();
    }
}
