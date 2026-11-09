package com.ssafy.web.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * Filter 실습 2 — 모든 요청을 UTF-8로 강제 (적용 경로: 전체)
 * 참고: Boot가 이미 CharacterEncodingFilter(UTF-8)를 등록해 두므로 실무에선 직접 만들 일이 드묾 → 원리 확인용
 */
public class EncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 전처리 — 인코딩 설정 (파라미터를 읽기 전에 해야 효과가 있음 → 가장 앞 순서)
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        // 교재에는 아래 줄도 있지만 뺌:
        // 필터에서 Content-Type을 text/html로 미리 박아 두면 @RestController가 JSON을 못 씀
        // → /api/users 가 500 "No converter for [...] with preset Content-Type 'text/html;charset=UTF-8'"
        // response.setContentType("text/html;charset=UTF-8");

        // 다음 필터 / 서블릿 호출
        chain.doFilter(request, response);
    }
}
