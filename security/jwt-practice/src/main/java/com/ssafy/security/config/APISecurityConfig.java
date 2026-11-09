package com.ssafy.security.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.ssafy.security.jwt.JWTAuthenticationFilter;
import com.ssafy.security.jwt.JWTVerificationFilter;
import com.ssafy.security.jwt.SecurityExceptionHandlingFilter;
import com.ssafy.security.member.CustomUserDetailService;

/**
 * TODO: 06 API 처리용 SecurityFilterChain
 *
 * FilterChainProxy
 *  ├─ @Order(1) apiSecurityFilterChain : /api/**  → 토큰(JWT)으로 인증
 *  └─ @Order(2) securityFilterChain    : 나머지    → session(form login)으로 인증
 *
 * API chain 필터 배치 (UsernamePasswordAuthenticationFilter 자리 기준)
 *   ... LogoutFilter → SecurityExceptionHandlingFilter → JWTVerificationFilter → JWTAuthenticationFilter → ...
 */
@Configuration
public class APISecurityConfig {

    /**
     * JWTAuthenticationFilter 생성자가 주입받을 AuthenticationManager 빈
     * (UserDetailsService 빈 + PasswordEncoder 빈으로 DaoAuthenticationProvider가 구성됨)
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    @Order(1)   // 낮을수록 우선순위 높음
    public SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http,
            @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfig,
            CustomUserDetailService userDetailsService,
            JWTAuthenticationFilter authFilter,
            JWTVerificationFilter jwtVerifyFilter,
            SecurityExceptionHandlingFilter exceptionFilter) throws Exception {

        // ① 범위·CORS·UserDetailsService·CSRF·세션
        http.securityMatcher("/api/**")                       // 이 SecurityFilterChain의 동작 범위
            .cors(cors -> cors.configurationSource(corsConfig)) // 명시적 CORS 설정 사용
            .userDetailsService(userDetailsService)           // 사용할 UserDetailsService
            .csrf(csrf -> csrf.disable())                     // 세션·쿠키를 안 쓰므로 CSRF 무력화
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션 사용 안 함

        // ② 인가 규칙
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/v1/etc/**", "/api/v1/auth/**").permitAll()  // 바로 통과
                .anyRequest().authenticated());

        // ③ 필터 배치
        http.addFilterBefore(jwtVerifyFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(authFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(exceptionFilter, JWTVerificationFilter.class);

        // (교재 외 추가) 토큰 없이 보호된 API 요청 시 기본값 403 대신 401 응답
        http.exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        return http.build();
    }
}
