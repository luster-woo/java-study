package com.ssafy.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ssafy.security.jwt.JWTUtil;
import com.ssafy.security.member.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;

/**
 * [JWT 생성] JWTUtil 단위 테스트 - Spring 없이 직접 생성
 */
class JWTUtilTest {

    static final String SECRET = "your-very-long-and-secure-random-secret-key-here-12345678";

    JWTUtil jwtUtil = new JWTUtil(1, 2, SECRET);
    Member member = new Member("hong@ssafy.com", "홍길동", "{noop}1234", "USER");

    @Test
    @DisplayName("access token 생성 후 claims 조회")
    void accessTokenTest() {
        String token = jwtUtil.createAccessToken(member);
        System.out.println(token);   // jwt.io 에 붙여 넣어 확인해 보기

        Claims claims = jwtUtil.getClaims(token);
        assertEquals("accessToken", claims.getSubject());
        assertEquals("hong@ssafy.com", claims.get("email"));
        assertEquals("홍길동", claims.get("name"));
        assertEquals("USER", claims.get("role"));
    }

    @Test
    @DisplayName("refresh token은 email만 담음")
    void refreshTokenTest() {
        Claims claims = jwtUtil.getClaims(jwtUtil.createRefreshToken(member));
        assertEquals("refreshToken", claims.getSubject());
        assertEquals("hong@ssafy.com", claims.get("email"));
        assertNull(claims.get("role"));
    }

    @Test
    @DisplayName("role이 null이면 USER로 채워서 발급 (Map.of는 null 불가)")
    void nullRoleTest() {
        Member noRole = new Member("kim@ssafy.com", "김싸피", "{noop}1234", null);
        assertEquals("USER", jwtUtil.getClaims(jwtUtil.createAccessToken(noRole)).get("role"));
    }

    @Test
    @DisplayName("만료된 토큰 → ExpiredJwtException")
    void expiredTest() {
        String expired = jwtUtil.create("accessToken", -1, Map.of("email", "hong@ssafy.com"));  // 1분 전 만료
        assertThrows(ExpiredJwtException.class, () -> jwtUtil.getClaims(expired));
    }

    @Test
    @DisplayName("형식이 잘못된 토큰 → MalformedJwtException")
    void malformedTest() {
        assertThrows(MalformedJwtException.class, () -> jwtUtil.getClaims("not-a-jwt-token"));
    }

    @Test
    @DisplayName("다른 키로 서명된 토큰 → SignatureException")
    void signatureTest() {
        JWTUtil other = new JWTUtil(1, 2, "another-very-long-and-secure-random-secret-key-987654321");
        String token = other.createAccessToken(member);
        assertThrows(SignatureException.class, () -> jwtUtil.getClaims(token));
    }

    @Test
    @DisplayName("32바이트 미만 키 → WeakKeyException")
    void weakKeyTest() {
        assertThrows(WeakKeyException.class, () -> new JWTUtil(1, 2, "short-key"));
    }
}
