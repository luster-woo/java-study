package com.ssafy.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ssafy.web.user.UserDto;
import com.ssafy.web.user.UserRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/login")
    public String loginForm() {
        System.out.println("    >> UserController.loginForm()");
        return "user/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String id, @RequestParam String password,
                        HttpSession session, Model model) {
        System.out.println("    >> UserController.login() id=" + id);
        UserDto user = userRepository.findById(id);
        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "아이디 또는 비밀번호가 틀렸습니다.");
            return "user/login";
        }
        // LoginInterceptor / AdminInterceptor가 꺼내 보는 속성
        session.setAttribute("loginUser", user);
        return "redirect:/board/list";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        System.out.println("    >> UserController.logout()");
        session.invalidate();
        return "redirect:/";
    }
}
