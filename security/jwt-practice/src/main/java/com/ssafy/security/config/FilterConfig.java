package com.ssafy.security.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ssafy.security.jwt.JWTAuthenticationFilter;
import com.ssafy.security.jwt.JWTVerificationFilter;
import com.ssafy.security.jwt.SecurityExceptionHandlingFilter;

/**
 * (교재 외 추가) 필터를 @Component로 등록하면 Spring Boot가 "일반 서블릿 필터"로도 자동 등록함
 * → Security chain 밖에서 한 번 더 실행될 수 있으므로 서블릿 필터 등록은 끄고, Security chain 안에서만 쓰게 함
 */
@Configuration
public class FilterConfig {

    @Bean
    FilterRegistrationBean<JWTVerificationFilter> jwtVerificationFilterRegistration(JWTVerificationFilter filter) {
        FilterRegistrationBean<JWTVerificationFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setEnabled(false);
        return reg;
    }

    @Bean
    FilterRegistrationBean<JWTAuthenticationFilter> jwtAuthenticationFilterRegistration(JWTAuthenticationFilter filter) {
        FilterRegistrationBean<JWTAuthenticationFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setEnabled(false);
        return reg;
    }

    @Bean
    FilterRegistrationBean<SecurityExceptionHandlingFilter> securityExceptionFilterRegistration(SecurityExceptionHandlingFilter filter) {
        FilterRegistrationBean<SecurityExceptionHandlingFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setEnabled(false);
        return reg;
    }
}
