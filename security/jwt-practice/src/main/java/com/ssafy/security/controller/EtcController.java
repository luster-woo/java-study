package com.ssafy.security.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * /api/v1/etc/** : permitAll → 토큰 없이 통과
 */
@RestController
@RequestMapping("/api/v1/etc")
public class EtcController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "토큰 없이 접근 가능한 API");
    }
}
