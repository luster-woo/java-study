package com.ssafy.fw_05_mvc.controller;

import com.ssafy.fw_05_mvc.service.SimpleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/simple")
@Slf4j
@RequiredArgsConstructor
public class SimpleController {

    private final SimpleService service;   // 생성자 주입

    @RequestMapping              // → /simple
    public String home1() {
        log.debug("home 1 called");
        return "index";
    }

    @RequestMapping("/hello")    // → /simple/hello
    public String home2() {
        log.debug("home 2 called");
        return "index";
    }

    @GetMapping("/echo")         // → /simple/echo?msg=Hello
    public String echo(@RequestParam String msg, Model model) {
        // 1. 요청 분석: @RequestParam 으로 msg 파라미터 받기
        String echoData = service.echo(msg);     // 2. 비즈니스 로직
        model.addAttribute("msg", echoData);     // 3. 자료 저장
        return "index";                          // 4. View 이름
    }
    @GetMapping("/json")
    public @ResponseBody Map<String, Object> json() {
        return Map.of("name", "hong gil dong", "age", 10);
    }
}
