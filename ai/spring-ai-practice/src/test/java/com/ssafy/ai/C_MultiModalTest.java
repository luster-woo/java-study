package com.ssafy.ai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;

import com.ssafy.ai.service.E_MultiModalService;

import lombok.extern.slf4j.Slf4j;

/** TODO: 06 객체 탐지 — images/sample.png (집·나무·해·사과를 그린 1.4KB 그림) */
@Slf4j
@SpringBootTest
class C_MultiModalTest {

    @Autowired E_MultiModalService multiModalService;

    @Test
    void imageDescTest() {
        String answer = multiModalService.imageDescGeneration(
                "사진 속 물체를 모두 찾아 설명해줘.",
                MimeTypeUtils.IMAGE_PNG,
                new ClassPathResource("images/sample.png"));
        log.debug("answer: {}", answer);
        assertThat(answer).isNotBlank();
    }
}
