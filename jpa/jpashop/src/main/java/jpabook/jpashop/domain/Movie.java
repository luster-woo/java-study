package jpabook.jpashop.domain;

import jakarta.persistence.Entity;

@Entity
// @DiscriminatorValue("A") 처럼 DTYPE 값을 바꿀 수 있음. 생략하면 엔티티 이름("Movie")이 들어감
public class Movie extends Item { // 영화

    private String director;
    private String actor;

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }
}
