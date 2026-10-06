# Servlet Web To Spring Boot

> 교재: 15기_자바전공_03_springboot_0507 (전공) — 3. Servlet Web To Spring Boot

> [← 주요 Annotations](02_주요_Annotations.md) · [day08 목록](../README.md) · [관점 지향 프로그래밍 →](04_관점_지향_프로그래밍.md)

---

## Servlet Web To Spring Boot

### 출발점: Servlet Web (JDBC 버전)

- 이전에 만든 서블릿 + JDBC 웹 프로젝트를 Spring Boot 프로젝트로 옮기는 과정
- 서블릿 시절 구조

```
Browser → Filter → Controller(HttpServlet) → Service → Dao → DBUtil(Connection) → DB
                                       └ DTO(Member, Address ...) 로 데이터 전달
```

### 스프링에서 빈으로 관리할 대상은?

- 기준: **재사용되는 로직을 가진, 상태 없는(stateless) 객체**인가 → 빈 / 데이터를 담는 객체 → 빈 아님

| 영역 | 빈으로 관리? | 앞으로 바뀌는 방향 |
|---|---|---|
| Model (Service, Dao) | O | Dao → **MyBatis** |
| Controller | O | 서블릿 → **Spring MVC** |
| 설정 (Connection) | O | DBUtil → **Connection Pool** |
| DTO | X (요청마다 새로 만드는 데이터) | **lombok**으로 코드 간결화 |

---

## 빈의 스코프

### @Scope — 빈 객체가 어떤 범위까지 존재하나

- 스프링 컨테이너가 빈 객체를 **어떻게 생성하고 언제까지 관리할지** 결정

```java
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Scope {
    /**
     * 생략하면 SCOPE_SINGLETON 적용
     * ConfigurableBeanFactory#SCOPE_PROTOTYPE
     * ConfigurableBeanFactory#SCOPE_SINGLETON
     * WebApplicationContext#SCOPE_REQUEST
     * WebApplicationContext#SCOPE_SESSION
     */
    @AliasFor("value")
    String scopeName() default "";
}
```

```java
// 묵시적 빈 등록 시 scope 지정: 클래스 레벨
@Component
@Scope("singleton")      // 기본 scope라 생략 가능
public class Watch { ... }

// 명시적 빈 등록 시 scope 지정: 메서드 레벨
@Bean
@Scope("prototype")
public SWasher sWasher() {
    return new SWasher();
}
```

### 스코프별 특징

| scope | 생성 시점 | 대상 |
|---|---|---|
| **singleton** | 컨테이너에 **하나만** 만들어 재사용 (기본값, `@Scope` 생략 시) | **stateless**해서 개별 구분이 필요 없는 빈 — 비즈니스 로직 재사용 |
| **prototype** | **요청할 때마다 새** 빈 객체 생성 | **stateful**해서 각 빈이 독립 상태를 가져야 할 때 |
| **request / session** | request·session이 유지되는 동안 존재 | 웹 환경 전용. 목적은 prototype과 비슷 |

- prototype은 재사용되지 않으므로 **꼭 빈이어야 하는지** 고민 필요
- 그래도 빈으로 만들면 얻는 장점: 다른 빈을 **주입**받기, 빈 **라이프사이클**에 따른 작업, **AOP** 적용 등

---

## 프로젝트 가져오기

### BE_FINAL의 model, exception 코드 가져오기 (TODO: 03)

- `Import > General > Archive File > BE_Proj_Final_Lab.zip` (pom.xml은 제외하고 가져옴)
- pom.xml에 커넥션 풀 의존성 추가 — 버전은 Boot가 관리하므로 생략

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
</dependency>
```

---

## lombok 활용

### lombok 설치

- 사이트: projectlombok.org
- **IDE에 설치**해야 IDE가 생성된 메서드를 인식함
  - Eclipse 계열: 설치 프로그램(lombok.jar 실행)
  - VSCode 등: Extension
- 의존성은 이미 추가되어 있음 (`provided` — 컴파일 때만 필요)

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>${lombok-version}</version>
    <scope>provided</scope>
</dependency>
```

