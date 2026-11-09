package com.ssafy.security.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.security.jwt.JWTUtil;
import com.ssafy.security.member.Member;
import com.ssafy.security.member.MemberService;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * /api/v1/auth/** : permitAll
 * - login 은 JWTAuthenticationFilter가 필터 단계에서 처리 (컨트롤러 없음)
 * - refresh, logout 은 Authorization 헤더 없이 Refresh-Token 헤더로 요청
 *   → JWTVerificationFilter 검증을 타지 않음 (만료된 access token을 실으면 바로 TOKEN_ERROR)
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JWTUtil jwtUtil;
    private final MemberService memberService;

    /**
     * TODO: 09 access token 재발급 (Refresh Token Rotation : refresh token도 새로 발급)
     * required = false : 헤더가 아예 없을 때도 메서드 안의 null 검사로 400 응답
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(
            @RequestHeader(value = "Refresh-Token", required = false) String refreshToken) {
        // 1. refresh token 확인
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Refresh token is required"));
        }
        // 2. 검증 및 정보 취득 (만료·변조 시 JwtException → 아래 @ExceptionHandler)
        Map<String, Object> claims = jwtUtil.getClaims(refreshToken);
        String email = (String) claims.get("email");
        if (email == null) {
            throw new JwtException("Invalid refresh token: email claim missing");
        }
        // 3. 정보 validation : DB에 저장된 refresh token과 같은지
        Member selected = memberService.selectDetail(email);
        if (selected == null || selected.getRefresh() == null || !selected.getRefresh().equals(refreshToken)) {
            return invalidRefreshToken();
        }
        // 4. 신규 토큰 발행
        String newAccessToken = jwtUtil.createAccessToken(selected);
        String newRefreshToken = jwtUtil.createRefreshToken(selected);
        // 5. DB 업데이트 → 예전 refresh token은 DB 값과 달라 재사용 불가
        selected.setRefresh(newRefreshToken);
        memberService.update(selected);
        log.debug("토큰 재발급: {}", email);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken));
    }

    /**
     * TODO: 11 logout : DB의 refresh token 삭제 → 이후 재발급 불가
     * 단, 이미 발급된 access token은 만료 전까지 여전히 유효(stateless) → access token을 짧게 두는 이유
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Refresh-Token", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Refresh token is required"));
        }
        Map<String, Object> claims = jwtUtil.getClaims(refreshToken);
        String email = (String) claims.get("email");   // refresh token 생성 시 넣은 claim 키
        if (email == null) {
            throw new JwtException("Invalid refresh token: email claim missing");
        }
        Member member = memberService.selectDetail(email);
        if (member != null) {
            member.setRefresh(null);        // 저장된 refresh token 삭제
            memberService.update(member);
        }
        return ResponseEntity.ok(Map.of("accessToken", "", "refreshToken", ""));
    }

    /**
     * (교재 외 추가) 컨트롤러에서 던진 JwtException은 DispatcherServlet 안에서 발생
     * → SecurityExceptionHandlingFilter에는 ServletException으로 감싸져 500이 되므로 여기서 401로 바꿈
     * message는 SPA 실습의 응답 인터셉터가 구분하는 INVALID_REFRESH_TOKEN
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwtException(JwtException e) {
        log.debug("refresh token 오류: {}", e.getMessage());
        return invalidRefreshToken();
    }

    private ResponseEntity<?> invalidRefreshToken() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("status", 401, "message", "INVALID_REFRESH_TOKEN"));
    }
}
