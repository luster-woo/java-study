package com.ssafy.ai.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ssafy.ai.advisor.ReReadingAdvisor;

/**
 * TODO: 07 Advisor 적용
 *  - defaultAdvisors : 빌드 시 등록 → 모든 호출에 적용 (공용 빌더에 SimpleLoggerAdvisor가 이미 있음)
 *  - advisors(...)   : 호출 시 등록 → 그 호출에만 적용
 *
 * 체인 순서 (order 작을수록 먼저, 응답은 역순)
 *   SafeGuard(0) → ReReading(0) → SimpleLogger(MAX-1) → (내부) ChatModel 호출
 *   ※ 같은 order끼리는 순서 보장 X
 */
@Service
public class D_AdvisorService {

    private final ChatClient chatClient;

    public D_AdvisorService(@Qualifier("openAiChatClientBuilder") ChatClient.Builder builder) {
        this.chatClient = builder.clone()
                .defaultAdvisors(new ReReadingAdvisor())
                .build();
    }

    /** ReReadingAdvisor 확인 — "복잡한"이 들어간 질문이면 로그의 user 메시지에 질문이 두 번 들어감 */
    public String reReadingGeneration(String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    /** SafeGuardAdvisor (Built-in) — 금칙어가 있으면 모델을 호출하지 않고 failureResponse 반환 */
    public String safeGuardGeneration(String question) {
        var safeGuard = new SafeGuardAdvisor(
                List.of("해킹", "폭탄"),
                "부적절한 요청이라 답변할 수 없습니다.",
                0);
        return chatClient.prompt()
                .user(question)
                .advisors(safeGuard)                                  // 이 호출에만 적용
                .advisors(spec -> spec.param("some", "context"))      // 공유 context에 값 넣기
                .call()
                .content();
    }
}
