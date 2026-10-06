# Spring Data JPA

> 출처: [Getting Started | Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/) · [Core concepts :: Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/repositories/core-concepts.html) · [Defining Query Methods :: Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/3.5/repositories/query-methods-details.html) · [JPA Query Methods :: Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/3.5/jpa/query-methods.html)

> Spring 공식 가이드 정리

> [← 다양한 연관관계 매핑](03_다양한_연관관계_매핑.md) · [day12 목록](../README.md)

---

## Spring Data JPA란

- JPA로 관계형 DB에 데이터를 저장·조회하는 일에 집중한 Spring 프로젝트
- 가장 큰 특징: **리포지토리 인터페이스만 선언하면 구현체를 실행 시점에 자동으로 만들어 줌**
  - 구현 클래스를 직접 작성하지 않음

## 준비

- Java 17 이상, Gradle 7.5+ 또는 Maven 3.5+
- [start.spring.io](https://start.spring.io)에서 의존성 **Spring Data JPA** + **H2 Database** 선택 → 메모리 DB로 실습

---

## 엔티티 정의

```java
package com.example.accessingdatajpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;

    protected Customer() {}                       // JPA용 기본 생성자

    public Customer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format("Customer[id=%d, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}
```

| 요소 | 의미 |
|---|---|
| `@Entity` | JPA 엔티티. `@Table`이 없으면 클래스 이름(`Customer`) 테이블에 매핑 |
| `@Id` | 식별자(PK) |
| `@GeneratedValue` | id 자동 생성 |
| 어노테이션 없는 필드 | 필드 이름과 같은 컬럼에 매핑 |
| `protected` 기본 생성자 | JPA가 쓰기 위한 것. 직접 쓰지 않으므로 `protected` |
| 인자 있는 생성자 | 저장할 객체를 만들 때 사용 |

---

## 리포지토리 인터페이스

```java
package com.example.accessingdatajpa;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    List<Customer> findByLastName(String lastName);   // 메서드 선언만으로 쿼리 생성

    Customer findById(long id);
}
```

- `CrudRepository<Customer, Long>` → 제네릭에 **엔티티 타입, ID 타입** 지정
- 상속만으로 저장·삭제·조회 메서드를 물려받음
- `findByLastName()`처럼 **메서드 시그니처만 선언**해도 쿼리 메서드가 됨
- 구현 클래스는 없음 — 애플리케이션 실행 시 Spring Data JPA가 생성
- 리포지토리 스캔 범위: `@SpringBootApplication`이 있는 패키지와 그 하위
  - 밖에 있으면 `@EnableJpaRepositories(basePackageClasses = MyRepository.class)`로 지정

### 리포지토리 인터페이스 계층

| 인터페이스 | 역할 |
|---|---|
| `Repository<T, ID>` | 중심 인터페이스. 다룰 도메인 타입과 ID 타입을 잡아 두는 **마커** |
| `CrudRepository` | CRUD 메서드 (여러 건 결과는 `Iterable`) |
| `ListCrudRepository` | `CrudRepository`와 같은 기능, 여러 건 결과를 `List`로 |
| `PagingAndSortingRepository` | `findAll(Sort)`, `findAll(Pageable)` 추가 |
| `JpaRepository` | JPA 전용 확장 — `CrudRepository`를 확장하고 JPA 기능을 추가 |

### CrudRepository 주요 메서드

```java
public interface CrudRepository<T, ID> extends Repository<T, ID> {
    <S extends T> S save(S entity);       // 저장
    Optional<T> findById(ID primaryKey);  // ID로 한 건 조회
    Iterable<T> findAll();                // 전체 조회
    long count();                         // 개수
    void delete(T entity);                // 삭제
    boolean existsById(ID primaryKey);    // 존재 여부
    // ...
}
```

---

## 실행해 보기

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AccessingDataJpaApplication {

    private static final Logger logger = LoggerFactory.getLogger(AccessingDataJpaApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AccessingDataJpaApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(CustomerRepository repository) {   // 앱 시작 시 자동 실행
        return (args) -> {
            repository.save(new Customer("Jack", "Bauer"));
            repository.save(new Customer("Chloe", "O'Brian"));
            repository.save(new Customer("Kim", "Bauer"));
            repository.save(new Customer("David", "Palmer"));
            repository.save(new Customer("Michelle", "Dessler"));

            repository.findAll().forEach(c -> logger.info(c.toString()));

            Customer customer = repository.findById(1L);
            logger.info(customer.toString());

            repository.findByLastName("Bauer").forEach(b -> logger.info(b.toString()));
        };
    }
}
```

```
Customers found with findAll():
Customer[id=1, firstName='Jack', lastName='Bauer']
Customer[id=2, firstName='Chloe', lastName='O'Brian']
Customer[id=3, firstName='Kim', lastName='Bauer']
Customer[id=4, firstName='David', lastName='Palmer']
Customer[id=5, firstName='Michelle', lastName='Dessler']

Customer found with findById(1L):
Customer[id=1, firstName='Jack', lastName='Bauer']

Customer found with findByLastName('Bauer'):
Customer[id=1, firstName='Jack', lastName='Bauer']
Customer[id=3, firstName='Kim', lastName='Bauer']
```

- 실행: Gradle `./gradlew bootRun`, Maven `./mvnw spring-boot:run`
- 가이드의 출력 예시(로그 헤더 줄 포함)를 옮긴 것 — 코드는 노트에서 헤더 로그를 줄였음

---

## findById는 Optional을 반환

- 상속받은 `CrudRepository.findById(ID)`의 반환 타입은 **`Optional<T>`** — 해당 ID가 없을 수 있음을 타입으로 표현
- 가이드의 Java 예제는 리포지토리에 `Customer findById(long id)`(기본형 `long`)를 **따로 선언**해서 엔티티를 바로 받음
  - Kotlin 예제는 상속받은 메서드를 써서 `repository.findById(1L).orElseThrow()`
- `findById`는 **예약 메서드** — 엔티티의 `@Id` 필드 이름이 `id`가 아니어도(예: `pk`) 식별자를 대상으로 조회
  - 식별자가 아닌 `id`라는 일반 필드로 찾고 싶으면 `findUserById`처럼 `find`와 `By` 사이에 단어를 넣어 충돌을 피함

```java
Customer c = repository.findById(1L)
        .orElseThrow(() -> new IllegalArgumentException("고객 없음: id=1"));
```

### 예제 — Optional 처리 방법 (순수 자바)

```java
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class OptionalFindDemo {
    static Map<Long, String> table = new HashMap<>();

    // CrudRepository.findById(ID)처럼 "없을 수도 있음"을 Optional로 표현
    static Optional<String> findById(Long id) {
        return Optional.ofNullable(table.get(id));
    }

    public static void main(String[] args) {
        table.put(1L, "Jack Bauer");

        System.out.println(findById(1L).orElseThrow());              // 값이 있으면 꺼냄
        System.out.println(findById(9L).isPresent());                // 없음 확인
        System.out.println(findById(9L).orElse("기본값"));            // 없으면 대체값
        System.out.println(findById(1L).map(String::length).orElse(0));

        try {
            findById(9L).orElseThrow(() -> new IllegalArgumentException("id=9 없음"));
        } catch (IllegalArgumentException e) {
            System.out.println("예외: " + e.getMessage());
        }
        try {
            findById(9L).orElseThrow();                              // 인자 없으면 NoSuchElementException
        } catch (NoSuchElementException e) {
            System.out.println("예외: " + e.getClass().getSimpleName());
        }
    }
}
/* 출력
Jack Bauer
false
기본값
10
예외: id=9 없음
예외: NoSuchElementException
*/
```

---

## 쿼리 메서드 이름 규칙

### 쿼리를 찾는 순서 (기본 전략 `CREATE_IF_NOT_FOUND`)

1. 선언된 쿼리(`@Query`, Named Query)가 있으면 그것을 사용
2. 없으면 **메서드 이름을 분석해 쿼리 생성**

| 전략 | 동작 |
|---|---|
| `CREATE` | 메서드 이름으로만 쿼리 생성 |
| `USE_DECLARED_QUERY` | 선언된 쿼리만 사용, 없으면 시작 시 실패 |
| `CREATE_IF_NOT_FOUND` (기본) | 선언된 쿼리 먼저, 없으면 이름으로 생성 |

### 이름 구조: 주어 + 서술부

```
find  Distinct  By  LastnameAndFirstname  OrderBy  AgeDesc
└주어(조회 방식)┘    └──────── 서술부(조건) ────────────────┘
```

- **주어**: `find…By`, `exists…By`, `count…By`, `delete…By` 등 — 쿼리 종류
- `find`와 `By` 사이 단어는 **설명용**(무시됨) — 단 `Distinct`, `Top`/`First`는 의미가 있음
- **첫 번째 `By`** 뒤부터가 조건. 속성 조건을 `And`·`Or`로 이음

```java
interface PersonRepository extends Repository<Person, Long> {

    List<Person> findByEmailAddressAndLastname(EmailAddress emailAddress, String lastname);

    List<Person> findDistinctPeopleByLastnameOrFirstname(String lastname, String firstname);

    List<Person> findByLastnameIgnoreCase(String lastname);
    List<Person> findByLastnameAndFirstnameAllIgnoreCase(String lastname, String firstname);

    List<Person> findByLastnameOrderByFirstnameAsc(String lastname);

    long countByLastname(String lastname);      // 파생 count 쿼리
    long deleteByLastname(String lastname);     // 파생 delete 쿼리
}
```

- `findByEmailAddressAndLastname` → `select u from User u where u.emailAddress = ?1 and u.lastname = ?2`와 같은 의미

### 주요 키워드 (JPA)

| 키워드 | 메서드 예 | JPQL 조건 |
|---|---|---|
| `And` / `Or` | `findByLastnameAndFirstname` | `where x.lastname = ?1 and x.firstname = ?2` |
| `Is`, `Equals` (생략 가능) | `findByFirstname` | `where x.firstname = ?1` (인자가 `null`이면 `is null`) |
| `Between` | `findByStartDateBetween` | `between ?1 and ?2` |
| `LessThan` / `LessThanEqual` | `findByAgeLessThan` | `x.age < ?1` / `<=` |
| `GreaterThan` / `GreaterThanEqual` | `findByAgeGreaterThan` | `x.age > ?1` / `>=` |
| `After` / `Before` | `findByStartDateAfter` | `x.startDate > ?1` / `<` |
| `IsNull` / `IsNotNull` | `findByAgeIsNull` | `x.age is null` / `is not null` |
| `Like` / `NotLike` | `findByFirstnameLike` | `x.firstname like ?1` |
| `StartingWith` / `EndingWith` / `Containing` | `findByFirstnameContaining` | `like ?1` + 인자 뒤/앞/양쪽에 `%` |
| `OrderBy` | `findByAgeOrderByLastnameDesc` | `where x.age = ?1 order by x.lastname desc` |
| `Not` | `findByLastnameNot` | `x.lastname <> ?1` |
| `In` / `NotIn` | `findByAgeIn(Collection<Age> ages)` | `x.age in ?1` |
| `True` / `False` | `findByActiveTrue()` | `x.active = true` |
| `IgnoreCase` | `findByFirstnameIgnoreCase` | `UPPER(x.firstname) = UPPER(?1)` |
| `Distinct` | `findDistinctByLastnameAndFirstname` | `select distinct ...` |

- `StartingWith`·`Containing` 등은 인자 안의 `%`, `_`를 **이스케이프**해서 리터럴로 비교

### 중첩 속성 탐색

```java
List<Person> findByAddressZipCode(ZipCode zipCode);   // x.address.zipCode
List<Person> findByAddress_ZipCode(ZipCode zipCode);  // _로 탐색 지점을 명시
```

- `AddressZipCode` 전체를 속성으로 먼저 찾고, 없으면 카멜 케이스를 **오른쪽부터** 잘라 `AddressZip` + `Code`, 다음엔 `Address` + `ZipCode` 순으로 시도
- `Person`에 `addressZip` 필드도 있으면 엉뚱한 속성을 고를 수 있음 → `_`로 직접 끊어 줌
- 그래서 필드 이름에는 `_`를 쓰지 말고 카멜 케이스를 쓰라고 권장

### 결과 개수 제한

```java
User findFirstByOrderByLastnameAsc();
User findTopByLastnameOrderByAgeDesc(String lastname);
List<User> findFirst10ByLastname(String lastname, Sort sort);
Slice<User> findTop3By(Pageable pageable);
```

- `First`와 `Top`은 같은 의미, 숫자를 생략하면 **1건**
- 한 건으로 제한한 쿼리는 결과를 `Optional`로 감싸는 것도 지원

### 파생 delete vs 벌크 delete

```java
interface UserRepository extends Repository<User, Long> {

    void deleteByRoleId(long roleId);              // 조회 후 한 건씩 삭제

    @Modifying
    @Query("delete from User u where u.role.id = ?1")
    void deleteInBulkByRoleId(long roleId);        // JPQL 한 번
}
```

| | 파생 `deleteBy…` | `@Modifying` + `@Query` |
|---|---|---|
| 동작 | 대상을 조회한 뒤 한 건씩 삭제 (`CrudRepository.delete(Iterable)`과 같음) | delete JPQL 한 번 |
| `@PreRemove` 콜백 | 호출됨 | 호출 안 됨 |
| 주의 | 삭제할 객체를 모두 메모리에 올림 | 영속성 컨텍스트가 옛 상태를 가질 수 있음 → 필요하면 `@Modifying(clearAutomatically = true)` |

---

## @Query로 직접 쓰기

- 메서드 이름 파서가 지원하지 않는 키워드가 필요하거나, 이름이 지나치게 길어질 때 사용
- `@Query`는 엔티티에 정의한 `@NamedQuery`보다 **우선**

```java
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.emailAddress = ?1")       // 위치 기반 파라미터
    User findByEmailAddress(String emailAddress);

    @Query("select u from User u where u.firstname = :firstname or u.lastname = :lastname")
    User findByLastnameOrFirstname(@Param("lastname") String lastname,
                                   @Param("firstname") String firstname);   // 이름 기반 파라미터

    @Modifying
    @Query("update User u set u.firstname = ?1 where u.lastname = ?2")
    int setFixedFirstnameFor(String firstname, String lastname);   // 수정 쿼리

    @NativeQuery("SELECT * FROM USERS WHERE EMAIL_ADDRESS = ?1")    // 네이티브 SQL
    User findByEmailAddressNative(String emailAddress);
}
```

- 이름 기반 파라미터(`@Param`)는 파라미터 순서가 바뀌어도 안전 — 컴파일 시 `-parameters` 플래그를 쓰면 `@Param` 생략 가능
- `@Modifying`은 `@Query`와 함께 쓸 때만 의미 있음 (파생 쿼리에는 필요 없음)
- 연관 엔티티를 함께 가져오는 `@Query("... join fetch ...")`, `@EntityGraph`는 [객체지향 쿼리 언어 중급](02_객체지향_쿼리_언어_중급.md) 참고

---

## 페이징과 정렬

```java
Page<User> findByLastname(String lastname, Pageable pageable);
Slice<User> findByLastname(String lastname, Pageable pageable);
List<User> findByLastname(String lastname, Sort sort);
```

```java
Page<User> users = repository.findAll(PageRequest.of(1, 20));   // 두 번째 페이지, 20개씩
```

| 반환 타입 | 아는 것 | 추가 쿼리 |
|---|---|---|
| `Page<T>` | 전체 개수·전체 페이지 수 | 전체 개수를 위한 **count 쿼리** |
| `Slice<T>` | 다음 Slice가 있는지만 | 없음 (`pageSize + 1`건을 조회해 판단) |
| `List<T>` | 요청한 범위의 데이터만 | 없음 |

- 페이징·정렬을 안 쓰고 싶으면 `null` 대신 `Pageable.unpaged()`, `Sort.unsorted()`

---

## 면접 포인트

- **구현 클래스 없이 어떻게 동작하나?** — Spring Data가 애플리케이션 실행 시 리포지토리 인터페이스의 구현체(리포지토리 프록시)를 만들어 애플리케이션 컨텍스트에 둠. 선언한 쿼리 메서드는 `@Query` 같은 선언된 쿼리가 있으면 그것을, 없으면 메서드 이름을 분석해 쿼리를 만듦.
- **`findById`가 `Optional`을 반환하는 이유와 처리** — 해당 ID의 데이터가 없을 수 있음을 타입으로 드러내 `null` 체크 누락을 막기 위함. `orElseThrow()`로 예외를 던지거나 `orElse()`로 기본값을 줌.
- **쿼리 메서드와 `@Query`는 언제 각각 쓰나?** — 조건이 단순하면 메서드 이름으로 만들고, 이름 파서가 지원하지 않는 기능이 필요하거나 이름이 너무 길어지면 `@Query`로 JPQL을 직접 씀.
- **`Page`와 `Slice` 차이** — `Page`는 전체 개수를 위해 count 쿼리를 한 번 더 실행하고, `Slice`는 count 없이 다음 데이터가 있는지만 알기 때문에 "더 보기" 방식에 가벼움.

---

## 참고

- 쿼리 메서드·`@Query` 문서는 Spring Boot 3 계열이 쓰는 Spring Data JPA 3.5 기준. `CrudRepository` 설명(Core concepts)은 최신 버전 문서에서 읽었으며 인터페이스 내용은 같음.
- 가이드 예제는 `jakarta.persistence.*`를 import함 — Spring Boot 3부터 `javax.persistence`가 아님.
- `@NativeQuery`는 `@Query(nativeQuery = true)`를 감싼 합성 어노테이션 (3.5 문서 기준) — `@Query(value = "...", nativeQuery = true)`로 써도 같은 뜻.
