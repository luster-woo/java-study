package com.ssafy.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * [Authorization 실습] 인증 상태에 따른 페이지 접근 테스트 (web chain, @Order(2))
 * - 비로그인 → 3xx redirect (인증 문제)
 * - 권한 부족 → 403 (인가 문제)
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("인증 없는 상태에서 여러 엔드포인트 접근 테스트")
    void notAuthenticatedTest() throws Exception {
        // given
        var req = MockMvcRequestBuilders.get("/");
        // when & then
        mockMvc.perform(req).andExpect(MockMvcResultMatchers.status().isOk());

        req = MockMvcRequestBuilders.get("/secured/user");
        mockMvc.perform(req).andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    @DisplayName("USER 권한이 있는 사용자가 권한별 페이지에 접근하는 경우를 테스트")
    @WithMockUser(roles = "USER")   // 테스트용 권한의 사용자
    void userRoleTest() throws Exception {
        mockMvc.perform(get("/secured/user")).andExpect(status().isOk());
        mockMvc.perform(get("/secured/admin")).andExpect(status().isForbidden());
    }

    // ---------------- 아래는 교재 외 추가 테스트 ----------------

    @Test
    @DisplayName("비로그인 : /auth/**, /secured/** 는 로그인 페이지로")
    void notAuthenticatedDetailTest() throws Exception {
        mockMvc.perform(get("/auth/mypage"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        mockMvc.perform(get("/secured/admin"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/member/login-form")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN은 RoleHierarchy(ADMIN ⊃ USER) 때문에 USER 페이지도 접근 가능")
    @WithMockUser(roles = "ADMIN")
    void adminRoleTest() throws Exception {
        mockMvc.perform(get("/secured/admin")).andExpect(status().isOk());
        mockMvc.perform(get("/secured/user")).andExpect(status().isOk());
        mockMvc.perform(get("/auth/mypage")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GUEST는 로그인은 됐지만 USER 페이지 권한이 없어 403")
    @WithMockUser(roles = "GUEST")
    void guestRoleTest() throws Exception {
        mockMvc.perform(get("/auth/mypage")).andExpect(status().isOk());
        mockMvc.perform(get("/secured/user")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("권한 부족 시 error/403 화면으로 forward")
    @WithMockUser(roles = "USER")
    void forbiddenPageTest() throws Exception {
        mockMvc.perform(get("/secured/admin"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/error/403"));
    }

    @Test
    @DisplayName("InMemory 계정으로 실제 form 로그인")
    void formLoginTest() throws Exception {
        mockMvc.perform(formLogin().user("user@ssafy.com").password("1234"))
                .andExpect(authenticated().withUsername("user@ssafy.com").withRoles("USER"));

        mockMvc.perform(formLogin().user("user@ssafy.com").password("wrong"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("csrf 토큰 없는 POST /logout 은 403")
    @WithMockUser
    void logoutCsrfTest() throws Exception {
        mockMvc.perform(post("/logout")).andExpect(status().isForbidden());
        mockMvc.perform(post("/logout").with(csrf())).andExpect(status().is3xxRedirection());
    }
}
