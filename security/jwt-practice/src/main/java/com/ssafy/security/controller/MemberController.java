package com.ssafy.security.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.security.member.CustomUserDetails;
import com.ssafy.security.member.MemberDto;
import com.ssafy.security.member.MemberService;

import lombok.RequiredArgsConstructor;

/**
 * /api/v1/members/** : authenticated → 유효한 access token이 있어야 접근 가능
 */
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public List<MemberDto> list() {
        return memberService.selectAll().stream().map(MemberDto::from).toList();
    }

    /** JWTVerificationFilter가 SecurityContext에 넣은 principal을 그대로 받음 */
    @GetMapping("/me")
    public MemberDto me(@AuthenticationPrincipal CustomUserDetails user) {
        return MemberDto.from(user.getMember());
    }
}
