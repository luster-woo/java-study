package com.ssafy.fw_06_rest.service;

import com.ssafy.fw_06_rest.dto.Member;
import com.ssafy.fw_06_rest.exception.MemberNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// DB 없이 메모리(Map)에 저장하는 간단한 서비스 → 서버를 재시작하면 데이터가 사라짐
// (DB 연동, @Transactional 실습은 tx 패키지의 TxMemberService 참고)
@Service
public class MemberService {

    private final Map<Integer, Member> store = new ConcurrentHashMap<>();
    private final AtomicInteger seq = new AtomicInteger();   // 회원 번호 자동 증가

    public int registMember(Member member) {
        member.setMno(seq.incrementAndGet());
        store.put(member.getMno(), member);
        return 1;   // 저장된 행 수 (교재의 mapper 반환값 흉내)
    }

    public List<Member> getMembers() {
        return new ArrayList<>(store.values());
    }

    public Member getMember(int mno) {
        Member member = store.get(mno);
        if (member == null) {
            throw new MemberNotFoundException(mno);
        }
        return member;
    }

    public Member updateMember(int mno, Member member) {
        getMember(mno);              // 없으면 예외
        member.setMno(mno);
        store.put(mno, member);      // PUT = 완전 교체
        return member;
    }

    public void deleteMember(int mno) {
        if (store.remove(mno) == null) {
            throw new MemberNotFoundException(mno);
        }
    }
}
