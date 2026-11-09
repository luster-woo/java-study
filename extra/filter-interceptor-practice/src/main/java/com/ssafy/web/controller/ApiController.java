package com.ssafy.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.web.user.UserRepository;

import lombok.RequiredArgsConstructor;

/** Filter 실습 2 확인용 — /api/* 라서 LoggingFilter가 걸림 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public List<Map<String, String>> users() {
        System.out.println("    >> ApiController.users()");
        // 비밀번호는 빼고 응답
        return userRepository.findAll().stream()
                .map(u -> Map.of("id", u.getId(), "name", u.getName(), "role", u.getRole()))
                .toList();
    }
}
