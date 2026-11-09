package com.ssafy.web.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ssafy.web.filter.FilterA;
import com.ssafy.web.filter.FilterB;
import com.ssafy.web.filter.FilterC;

/**
 * Filter 실습 1 — 필터 3개를 체인으로 등록
 * 전처리는 order 순서대로(A→B→C), 후처리는 역순(C→B→A)
 *
 * 교재는 A=1, B=2, C=3 이지만, 실습 2의 EncodingFilter(1)를 가장 앞에 두려고 한 칸씩 밀어서 2, 3, 4로 둠
 * (order는 "상대적인 크기"만 의미 있음 → 순서는 교재와 같음)
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<FilterA> filterARegistration() {
        FilterRegistrationBean<FilterA> registration = new FilterRegistrationBean<>();
        registration.setFilter(new FilterA());
        registration.addUrlPatterns("/*");      // 서블릿 패턴: /* = 모든 하위 경로
        registration.setOrder(2);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<FilterB> filterBRegistration() {
        FilterRegistrationBean<FilterB> registration = new FilterRegistrationBean<>();
        registration.setFilter(new FilterB());
        registration.addUrlPatterns("/*");
        registration.setOrder(3);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<FilterC> filterCRegistration() {
        FilterRegistrationBean<FilterC> registration = new FilterRegistrationBean<>();
        registration.setFilter(new FilterC());
        registration.addUrlPatterns("/*");
        registration.setOrder(4);
        return registration;
    }
}
