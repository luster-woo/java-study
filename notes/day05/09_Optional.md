# Optional

> 출처: [Optional 클래스 - TCP School](https://tcpschool.com/java/java_stream_optional) · [Optional (Java SE 17)](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Optional.html) · [CrudRepository (Spring Data)](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html)

> [← Wrapper 클래스](08_Wrapper_클래스.md) · [day05 목록](../README.md)

## `java.util.Optional<T>`

### Optional이란
- `T` 타입 객체를 한 번 감싸는 **포장 객체** — Integer가 int를 감싸듯 어떤 참조든 담을 수 있음
- 값이 **있을 수도, 없을 수도 있음**을 타입으로 드러냄
  - 값 있음 → `isPresent()` true / 값 없음 → "비어 있는(empty)" Optional
- 제공 메서드로 `null` 확인용 if문 없이 NPE를 피함
- Java 8에서 추가(`java.util`)

### 어디에 쓰는가 (공식 문서의 API Note)
- 주 용도는 **메서드 반환 타입** — "결과가 없을 수 있음"을 나타내야 하고 `null`을 돌려주면 실수하기 쉬운 곳
- `Optional` 타입 변수 자체는 **절대 `null`이면 안 됨** → 항상 Optional 인스턴스를 가리키게

## Optional 객체의 생성

| 메서드 | 값이 있을 때 | 값이 `null`일 때 |
|---|---|---|
| `Optional.of(value)` | 값을 담은 Optional | **NullPointerException** |
| `Optional.ofNullable(value)` | 값을 담은 Optional | 빈 Optional |
| `Optional.empty()` | — | 빈 Optional |

- `null`이 될 가능성이 조금이라도 있으면 `ofNullable()`

## Optional 객체에 접근

### get()과 isPresent()
- `get()`: 저장된 값 반환, 비어 있으면 **NoSuchElementException**
  - 그래서 `isPresent()`로 먼저 확인 후 호출 — 다만 이러면 null 체크와 다를 바 없음
  - 공식 문서는 `get()` 대신 **`orElseThrow()`**(Java 10+) 사용을 권장
- `isEmpty()`(Java 11+): `isPresent()`의 반대

### 대체값 지정

| 메서드 | 값이 없을 때 |
|---|---|
| `orElse(other)` | 인수로 준 값 반환 |
| `orElseGet(supplier)` | 인수로 준 람다의 결과 반환 |
| `orElseThrow(exceptionSupplier)` | 인수로 준 예외를 던짐 |
| `orElseThrow()` (Java 10+) | NoSuchElementException |

- `orElse(x)`는 **값이 있어도 `x`를 먼저 계산**함(그냥 메서드 인수이므로) → 만드는 데 비용이 드는 기본값이면 `orElseGet`

### 값 가공 (Java 8~9 추가 메서드)
- `map(f)`: 값이 있으면 `f`를 적용한 결과를 Optional로, 없으면 빈 Optional
- `filter(p)`: 조건을 만족할 때만 값 유지
- `ifPresent(action)`: 값이 있을 때만 실행 / `ifPresentOrElse(action, emptyAction)`(Java 9+)
- `stream()`(Java 9+): 값이 있으면 요소 1개짜리 Stream

```java
import java.util.*;
import java.util.stream.IntStream;

public class OptionalTest {
    public static void main(String[] args) {
        // 생성
        Optional<String> opt = Optional.ofNullable("자바 Optional 객체");
        System.out.println(opt.get());                         // 자바 Optional 객체

        Optional<String> none = Optional.ofNullable(null);     // 빈 Optional
        System.out.println(none.isPresent());                  // false
        System.out.println(none.isEmpty());                    // true  (Java 11+)
        try {
            Optional.of(null);
        } catch (NullPointerException e) {
            System.out.println("of(null) -> NPE");             // of(null) -> NPE
        }
        try {
            none.get();
        } catch (NoSuchElementException e) {
            System.out.println("빈 Optional get() -> NoSuchElementException");
        }

        // 꺼내기 / 대체값
        Optional<String> empty = Optional.empty();
        System.out.println(empty.orElse("빈 Optional 객체"));    // 빈 Optional 객체
        System.out.println("[" + empty.orElseGet(String::new) + "]"); // []
        try {
            empty.orElseThrow(() -> new IllegalArgumentException("값 없음"));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());                // 값 없음
        }

        // orElse 는 값이 있어도 인수를 먼저 계산, orElseGet 은 비었을 때만 호출
        Optional<String> some = Optional.of("있음");
        System.out.println(some.orElse(make("orElse")));       // [orElse 호출됨] 출력 후 있음
        System.out.println(some.orElseGet(() -> make("orElseGet"))); // 있음

        // map / filter / ifPresent
        some.map(String::length).ifPresent(len -> System.out.println("길이 " + len)); // 길이 2
        System.out.println(some.filter(s -> s.startsWith("없")).isPresent());        // false

        // 기본형 Optional
        OptionalInt first = IntStream.of(4, 2, 1, 3).findFirst();
        System.out.println(first.getAsInt());                  // 4

        // findById 흉내: 없으면 Optional.empty()
        Map<Long, String> db = Map.of(1L, "kim");
        System.out.println(findById(db, 1L).orElse("없음"));   // kim
        System.out.println(findById(db, 2L).orElse("없음"));   // 없음
    }

    static String make(String who) {
        System.out.println("[" + who + " 호출됨]");
        return "기본값";
    }

    static Optional<String> findById(Map<Long, String> db, Long id) {
        return Optional.ofNullable(db.get(id));
    }
}
```

## 기본 타입의 Optional 클래스

- 기본형 스트림(`IntStream` 등)용으로 따로 있음 — 박싱 없이 기본형을 그대로 담음

| 클래스 | 값 꺼내기 |
|---|---|
| `Optional<T>` | `T get()` |
| `OptionalInt` | `int getAsInt()` |
| `OptionalLong` | `long getAsLong()` |
| `OptionalDouble` | `double getAsDouble()` |

- 예: `IntStream.findFirst()`, `average()`의 반환 타입이 이것들

## JPA에서 만나는 Optional — `findById()`

- Spring Data `CrudRepository`의 `findById(ID id)` 반환 타입이 **`Optional<T>`**
  - 찾으면 엔티티를 담은 Optional, 없으면 `Optional.empty()`
  - `id`가 `null`이면 IllegalArgumentException
- 그래서 서비스 계층에서는 보통 "없으면 예외"로 바로 풀어냄

```java
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;   // extends JpaRepository<Member, Long>

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음: " + id));
    }

    public String findNameOrGuest(Long id) {
        return memberRepository.findById(id)
                .map(Member::getName)
                .orElse("guest");
    }
}
```

- `findById(id).get()`은 회원이 없으면 NoSuchElementException("No value present")이 그대로 올라감 → 어떤 예외로 바꿀지 직접 정하는 `orElseThrow(...)`를 씀

---

## 참고

- TCP School은 `empty()`를 "Optional을 null로 초기화"라고 설명하지만, 정확히는 **값이 없는 Optional 객체**를 돌려주는 것(Optional 변수 자체는 null이 아님)
- 공식 문서: 빈 Optional인지 `== Optional.empty()`로 비교하지 말 것 — 싱글턴이라는 보장이 없음. `isEmpty()`/`isPresent()` 사용
- Spring 코드는 이 환경에서 실행하지 못함(문법·import만 확인). 위 순수 자바 예제는 JDK 11로 실행해 출력 확인

## 면접 포인트

- **Optional을 왜 쓰나?** "값이 없을 수 있음"을 반환 타입으로 드러내서 호출하는 쪽이 빈 경우를 처리하도록 강제함. null 반환보다 NPE 실수가 줄어듦.
- **orElse와 orElseGet 차이?** `orElse`는 값이 있어도 인수를 먼저 평가하고, `orElseGet`은 비어 있을 때만 Supplier를 호출함. 기본값을 만드는 비용이 크면 `orElseGet`.
- **findById가 Optional을 돌려주는 이유와 처리?** 해당 id의 엔티티가 없을 수 있기 때문. `orElseThrow`로 도메인에 맞는 예외로 바꾸는 것이 일반적이고 `get()`을 바로 부르지 않음.
- **Optional을 필드나 매개변수로 써도 되나?** 공식 문서는 주 용도를 반환 타입으로 한정함. Optional 변수 자체가 null이 되면 의미가 없어짐.
