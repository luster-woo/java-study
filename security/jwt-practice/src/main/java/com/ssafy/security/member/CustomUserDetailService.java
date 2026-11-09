package com.ssafy.security.member;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * DB 기반 UserDetailsService
 * - Authentication 실습의 InMemoryUserDetailsManager를 대신함 (UserDetailsService 빈은 이것 하나만)
 * - web(form login), api(JWT) 두 FilterChain이 모두 사용
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberService.selectDetail(email);
        if (member == null) {
            throw new UsernameNotFoundException(email);
        }
        return new CustomUserDetails(member);
    }
}