### 자주 사용하는 애너테이션

| 애너테이션 | 설명 |
|---|---|
| `@Getter` / `@Setter` | 멤버 변수의 getter / setter 생성 |
| `@ToString` | `toString()` 재정의 |
| `@EqualsAndHashCode` | `equals()`, `hashCode()` 재정의 |
| `@NonNull` | 객체형에 null 불가 표시 + null 체크 로직 삽입 |
| `@AllArgsConstructor` | 모든 멤버 변수를 파라미터로 받는 생성자 |
| `@NoArgsConstructor` | 기본 생성자 명시적 생성 |
| `@RequiredArgsConstructor` | `final` 및 `@NonNull` 멤버 변수로 생성자 생성 |
| `@Data` | `@ToString` + `@EqualsAndHashCode` + `@Getter` + `@Setter` + `@RequiredArgsConstructor` (`@RequiredArgsConstructor`는 다른 생성자가 없을 때만) |
| `@Builder` | 빌더 패턴으로 객체 생성 지원 |
| `@Slf4j` | Slf4j `Logger` 타입 변수 `log` 생성 |

### 동작 확인 (TODO: 04)

```java
@Data   // @Getter/@Setter/@ToString/@EqualsAndHashCode/@RequiredArgsConstructor
public class Member {
    private int mno;
    private String name;
    private String email;
    private String password;
    private String role;
    private List<Address> addresses = new ArrayList<>();   // 주소 목록

    public Member() {}

    public Member(String name, String email, String password) {
        this(0, name, email, password, null);
    }

    public Member(int mno, String name, String email, String password, String role) {
        this.mno = mno;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
```

- getter/setter/toString/equals/hashCode를 직접 안 써도 사용 가능해짐

### @Data 사용 주의 — toString() 순환 참조

- `@Data`는 많은 코드를 자동으로 만들어서 생각지 못한 문제 발생 가능
- Member ↔ Address가 서로를 참조하면 `toString()`이 서로를 무한히 호출

```
Member.toString() → addresses(Address).toString() → member(Member).toString() → ...  StackOverflowError
```

```java
@Data
public class Member {
    private int mno;
    private String name;
    private List<Address> addresses = new ArrayList<>();
}

@Data
public class Address {
    private int ano;
    private int mno;
    private Member member;
}
```

- 순수 자바로 같은 모양을 재현하면

```java
import java.util.ArrayList;
import java.util.List;

public class CircularToString {
    public static void main(String[] args) {
        Member m = new Member();
        Address a = new Address();
        a.member = m;
        m.addresses.add(a);
        try {
            System.out.println(m);
        } catch (StackOverflowError e) {
            System.out.println("StackOverflowError");
        }
    }
}

class Member {   // @Data가 만드는 toString()과 같은 모양
    String name = "hong";
    List<Address> addresses = new ArrayList<>();
    public String toString() { return "Member(name=" + name + ", addresses=" + addresses + ")"; }
}

class Address {
    int ano = 1;
    Member member;
    public String toString() { return "Address(ano=" + ano + ", member=" + member + ")"; }
}
// 결과
// StackOverflowError
```

- 해결: 한쪽 필드를 `toString`·`equals/hashCode` 대상에서 **제외**

```java
@Data
public class Member {
    private int mno;
    private String name;
    // ...
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Address> addresses = new ArrayList<>();
}
```

### 생성자 관리

```java
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Member {
    private int mno;
    private @NonNull String name;
    private @NonNull String email;
    private @NonNull String password;
    private String role;
    private List<Address> addresses = new ArrayList<>();

    // public Member() { }                                              // @NoArgsConstructor
    // public Member(String name, String email, String password) {...}  // @RequiredArgsConstructor
    // public Member(int mno, ..., List<Address> addresses) {...}       // @AllArgsConstructor
}
```

