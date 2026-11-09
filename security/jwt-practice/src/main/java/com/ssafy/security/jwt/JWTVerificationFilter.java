package com.ssafy.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ssafy.security.member.CustomUserDetailService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO: 04 요청마다 토큰 검증 → SecurityContext에 인증 정보 저장
 * - 세션 대신 이 필터가 "로그인 상태"를 만들어 줌
 * - 토큰이 없으면 그냥 통과 (login, refresh, permitAll 경로 등) → 이후 인가에서 걸러짐
 * - 토큰에 문제가 있으면 getClaims()가 JwtException → SecurityExceptionHandlingFilter가 처리
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JWTVerificationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final CustomUserDetailService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token == null) {                  // 인증 토큰이 없는 요청 → 그냥 통과
            filterChain.doFilter(request, response);
            return;
        }

        // 1. 토큰에서 claims 획득 - 예외 없이 통과하면 신뢰할 만한 사용자
        Claims claims = jwtUtil.getClaims(token);

        // (교재 외 추가) refresh token도 email claim이 있어 그대로 통과되므로 access token인지 확인
        if (!"accessToken".equals(claims.getSubject())) {
            throw new JwtException("access token이 아닙니다: " + claims.getSubject());
        }

        // 2. 실제 사용자 정보 조회
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.get("email").toString());

        // 3. Authentication 생성 → SecurityContextHolder에 저장 (Controller 등에서 사용)
        //    권한 목록을 넘기는 3인자 생성자 = 인증 완료 상태의 토큰
        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("JWT 인증 성공: {}", userDetails.getUsername());

        // 4. 다음 filter 호출
        filterChain.doFilter(request, response);
    }

    /** Authorization: Bearer {token} 에서 token만 추출 */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);   // "Bearer " 공백 포함 7글자
        }
        return null;   // 토큰 없음 (교재의 throw null 은 NPE를 던지므로 return null)
    }
}
