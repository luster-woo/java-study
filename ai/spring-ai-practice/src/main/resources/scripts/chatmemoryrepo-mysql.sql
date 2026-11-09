-- JdbcChatMemoryRepository용 테이블 (테이블만 있으면 CRUD는 Spring AI가 제공)
-- 교재 스키마에 IF NOT EXISTS만 추가 (initialize-schema=always라 재시작마다 실행되므로)
CREATE TABLE IF NOT EXISTS SPRING_AI_CHAT_MEMORY (
    conversation_id VARCHAR(255) NOT NULL,
    type            VARCHAR(50)  NOT NULL,
    content         TEXT         NOT NULL,
    metadata        JSON,
    `timestamp`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_conversation_id (conversation_id)
);
