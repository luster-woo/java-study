# 02. JPA 시작 — Hello JPA

> 강의: 인프런 「자바 ORM 표준 JPA 프로그래밍 - 기본편」 섹션 2 (`02. JPA 시작.pdf`)

> [← JPA 소개](01_JPA_소개.md) · [목차](README.md) · [영속성 관리 →](03_영속성_관리.md)

---

## 한 줄 요약

`persistence.xml` 설정 → `EntityManagerFactory`(1개) → `EntityManager`(요청마다) → **트랜잭션 안에서** 엔티티 저장·조회·수정·삭제.

---

## 1. 프로젝트 생성

### H2 데이터베이스

- 최고의 실습용 DB: 가볍고(1.5M), 웹 쿼리 툴 제공, MySQL·Oracle 시뮬레이션, 시퀀스·AUTO INCREMENT 지원
- 처음엔 `jdbc:h2:~/test`로 접속해 DB 파일(`~/test.mv.db`) 생성 → 이후엔 `jdbc:h2:tcp://localhost/~/test`(서버 모드)로 접속

### 메이븐 프로젝트 (강의 원본)

- groupId `jpa-basic`, artifactId `ex1-hello-jpa`
- 의존성: `hibernate-entitymanager 5.3.10.Final`, `h2 1.4.199`

### 강의 소스코드 — 2024 최신 버전 시작 프로젝트 (`ex1-hello-jpa-start`)

강의 섹션 0의 「2024 최신 버전으로 프로젝트 설정하기」 소스. 슬라이드(Hibernate 5, `javax`)와 다른 점만 정리.

| 항목 | 슬라이드 | 2024 시작 프로젝트 |
|---|---|---|
| Java | 8 | **17** (`maven.compiler.source/target`) |
| JPA 구현체 | `hibernate-entitymanager 5.3.10.Final` | **`hibernate-core 6.4.2.Final`** |
| 패키지 | `javax.persistence.*` | **`jakarta.persistence.*`** |
| H2 | 1.4.199 | **2.2.224** (설치한 H2 서버 버전과 맞출 것) |
| 기타 의존성 | — | `javax.xml.bind:jaxb-api 2.3.1` |
| 설정 키 | `javax.persistence.jdbc.*` | **`jakarta.persistence.jdbc.*`** |
| DDL 자동 생성 | 주석 처리 | **`hibernate.hbm2ddl.auto=create` 켜져 있음** |

```
ex1-hello-jpa-start/
├─ pom.xml
└─ src/main/
   ├─ java/hellojpa/JpaMain.java          ← 여기서부터 따라 치기 (//code 자리)
   └─ resources/
      ├─ META-INF/persistence.xml          ← persistence-unit "hello"
      └─ logback.xml
```

`logback.xml` 포인트 — **바인딩 파라미터까지 보기**

```xml
<logger name="org.hibernate.orm.jdbc.bind" level="trace" />  <!-- Hibernate 6: ? 자리에 들어간 값 출력 -->
<root level="info"> ... </root>
```
- `show_sql`은 `?`만 보여주므로 이 로거를 trace로 열면 실제 값까지 확인 가능 (Hibernate 5에선 `org.hibernate.type.descriptor.sql`)
- pom의 `logback-classic` 의존성은 주석 처리돼 있음 → 로그를 보고 싶으면 주석 해제

