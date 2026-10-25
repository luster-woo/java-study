package jpabook.jpashop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

// 9장 실전 예제 6: 값 타입 매핑
// Member와 Delivery에 똑같이 있던 city/street/zipcode를 임베디드 값 타입으로 묶음
// - 테이블은 그대로 (MEMBER, DELIVERY 테이블에 CITY/STREET/ZIPCODE 컬럼) → 객체만 더 의미 있게 쪼갠 것
// - 불변 객체: setter 없음, 생성자로만 값 세팅 → 값을 바꾸려면 new Address(...)로 통째로 교체
@Embeddable
public class Address {

    // 값 타입에 컬럼 제약을 두면 이 타입을 쓰는 모든 엔티티에 공통 적용됨
    @Column(length = 10)
    private String city;

    @Column(length = 20)
    private String street;

    @Column(length = 5)
    private String zipcode;

    // JPA 스펙상 기본 생성자 필수 (리플렉션으로 객체 생성) → 외부에서 못 쓰게 protected
    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    // 값 타입이라 의미 있는 비즈니스 메서드를 둘 수 있음 (응집도 ↑)
    public String fullAddress() {
        return getCity() + " " + getStreet() + " " + getZipcode();
    }

    // getter만 공개, setter는 만들지 않음 (불변)
    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getZipcode() {
        return zipcode;
    }

    // 값 타입 비교는 equals로 (동등성) → 값 타입 컬렉션 remove 등에서도 필요
    // 포인트 1: getClass() 대신 instanceof → 프록시(하위 클래스)여도 같은 값이면 true
    // 포인트 2: 필드 직접 접근(o.city) 대신 getter → 프록시면 필드가 비어 있고 getter로 접근해야 실제 값이 나옴
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address address)) return false;
        return Objects.equals(getCity(), address.getCity())
                && Objects.equals(getStreet(), address.getStreet())
                && Objects.equals(getZipcode(), address.getZipcode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCity(), getStreet(), getZipcode());
    }
}
