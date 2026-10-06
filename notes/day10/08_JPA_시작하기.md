# JPA 시작하기

> 출처: [2-03 JPA로 데이터베이스 사용하기 - 점프 투 스프링부트](https://wikidocs.net/161164) · [2-04 엔티티로 테이블 매핑하기 - 점프 투 스프링부트](https://wikidocs.net/161165) · [An Introduction to Hibernate 6](https://docs.jboss.org/hibernate/orm/6.6/introduction/html_single/Hibernate_Introduction.html) · [Introduction to Jakarta Persistence (Jakarta EE Tutorial)](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/persist/persistence-intro/persistence-intro.html) · [2-05 리포지터리로 데이터베이스 관리하기 - 점프 투 스프링부트](https://wikidocs.net/160890) · [Persisting Entities :: Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/jpa/entity-persistence.html) · [The Jakarta Persistence Query Language (Jakarta EE Tutorial)](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/persist/persistence-querylanguage/persistence-querylanguage.html) · [Hibernate 6.6 Javadoc - Session](https://docs.jboss.org/hibernate/orm/6.6/javadocs/org/hibernate/Session.html) · [SQL Databases :: Spring Boot](https://docs.spring.io/spring-boot/reference/data/sql.html)

> 인프런 「자바 ORM 표준 JPA 프로그래밍 - 기본편」 섹션 3(JPA 시작하기)과 같은 주제를 공개 자료로 정리한 노트 — 강의 내용 자체는 아님

> [← JPA 소개](07_JPA_소개.md) · [day10 목록](../README.md)

---

## 프로젝트 설정

### 두 가지 시작 방법

| 방법 | 설정 위치 | 엔티티 등록 | 언제 |
|---|---|---|---|
| **순수 JPA** (스프링 없이) | `META-INF/persistence.xml` | `<class>`로 나열 | JPA 동작 원리를 직접 볼 때 |
| **Spring Boot** | `application.properties` | 자동 스캔(Entity Scanning) — `persistence.xml` 불필요 | 실제 프로젝트 |

### 순수 JPA — 의존성
- 꼭 필요한 건 `hibernate-core` 하나 + 사용할 DB의 **JDBC 드라이버**

```groovy
dependencies {
    implementation 'org.hibernate.orm:hibernate-core:{version}'   // JPA 구현체
    runtimeOnly 'com.h2database:h2:{version}'                    // JDBC 드라이버
}
```

### 순수 JPA — persistence.xml
- `META-INF/persistence.xml`에 **영속성 유닛(persistence unit)** 을 정의
  - 영속성 유닛 = 애플리케이션의 `EntityManager`가 관리할 엔티티 클래스 집합 + DB 연결 정보, 이름으로 구분
- 꼭 필요한 속성은 JDBC URL·사용자·비밀번호 셋

```xml
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
    <persistence-unit name="hello">
        <class>hellojpa.Member</class>
        <properties>
            <!-- 필수: DB 접속 정보 -->
            <property name="jakarta.persistence.jdbc.url" value="jdbc:h2:mem:test"/>
            <property name="jakarta.persistence.jdbc.user" value="sa"/>
            <property name="jakarta.persistence.jdbc.password" value=""/>

            <!-- 선택: 실행되는 SQL을 콘솔에 보기 좋게 출력 -->
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
        </properties>
    </persistence-unit>
</persistence>
```

| 속성 | 뜻 |
|---|---|
| `jakarta.persistence.jdbc.url` | DB JDBC URL |
| `jakarta.persistence.jdbc.user` / `password` | DB 계정 |
| `hibernate.show_sql` | 실행 SQL을 콘솔에 출력 |
| `hibernate.format_sql` | SQL을 여러 줄로 들여쓰기해 출력 |
| `hibernate.highlight_sql` | SQL에 색 입히기(ANSI) |

- `jakarta.persistence.*` = JPA 표준 속성(구현체 바꿔도 그대로), `hibernate.*` = Hibernate 전용 속성
- Hibernate 6은 DB에 맞는 **방언(Dialect)** 을 자동으로 고름 → `hibernate.dialect`는 직접 만든·서드파티 Dialect를 쓸 때만 지정. 지원 DB면 드라이버 클래스 지정도 불필요

### Spring Boot — 의존성과 설정 (위키독스 SBB 예제)

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'   // JPA + Hibernate
    runtimeOnly 'com.h2database:h2'                                         // H2 DB
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
}
```

```properties
# DATABASE
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:~/local
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
```

| 설정 | 뜻 |
|---|---|
| `spring.h2.console.enabled` / `path` | H2 웹 콘솔 사용 여부와 접속 경로(`/h2-console`) |
| `spring.datasource.url` | 접속 경로. `jdbc:h2:~/local` → 홈 디렉터리의 `local.mv.db` 파일 DB |
| `spring.datasource.driverClassName` | JDBC 드라이버 클래스 |
| `spring.datasource.username` / `password` | 계정 (로컬 개발용이라 비밀번호 비움) |
| `spring.jpa.properties.hibernate.*` | Hibernate 속성을 그대로 전달 |
| `spring.jpa.hibernate.ddl-auto` | 엔티티 기준 테이블 생성 규칙 → [엔티티 매핑](../day11/02_엔티티_매핑.md) |

- **H2**: 자바 기반 경량 DB, 주로 개발 환경용 → 운영은 MySQL·Oracle 등으로
- 위키독스 방식(파일 DB)은 `local.mv.db` 빈 파일(0KB)을 먼저 만들어 둬야 함. 이 파일을 서버가 잡고 있으면 테스트 실행 시 "file is locked" 오류 → 테스트 전 서버 중지

---

## 핵심 구조 — EntityManagerFactory · EntityManager · 트랜잭션

```
Persistence.createEntityManagerFactory("hello")   ← persistence.xml의 유닛 이름
        │
        ▼