- `@RequiredArgsConstructor`의 대상을 만들 때 `@NonNull`보다 **`final`을 주로 많이 사용**

### @Builder

- 생성자의 파라미터를 바탕으로 **builder 패턴** 형식으로 객체 설정
  - **클래스 레벨**에 선언: 전체 필드 생성자가 필요
  - **생성자 레벨**에 선언: 그 생성자의 파라미터만 builder 대상

```java
public class Member {
    // ...
    @Builder
    public Member(int mno, String name, String email, String password, String role) {
        this.mno = mno;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}

// 생성자: 순서와 개수를 맞춰야 하고, 어떤 값이 무엇인지 안 보임
Member old = new Member(0, "hong", "hong@ssafy.com", "1234", null);

// builder: 이름으로 설정, 순서 무관, 안 넣은 값은 기본값
Member member = Member.builder().password("1234")
                                .name("hong")
                                .email("hong@ssafy.com")
                                .build();
```

### @Builder의 default 값

- `@AllArgsConstructor`에 적용된 `@Builder`는 **필드 초기값을 무시**하고 모든 필드를 기본값(0, null)으로 채움
  - `addresses = new ArrayList<>()`를 써 놔도 builder로 만들면 `null`
- 초기값을 유지하려면 **`@Builder.Default`**

```java
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    private int mno;
    private @NonNull String name;
    private @NonNull String email;
    private @NonNull String password;
    private String role;
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();
}
```

### Lombok을 이용한 DTO 수정 (TODO: 05)

| 클래스 | 적용 | 이유 |
|---|---|---|
| `Address` | `@Data @AllArgsConstructor @NoArgsConstructor @Builder` | 단순 데이터 클래스 |
| `Page<T>` | `@Getter @Setter @ToString` | 생성자가 파라미터를 단순 대입하지 않고 계산까지 하므로 생성자는 직접 작성 |
| `SearchCondition` | `@Data` + 생성자 레벨 `@Builder` | `itemsPerPage = 5` 같은 기본값 필드는 builder 대상에서 빼고 `key, word, currentPage`만 받음 |

```java
@Data
public class SearchCondition {
    private String key;
    // ...
    private int itemsPerPage = 5;

    @Builder
    public SearchCondition(String key, String word, int currentPage) {
        this.key = key;
        this.word = word;
        this.currentPage = currentPage;
    }
}
```

### DTO 동작 테스트 (TODO: 06)

- `@Slf4j` → `private static final Logger log` 변수 자동 생성

```java
@SpringBootTest
@Slf4j
public class LombokTest {
    @Test
    public void makeMemberTest() {
        // @NonNull 필드(name, email, password)를 안 넣으면 null 체크에서 예외
        Assertions.assertThrows(NullPointerException.class, () -> Member.builder().build());

        Member member = Member.builder().password("1234").name("hong").email("hong@ssafy.com")
                              .build();
        log.debug("member: {}", member);
    }

    @Test
    public void makeSearchConditionTest() {
        SearchCondition sc = SearchCondition.builder().currentPage(1).key("email").word("01")
                                            .build();
        log.debug("sc: {}", sc);
    }
}
```

---

## DBUtil 수정

### DBUtil은 재사용 가능한 컴포넌트 (TODO: 07)

- `@Component`로 빈 등록
- **singleton은 스프링 프레임워크가 지원** → 직접 싱글톤 패턴(static 인스턴스 + `getUtil()`)을 구현할 필요 없음, 빈은 **POJO**로 구성
- 실습 전 SQL을 실행해 스키마 설치

```java
@Component
public class DBUtil {
    private final String driverClassName = "com.mysql.cj.jdbc.Driver";
    private final String url = "jdbc:mysql://localhost:3306/ssafylive?serverTimezone=UTC";
    private final String user = "ssafy";
    private final String password = "ssafy";
    private HikariDataSource ds;

    private DBUtil() {
        init();
    }

    // public static DBUtil getUtil() {   // 싱글톤 패턴 코드 제거
    //     return util;
    // }
}
```

