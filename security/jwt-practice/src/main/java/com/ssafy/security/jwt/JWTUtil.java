package com.ssafy.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ssafy.security.member.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * 토큰 생성·검증(claim 조회)
 */
@Component
@Slf4j
public class JWTUtil {

    private final long accessExpMin;
    private final long refreshExpMin;
    private final SecretKey key;

    public JWTUtil(@Value("${ssafy.jwt.access-expmin}") long accessExpMin,
                   @Value("${ssafy.jwt.refresh-expmin}") long refreshExpMin,
                   @Value("${ssafy.jwt.secret-string}") String secretKeyString) {
        this.accessExpMin = accessExpMin;
        this.refreshExpMin = refreshExpMin;
        // 문자열로부터 HMAC-SHA용 SecretKey 생성 (32바이트 미만이면 WeakKeyException)
        this.key = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        log.debug("jwt secret key: {}", Arrays.toString(key.getEncoded()));
    }

    // ===================== TODO: 03 JWT 생성 =====================

    /** access token : email, name, role 을 담음 */
    public String createAccessToken(Member member) {
        if (member.getRole() == null) {   // Map.of()는 null 값을 넣을 수 없음
            member.setRole("USER");
        }
        return create("accessToken", accessExpMin,
                Map.of("email", member.getEmail(), "name", member.getName(), "role", member.getRole()));
    }

    /** refresh token : 재발급용이므로 email만 담음 */
    public String createRefreshToken(Member member) {
        return create("refreshToken", refreshExpMin, Map.of("email", member.getEmail()));
    }

    /** access/refresh는 만료 시간과 claim만 다름 → 공통 메서드 */
    public String create(String subject, long expireMin, Map<String, Object> claims) {
        Date expireDate = new Date(System.currentTimeMillis() + 1000 * 60 * expireMin);
        return Jwts.builder()
                .subject(subject)        // 제목 (sub)
                .claims(claims)          // claim 추가
                .expiration(expireDate)  // 만료일 (exp)
                .signWith(key)           // 서명 - 키 길이에 맞는 알고리즘(HS256 등) 자동 선택
                .compact();              // header.payload.signature 로 직렬화
    }

    // ===================== TODO: 03 Claims 조회 =====================

    /**
     * 같은 키로 다시 서명해 비교 → 문제가 있으면 예외(JwtException 하위)
     * - ExpiredJwtException : 유효기간 지남
     * - MalformedJwtException : 형식 오류
     * - SignatureException(io.jsonwebtoken.security) : 서명 불일치(변조)
     */
    public Claims getClaims(String jwt) {
        JwtParser parser = Jwts.parser().verifyWith(key).build();
        Jws<Claims> jws = parser.parseSignedClaims(jwt);
        return jws.getPayload();
    }
}
