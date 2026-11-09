package com.ssafy.web.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Filter 실습 2 — URI와 처리 시간을 콘솔에 기록 (적용 경로: /api/* 만)
 */
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        long start = System.currentTimeMillis();
        // ServletRequest에는 URI 정보가 없음 → HttpServletRequest로 형변환
        String uri = ((HttpServletRequest) req).getRequestURI();
        System.out.println("[Filter 시작] " + uri);                                  // 전처리

        chain.doFilter(req, res);                                                    // ← 이 시점 전후

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("[Filter 종료] " + uri + " → " + elapsed + "ms");         // 후처리
    }
}
