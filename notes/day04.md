# D4 · 추상 클래스·인터페이스·제네릭·enum·annotation

## 오늘 볼 강의

| 순서 | 반 | 강의 |
|---|---|---|
| 1 | 전공 | [인터페이스] abstract class |
| 2 | 전공 | [인터페이스] Interface |
| 3 | 전공 | [인터페이스] Interface의 필요성 |
| 4 | 전공 | [인터페이스] default_static_private |
| 5 | 전공 | [제너릭] generic |
| 6 | 전공 | [제너릭] enum |
| 7 | 전공 | [제너릭] annotation |

오늘은 **Spring을 이해하는 데 가장 중요한 날**이다. 인터페이스와 애너테이션이 Spring 코드의 대부분이다.

---

## 1. 추상 클래스

**몸통 없는 메서드(추상 메서드)를 가진 미완성 클래스.** 직접 `new` 할 수 없고, 자식이 완성해야 한다.

```java
abstract class Shape {
    String name;

    Shape(String name) { this.name = name; }

    abstract double area();          // 몸통 없음. 자식이 반드시 구현

    void print() {                   // 일반 메서드도 가질 수 있음
        System.out.println(name + " 넓이: " + area());
    }
}

class Circle extends Shape {
    double r;
    Circle(double r) { super("원"); this.r = r; }

    @Override
    double area() { return Math.PI * r * r; }
}

class Rect extends Shape {
    double w, h;
    Rect(double w, double h) { super("사각형"); this.w = w; this.h = h; }

    @Override
    double area() { return w * h; }
}

public class ShapeTest {
    public static void main(String[] args) {
        // Shape s = new Shape("?");     // 에러: 추상 클래스는 객체 생성 불가
        Shape[] shapes = { new Circle(1), new Rect(2, 3) };
        for (Shape s : shapes) s.print();
    }
}
```

## 2. 인터페이스

**"이 기능들을 반드시 제공한다"는 약속(규격)만 정의한 것.** 구현은 클래스가 한다.

```java
interface Payment {
    void pay(int amount);            // public abstract가 자동으로 붙음
}

class CardPayment implements Payment {
    @Override
    public void pay(int amount) {    // 구현할 때는 public 필수
        System.out.println("카드로 " + amount + "원 결제");
    }
}

class KakaoPayment implements Payment {
    @Override
    public void pay(int amount) {
        System.out.println("카카오페이로 " + amount + "원 결제");
    }
}

class OrderService {
    private final Payment payment;   // 인터페이스 타입에만 의존

    OrderService(Payment payment) {  // 어떤 결제 수단이 올지는 밖에서 정해 줌
        this.payment = payment;
    }

    void order(int price) {
        payment.pay(price);
    }
}

public class PaymentTest {
    public static void main(String[] args) {
        new OrderService(new CardPayment()).order(10000);
        new OrderService(new KakaoPayment()).order(5000);
    }
}
```

**이게 Spring DI(D7)의 원형이다.** `OrderService`는 `CardPayment`인지 `KakaoPayment`인지 모르고 `Payment`만 안다. 결제 수단을 바꿔도 `OrderService` 코드는 한 줄도 안 바뀐다. 이걸 **느슨한 결합**이라고 한다. Spring에서는 `new CardPayment()`를 넣어 주는 일을 Spring이 대신 한다.

### 추상 클래스 vs 인터페이스

| | 추상 클래스 | 인터페이스 |
|---|---|---|
| 키워드 | `extends` | `implements` |
| 상속 개수 | **하나만** | **여러 개** (`implements A, B`) |
| 필드 | 일반 필드 가능 | 상수만 (`public static final` 자동) |
| 생성자 | 있음 | 없음 |
| 용도 | "is-a" 관계, 공통 코드 공유 | "can-do" 규격, 역할 정의 |
| 예 | `Shape` → `Circle` | `Payment`, `Comparable`, `Runnable` |

요즘은 **인터페이스를 먼저** 쓰고, 공통 코드가 정말 필요할 때 추상 클래스를 쓴다.

### default·static·private 메서드 (Java 8, 9+)

