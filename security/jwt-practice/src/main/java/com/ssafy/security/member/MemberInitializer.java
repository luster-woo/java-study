package com.ssafy.security.member;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * 실습용 기본 회원 등록 (Authentication 실습의 InMemory 계정을 DB로 옮김)
 */
@Component
@RequiredArgsConstructor
public class MemberInitializer implements CommandLineRunner {

    private final MemberService memberService;
    private final PasswordEncoder passEncoder;

    @Override
    public void run(String... args) {
        if (memberService.selectDetail("admin@ssafy.com") == null) {
            memberService.insert(new Member("admin@ssafy.com", "관리자", passEncoder.encode("1234"), "ADMIN"));
        }
        if (memberService.selectDetail("user@ssafy.com") == null) {
            memberService.insert(new Member("user@ssafy.com", "사용자", passEncoder.encode("1234"), "USER"));
        }
    }
}
