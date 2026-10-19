package com.ssafy.fw_06_rest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// [04] CORS 전역 설정: "요청을 받는 서버" 가 이 출처는 허용한다고 응답 헤더(Access-Control-Allow-Origin)로 알려 줌
// 실습: http://localhost:8080 화면에서 http://127.0.0.1:8080/api/v1/... 로 fetch (도메인 글자가 달라서 다른 출처)
//      → 이 설정이 있으면 성공, 아래 @Configuration 을 주석 처리하고 재시작하면 CORS 에러
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**")                                  // 이 URL 들에 대해
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")    // 이 HTTP 메서드를
                .allowedOrigins("http://localhost:8080");                  // 이 출처에게 허용 (교재는 "*" = 모두 허용)
    }
}