```java
interface Greeter {
    String name();

    default void greet() {                     // 구현을 가진 메서드. 구현 클래스가 그대로 쓰거나 재정의
        System.out.println(prefix() + name());
    }

    static Greeter of(String n) {              // 인터페이스 이름으로 호출: Greeter.of("규")
        return () -> n;                        // 람다 (D5에서 배움)
    }

    private String prefix() {                  // 인터페이스 내부에서만 쓰는 도우미
        return "안녕, ";
    }
}

public class GreeterTest {
    public static void main(String[] args) {
        Greeter.of("규").greet();   // 안녕, 규
    }
}
```
`default`는 이미 많은 클래스가 구현한 인터페이스에 **새 기능을 추가해도 기존 코드가 안 깨지게** 하려고 생겼다.

## 3. 제네릭 (Generic)

**타입을 나중에 정하는 문법.** `<T>`의 T 자리에 사용할 때 타입을 넣는다.

```java
import java.util.ArrayList;
import java.util.List;

class Box<T> {                      // T = 아직 정하지 않은 타입
    private T item;
    void put(T item) { this.item = item; }
    T get() { return item; }
}

public class GenericTest {
    // 제네릭 메서드: 반환 타입 앞에 <T>
    static <T> void printAll(List<T> list) {
        for (T x : list) System.out.print(x + " ");
        System.out.println();
    }

    // 상한 제한: Number를 상속한 타입만 (Integer, Double ...)
    static <T extends Number> double sum(List<T> list) {
        double s = 0;
        for (T x : list) s += x.doubleValue();
        return s;
    }

    public static void main(String[] args) {
        Box<String> sb = new Box<>();     // 오른쪽 <>는 비워도 됨 (다이아몬드)
        sb.put("hello");
        String s = sb.get();              // 형변환 필요 없음
        // sb.put(123);                   // 컴파일 에러: 잘못된 타입을 미리 막아 줌

        List<Integer> nums = new ArrayList<>();
        nums.add(1); nums.add(2); nums.add(3);
        printAll(nums);
        System.out.println(sum(nums));    // 6.0
    }
}
```

- 제네릭에는 **기본형을 못 넣는다.** `List<int>` ❌ → `List<Integer>` ⭕ (Wrapper 클래스, D5)
- 타입 이름 관례: `T`(Type), `E`(Element), `K`·`V`(Key·Value)
- 와일드카드 `?`: `List<?>`는 "아무 타입의 List", `List<? extends Number>`는 "Number 이하 타입의 List". 지금은 읽을 줄만 알면 된다.

**Spring에서 보게 될 모습**: `JpaRepository<User, Long>` → "User 엔티티를, Long 타입 ID로 다루는 Repository"

## 4. enum (열거형)

**정해진 값 몇 개만 가질 수 있는 타입.** 문자열 상수보다 안전하다(오타를 컴파일러가 잡음).

```java
enum Role {
    USER("일반 사용자"),
    ADMIN("관리자");

    private final String label;          // enum도 필드·생성자·메서드를 가질 수 있음

    Role(String label) { this.label = label; }

    public String getLabel() { return label; }
}

public class EnumTest {
    public static void main(String[] args) {
        Role r = Role.ADMIN;
        System.out.println(r);              // ADMIN
        System.out.println(r.getLabel());   // 관리자
        System.out.println(r.name());       // ADMIN (String 타입으로 돌려줌)
        System.out.println(r.ordinal());    // 1 (순서)
        System.out.println(Role.valueOf("USER"));  // 문자열 → enum

        for (Role x : Role.values()) System.out.println(x);

        switch (r) {
            case USER -> System.out.println("일반");
            case ADMIN -> System.out.println("관리");
        }
    }
}
```
JPA에서는 `@Enumerated(EnumType.STRING)`으로 회원 권한·주문 상태를 저장할 때 쓴다. **`ordinal`(숫자)로 저장하면 순서를 바꿨을 때 데이터가 꼬이니** 항상 STRING.

## 5. 애너테이션 (Annotation)

`@`로 시작하는 **표시(메타데이터)**. 그 자체로는 아무 일도 안 하고, **컴파일러나 프레임워크가 읽어서** 동작을 바꾼다.

