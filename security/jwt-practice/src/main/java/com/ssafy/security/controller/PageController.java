package com.ssafy.security.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 실습 화면 컨트롤러
 *
 * | 경로                 | 접근 조건                    |
 * |---------------------|-----------------------------|
 * | /, /index, /member/** | 누구나                       |
 * | /auth/**            | 로그인한 사용자만               |
 * | /secured/user/**    | USER role (ADMIN 포함)        |
 * | /secured/admin/**   | ADMIN role                  |
 * | /error/403          | 권한 부족 시 이동하는 화면       |
 *
 * 접근 제어는 컨트롤러가 아니라 SecurityFilterChain(AuthorizationFilter)이 담당
 * → 이전 실습의 SessionInterceptor 같은 세션 검사 코드가 필요 없음
 */
@Controller
public class PageController {

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    // 로그인 화면 (formLogin().loginPage("/member/login-form")로 연결하면 사용)
    @GetMapping("/member/login-form")
    public String loginForm() {
        return "member/login-form";
    }

    // 로그인만 하면 접근 가능
    @GetMapping("/auth/mypage")
    public String mypage(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", user);
        return "auth/mypage";
    }

    // USER role 필요
    @GetMapping("/secured/user")
    public String userPage() {
        return "secured/user";
    }

    // ADMIN role 필요
    @GetMapping("/secured/admin")
    public String adminPage() {
        return "secured/admin";
    }

    // 권한 부족(forbidden) 시 이동하는 화면
    @GetMapping("/error/403")
    public String forbidden() {
        return "error/403";
    }
}
