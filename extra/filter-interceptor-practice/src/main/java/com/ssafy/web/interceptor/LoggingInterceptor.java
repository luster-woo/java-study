package com.ssafy.web.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 실습 7-A — 모든 요청의 URI와 처리 시간 기록
 * 인터셉터는 싱글톤 Bean → 시작 시각을 필드에 두면 동시 요청끼리 섞임 → request 속성에 저장
 */
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        System.out.println("[요청 시작] " + request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            Exception ex) throws Exception {
        long start = (Long) request.getAttribute("startTime");   // Object → Long → long (언박싱)
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("[요청 완료] " + request.getRequestURI()
                + " → " + elapsed + "ms");
    }
}
