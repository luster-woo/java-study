package jpabook.jpashop.domain;

import jakarta.persistence.EntityManager;
import org.hibernate.collection.spi.PersistentBag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

// 2장 엔티티 설계 시 주의점: "컬렉션은 필드에서 초기화하자"
// 하이버네이트는 영속화할 때 컬렉션을 내장 컬렉션으로 감싸서 바꾼다
@SpringBootTest
@Transactional
class CollectionInitTest {

    @Autowired
    EntityManager em;

    @Test
    void 영속화하면_컬렉션이_하이버네이트_내장_컬렉션으로_바뀐다() {
        Member member = new Member();
        System.out.println(member.getOrders().getClass()); // class java.util.ArrayList
        assertThat(member.getOrders()).isInstanceOf(ArrayList.class);

        em.persist(member);
        System.out.println(member.getOrders().getClass()); // class org.hibernate.collection.spi.PersistentBag
        assertThat(member.getOrders()).isInstanceOf(PersistentBag.class);
        // PDF의 org.hibernate.collection.internal.PersistentBag은 Hibernate 5 기준.
        // Boot 3(Hibernate 6)에서는 패키지가 spi로 바뀌었다.
    }
}
