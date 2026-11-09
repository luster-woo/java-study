package com.ssafy.security.member;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository repository;

    /** email로 회원 조회 - 없으면 null */
    public Member selectDetail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public List<Member> selectAll() {
        return repository.findAll();
    }

    /** 회원 정보 수정 (refresh token 저장/삭제에 사용) */
    @Transactional
    public Member update(Member member) {
        return repository.save(member);   // id가 있으므로 merge
    }

    @Transactional
    public Member insert(Member member) {
        return repository.save(member);
    }
}
