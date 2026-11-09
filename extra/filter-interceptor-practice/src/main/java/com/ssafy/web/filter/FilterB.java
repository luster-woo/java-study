package com.ssafy.web.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/** Filter 실습 1 — FilterA와 같은 모양 (출력 글자만 B) */
public class FilterB implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("B: 필터 전처리");
        chain.doFilter(request, response);      // 다음 필터(C)로
        System.out.println("B: 필터 후처리");
    }
}
