package jpabook.jpashop.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    private String name;

    // 9장 실전 예제 6: city/street/zipcode → 임베디드 값 타입으로 묶음 (MEMBER 테이블 컬럼은 그대로)
    @Embedded
    private Address address;

    // 5장 실전 예제 2: 양방향 매핑의 "가짜 매핑"(읽기 전용) 쪽
    // FK(MEMBER_ID)는 ORDERS 테이블에 있으므로 주인은 Order.member → 여기는 mappedBy
    // 강의 포인트: 회원이 주문 목록을 들고 있는 건 좋은 설계는 아님 (주문에서 member로 찾으면 됨). 예제라서 넣음
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>(); // 관례: 필드에서 바로 초기화 (NPE 방지)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Address getAddress() {
        return address;
    }

    // Address는 불변이라 일부 값만 바꾸는 건 불가능 → 새 Address로 통째로 교체하는 setter만 둠
    public void setAddress(Address address) {
        this.address = address;
    }
}
