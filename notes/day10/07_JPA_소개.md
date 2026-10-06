# JPA 소개

> 출처: [2-03 JPA로 데이터베이스 사용하기 - 점프 투 스프링부트](https://wikidocs.net/161164) · [An Introduction to Hibernate 6](https://docs.jboss.org/hibernate/orm/6.6/introduction/html_single/Hibernate_Introduction.html) · [Introduction to Jakarta Persistence (Jakarta EE Tutorial)](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/persist/persistence-intro/persistence-intro.html) · [Jakarta Persistence 3.1 Specification](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1) · [SQL Databases :: Spring Boot](https://docs.spring.io/spring-boot/reference/data/sql.html)

> 인프런 「자바 ORM 표준 JPA 프로그래밍 - 기본편」 섹션 2(JPA 소개)와 같은 주제를 공개 자료로 정리한 노트 — 강의 내용 자체는 아님

> [← Transactional](06_Transactional.md) · [day10 목록](../README.md) · [JPA 시작하기 →](08_JPA_시작하기.md)

---

## SQL 중심 개발의 문제

### DB는 자바를 모름
- 웹 서비스 데이터(질문·답변 등)는 DB에 저장·조회·수정·삭제해야 함
- DB를 다루려면 **SQL**을 써야 하고, DB는 자바 객체를 이해하지 못함
- 그래서 자바 코드 안에 SQL 문자열을 쓰고, 결과를 다시 객체에 옮겨 담는 코드가 반복됨

### 반복되는 변환 코드
- 객체 그래프(서로 참조하는 객체들)를 **평평한 테이블 행으로 펼치는 코드**, 반대로 SQL 결과 집합에서 **객체 그래프를 다시 조립하는 코드**를 손으로 짜야 함
- Hibernate 문서는 이 코드를 지루하고, 반복적이고, 깨지기 쉬운 코드로 봄 → ORM의 목표는 타입 안전하지 않고 깨지기 쉬운 코드를 없애 대규모 프로그램을 오래 유지보수하기 쉽게 만드는 것

### 같은 저장, 두 방식 비교 (질문 테이블 예)

| id | subject | content |
|---|---|---|
| 1 | 안녕하세요 | 가입 인사드립니다 |
| 2 | 질문 있습니다 | ORM이 궁금합니다 |

```sql
-- SQL로 직접
insert into question (id, subject, content) values (1, '안녕하세요', '가입 인사드립니다');
insert into question (id, subject, content) values (2, '질문 있습니다', 'ORM이 궁금합니다');
```

```java
// ORM(JPA)으로 — 자바 객체를 만들어 저장만 요청
Question q1 = new Question();
q1.setSubject("안녕하세요");
q1.setContent("가입 인사드립니다");
questionRepository.save(q1);   // SQL은 JPA 구현체가 만들어 실행
```

- 코드 양만 보면 ORM 쪽이 길어 보이지만, SQL 문법 없이 자바 문법으로 DB를 다룰 수 있음
- 여기서 `Question`처럼 테이블과 매핑되는 자바 클래스를 **엔티티(entity)** 라고 함

---

## 패러다임 불일치

- 객체는 **참조·상속·식별자(메모리 주소)** 로 데이터를 다루고, 관계형 DB는 **테이블·외래 키·기본 키**로 다룸 → 둘을 잇는 지점마다 변환이 필요함
- 아래 표의 "JPA의 해결"은 Jakarta Persistence·Hibernate 문서에 있는 기능 기준

| 차이 | 객체 | 관계형 DB | JPA의 해결 |
|---|---|---|---|
| **상속** | `extends`로 부모 필드를 물려받음 | 테이블에는 상속 개념이 없음 | `@Inheritance` 전략(SINGLE_TABLE·JOINED·TABLE_PER_CLASS)으로 테이블에 매핑 |
| **연관관계** | 다른 객체를 **참조**로 가짐 (`answer.getQuestion()`) | 한쪽 테이블의 **외래 키** 컬럼으로 연결 | `@ManyToOne` 등으로 참조 ↔ 외래 키를 매핑 |
| **방향** | 참조는 한쪽 방향 (A가 B를 알아도 B는 A를 모를 수 있음) | 외래 키 하나로 양쪽 조인 가능 | 단방향/양방향 매핑, `mappedBy` |
| **객체 그래프 탐색** | `a.getQuestion().getSubject()`처럼 자유롭게 따라감 | 처음 SELECT에서 가져온 범위까지만 데이터가 있음 | 연관 객체를 **프록시**로 두었다가 접근할 때 조회(지연 로딩) |
| **식별/비교** | 같은 객체인지 `==`(주소)로 비교 | 같은 행인지 **기본 키**로 구분 | 한 영속성 컨텍스트 안에서 **같은 기본 키 = 같은 인스턴스** 보장 |

### 비교 문제 직접 보기 (SQL 방식 흉내)
- SQL을 직접 실행해 결과를 객체로 옮기는 방식은 조회할 때마다 **새 객체**를 만듦 → 같은 행인데 `==`가 false

```java
import java.util.HashMap;
import java.util.Map;

public class IdentityTest {
    // DB의 한 행을 흉내 낸 데이터 (id -> name)
    static Map<Long, String> table = new HashMap<>();

    static class Member {
        Long id; String name;
        Member(Long id, String name) { this.id = id; this.name = name; }
    }

    // SQL 직접 실행 방식: 조회할 때마다 결과로 새 객체를 만든다
    static Member findBySql(Long id) {
        return new Member(id, table.get(id));
    }

    public static void main(String[] args) {
        table.put(100L, "kim");
        Member m1 = findBySql(100L);
        Member m2 = findBySql(100L);
        System.out.println(m1 == m2);                 // false
        System.out.println(m1.name.equals(m2.name));  // true
    }
}
```

- JPA 명세는 영속성 컨텍스트를 "어떤 영속 식별자(기본 키)에 대해서도 **인스턴스가 하나뿐인** 엔티티 집합"으로 정의함 → 같은 컨텍스트에서 같은 id를 두 번 찾으면 같은 객체(`==` true). 자세한 건 [영속성 관리](../day11/01_영속성_관리.md)

---

## ORM과 JPA

### ORM(Object-Relational Mapping)
- DB 테이블을 자바 클래스(엔티티)로 다루게 해 주는 도구 → 개발자가 SQL을 직접 쓰지 않아도 데이터 처리 가능
- 장점
  - DBMS 종류(MySQL, Oracle, MS SQL 등)가 달라도 **같은 자바 코드** 사용 → 유지보수 편함
  - SQL을 내부에서 자동 생성 → 개발자마다 다른 쿼리 스타일이 통일되고 오류가 줄어듦
  - 기본 영속성 로직을 먼저 쓰고 **성능 튜닝은 나중에** 하기 쉬움 (Hibernate 문서)
- ORM이냐 SQL이냐는 양자택일이 아님 → Hibernate 문서의 답은 "**둘 다 쓴다**". JPA·Hibernate는 직접 쓴 SQL과 같이 쓰도록 설계됨. 특정 로직에서 ORM이 오히려 어렵게 만들면 그 부분만 SQL로

### JPA(Jakarta Persistence, 옛 이름 Java Persistence API)
- 자바 진영 **ORM 기술 표준** — 스프링 부트도 JPA를 ORM 표준으로 사용
- JPA 자체는 **인터페이스 모음(명세)** → 실제 동작하려면 구현체가 필요함
- Jakarta Persistence가 다루는 4영역

| 영역 | 내용 |
|---|---|
| Persistence API | `EntityManager` 등 엔티티를 저장·조회·삭제하는 API |
| 쿼리 언어 | JPQL — 테이블이 아니라 **엔티티**를 대상으로 하는 SQL 비슷한 언어 |
| Criteria API | 자바 코드로 타입 안전하게 쿼리 작성 |
| O/R 매핑 메타데이터 | `@Entity`, `@Id`, `@ManyToOne` 같은 매핑 어노테이션(또는 XML) |

### JPA와 Hibernate의 관계
- **Hibernate** = JPA 인터페이스를 구현한 실제 클래스이자 자바 ORM 프레임워크 (가장 대표적인 구현체)
- 역사: Hibernate 프로젝트가 2001년 시작 → JPA가 Hibernate를 본떠 만들어짐 → JPA 1.0 최종안 2006년 4월 승인
- Hibernate API를 세 덩어리로 볼 수 있음

| 구성 | 내용 |
|---|---|
| JPA 구현 | `EntityManagerFactory`, `EntityManager`, JPA 매핑 어노테이션 구현 |
| 네이티브 API | `SessionFactory`(EntityManagerFactory를 확장), `Session`(EntityManager를 확장) |
| 추가 매핑 어노테이션 | JPA 어노테이션을 보강하는 `org.hibernate.annotations.*` |

- 코드는 가능하면 JPA 표준(`EntityManager`)으로 쓰고, 필요할 때만 Hibernate 네이티브 API를 쓰면 다른 구현체로의 이식성이 유지됨
- 이름이 겹치는 API가 있으니 import 주의

| Hibernate | JPA |
|---|---|
| `org.hibernate.annotations.CascadeType` | `jakarta.persistence.CascadeType` |
| `org.hibernate.FlushMode` | `jakarta.persistence.FlushModeType` |
| `org.hibernate.annotations.FetchMode` | `jakarta.persistence.FetchType` |
| `org.hibernate.query.Query` | `jakarta.persistence.Query` |

### 스프링 부트에서의 구성
- `spring-boot-starter-data-jpa` 하나로 아래가 같이 들어옴

```
spring-boot-starter-data-jpa
 ├─ Hibernate          JPA 구현체
 ├─ Spring Data JPA    JPA 기반 리포지터리(JpaRepository 등) 구현을 도와줌
 └─ Spring ORM         스프링 프레임워크의 ORM 지원
```

```
애플리케이션 코드
   │  (엔티티 객체를 저장/조회 요청)
   ▼
JPA 인터페이스 (jakarta.persistence.*)
   │
   ▼
Hibernate (구현체) ── SQL 생성 ──▶ JDBC ──▶ DB
```

---

## 참고

> 참고: Hibernate 문서의 이름 비교표에는 옛 패키지 `javax.persistence.*`가 그대로 적혀 있음. Spring Boot 3 / Hibernate 6 기준 코드(위키독스 2-04 예제, Hibernate 6 "Hello, JPA" 예제)는 모두 `jakarta.persistence.*`를 import함.

- 위키독스는 "엔티티에는 Setter를 두지 않는 편이 안전하다(생성자와 변경용 메서드로 대신)"고 권하면서도, 설명 편의상 예제에는 Setter를 씀

---

## 면접 포인트

- **JPA와 Hibernate의 차이?** JPA는 자바 ORM 표준 명세(인터페이스 모음)이고, Hibernate는 그 명세를 구현한 대표 구현체임. 코드는 JPA 인터페이스(`EntityManager`)로 작성하고 실제 동작은 Hibernate가 함.
- **ORM을 쓰는 이유?** 객체 그래프 ↔ 테이블 행 변환 코드를 손으로 반복하지 않아도 되고, SQL을 구현체가 생성해 DBMS가 바뀌어도 같은 자바 코드를 쓸 수 있음. 복잡한 조회는 SQL과 같이 쓰면 됨.
- **패러다임 불일치 예시?** 상속(테이블엔 상속 없음), 연관관계(참조 vs 외래 키), 객체 그래프 탐색, 동일성 비교(`==` vs 기본 키). JPA는 상속 매핑 전략, 연관관계 매핑, 지연 로딩, 영속성 컨텍스트의 동일성 보장으로 메움.
