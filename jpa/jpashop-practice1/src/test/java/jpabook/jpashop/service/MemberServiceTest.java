package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // 스프링 컨테이너를 띄우고 테스트 (없으면 @Autowired 실패)
@Transactional  // 테스트마다 트랜잭션 시작, 끝나면 롤백 → 반복 가능한 테스트
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
        //Given
        Member member = new Member();
        member.setName("kim");

        //When
        Long saveId = memberService.join(member);

        //Then
        assertEquals(member, memberRepository.findOne(saveId)); // 같은 영속성 컨텍스트 → 같은 인스턴스
        // INSERT 로그가 안 보이는 이유: 롤백이라 flush를 안 함 → 보려면 @Rollback(false) 또는 em.flush()
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        //Given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //When
        memberService.join(member1);

        //Then
        assertThrows(IllegalStateException.class, () -> memberService.join(member2)); // 예외가 발생해야 한다
    }
}
