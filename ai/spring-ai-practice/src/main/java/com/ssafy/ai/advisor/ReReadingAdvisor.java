package com.ssafy.ai.advisor;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;

/**
 * TODO: 07 사용자 정의 Advisor — Re2(재 읽기) 기법
 * 질문에 "복잡한"이 들어가거나 1000글자를 넘으면 질문을 한 번 더 읽게 해서 추론 능력을 높임
 * BaseAdvisor 구현 → before()에서 요청 수정, after()는 그대로 반환
 */
public class ReReadingAdvisor implements BaseAdvisor {

    private static final String DEFAULT_RE2_ADVISE_TEMPLATE =
            "{re2_input_query}Read the question again:{re2_input_query}";

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String message = chatClientRequest.prompt().getUserMessage().getText();
        if (message.length() > 1_000 || message.contains("복잡한")) {
            String augmentedUserText = PromptTemplate.builder()
                    .template(DEFAULT_RE2_ADVISE_TEMPLATE)
                    .variables(Map.of("re2_input_query", message))
                    .build()
                    .render();
            // 요청은 불변 → mutate()로 user 메시지만 바꾼 새 요청 생성
            return chatClientRequest.mutate()
                    .prompt(chatClientRequest.prompt().augmentUserMessage(augmentedUserText))
                    .build();
        }
        return chatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        // 교재 코드에는 없지만 BaseAdvisor는 Ordered를 상속 → 구현해야 컴파일됨
        return 0;
    }
}
