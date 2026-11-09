package com.ssafy.web.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * Filter 실습 1 — 필터 체이닝
 * chain.doFilter() 앞 = 전처리, 뒤 = 후처리
 */
public class FilterA implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("A: 필터 전처리");
        chain.doFilter(request, response);      // 다음 필터(B)로
        System.out.println("A: 필터 후처리");
    }
}