EntityManagerFactory (애플리케이션에 하나, 스레드 안전)
        │ createEntityManager()
        ▼
EntityManager (작업 단위마다 새로, 스레드 안전 X)  ── 영속성 컨텍스트 1개와 연결
        │ getTransaction()
        ▼
EntityTransaction  begin → (persist/find/변경/remove) → commit / rollback
```

| 객체 | 특징 |
|---|---|
| `EntityManagerFactory` | 영속성 유닛 하나당 하나 만들어 공유. **스레드 안전** |
| `EntityManager` | 엔티티 저장·조회·삭제·쿼리 API. **스레드 안전하지 않음** → 스레드(요청)·트랜잭션마다 따로 써야 함 |
| `EntityTransaction` | 리소스 로컬 트랜잭션. JPA의 모든 **데이터 변경은 트랜잭션 안에서** |

- `EntityManager`(Hibernate에선 `Session`)는 **짧게 쓰고 버리는 객체** → 논리적 트랜잭션이 끝나면 닫아서 JDBC 자원을 돌려줘야 함
- 예외가 나면 현재 트랜잭션은 롤백하고 그 `EntityManager`도 버려야 함 → 예외 후 내부 상태가 DB와 일치한다고 기대할 수 없기 때문

---

## 엔티티 만들기

```java
package hellojpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Member {

    @Id
    private Long id;
    private String name;

    protected Member() {}              // JPA용 기본 생성자 (public 또는 protected)

    public Member(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

- `@Entity`: JPA가 관리할 클래스라는 표시 (이게 있어야 엔티티로 인식)
- `@Id`: 기본 키(PK)에 매핑. 모든 엔티티에 필수
- 엔티티 클래스 조건: **인자 없는 생성자**(public/protected) 필수, 다른 생성자 추가 가능, 클래스·메서드·영속 필드에 `final` 금지
- 필드 이름이 기본으로 컬럼 이름이 됨 → 매핑 세부는 [엔티티 매핑](../day11/02_엔티티_매핑.md)

---

## 엔티티 CRUD

```java
package hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 저장 (Create)
            Member member = new Member(1L, "HelloA");
            em.persist(member);

            // 조회 (Read) — 기본 키로
            Member found = em.find(Member.class, 1L);

            // 수정 (Update) — em.update() 같은 메서드는 없음. 값만 바꾸면 됨
            found.setName("HelloJPA");

            // 여러 건 조회 — JPQL
            List<Member> members = em.createQuery("select m from Member m", Member.class)
                                     .getResultList();

            // 삭제 (Delete)
            // em.remove(found);

            tx.commit();             // 이 시점에 변경 내용이 DB에 반영
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
        emf.close();
    }
}
```

| 작업 | 메서드 | 메모 |
|---|---|---|
| 저장 | `em.persist(entity)` | 새 엔티티를 영속 상태로 → 트랜잭션이 끝날 때 DB에 저장 |
| 조회 | `em.find(Class, pk)` | 없으면 `null` |
| 수정 | (없음) 객체 값 변경 | 관리 중인 엔티티의 변경은 자동 감지돼 반영됨(**변경 감지**) |
| 삭제 | `em.remove(entity)` | 관리 중인 엔티티에만. 트랜잭션 완료(또는 flush) 때 DB에서 삭제 |

- `EntityManager` 인터페이스에는 `persist`, `merge`, `remove`, `find`, `flush`, `clear`, `detach`, `createQuery` 등이 있고 **update 메서드는 없음** → 수정이 왜 값 변경만으로 되는지는 [영속성 관리](../day11/01_영속성_관리.md)
- Hibernate 문서 표현: `Session`의 메서드를 호출해도 SQL이 **바로(동기적으로) 실행되지 않는 경우가 많음** → flush 시점에 INSERT·UPDATE·DELETE가 나감

### Spring Data JPA로 보면 (위키독스 SBB)
- `JpaRepository<엔티티, PK 타입>`을 상속한 인터페이스만 만들면 CRUD 메서드를 바로 씀 → 내부에서 `EntityManager`를 사용
- `save()`는 새 엔티티(기본 키가 `null` 등)면 `em.persist()`, 아니면 `em.merge()`를 호출함

| EntityManager | JpaRepository |
|---|---|
| `em.persist(q)` / `em.merge(q)` | `repository.save(q)` |
| `em.find(Question.class, id)` | `repository.findById(id)` → `Optional` |
| `em.createQuery("select q from Question q", ...)` | `repository.findAll()` |
| `em.remove(q)` | `repository.delete(q)` |

---

## JPQL 맛보기

### 왜 필요한가
- `em.find`는 기본 키 한 건 조회만 됨 → "이름에 kim이 들어간 회원 전부" 같은 조건 조회는 쿼리가 필요
- **JPQL(Jakarta Persistence Query Language)**: SQL과 비슷한 문법이지만 **테이블이 아니라 엔티티와 그 필드**를 대상으로 작성
  - `from Member m` 의 `Member`는 테이블명이 아니라 **엔티티 이름**, `m.name`은 컬럼이 아니라 **엔티티 필드**
  - JPA가 엔티티 매핑 정보를 보고 실제 DB 스키마용 SQL로 번역 → DB 방언에 덜 묶임

### 기본 문법

```java
// 전체 조회
List<Member> all = em.createQuery("select m from Member m", Member.class)
                     .getResultList();

// 이름 파라미터 (:이름)
List<Member> kims = em.createQuery(
        "select m from Member m where m.name like :name", Member.class)
        .setParameter("name", "%kim%")
        .setMaxResults(10)          // 최대 10건
        .getResultList();

// 위치 파라미터 (?1부터 시작)
List<Member> byName = em.createQuery(
        "select m from Member m where m.name = ?1", Member.class)
        .setParameter(1, "HelloJPA")
        .getResultList();
```

- 구조: `SELECT`·`FROM` 필수, `WHERE`·`GROUP BY`·`HAVING`·`ORDER BY` 선택
- `createQuery(문자열, 결과타입.class)` → `TypedQuery`라 결과 캐스팅 불필요
- 파라미터 이름은 대소문자 구분
- 벌크 `UPDATE`·`DELETE` 문도 있음

### JPQL vs Criteria API

| | JPQL | Criteria API |
|---|---|---|
| 형태 | 문자열 쿼리 | 자바 메서드 호출로 쿼리 조립 |
| 장점 | 짧고 읽기 쉬움, SQL 아는 사람은 바로 씀 | 타입 안전, 컴파일 시점 오류 확인 |
| 단점 | 문자열이라 타입 오류가 컴파일 때 안 잡힘 | 코드가 장황함 |

---

## 참고

> 참고: 위키독스 설정의 `spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect`는 Hibernate 6에서는 없어도 됨(방언 자동 선택). 지정해도 동작에는 문제없음.

- Spring Boot 환경에서는 `persistence.xml` 없이 `@Entity`·`@Embeddable`·`@MappedSuperclass`가 붙은 클래스를 자동 스캔함
- 위키독스 SBB 예제에서 `@Autowired` 필드 주입은 테스트 코드에서만 쓰고, 실제 코드는 생성자 주입을 권장함

---

## 면접 포인트

- **EntityManagerFactory와 EntityManager의 차이?** EntityManagerFactory는 영속성 유닛당 하나 만들어 공유하는 스레드 안전 객체이고, EntityManager는 요청·트랜잭션마다 만들어 쓰고 닫는 스레드 안전하지 않은 객체임.
- **JPA에 update 메서드가 없는 이유?** 트랜잭션 안에서 관리(영속) 상태인 엔티티의 값을 바꾸면 변경 감지로 flush 때 UPDATE가 자동 실행되기 때문.
- **JPQL과 SQL의 차이?** SQL은 테이블·컬럼을 대상으로, JPQL은 엔티티·필드를 대상으로 작성함. JPQL은 JPA가 매핑 정보를 보고 SQL로 번역함.
