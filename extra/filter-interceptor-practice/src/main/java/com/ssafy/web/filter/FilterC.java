package com.ssafy.web.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/** Filter 실습 1 — FilterA와 같은 모양 (출력 글자만 C) */
public class FilterC implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("C: 필터 전처리");
        chain.doFilter(request, response);      // 다음 필터(LoggingFilter) 또는 DispatcherServlet으로
        System.out.println("C: 필터 후처리");
    }
}
