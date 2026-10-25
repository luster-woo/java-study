package jpabook.jpashop.domain;

import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 8장 실전 예제 5 확인용 — 지연 로딩(프록시) + 영속성 전이(cascade)
 */
@SpringBootTest
@Transactional
class OrderCascadeLazyTest {

    @Autowired
    EntityManager em;

    @Test
    void 주문만_persist해도_주문상품과_배송이_같이_저장된다() {
        Member member = new Member();
        member.setName("member1");
        em.persist(member);                    // Member, Item은 cascade 대상 아님 → 직접 persist

        Book book = new Book();
        book.setName("JPA 책");
        em.persist(book);

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(book);
        orderItem.setCount(1);

        Delivery delivery = new Delivery();
        delivery.setAddress(new Address("서울", "강남대로", "12345"));

        Order order = new Order();
        order.setMember(member);
        order.addOrderItem(orderItem);
        order.setDelivery(delivery);
        em.persist(order);                     // ★ persist는 이거 하나 → INSERT가 ORDERS, ORDER_ITEM, DELIVERY 3개 나감

        em.flush();
        em.clear();

        Order findOrder = em.find(Order.class, order.getId());
        assertThat(findOrder.getOrderItems()).hasSize(1);
        assertThat(findOrder.getDelivery().getAddress().getCity()).isEqualTo("서울");
    }

    @Test
    void 지연_로딩이면_연관_엔티티는_프록시로_들어온다() {
        Member member = new Member();
        member.setName("member1");
        em.persist(member);

        Order order = new Order();
        order.setMember(member);
        em.persist(order);

        em.flush();
        em.clear();

        Order findOrder = em.find(Order.class, order.getId());   // SELECT ORDERS만 (MEMBER 조인 X)

        Member proxy = findOrder.getMember();
        System.out.println("member class = " + proxy.getClass()); // ...Member$HibernateProxy...
        assertThat(Hibernate.isInitialized(proxy)).isFalse();     // 아직 DB 조회 안 함

        proxy.getName();                                          // 이 시점에 SELECT MEMBER
        assertThat(Hibernate.isInitialized(proxy)).isTrue();
    }
}
