package com.ssafy.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ssafy.web.interceptor.AdminInterceptor;
import com.ssafy.web.interceptor.LoggingInterceptor;
import com.ssafy.web.interceptor.LoginInterceptor;

import lombok.RequiredArgsConstructor;

/**
 * 실습 7-D — 인터셉터 3개 다중 등록 (등록 순서 = 실행 순서)
 * 교재의 @Autowired 필드 주입 대신 final + 생성자 주입(@RequiredArgsConstructor) — 동작은 같음
 *
 * 인터셉터 경로 패턴: /* = 한 단계, /** = 하위 전체  (필터의 /* 와 규칙이 다름!)
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoggingInterceptor loggingInterceptor;
    private final LoginInterceptor loginInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ① 로깅 – 모든 요청 (정적 리소스 제외)
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/resources/**", "/css/**", "/js/**");

        // ② 로그인 체크 – 게시판
        //    교재의 .excludePathPatterns("/")는 "/"가 애초에 /board/**에 해당하지 않아 하는 일이 없음 → 생략
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/board/**");

        // ③ 관리자 체크 – 삭제만
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/board/delete/**");
    }
}
