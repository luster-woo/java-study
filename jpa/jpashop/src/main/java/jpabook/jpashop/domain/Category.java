package jpabook.jpashop.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Category extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "CATEGORY_ID")
    private Long id;

    private String name;

    // 계층 구조 = 자기 자신과의 다대일/일대다 (같은 CATEGORY 테이블 안에서 PARENT_ID가 FK)
    // 자식 N : 부모 1 → 주인은 parent
    @ManyToOne(fetch = FetchType.LAZY) // 8장: 기본 EAGER → LAZY
    @JoinColumn(name = "PARENT_ID")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    // 다대다: 예제라서 @ManyToMany 사용. 실무에서는 X → CategoryItem 엔티티로 풀어서 @OneToMany + @ManyToOne
    // 연결 테이블 CATEGORY_ITEM(CATEGORY_ID, ITEM_ID)이 생기고, 컬럼 추가(등록일 등)가 불가능한 게 문제
    @ManyToMany
    @JoinTable(name = "CATEGORY_ITEM",
            joinColumns = @JoinColumn(name = "CATEGORY_ID"),        // 내 쪽(Category) FK
            inverseJoinColumns = @JoinColumn(name = "ITEM_ID"))     // 반대쪽(Item) FK
    private List<Item> items = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.parent = this;
    }

    public void addItem(Item item) {
        items.add(item);
        item.getCategories().add(this);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParent() {
        return parent;
    }

    public List<Category> getChild() {
        return child;
    }

    public List<Item> getItems() {
        return items;
    }
}
