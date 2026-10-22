package jpabook.jpashop.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ORDERS") // ORDER는 SQL 예약어(ORDER BY)라서 ORDERS로 바꿈
public class Order {

    @Id @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    @Column(name = "MEMBER_ID") // 아직은 FK 값을 그대로 들고 있음 → 5장에서 Member 참조로 바꿈
    private Long memberId;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING) // ORDINAL(기본값) 쓰면 enum 순서 바뀔 때 데이터 꼬임
    private OrderStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
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
