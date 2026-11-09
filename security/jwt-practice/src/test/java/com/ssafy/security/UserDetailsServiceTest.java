package com.ssafy.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * [Authentication 실습] UserDetailsService 테스트 - JWT 실습부터는 DB 기반 CustomUserDetailService
 */
@SpringBootTest
public class UserDetailsServiceTest {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    PasswordEncoder passEncoder;

    @Test
    void 사용자생성_테스트() {
        UserDetails user = userDetailsService.loadUserByUsername("admin@ssafy.com");
        Assertions.assertNotNull(user);
        // 평문 "1234"와 저장된 암호문 비교
        Assertions.assertTrue(passEncoder.matches("1234", user.getPassword()));
        // roles("ADMIN") → ROLE_ADMIN
        Assertions.assertTrue(user.getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    // ---- 아래는 교재 외 추가 확인 ----

    @Test
    @DisplayName("비밀번호는 {bcrypt} 접두사가 붙은 암호문으로 저장된다")
    void 암호화_형식_테스트() {
        UserDetails user = userDetailsService.loadUserByUsername("user@ssafy.com");
        Assertions.assertTrue(user.getPassword().startsWith("{bcrypt}"));
        Assertions.assertNotEquals("1234", user.getPassword());
    }

    @Test
    @DisplayName("없는 사용자는 UsernameNotFoundException")
    void 없는사용자_테스트() {
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nobody@ssafy.com"));
    }
}
