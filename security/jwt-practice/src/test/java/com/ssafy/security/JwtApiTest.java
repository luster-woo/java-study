package com.ssafy.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;
import com.ssafy.security.jwt.JWTUtil;

/**
 * [Spring Security와의 연동 / refresh token] API chain(@Order(1)) 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
class JwtApiTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JWTUtil jwtUtil;

    /** 로그인 → {accessToken, refreshToken} */
    Map<String, String> login(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", email)          // form 파라미터 (JSON 아님)
                        .param("password", password))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        return Map.of("accessToken", JsonPath.read(body, "$.accessToken"),
                      "refreshToken", JsonPath.read(body, "$.refreshToken"));
    }

    @Test
    @DisplayName("로그인 성공 → access/refresh token 발급")
    void loginSuccessTest() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login").param("email", "user@ssafy.com").param("password", "1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @DisplayName("로그인 실패 → 401 (BadCredentialsException을 SecurityExceptionHandlingFilter가 처리)")
    void loginFailTest() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login").param("email", "user@ssafy.com").param("password", "wrong"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("permitAll 경로는 토큰 없이, 나머지는 토큰이 있어야 접근")
    void authorizationTest() throws Exception {
        mockMvc.perform(get("/api/v1/etc/hello")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/members")).andExpect(status().isUnauthorized());

        String accessToken = login("user@ssafy.com", "1234").get("accessToken");
        mockMvc.perform(get("/api/v1/members").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(get("/api/v1/members/me").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@ssafy.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("변조·만료·refresh 토큰을 Authorization에 실으면 401 TOKEN_ERROR")
    void tokenErrorTest() throws Exception {
        Map<String, String> tokens = login("user@ssafy.com", "1234");

        // 변조 : 서명 마지막 글자 바꾸기
        String access = tokens.get("accessToken");
        String forged = access.substring(0, access.length() - 2) + (access.endsWith("AA") ? "BB" : "AA");
        mockMvc.perform(get("/api/v1/members").header("Authorization", "Bearer " + forged))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("TOKEN_ERROR"));

        // 만료
        String expired = jwtUtil.create("accessToken", -1, Map.of("email", "user@ssafy.com"));
        mockMvc.perform(get("/api/v1/members").header("Authorization", "Bearer " + expired))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("TOKEN_ERROR"));

        // refresh token을 access token처럼 사용 (JWTVerificationFilter의 subject 검사)
        mockMvc.perform(get("/api/v1/members").header("Authorization", "Bearer " + tokens.get("refreshToken")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("TOKEN_ERROR"));
    }

    @Test
    @DisplayName("refresh → 새 토큰 발급, 예전 refresh token은 재사용 불가(Rotation)")
    void refreshTest() throws Exception {
        String oldRefresh = login("user@ssafy.com", "1234").get("refreshToken");
        // exp는 초 단위 → 같은 초에 만들면 같은 토큰이 나오므로 1초 이상 기다림
        Thread.sleep(1100);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/refresh").header("Refresh-Token", oldRefresh))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();
        String newAccess = JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");

        // 새 access token으로 API 호출
        mockMvc.perform(get("/api/v1/members").header("Authorization", "Bearer " + newAccess))
                .andExpect(status().isOk());

        // 예전 refresh token → DB 값과 달라 401
        mockMvc.perform(post("/api/v1/auth/refresh").header("Refresh-Token", oldRefresh))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("INVALID_REFRESH_TOKEN"));
    }

    @Test
    @DisplayName("refresh 요청 오류 : 헤더 없음 400, 만료된 refresh token 401")
    void refreshErrorTest() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh")).andExpect(status().isBadRequest());

        String expired = jwtUtil.create("refreshToken", -1, Map.of("email", "user@ssafy.com"));
        mockMvc.perform(post("/api/v1/auth/refresh").header("Refresh-Token", expired))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("INVALID_REFRESH_TOKEN"));
    }

    @Test
    @DisplayName("logout 후에는 refresh 불가, 하지만 access token은 만료 전까지 유효(stateless)")
    void logoutTest() throws Exception {
        Map<String, String> tokens = login("admin@ssafy.com", "1234");

        mockMvc.perform(post("/api/v1/auth/logout").header("Refresh-Token", tokens.get("refreshToken")))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/refresh").header("Refresh-Token", tokens.get("refreshToken")))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/members").header("Authorization", "Bearer " + tokens.get("accessToken")))
                .andExpect(status().isOk());
    }
}