시작 상태의 `JpaMain` — 여기에 아래 [기본 코드 틀](#기본-코드-틀-jpamain)을 채워 넣으면 된다.

```java
package hellojpa;

import jakarta.persistence.*;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        //code
        em.close();
        emf.close();
    }
}
```

### persistence.xml — JPA 설정 파일

> 아래는 슬라이드 원본(`javax`). 2024 프로젝트에선 `javax.persistence` → `jakarta.persistence`로만 바꾸면 같다.

- 위치: **`/META-INF/persistence.xml`** (resources 아래)
- `persistence-unit name`으로 이름 지정 (보통 DB당 하나)
- `javax.persistence.*` = JPA 표준 속성, `hibernate.*` = 하이버네이트 전용 속성

```xml
<persistence-unit name="hello">
  <properties>
    <!-- 필수 속성 -->
    <property name="javax.persistence.jdbc.driver"   value="org.h2.Driver"/>
    <property name="javax.persistence.jdbc.user"     value="sa"/>
    <property name="javax.persistence.jdbc.password" value=""/>
    <property name="javax.persistence.jdbc.url"      value="jdbc:h2:tcp://localhost/~/test"/>
    <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
    <!-- 옵션 -->
    <property name="hibernate.show_sql" value="true"/>        <!-- 실행 SQL 출력 -->
    <property name="hibernate.format_sql" value="true"/>      <!-- 보기 좋게 -->
    <property name="hibernate.use_sql_comments" value="true"/><!-- 왜 나간 SQL인지 주석 -->
    <!-- <property name="hibernate.hbm2ddl.auto" value="create"/> -->
  </properties>
</persistence-unit>
```

> 참고: Spring Boot에서는 이 파일 대신 `application.yml`의 `spring.datasource.*`, `spring.jpa.*`로 설정한다.

### 데이터베이스 방언 (Dialect)

- JPA는 특정 DB에 종속되지 않음. 그런데 DB마다 SQL이 조금씩 다름
  - 가변 문자: MySQL `VARCHAR` / Oracle `VARCHAR2`
  - 문자열 자르기: 표준 `SUBSTRING()` / Oracle `SUBSTR()`
  - 페이징: MySQL `LIMIT` / Oracle `ROWNUM`
- **방언** = SQL 표준을 지키지 않는 특정 DB만의 고유 기능
- `hibernate.dialect`에 지정하면 JPA 코드는 그대로, SQL만 DB에 맞게 생성 (H2Dialect, Oracle10gDialect, MySQL5InnoDBDialect … 40개 이상)

---

## 2. 애플리케이션 개발

### JPA 구동 방식

```
Persistence ──(persistence.xml 읽음)──▶ EntityManagerFactory ──생성──▶ EntityManager, EntityManager, ...
```

### 엔티티 매핑

```java
@Entity                 // JPA가 관리할 객체
public class Member {
    @Id                 // DB PK와 매핑
    private Long id;
    private String name;
    // getter, setter
}
```

### 기본 코드 틀 (JpaMain)

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
EntityManager em = emf.createEntityManager();
EntityTransaction tx = em.getTransaction();
tx.begin();
try {
    // 등록
    Member member = new Member();
    member.setId(1L);
    member.setName("HelloA");
    em.persist(member);

    // 조회
    Member findMember = em.find(Member.class, 1L);

    // 수정 — em.persist() 다시 호출 안 해도 됨 (변경 감지)
    findMember.setName("HelloJPA");

    // 삭제
    // em.remove(findMember);

    tx.commit();
} catch (Exception e) {
    tx.rollback();
} finally {
    em.close();
}
emf.close();
```

> 참고: 스프링을 쓰면 위의 `emf`·`em`·트랜잭션 코드는 컨테이너와 `@Transactional`이 대신 해준다.

### 주의 ★

- **엔티티 매니저 팩토리는 하나만** 생성해서 애플리케이션 전체에서 공유 (생성 비용 큼)
- **엔티티 매니저는 쓰레드 간에 공유 X** (요청마다 쓰고 버린다)
- **JPA의 모든 데이터 변경은 트랜잭션 안에서** 실행

---

## 3. JPQL 맛보기

- 가장 단순한 조회: `em.find()`, 객체 그래프 탐색(`a.getB().getC()`)
- 그런데 "나이가 18살 이상인 회원 모두"는? → 모든 DB 데이터를 객체로 가져와 거를 수는 없음 → **검색 조건이 포함된 SQL이 필요**
- JPA는 SQL을 추상화한 **JPQL**(객체 지향 쿼리 언어) 제공
  - SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
  - **JPQL은 엔티티 객체 대상**, SQL은 DB 테이블 대상
  - 특정 DB SQL에 의존 X → 방언만 바꾸면 그대로 동작

```java
List<Member> result = em.createQuery("select m from Member as m", Member.class)
        .setFirstResult(5)   // 페이징도 방언에 맞게 변환
        .setMaxResults(8)
        .getResultList();
```

- 한마디로 **객체 지향 SQL** — 자세한 내용은 [10장](10_객체지향_쿼리_언어.md)

---

## 면접 포인트

- **EntityManagerFactory와 EntityManager의 차이?** 팩토리는 생성 비용이 커서 애플리케이션에 하나만 두고 공유, EntityManager는 쓰레드 안전하지 않아 요청(트랜잭션)마다 새로 만들어 쓰고 닫음.
- **방언(Dialect)이란?** DB마다 다른 SQL 문법·함수를 하이버네이트가 추상화한 것. 설정만 바꾸면 DB를 바꿔도 JPA 코드는 그대로.
