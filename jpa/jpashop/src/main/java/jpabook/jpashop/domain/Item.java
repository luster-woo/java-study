package jpabook.jpashop.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
// 7장 실전 예제 4: 상품 종류 = 음반(Album), 도서(Book), 영화(Movie) → 상속관계 매핑
// SINGLE_TABLE: ITEM 테이블 하나에 자식 컬럼(ARTIST, AUTHOR, DIRECTOR …)까지 전부 + DTYPE
//   → 조인 없어서 조회 빠름 / 대신 자식 컬럼은 전부 null 허용
// JOINED로 바꾸면 ITEM + ALBUM/BOOK/MOVIE 테이블로 나뉨 (어노테이션 값만 바꾸면 됨 — JPA의 장점)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn // 기본 이름 DTYPE. 어떤 자식 타입의 행인지 구분
public abstract class Item extends BaseEntity { // Item 단독으로 저장할 일은 없으니 abstract

    @Id @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    // 다대다 역방향: 주인은 Category.items (연결 테이블 CATEGORY_ITEM은 Category 쪽에서 관리)
    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public List<Category> getCategories() {
        return categories;
    }
}
