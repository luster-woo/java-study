package com.ssafy.security.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.security.member.CustomUserDetails;
import com.ssafy.security.member.Member;
import com.ssafy.security.member.MemberService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * TODO: 05 로그인 처리 → 성공 시 토큰 발급
 * - UsernamePasswordAuthenticationFilter를 확장 → AuthenticationManager → UserDetailsService 인증 과정을 그대로 활용
 * - POST /api/v1/auth/login, 파라미터 email / password (form-urlencoded)
 * - 필터에서 응답까지 끝내므로 login 컨트롤러는 필요 없음
 */
@Component
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;
    private final ObjectMapper mapper = new ObjectMapper();

    public JWTAuthenticationFilter(AuthenticationManager authManager,
                                   MemberService memberService, JWTUtil jwtUtil) {
        super(authManager);                                // 조상 생성자 호출 필요
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
        this.setFilterProcessesUrl("/api/v1/auth/login");  // 로그인 처리 URL
        this.setUsernameParameter("email");                // email을 username으로 사용
        this.setPasswordParameter("password");
    }

    /** 로그인 성공 : JWT 발행 → response로 json 전달 */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) {
        Map<String, String> result = new HashMap<>();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.getMember();

        // TODO: 05 access token 발급
        String accessToken = jwtUtil.createAccessToken(member);
        result.put("accessToken", accessToken);

        // TODO: 08 refresh token 추가 발급 + 회원 정보(DB)에 저장 → 재발급 요청 때 대조
        String refreshToken = jwtUtil.createRefreshToken(member);
        member.setRefresh(refreshToken);
        memberService.update(member);
        result.put("refreshToken", refreshToken);

        handleResult(response, result, HttpStatus.OK);
    }

    /** 로그인 실패 : 여기서 응답하지 않고 다시 던짐 → SecurityExceptionHandlingFilter 한 곳에서 처리 */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) {
        throw failed;
    }

    private void handleResult(HttpServletResponse response, Map<String, String> data, HttpStatus status) {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String jsonResponse = mapper.writeValueAsString(data);
            response.setStatus(status.value());
            response.getWriter().write(jsonResponse);
        } catch (IOException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