| 애너테이션 | 누가 읽나 | 하는 일 |
|---|---|---|
| `@Override` | 컴파일러 | 진짜 재정의인지 검사 |
| `@Deprecated` | 컴파일러 | 쓰지 말라는 경고 |
| `@FunctionalInterface` | 컴파일러 | 추상 메서드가 하나인지 검사 (D5) |
| `@Component`, `@Service` | Spring | 이 클래스를 Bean으로 등록 (D7) |
| `@GetMapping("/users")` | Spring | 이 메서드를 URL과 연결 (D9) |
| `@Entity` | JPA | 이 클래스를 DB 테이블과 연결 (D11) |
| `@Getter` | Lombok | getter 코드 자동 생성 (D7) |

Python 데코레이터(`@app.route`)와 모양은 같지만, Python 데코레이터는 **함수를 감싸서 직접 실행**되고 Java 애너테이션은 **표시만** 한다는 점이 다르다.

직접 만들 일은 거의 없지만, 모양은 이렇다.

```java
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)    // 실행 중에도 읽을 수 있게
@Target(ElementType.METHOD)            // 메서드에만 붙일 수 있게
@interface MyLog {
    String value() default "";
}

public class AnnotationTest {
    @MyLog("주문 처리")
    void order() { }

    public static void main(String[] args) throws Exception {
        MyLog log = AnnotationTest.class.getDeclaredMethod("order").getAnnotation(MyLog.class);
        System.out.println(log.value());   // 주문 처리  ← 이렇게 "읽어서" 쓰는 게 프레임워크의 방식
    }
}
```

---

## 자주 하는 실수

| 실수 | 증상 | 고치기 |
|---|---|---|
| 인터페이스 메서드 구현할 때 `public` 빠뜨림 | "weaker access privileges" 에러 | 구현 메서드에 `public` |
| 추상 메서드를 다 구현 안 함 | 클래스에 빨간 줄 | `Alt + Enter` → Implement methods |
| `List<int>` | 에러 | `List<Integer>` |
| enum 비교를 `equals`로 | 동작은 하지만 불필요 | enum은 `==`로 비교해도 안전 |
| `extends`와 `implements` 혼동 | 에러 | 클래스→클래스 `extends`, 클래스→인터페이스 `implements`, 인터페이스→인터페이스 `extends` |

---

## 면접 질문

1. **추상 클래스와 인터페이스의 차이는?**
   추상 클래스는 상태(필드)와 생성자를 가질 수 있고 하나만 상속할 수 있다. 인터페이스는 구현해야 할 규격을 정의하고, 한 클래스가 여러 개를 구현할 수 있다. 공통 코드 공유가 필요하면 추상 클래스, 역할 정의는 인터페이스.
2. **인터페이스를 왜 쓰나요?**
   사용하는 쪽이 구현체가 아니라 규격에만 의존하게 해서 결합도를 낮추기 위해. 구현을 바꿔도 사용하는 코드는 수정하지 않아도 되고, 테스트할 때 가짜 구현으로 바꾸기도 쉽다.
3. **제네릭을 쓰는 이유는?**
   컴파일 시점에 타입을 검사해서 잘못된 타입이 들어가는 걸 막고, 꺼낼 때 형변환이 필요 없게 하기 위해. 한 코드로 여러 타입을 다룰 수 있다.
4. **enum을 쓰면 좋은 점은?**
   허용된 값만 쓸 수 있어서 오타나 잘못된 값을 컴파일 시점에 막고, 값마다 필드·메서드를 붙일 수 있다.
5. **애너테이션은 어떻게 동작하나요?**
   애너테이션 자체는 메타데이터일 뿐이다. 컴파일러는 컴파일 시점에(애너테이션 프로세서, Lombok이 이 방식), Spring 같은 프레임워크는 실행 중에 리플렉션으로 읽어서 정해진 동작을 한다.

---

## 오늘의 실습

- [ ] 강의 코드 따라 치기 (`src/day04/`)
- [ ] `Repository<T>` 인터페이스 만들기: `save(T item)`, `findAll()` (반환 `List<T>`)
- [ ] `MemoryRepository<T> implements Repository<T>`: 내부에 `ArrayList<T>`로 저장
- [ ] `Account`를 `MemoryRepository<Account>`에 저장하고 전체 출력 → Spring의 `JpaRepository<User, Long>`이 이 구조의 확장판이다
- [ ] 연습문제 3개: [problems/day04.md](problems/day04.md) (`src/practice/`에 풀기)
- [ ] 여유 있으면 SWEA·프로그래머스에서 1~2문제 더
- [ ] 커밋: `day04: 인터페이스·제네릭·enum / Repository<T> 실습`