---

## Model 영역

### Dao의 빈 구성 (TODO: 08)

- stereotype annotation **`@Repository`** 적용

```java
@Repository
public class BasicAddressDao implements AddressDao { ... }

@Repository
public class BasicMemberDao implements MemberDao { ... }
```

### Service의 빈 구성 (TODO: 09)

- stereotype annotation **`@Service`** 적용
- service는 DBUtil, dao(== repository)를 **반드시 필요한 의존성**으로 가짐
  - **final 필드** + **`@RequiredArgsConstructor`** → 생성자 주입
  - 생성자가 하나뿐이면 **`@Autowired` 생략** 가능

```java
@Service
@RequiredArgsConstructor
public class BasicAddressService implements AddressService {
    private final AddressDao dao;
    private final DBUtil util;
    // ...
}

@Service
@RequiredArgsConstructor
public class BasicMemberService implements MemberService {
    private final MemberDao mDao;
    private final AddressDao aDao;
    private final DBUtil util;
    // ...
}
```

---

## Web 부분 처리

### Controller 수정 (TODO: 10)

- 아직 웹 서비스는 불가 — **컴파일되도록** 기존 코드 변경분만 반영 (서블릿은 다음에 Spring MVC로 교체)

```java
@RequiredArgsConstructor
public class AuthController extends HttpServlet implements ControllerHelper {
    private final MemberService mService;
    private final AddressService aService;
    // ...
}

@RequiredArgsConstructor
public class MemberController extends HttpServlet implements ControllerHelper {
    private final BasicMemberService mService;
    // ...
}
```

### 기타 (TODO: 11)

| 대상 | 처리 |
|---|---|
| Listener | 객체 초기화는 프레임워크의 역할 → **제거** (`DBUtil.getUtil()` 호출 불필요) |
| Filter | 나중에 **Interceptor로 대체**, 일단 유지 |

```java
@WebListener
public class MyContextListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("context 초기화 중입니다. 전체 애플리케이션 초기화와 관련된 작업 진행");
        // DBUtil.getUtil();
    }
}
```

---

## Spring HRM

### 빈 구성 테스트 (TODO: 12)

```java
@SpringBootTest
@Slf4j
public class ServiceTest {
    @Autowired
    MemberService mService;
    @Autowired
    MemberDao mDao;

    @Test
    void beanCreationTest() {               // 빈이 등록·주입되었는가
        Assertions.assertNotNull(mService);
        Assertions.assertNotNull(mDao);
    }

    @Test
    void loginTest() throws SQLException {  // 주입된 빈으로 실제 DB 조회
        Member m = mService.login("admin@ssafy.com", "1234");
        log.debug("member: {}", m);
        Assertions.assertNotNull(m);
        Assertions.assertEquals("관리자", m.getName());
    }
}
```

---

## 내일 수업을 위해서

- Git에서 `FW_04_aop_lab.zip` 다운로드 → 본인 Workspace에 압축 해제
- `File > Open Projects from File System > Import Source: Directory` → 대상 폴더 선택 → 실행 결과 확인

---

## 참고

> 참고: 교재 30쪽 `new Member("hong", "hong@ssafy.com", "1234", null)`은 파라미터 4개짜리 생성자가 없어 컴파일 오류(3개 또는 5개만 있음). 위에서는 5개짜리로 고침. 교재 원문의 닫는 따옴표도 `“`(전각)로 되어 있어 그대로 옮기면 컴파일 오류.

- `private` 생성자여도 스프링은 리플렉션으로 빈을 생성할 수 있음(교재 DBUtil이 그 경우)
- `CircularToString` 예제는 이 sandbox(JDK 11)에서 실행해 결과 확인
