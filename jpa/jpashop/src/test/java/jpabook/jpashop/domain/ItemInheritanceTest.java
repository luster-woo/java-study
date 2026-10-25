package jpabook.jpashop.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 7장 실전 예제 4 확인용 — 상속관계 매핑 + @MappedSuperclass
 */
@SpringBootTest
@Transactional
class ItemInheritanceTest {

    @Autowired
    EntityManager em;

    @Test
    void 부모_타입으로_조회하면_실제_자식_타입으로_나온다() {
        Book book = new Book();
        book.setName("JPA 책");
        book.setPrice(10000);
        book.setAuthor("김영한");
        book.setIsbn("1234");
        em.persist(book);              // INSERT INTO ITEM (..., AUTHOR, ISBN, DTYPE='Book')

        Album album = new Album();
        album.setName("앨범");
        album.setArtist("아이유");
        em.persist(album);

        em.flush();
        em.clear();

        // 부모 타입(Item)으로 조회해도 DTYPE을 보고 Book 객체로 만들어 줌
        Item findItem = em.find(Item.class, book.getId());
        assertThat(findItem).isInstanceOf(Book.class);
        assertThat(((Book) findItem).getAuthor()).isEqualTo("김영한");

        // 부모 타입 JPQL → 자식 전부 조회 (다형성 쿼리)
        List<Item> items = em.createQuery("select i from Item i", Item.class).getResultList();
        assertThat(items).hasSize(2);

        // 자식 타입 JPQL → SQL에 where DTYPE = 'Book' 조건이 붙음 (콘솔에서 확인)
        List<Book> books = em.createQuery("select b from Book b", Book.class).getResultList();
        assertThat(books).hasSize(1);
    }

    @Test
    void BaseEntity_공통_필드() {
        Member member = new Member();
        member.setName("member1");
        member.setCreatedBy("kim");                    // BaseEntity에서 물려받은 메서드
        member.setCreatedDate(LocalDateTime.now());
        em.persist(member);                            // MEMBER 테이블에 created_by, created_date 컬럼이 생김 (부트 기본 네이밍: 카멜 → 스네이크)

        em.flush();
        em.clear();

        Member findMember = em.find(Member.class, member.getId());
        assertThat(findMember.getCreatedBy()).isEqualTo("kim");
        assertThat(findMember.getCreatedDate()).isNotNull();
    }
}
