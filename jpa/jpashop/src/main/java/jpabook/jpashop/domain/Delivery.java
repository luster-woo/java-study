package jpabook.jpashop.domain;

import jakarta.persistence.*;

@Entity
public class Delivery extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "DELIVERY_ID")
    private Long id;

    // 9장 실전 예제 6: Member와 같은 Address 타입 재사용 (DELIVERY 테이블 컬럼은 그대로)
    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    // 일대일 양방향: FK(DELIVERY_ID)는 ORDERS 테이블에 있음 → 주인은 Order.delivery, 여기는 읽기 전용
    // 8장: LAZY로 적어 두긴 하지만, FK가 없는 쪽(mappedBy)의 일대일은 프록시를 만들 수 없어서 실제로는 즉시 로딩됨
    // (FK가 없으니 값이 있는지 없는지(null인지) 알려면 어차피 조회해야 함)
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    public Long getId() {
        return id;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Address getAddress() {
        return address;
    }

    // Address는 불변이라 일부 값만 바꾸는 건 불가능 → 새 Address로 통째로 교체하는 setter만 둠
    public void setAddress(Address address) {
        this.address = address;
    }
}
