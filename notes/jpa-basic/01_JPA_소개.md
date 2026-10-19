# 01. JPA 소개 — JPA와 모던 자바 데이터 저장 기술

> 강의: 인프런 「자바 ORM 표준 JPA 프로그래밍 - 기본편」 섹션 1 (`01. JPA 소개.pdf`)

> [목차](README.md) · [JPA 시작 →](02_JPA_시작.md)

---

## 한 줄 요약

객체는 객체답게, DB는 DB답게 설계하고 **그 사이의 매핑을 ORM(JPA)에게 맡겨서** "자바 컬렉션에 저장하듯이 DB에 저장"하자.

---

## 1. SQL 중심적인 개발의 문제점

### 무한 반복되는 CRUD SQL

- 애플리케이션은 객체지향 언어, 저장소는 관계형 DB → 결국 객체를 SQL로, SQL을 객체로 바꾸는 코드를 계속 짬
- `Member`에 `tel` 필드 하나만 추가해도 INSERT·SELECT·UPDATE **모든 SQL을 찾아 고쳐야** 함
- 결론: **SQL에 의존적인 개발을 피하기 어렵다** → 개발자 ≒ SQL 매퍼

### 패러다임의 불일치 (객체 vs RDB)

객체를 영구 보관하는 현실적인 대안은 RDB인데, 둘은 사고방식이 다르다.

| 차이 | 객체 | 관계형 DB |
|---|---|---|
| 1. 상속 | 상속 있음 | 없음 (슈퍼타입-서브타입 모델링으로 흉내) |
| 2. 연관관계 | **참조** `member.getTeam()` | **외래 키** `JOIN ON M.TEAM_ID = T.TEAM_ID` |
| 3. 데이터 타입 | 다양한 타입 | 컬럼 타입 |
| 4. 식별 방법 | `==`(동일성), `equals` | 기본 키 |

**① 상속** — `Album extends Item`을 저장하려면 객체를 분해해서 `INSERT ITEM` + `INSERT ALBUM`, 조회하려면 조인 SQL 작성 후 객체 각각 생성… 너무 복잡해서 DB에 저장할 객체에는 상속을 안 쓰게 됨. 반면 자바 컬렉션이면 `list.add(album)`, `Item item = list.get(id)`로 다형성까지 그냥 됨.

**② 연관관계** — 테이블에 맞춰 모델링하면 `Long teamId`를 들고 다니게 됨. 객체답게 `Team team`으로 모델링하면 저장할 때 `member.getTeam().getId()`로 FK를 꺼내야 하고, 조회할 때는 조인 SQL 결과로 Member·Team을 각각 만들어 `member.setTeam(team)`까지 직접 해야 함.

**③ 객체 그래프 탐색** — 처음 실행한 SQL에 따라 탐색 범위가 정해짐.

```java
// SELECT M.*, T.* FROM MEMBER M JOIN TEAM T ... 로 조회했다면
member.getTeam();   // OK
member.getOrder();  // null  ← SQL에 없었으니까
```

- → **엔티티 신뢰 문제**: 서비스 계층에서 `memberDAO.find()`로 받은 엔티티의 `getTeam()`, `getOrder().getDelivery()`를 믿고 쓸 수 없음. DAO 내부 SQL을 까봐야 함
- 그렇다고 모든 걸 미리 로딩할 순 없으니 `getMember()`, `getMemberWithTeam()`, `getMemberWithOrderWithDelivery()`… 메서드가 늘어남
- → 진정한 의미의 **계층 분할이 어렵다**

**④ 비교** — 같은 ID로 두 번 조회하면 DAO는 매번 `new Member(...)`를 만드므로 `member1 == member2`가 **false**. 자바 컬렉션에서 꺼내면 true.

> 객체답게 모델링할수록 매핑 작업만 늘어난다 → "객체를 자바 컬렉션에 저장하듯이 DB에 저장할 수는 없을까?" → **JPA**

---

## 2. JPA 소개

### JPA와 ORM

- **JPA (Java Persistence API)**: 자바 진영의 **ORM 기술 표준**
- **ORM (Object-Relational Mapping)**: 객체는 객체대로, RDB는 RDB대로 설계하고 ORM 프레임워크가 중간에서 매핑
- JPA는 **애플리케이션과 JDBC 사이**에서 동작 (JPA가 JDBC API를 사용해 SQL을 만들고 실행)
  - 저장: `persist(entity)` → JPA가 엔티티 분석 → INSERT SQL 생성 → JDBC로 DB에 전송 (**패러다임 불일치 해결**)
  - 조회: `find(id)` → SELECT SQL 생성 → 결과(ResultSet)를 객체로 매핑

### 역사와 구현체

