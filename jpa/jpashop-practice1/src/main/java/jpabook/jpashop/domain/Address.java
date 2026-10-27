package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter // Setter 없음 → 값 타입은 불변
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() { // JPA 스펙상 기본 생성자 필요 (protected가 안전)
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
