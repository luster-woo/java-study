package com.ssafy.fw_06_rest.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 문서 경로: http://localhost:8080/swagger-ui.html
@Configuration
@OpenAPIDefinition(info = @Info(title = "day10 REST API 명세서",
                                description = "회원 관리 REST API 연습",
                                version = "v1"))
public class SwaggerConfig {

    // 화면 오른쪽 위 "Select a definition" 에서 그룹을 골라 볼 수 있음
    @Bean
    GroupedOpenApi memberOpenApi() {
        return GroupedOpenApi.builder()
                .group("Member 관련 API")
                .pathsToMatch("/api/v1/members/**")
                .build();
    }

    @Bean
    GroupedOpenApi etcOpenApi() {
        return GroupedOpenApi.builder()
                .group("RestClient 실습")
                .pathsToMatch("/api/v1/etc/**")
                .build();
    }

    @Bean
    GroupedOpenApi txOpenApi() {
        return GroupedOpenApi.builder()
                .group("Transaction 실습")
                .pathsToMatch("/api/v1/tx/**")
                .build();
    }

    @Bean
    GroupedOpenApi helloOpenApi() {
        return GroupedOpenApi.builder()
                .group("기타 API")
                .pathsToMatch("/hello", "/hello-json")
                .build();
    }
}
