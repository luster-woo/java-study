package com.ssafy.web.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ssafy.web.filter.EncodingFilter;
import com.ssafy.web.filter.LoggingFilter;

/**
 * Filter 실습 2 — EncodingFilter(전체) + LoggingFilter(/api/*) 등록
 *
 * 최종 필터 순서 (FilterConfig와 합쳐서)
 *   1 EncodingFilter  /*
 *   2 FilterA         /*
 *   3 FilterB         /*
 *   4 FilterC         /*
 *   5 LoggingFilter   /api/*
 */
@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<EncodingFilter> encodingFilter() {
        FilterRegistrationBean<EncodingFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new EncodingFilter());
        bean.addUrlPatterns("/*");          // 전체 URL
        bean.setOrder(1);                   // 인코딩을 가장 먼저
        return bean;
    }

    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LoggingFilter());
        bean.addUrlPatterns("/api/*");      // /api/ 아래만 (/api/users, /api/users/1 모두 해당)
        bean.setOrder(5);                   // 교재는 2, A·B·C 뒤에 두려고 5
        return bean;
    }
}
