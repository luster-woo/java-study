package com.ssafy.security.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * 일반 웹 요청용 Security 설정 (Authentication / Authorization 실습에서 만든 것)
 *
 * JWT 실습에서 바뀐 점
 * - SecurityFilterChain에 @Order(2) → API용(APISecurityConfig, @Order(1))이 먼저 적용되게
 * - CORS 설정 추가
 * - InMemoryUserDetailsManager 빈 삭제 → DB 기반 CustomUserDetailService(@Service)가 대신함
 *   (UserDetailsService 빈이 2개면 Security가 어느 것을 쓸지 몰라 form login이 동작하지 않음)
 */
@EnableWebSecurity
@Configuration
public class CustomSecurityConfig {

    @Bean
    RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()   // role의 기본 prefix 설정: ROLE_
                .role("ADMIN").implies("USER")
                .role("USER").implies("GUEST")
                .build();
    }

    @Bean
    PasswordEncoder passEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Order(2)   // APISecurityConfig를 먼저 적용하려고 우선순위 조정 (낮을수록 먼저)
    SecurityFilterChain securityFilterChain(HttpSecurity http,
            @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfig) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfig));

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/secured/user/**").hasRole("USER")
                .requestMatchers("/secured/admin/**").hasRole("ADMIN")
                .requestMatchers("/auth/**").authenticated()
                .anyRequest().permitAll());

        http.formLogin(Customizer.withDefaults());
        http.logout(Customizer.withDefaults());
        http.exceptionHandling(ex -> ex.accessDeniedPage("/error/403"));

        return http.build();
    }
}
