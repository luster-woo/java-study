package com.ssafy.fw_05_mvc.controller;

import com.ssafy.fw_05_mvc.service.SimpleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// TODO: 07 테스트 클래스 작성
@WebMvcTest(value = SimpleController.class)   // 테스트할 대상 Controller (웹 계층만 로딩하는 slice test)
@Slf4j
public class SimpleControllerTest {

    @Autowired
    MockMvc mock;                             // 가상의 웹 환경을 위한 객체

    @MockitoBean
    SimpleService service;                    // 실제 서비스를 테스트할 계획은 없음 → 가짜 객체

    @BeforeEach
    public void setupMockBean() {
        when(service.echo("Hello")).thenReturn("echo: Hello");   // Mock 객체의 동작 정의
    }

    @Test
    public void echoTest() throws Exception {
        // 1. 요청 만들기
        MockHttpServletRequestBuilder request = get("/simple/echo").param("msg", "Hello");
        // 2. 실행
        ResultActions actions = mock.perform(request);
        // 3. 검증
        actions.andExpect(handler().handlerType(SimpleController.class))
               .andExpect(handler().methodName("echo"))
               .andExpect(view().name("index"))
               .andExpect(forwardedUrl("/WEB-INF/views/index.jsp"))   // prefix + view 이름 + suffix
               .andExpect(model().attribute("msg", "echo: Hello"))
               .andExpect(status().isOk());
    }

    @Test
    public void jsonTest() throws Exception {
        mock.perform(get("/simple/json"))
            .andExpect(handler().methodName("json"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", equalTo("hong gil dong")))
            .andExpect(jsonPath("$.age", equalTo(10)));
    }
}
