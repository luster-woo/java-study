package jpabook.jpashop.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERS") // ORDER는 SQL 예약어(ORDER BY)라서 ORDERS로 바꿈
public class Order extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    // 4장: @Column(name = "MEMBER_ID") private Long memberId;  ← FK 값을 그대로 들고 있었음
    // 5장: 객체 참조로 변경. ORDERS 테이블에 FK가 있으니 여기가 연관관계의 주인
    // 8장 실전 예제 5: @ManyToOne은 기본이 EAGER → 전부 LAZY로 (Member는 프록시로 들어옴)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    // 주인은 OrderItem.order (ORDER_ITEM 테이블에 ORDER_ID FK) → 여기는 읽기 전용
    // 8장: cascade = ALL → 주문 저장/삭제 시 주문상품도 같이 (주문상품은 주문만 관리하는 단일 소유자)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 6장 실전 예제 3: 주문 1 : 배송 1
    // 일대일은 FK를 어느 쪽에 둬도 되는데, 주로 조회하는 주 테이블(ORDERS)에 둠 → 여기가 주인
    // 8장: @OneToOne도 기본이 EAGER → LAZY. 배송도 주문이 생명주기를 관리 → cascade = ALL
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "DELIVERY_ID")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING) // ORDINAL(기본값) 쓰면 enum 순서 바뀔 때 데이터 꼬임
    private OrderStatus status;

    // 연관관계 편의 메서드: 한 번 호출로 양쪽(주인 + 역방향)을 같이 세팅
    // 편의 메서드는 한쪽에만 둔다 → OrderItem 쪽엔 만들지 않음
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public Delivery getDelivery() {
        return delivery;
    }

    // 주인(Order.delivery) + 역방향(Delivery.order)을 같이 세팅
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
