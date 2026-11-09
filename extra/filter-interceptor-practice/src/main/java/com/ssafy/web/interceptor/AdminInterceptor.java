package com.ssafy.web.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ssafy.web.user.UserDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 실습 7-C — /board/delete/** 에만 적용, 관리자가 아니면 403
 *   로그인 안 함        → redirect(302)  "로그인부터"
 *   로그인했지만 USER   → 403            인증 O, 권한(인가) X
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req,
            HttpServletResponse res, Object handler) throws Exception {
        System.out.println("  [AdminInterceptor] 관리자 체크 " + req.getRequestURI());

        HttpSession session = req.getSession(false);
        if (session == null) {
            // 교재는 res.sendRedirect("/user/login") — 컨텍스트 경로가 있을 때를 대비해 7-B처럼 맞춤
            res.sendRedirect(req.getContextPath() + "/user/login");
            return false;
        }
        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null || !user.isAdmin()) {
            System.out.println("  [AdminInterceptor] 관리자 아님 → 403");
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자만 가능");
            return false;
        }
        return true;
    }
}
