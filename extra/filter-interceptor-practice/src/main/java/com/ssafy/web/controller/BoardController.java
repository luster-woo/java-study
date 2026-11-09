package com.ssafy.web.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * /board/**        → LoginInterceptor 통과해야 들어옴
 * /board/delete/** → AdminInterceptor까지 통과해야 들어옴
 * → 컨트롤러에는 로그인·권한 체크 코드가 없음 (공통 관심사를 인터셉터로 뺀 것)
 */
@Controller
@RequestMapping("/board")
public class BoardController {

    // 실습용 메모리 게시판 (재시작하면 초기화)
    private final Map<Integer, String> boards = new ConcurrentSkipListMap<>(Map.of(
            1, "Filter는 서블릿 컨테이너 영역",
            2, "Interceptor는 스프링 MVC 영역",
            3, "삭제는 관리자만 가능"));

    @GetMapping("/list")
    public String list(Model model) {
        System.out.println("    >> BoardController.list()");
        model.addAttribute("boards", boards);
        return "board/list";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        System.out.println("    >> BoardController.delete() id=" + id);
        boards.remove(id);
        return "redirect:/board/list";
    }
}