- EJB 엔티티 빈(자바 표준, 너무 무겁고 불편) → **하이버네이트**(오픈소스, 개빈 킹) → 하이버네이트를 바탕으로 **JPA(자바 표준)** 탄생
- JPA는 **인터페이스(명세)의 모음**. 구현체: **Hibernate**, EclipseLink, DataNucleus
- 버전: 1.0(2006, 기능 부족) → 2.0(2009, 대부분의 ORM 기능 + Criteria) → 2.1(2013, 스토어드 프로시저, 컨버터, 엔티티 그래프)

> 참고: 이후 Java EE가 Eclipse 재단으로 넘어가면서 **Jakarta Persistence 3.x**(`jakarta.persistence`)로 이름이 바뀜. Spring Boot 3은 이걸 쓴다.

### JPA를 왜 사용해야 하는가?

SQL 중심 → **객체 중심** 개발 / 생산성 / 유지보수 / 패러다임 불일치 해결 / 성능 / 데이터 접근 추상화와 벤더 독립성 / 표준

**생산성** — CRUD가 컬렉션 다루듯 끝남

```java
jpa.persist(member);                     // 저장
Member member = jpa.find(memberId);      // 조회
member.setName("변경할 이름");            // 수정 (update 메서드 없음!)
jpa.remove(member);                      // 삭제
```

**유지보수** — 필드만 추가하면 SQL은 JPA가 알아서 수정

**패러다임 불일치 해결**

- 상속: `jpa.persist(album)` → JPA가 `INSERT ITEM`, `INSERT ALBUM` 둘 다 실행 / `jpa.find(Album.class, id)` → `ITEM JOIN ALBUM` 조회
- 연관관계·그래프 탐색: `member.setTeam(team); jpa.persist(member);` / `member.getTeam()` 자유롭게 탐색 → **엔티티·계층을 신뢰할 수 있음**
- 비교: **동일한 트랜잭션에서 조회한 엔티티는 같음(`==`)을 보장**

---

## 3. JPA의 성능 최적화 기능

중간 계층이 생기면 할 수 있는 것: **버퍼링**(모아서 쓰기)과 **캐싱**(읽어둔 것 재사용)

### ① 1차 캐시와 동일성 보장

```java
Member m1 = jpa.find(Member.class, "100"); // SQL 실행
Member m2 = jpa.find(Member.class, "100"); // 캐시에서 반환
m1 == m2  // true, SQL은 1번만
```

- 같은 트랜잭션 안에서 같은 엔티티 반환 → 약간의 조회 성능 향상 (트랜잭션 범위라 실무 이득은 크지 않음)
- DB 격리 수준이 READ COMMITTED여도 **애플리케이션에서 REPEATABLE READ 보장**

### ② 트랜잭션을 지원하는 쓰기 지연 (transactional write-behind)

- **INSERT**: 커밋할 때까지 SQL을 모았다가 **JDBC BATCH**로 한 번에 전송
- **UPDATE/DELETE**: 커밋 직전에 실행하고 바로 커밋 → **로우 락 시간 최소화** (비즈니스 로직 수행 중엔 락이 안 걸림)

```java
transaction.begin();
em.persist(memberA);
em.persist(memberB);
em.persist(memberC);
// 여기까지 INSERT SQL을 보내지 않음
transaction.commit(); // 커밋 순간 모아서 보냄
```

### ③ 지연 로딩과 즉시 로딩

| | 동작 | SQL |
|---|---|---|
| **지연 로딩** | 객체가 **실제 사용될 때** 로딩 | `SELECT * FROM MEMBER` → `team.getName()` 시점에 `SELECT * FROM TEAM` |
| **즉시 로딩** | JOIN으로 연관 객체까지 **미리** 조회 | `SELECT M.*, T.* FROM MEMBER JOIN TEAM …` |

- 강의 팁: 일단 지연 로딩으로 다 짜두고, 최적화가 필요한 곳만 즉시(페치 조인)로 바꾼다 (8·10장에서 자세히)

> **ORM은 객체와 RDB 두 기둥 위에 있는 기술** — JPA를 잘 쓰려면 SQL·DB도 잘 알아야 한다.

---

## 면접 포인트

- **패러다임 불일치란?** 객체는 상속·참조·그래프 탐색·동일성 비교를 쓰는데 RDB는 상속이 없고 외래 키·조인·기본 키로 표현함. 이 간극을 메우는 매핑 코드가 SQL 중심 개발의 비용.
- **JPA vs Hibernate?** JPA는 표준 인터페이스(명세), Hibernate는 그 구현체 중 하나.
- **JPA가 성능에 주는 이점?** 1차 캐시(동일성 보장), 쓰기 지연(JDBC 배치, 락 시간 단축), 지연 로딩.
