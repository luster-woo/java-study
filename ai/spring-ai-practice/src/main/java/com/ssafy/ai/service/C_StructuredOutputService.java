package com.ssafy.ai.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.ssafy.ai.dto.Joke;

/**
 * TODO: 05 Structured Output — 응답을 T(자바 객체)로 받기
 *  - 저수준: BeanOutputConverter 직접 생성 → getFormat()으로 형식 지침 주입 → convert()
 *  - 고수준: entity() — 내부에서 같은 일을 해 줌
 */
@Service
public class C_StructuredOutputService {

    private final ChatClient genAiClient;

    public C_StructuredOutputService(@Qualifier("geminiChatClientBuilder") ChatClient.Builder builder) {
        this.genAiClient = builder.clone().build();
    }

    // 저수준: BeanOutputConverter 직접 사용
    public Joke generateRecordJoke1(String topic) {
        String user = "{topic} 과 관련된 농담과 해설을 하나만 만들어줘. \n {format}";
        BeanOutputConverter<Joke> converter = new BeanOutputConverter<>(Joke.class);

        String response = genAiClient.prompt()
                .user(spec -> spec.text(user)
                        .param("topic", topic)
                        .param("format", converter.getFormat()))   // 포맷 지시어 주입
                .call().content();
        return converter.convert(response);                         // 수동 변환
    }

    // 고수준: entity()
    public Joke generateRecordJoke2(String topic) {
        String user = "{topic} 과 관련된 농담과 해설을 하나만 만들어줘.";
        return genAiClient.prompt()
                .user(spec -> spec.text(user).param("topic", topic))
                .call().entity(Joke.class);
    }

    // 제네릭 타입(List<Joke>)은 Class로 표현할 수 없음 → ParameterizedTypeReference
    public List<Joke> generateJokeList(String topic, int count) {
        String user = "{topic} 과 관련된 농담과 해설을 {count}개 만들어줘.";
        return genAiClient.prompt()
                .user(spec -> spec.text(user).param("topic", topic).param("count", count))
                .call().entity(new ParameterizedTypeReference<List<Joke>>() {});
    }
}
