package com.ssafy.fw_06_rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

// 비교용: 일반 @Controller 에서는 메서드마다 @ResponseBody 를 붙여야 데이터로 응답
@Controller
@Tag(name = "Hello API", description = "@ResponseBody 비교용")
public class HelloController {

    @GetMapping("/hello")
    @Operation(summary = "문자열 응답")
    @ResponseBody                         // 없으면 "hello" 라는 view 를 찾으러 감
    public String hello() {
        return "hello";                   // 문자열 그대로 응답
    }

    // [04] @CrossOrigin: 이 메서드만 다른 출처의 AJAX 요청 허용 (전역 설정 CorsConfig 는 /api/v1/** 만 허용)
    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/hello-json")
    @Operation(summary = "Map → JSON 응답")
    @ResponseBody
    public Map<String, Object> helloJson() {
        return Map.of("msg", "hello", "day", 10);   // Map → JSON
    }
}
