package com.ssafy.security.jwt;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO: 05 커스텀 필터들의 예외 집중 처리
 * - 필터는 DispatcherServlet 앞에서 동작 → @ControllerAdvice가 잡지 못함 → 예외 처리 필터를 앞쪽에 둠
 * - filterChain.doFilter()를 try로 감싸 뒤쪽 필터(JWTVerificationFilter, JWTAuthenticationFilter)의 예외를 받음
 */
@Component
@Slf4j
public class SecurityExceptionHandlingFilter extends OncePerRequestFilter {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            if (e instanceof JwtException) {                    // 토큰 만료·형식 오류·변조
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, "TOKEN_ERROR");
            } else if (e instanceof BadCredentialsException) {  // 로그인 실패
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            } else {                                            // 기타
                setErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, String.valueOf(e.getMessage()));
            }
        }
    }

    private void setErrorResponse(HttpServletResponse response, HttpStatus status, String message)
            throws IOException {
        log.debug("커스텀 필터에서 발생한 예외 처리: {}, {}", status, message);
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        Map<String, Object> errorDetails = Map.of("status", status.value(), "message", message);
        response.getWriter().write(mapper.writeValueAsString(errorDetails));
    }
}
