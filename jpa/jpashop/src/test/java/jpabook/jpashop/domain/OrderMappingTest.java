package jpabook.jpashop.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 5장 실전 예제 2 확인용 — FK 대신 객체 참조로 바뀌어서 객체 그래프 탐색이 되는지 본다.
 * (기본편 강의의 JpaMain 코드를 스프링 부트 테스트로 옮긴 것)
 */
@SpringBootTest
@Transactional // 테스트 끝나면 롤백
class OrderMappingTest {

    @Autowired
    EntityManager em;

    @Test
    void 객체_그래프_탐색() {
        // given
        Member member = new Member();
        member.setName("member1");
        em.persist(member);

        Book item = new Book(); // Item은 abstract라 자식 타입으로 생성
        item.setName("JPA 책");
        item.setPrice(10000);
        item.setStockQuantity(10);
        em.persist(item);

        Order order = new Order();
        order.setMember(member);                 // 주인(Order.member)에 값 → ORDERS.MEMBER_ID 저장
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.ORDER);
        em.persist(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(10000);
        orderItem.setCount(2);
        order.addOrderItem(orderItem);           // 편의 메서드: order.orderItems + orderItem.order 둘 다 세팅
        em.persist(orderItem);

        em.flush(); // INSERT 쿼리 실행
        em.clear(); // 1차 캐시 비우기 → 아래 find는 DB에서 새로 읽어옴

        // when
        Order findOrder = em.find(Order.class, order.getId());

        // then: 4장에선 findOrder.getMemberId()로 em.find를 한 번 더 해야 했음
        assertThat(findOrder.getMember().getName()).isEqualTo("member1");
        assertThat(findOrder.getOrderItems()).hasSize(1);
        assertThat(findOrder.getOrderItems().get(0).getItem().getName()).isEqualTo("JPA 책");

        // 역방향(mappedBy)도 조회는 된다 — 값은 주인 쪽 FK로 채워짐
        Member findMember = em.find(Member.class, member.getId());
        assertThat(findMember.getOrders()).hasSize(1);
    }

    @Test
    void 주인이_아닌_쪽에만_넣으면_FK가_null() {
        Member member = new Member();
        member.setName("member1");
        em.persist(member);

        Order order = new Order();
        member.getOrders().add(order);           // 역방향(가짜 매핑)에만 넣음 — 가장 많이 하는 실수
        em.persist(order);

        em.flush();
        em.clear();

        Order findOrder = em.find(Order.class, order.getId());
        assertThat(findOrder.getMember()).isNull(); // ORDERS.MEMBER_ID = null 로 저장됨
    }
}
