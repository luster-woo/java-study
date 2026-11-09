package com.ssafy.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * TODO: 03 사용자 정의 Security 설정
 * - @EnableWebSecurity : SecurityFilterChain 등 Security 관련 빈을 노출해 Spring Security 구성
 * - 이 클래스에서 UserDetailsService / SecurityFilterChain 빈을 직접 만들면 Boot 기본 설정(user 계정, 기본 chain)은 사라진다.
 */
@EnableWebSecurity
@Configuration
public class CustomSecurityConfig {

    // =========================================================================
    // [Authentication 실습]
    // =========================================================================

    /**
     * TODO: 03 계층적 권한 관리 : ADMIN ⊃ USER ⊃ GUEST
     * - ADMIN은 USER가 갈 수 있는 곳도, USER는 GUEST가 갈 수 있는 곳도 접근 가능
     */
    @Bean
    RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()   // role의 기본 prefix 설정: ROLE_
                .role("ADMIN").implies("USER")
                .role("USER").implies("GUEST")
                .build();
    }

    /**
     * TODO: 04 PasswordEncoder 빈 등록
     * - Spring Security에서 Credential(비밀번호)은 암호화 필수
     * - DelegatingPasswordEncoder : 암호문 앞에 {bcrypt} 처럼 암호 형식을 붙임 (기본 encoder는 bcrypt)
     */
    @Bean
    PasswordEncoder passEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // return new Argon2PasswordEncoder();  // 더 강력하지만 bouncycastle 의존성이 추가로 필요
    }

    /**
     * TODO: 05 사용자 인증을 위한 UserDetailsService (메모리 기반)
     * - username(Principal)은 로그인 id → 유일해야 함
     * - password(Credential)는 반드시 암호화해서 저장
     * - roles("ADMIN") → 실제 권한 이름은 ROLE_ADMIN
     *
     * 교재는 @ConditionalOnMissingBean(UserDetailsService.class)을 붙이지만,
     * 이 어노테이션은 자동 설정 클래스용이라 직접 만든 설정 클래스에서는 생략함.
     */
    @Bean
    UserDetailsService userDetailsService(PasswordEncoder pe) {
        UserDetails admin = User.builder()
                .username("admin@ssafy.com")      // Principal
                .password(pe.encode("1234"))      // Credential (암호화해서 저장)
                .roles("ADMIN")                   // Authority → ROLE_ADMIN
                .build();

        UserDetails user = User.builder()
                .username("user@ssafy.com")
                .password(pe.encode("1234"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    // =========================================================================
    // [Authorization 실습]
    // =========================================================================

    /**
     * TODO: 06 SecurityFilterChain 구성
     * - HttpSecurity의 메서드로 각 Security Filter의 동작을 제어
     * - 규칙은 위에서부터 검사하다가 처음 매칭된 곳에서 멈춤 → 세부 경로 먼저, 뭉뚱그린 경로는 마지막
     * - SecurityFilterChain 빈을 직접 등록하면 Boot 기본 설정이 무력화됨 → formLogin()을 다시 켜야 로그인 가능
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Authorization 필터 설정 : AuthorizationFilter
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/secured/user/**").hasRole("USER")    // USER role 필요 (ADMIN도 계층상 통과)
                .requestMatchers("/secured/admin/**").hasRole("ADMIN")  // ADMIN role 필요
                .requestMatchers("/auth/**").authenticated()            // auth는 인증만 필요
                .anyRequest().permitAll());                             // 이외의 경로는 그냥 통과

        // 기본 form login 활용 : UsernamePasswordAuthenticationFilter 설정
        http.formLogin(Customizer.withDefaults());
        // (다음 단계) 직접 만든 로그인 화면을 쓰려면 위 줄 대신 아래 사용
        // http.formLogin(form -> form
        //         .loginPage("/member/login-form")   // GET : 로그인 화면
        //         .loginProcessingUrl("/login")      // POST : 인증 처리 (UsernamePasswordAuthenticationFilter)
        //         .defaultSuccessUrl("/")
        //         .permitAll());

        // 기본 logout 활용 : LogoutFilter 설정
        http.logout(Customizer.withDefaults());

        // 권한 부족(403) 시 error/403 화면으로 (교재 표의 error/403 화면 연결 — 교재 코드엔 없는 추가 설정)
        http.exceptionHandling(ex -> ex.accessDeniedPage("/error/403"));

        return http.build();
    }
}
