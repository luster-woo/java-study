package jpabook.jpashop.web;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookForm {

    private Long id; // 수정 때 사용

    private String name;
    private int price;
    private int stockQuantity;

    private String author;
    private String isbn;
}
