package com.ssafy.security.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * /api/** : 누구나 접근 가능 (anyRequest().permitAll())
 * 로그인 상태면 principal이 들어오고, 아니면 null
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/hello")
    public Map<String, Object> hello(Principal principal) {
        return Map.of(
                "message", "hello security",
                "user", principal == null ? "anonymous" : principal.getName());
    }
}
