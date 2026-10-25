package jpabook.jpashop.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 9장 실전 예제 6 확인용 — 임베디드 값 타입(Address)
 */
@SpringBootTest
@Transactional
class AddressValueTypeTest {

    @Autowired
    EntityManager em;

    @Test
    void 임베디드_값_타입_저장과_조회() {
        Member member = new Member();
        member.setName("kim");
        member.setAddress(new Address("서울", "강남대로", "12345"));
        em.persist(member);

        em.flush();
        em.clear();

        // MEMBER 테이블의 CITY/STREET/ZIPCODE 컬럼에서 읽어 와 Address 객체로 조립됨
        Member findMember = em.find(Member.class, member.getId());
        assertThat(findMember.getAddress().getCity()).isEqualTo("서울");
        assertThat(findMember.getAddress().fullAddress()).isEqualTo("서울 강남대로 12345");
    }

    @Test
    void 값_타입은_equals로_비교() {
        Address a = new Address("서울", "강남대로", "12345");
        Address b = new Address("서울", "강남대로", "12345");

        assertThat(a == b).isFalse();     // 동일성(참조) 비교: 다른 인스턴스
        assertThat(a).isEqualTo(b);       // 동등성(값) 비교: equals 재정의했으므로 같음
    }

    @Test
    void 회원_주소를_배송지로_쓰기_불변이라_공유해도_안전() {
        Address address = new Address("서울", "강남대로", "12345");

        Member member = new Member();
        member.setAddress(address);
        em.persist(member);

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress()); // 같은 인스턴스를 공유
        em.persist(delivery);

        // 회원이 이사 → 기존 Address를 수정하는 게 아니라 새 Address로 교체
        // (setter가 없으니 address.setCity(...)는 애초에 컴파일 안 됨 → 배송지가 같이 바뀌는 부작용 원천 차단)
        member.setAddress(new Address("부산", "해운대로", "54321"));

        em.flush();
        em.clear();

        Member findMember = em.find(Member.class, member.getId());
        Delivery findDelivery = em.find(Delivery.class, delivery.getId());
        assertThat(findMember.getAddress().getCity()).isEqualTo("부산");
        assertThat(findDelivery.getAddress().getCity()).isEqualTo("서울"); // 배송지는 그대로
    }
}
