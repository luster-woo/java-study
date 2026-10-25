package jpabook.jpashop.domain;

import jakarta.persistence.*;

@Entity
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    // 4장: Long orderId, Long itemId → 5장: 객체 참조로 변경
    // ORDER_ITEM 테이블에 FK 두 개가 다 있으므로 둘 다 이쪽이 주인 (N 쪽 = @ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY) // 8장: 기본 EAGER → LAZY
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY) // 상품은 여러 주문상품이 참조 → cascade 걸면 안 됨
    @JoinColumn(name = "ITEM_ID")
    private Item item; // Item → OrderItem 방향은 필요 없어서 단방향만

    private int orderPrice;
    private int count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(int orderPrice) {
        this.orderPrice = orderPrice;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
