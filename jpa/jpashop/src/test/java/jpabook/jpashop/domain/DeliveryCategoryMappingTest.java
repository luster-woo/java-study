package jpabook.jpashop.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 6장 실전 예제 3 확인용 — 일대일(주문-배송), 다대다(카테고리-상품), 셀프 참조(카테고리 계층)
 */
@SpringBootTest
@Transactional
class DeliveryCategoryMappingTest {

    @Autowired
    EntityManager em;

    @Test
    void 주문_배송_일대일() {
        Delivery delivery = new Delivery();
        delivery.setAddress(new Address("서울", "강남대로", "12345"));
        delivery.setStatus(DeliveryStatus.READY);
        em.persist(delivery);

        Order order = new Order();
        order.setDelivery(delivery);             // 주인(Order.delivery)에 값 → ORDERS.DELIVERY_ID 저장
        order.setStatus(OrderStatus.ORDER);
        em.persist(order);

        em.flush();
        em.clear();

        Order findOrder = em.find(Order.class, order.getId());
        assertThat(findOrder.getDelivery().getAddress().getCity()).isEqualTo("서울");

        Delivery findDelivery = em.find(Delivery.class, delivery.getId());
        assertThat(findDelivery.getOrder().getId()).isEqualTo(order.getId()); // 역방향도 조회됨
    }

    @Test
    void 카테고리_계층과_다대다() {
        Book item = new Book(); // Item은 abstract라 자식 타입으로 생성
        item.setName("JPA 책");
        em.persist(item);

        Category parent = new Category();
        parent.setName("도서");
        Category child = new Category();
        child.setName("IT");
        parent.addChildCategory(child);          // child.parent 세팅 → CATEGORY.PARENT_ID 저장
        child.addItem(item);                     // CATEGORY_ITEM 연결 테이블에 (CATEGORY_ID, ITEM_ID) INSERT
        em.persist(parent);
        em.persist(child);

        em.flush();
        em.clear();

        Category findChild = em.find(Category.class, child.getId());
        assertThat(findChild.getParent().getName()).isEqualTo("도서");
        assertThat(findChild.getItems()).extracting(Item::getName).containsExactly("JPA 책");

        Item findItem = em.find(Item.class, item.getId());
        assertThat(findItem.getCategories()).extracting(Category::getName).containsExactly("IT");
    }
}
