package jpabook.jpashop.domain;

import jakarta.persistence.Entity;

@Entity
// @DiscriminatorValue("A") 처럼 DTYPE 값을 바꿀 수 있음. 생략하면 엔티티 이름("Book")이 들어감
public class Book extends Item { // 도서

    private String author;
    private String isbn;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
