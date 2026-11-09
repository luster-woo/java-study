package com.ssafy.web.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ssafy.web.user.UserDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 실습 7-B — /board/** 접근 시 로그인 여부 확인
 * 세션에 loginUser가 없으면 /user/login 으로 redirect(302)
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        System.out.println("  [LoginInterceptor] 로그인 체크 " + request.getRequestURI());

        // 세션 가져오기 (없으면 새로 만들지 않고 null)
        HttpSession session = request.getSession(false);

        // 세션 없음 → 로그인 페이지로
        if (session == null) {
            System.out.println("  [LoginInterceptor] 세션 없음 → /user/login");
            response.sendRedirect(request.getContextPath() + "/user/login");
            return false;                       // 컨트롤러 실행 막음
        }
        // loginUser 속성 확인
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            System.out.println("  [LoginInterceptor] loginUser 없음 → /user/login");
            response.sendRedirect(request.getContextPath() + "/user/login");
            return false;
        }
        // 로그인 상태 → 통과
        return true;
    }
}
