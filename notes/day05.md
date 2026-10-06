# D5 · 컬렉션·람다·Stream·Wrapper·Optional

## 오늘 볼 강의

| 순서 | 반 | 강의 |
|---|---|---|
| 1 | 전공 | [컬렉션1] List 계열 |
| 2 | 전공 | [컬렉션1] Set 계열 |
| 3 | 전공 | [컬렉션1] Map 계열 |
| 4 | 전공 | [컬렉션1] 정렬 |
| 5 | 전공 | [컬렉션2] Lambda 표현식 |
| 6 | 전공 | [컬렉션2] 표준함수형 인터페이스 |
| 7 | 전공 | [컬렉션2] Stream API |
| 8 | 읽기 | TCP School — Wrapper 클래스 |
| 9 | 읽기 | TCP School — Optional 클래스 |

---

## 1. Wrapper 클래스 (먼저 보면 컬렉션이 쉬워진다)

컬렉션·제네릭에는 **기본형을 못 넣는다.** 그래서 기본형을 객체로 감싼 Wrapper 클래스를 쓴다.

| 기본형 | Wrapper |
|---|---|
| `int` | `Integer` |
| `long` | `Long` |
| `double` | `Double` |
| `boolean` | `Boolean` |
| `char` | `Character` |

```java
import java.util.ArrayList;
import java.util.List;

public class WrapperTest {
    public static void main(String[] args) {
        Integer a = 10;          // 오토박싱: int → Integer 자동
        int b = a;               // 오토언박싱: Integer → int 자동

        List<Integer> list = new ArrayList<>();
        list.add(5);             // 5가 Integer로 자동 박싱

        // 함정 1: Wrapper끼리 == 비교
        Integer x = 127, y = 127;
        System.out.println(x == y);        // true  (-128~127은 캐시된 같은 객체)
        Integer p = 128, q = 128;
        System.out.println(p == q);        // false (다른 객체!)
        System.out.println(p.equals(q));   // true  ← Wrapper 비교는 equals

        // 함정 2: null 언박싱
        Integer n = null;
        // int m = n;                      // NullPointerException

        System.out.println(Integer.parseInt("42") + 1);   // 43
        System.out.println(Integer.MAX_VALUE);
    }
}
```

**JPA와의 연결**: 엔티티 ID를 `long`이 아니라 `Long`으로 쓴다. 아직 저장 안 된 객체는 ID가 **없음(`null`)**이어야 하는데, `long`은 `null`을 못 가지고 `0`이 되기 때문이다.

## 2. 컬렉션 프레임워크

| Python | Java 인터페이스 | 주로 쓰는 구현체 |
|---|---|---|
| `list` | `List<E>` | `ArrayList` |
| `set` | `Set<E>` | `HashSet` (순서 없음), `TreeSet` (정렬됨), `LinkedHashSet` (넣은 순서) |
| `dict` | `Map<K, V>` | `HashMap`, `TreeMap` (키 정렬), `LinkedHashMap` (넣은 순서) |
| `collections.deque` | `Deque<E>` | `ArrayDeque` (스택·큐 둘 다) |
| `heapq` | `PriorityQueue<E>` | `PriorityQueue` |

**변수 타입은 인터페이스로, 생성은 구현체로** 쓰는 게 관례다. D4에서 배운 "인터페이스에 의존"이다.

```java
List<String> list = new ArrayList<>();   // ⭕
ArrayList<String> list = new ArrayList<>();   // 동작은 하지만 비추천
```

### List

```java
import java.util.*;

public class ListTest {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("b");                    // append
        list.add("a");
        list.add(0, "c");                 // insert(0, "c")
        System.out.println(list);         // [c, b, a]  (List는 println으로 바로 보임)
        System.out.println(list.get(1));  // b       ← list[1] 문법은 없음
        list.set(1, "B");                 // list[1] = "B"
        System.out.println(list.size());  // 3       ← len(list)
        System.out.println(list.contains("a"));   // true   ← "a" in list
        list.remove("a");                 // 값으로 삭제
        list.remove(0);                   // 인덱스로 삭제
        System.out.println(list);         // [B]

        List<Integer> nums = new ArrayList<>(List.of(5, 3, 1));  // List.of는 수정 불가 리스트
        nums.remove(Integer.valueOf(3));  // 주의: remove(3)은 "인덱스 3"으로 해석됨
        System.out.println(nums);         // [5, 1]
    }
}
```

| | `ArrayList` | `LinkedList` |
|---|---|---|
| 구조 | 배열 | 노드가 서로 연결 |
| `get(i)` | 빠름 O(1) | 느림 O(n) |
| 중간 삽입·삭제 | 느림 O(n) | 위치를 알면 빠름 O(1) |
| 결론 | **거의 항상 이것** | 큐가 필요하면 `ArrayDeque`가 더 나음 |

### Set과 Map

```java
import java.util.*;

public class SetMapTest {
    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        set.add("java"); set.add("spring"); set.add("java");
        System.out.println(set.size());           // 2 (중복 제거)

        Map<String, Integer> count = new HashMap<>();
        String[] words = {"a", "b", "a", "c", "a"};
        for (String w : words) {
            count.put(w, count.getOrDefault(w, 0) + 1);   // dict.get(w, 0) + 1
        }
        System.out.println(count.get("a"));        // 3   ← count["a"]
        System.out.println(count.containsKey("z"));// false ← "z" in count

        for (Map.Entry<String, Integer> e : count.entrySet()) {   // for k, v in count.items()
            System.out.println(e.getKey() + "=" + e.getValue());
        }

        Map<String, Integer> sorted = new TreeMap<>(count);   // 키 순 정렬
        System.out.println(sorted);                // {a=3, b=1, c=1}
    }
}
```

### 스택·큐 (백준 단골)

```java
import java.util.*;

public class StackQueueTest {
    public static void main(String[] args) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1); stack.push(2);
        System.out.println(stack.pop());     // 2

        Deque<Integer> queue = new ArrayDeque<>();
        queue.offer(1); queue.offer(2);
        System.out.println(queue.poll());    // 1

        PriorityQueue<Integer> pq = new PriorityQueue<>();                       // 최소 힙
        PriorityQueue<Integer> maxPq = new PriorityQueue<>(Comparator.reverseOrder());  // 최대 힙
        pq.add(5); pq.add(1); pq.add(3);
        System.out.println(pq.poll());       // 1
    }
}
```
`Stack` 클래스는 오래된 방식이라 `ArrayDeque`를 쓴다.

### 정렬: Comparable과 Comparator

| | `Comparable` | `Comparator` |
|---|---|---|
| 어디에 | 클래스 안에 `implements Comparable<T>` | 클래스 밖에서 따로 |
| 메서드 | `compareTo(T o)` | `compare(T a, T b)` |
| 뜻 | 이 객체의 **기본 정렬 기준** | 상황에 따른 **다른 정렬 기준** |
| Python으로 치면 | `__lt__` | `sorted(key=...)` |

반환값: **음수면 앞, 0이면 같음, 양수면 뒤**.

```java
import java.util.*;

class Student implements Comparable<Student> {
    String name;
    int score;

    Student(String name, int score) { this.name = name; this.score = score; }

    @Override
    public int compareTo(Student o) {
        return Integer.compare(this.score, o.score);   // 점수 오름차순이 기본
    }

    @Override
    public String toString() { return name + "(" + score + ")"; }
}

public class SortTest {
    public static void main(String[] args) {
        List<Student> list = new ArrayList<>(List.of(
            new Student("규", 90), new Student("철수", 80), new Student("영희", 90)));

        Collections.sort(list);                       // compareTo 기준
        System.out.println(list);                     // [철수(80), 규(90), 영희(90)]

        // 점수 내림차순, 같으면 이름 오름차순 (Python: key=lambda s: (-s.score, s.name))
        list.sort(Comparator.comparingInt((Student s) -> s.score).reversed()
                            .thenComparing(s -> s.name));
        System.out.println(list);

        int[][] pts = {{3, 1}, {1, 2}, {1, 1}};
        Arrays.sort(pts, (a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);  // 백준 단골
        System.out.println(Arrays.deepToString(pts));  // [[1, 1], [1, 2], [3, 1]]
    }
}
```

## 3. 람다 (Lambda)

**메서드 하나짜리 인터페이스(함수형 인터페이스)를 짧게 구현하는 문법.**

| Python | Java |
|---|---|
| `lambda x: x * 2` | `x -> x * 2` |
| `lambda a, b: a + b` | `(a, b) -> a + b` |
| `lambda: print("hi")` | `() -> System.out.println("hi")` |
| 여러 줄 불가 | `x -> { ...; return y; }` 가능 |

람다는 **아무 데나 쓸 수 없고, 함수형 인터페이스 자리에만** 들어간다.

### 표준 함수형 인터페이스

| 인터페이스 | 모양 | 메서드 | 예 |
|---|---|---|---|
| `Function<T, R>` | T → R | `apply` | `s -> s.length()` |
| `Predicate<T>` | T → boolean | `test` | `n -> n > 0` |
| `Consumer<T>` | T → 없음 | `accept` | `s -> System.out.println(s)` |
| `Supplier<T>` | 없음 → T | `get` | `() -> new ArrayList<>()` |
| `BiFunction<T, U, R>` | (T, U) → R | `apply` | `(a, b) -> a + b` |

```java
import java.util.function.*;

public class LambdaTest {
    public static void main(String[] args) {
        Function<String, Integer> len = s -> s.length();
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Consumer<String> printer = s -> System.out.println(">> " + s);
        Supplier<Double> random = () -> Math.random();

        System.out.println(len.apply("spring"));   // 6
        System.out.println(isEven.test(4));        // true
        printer.accept("hello");
        System.out.println(random.get() < 1);      // true

        // 메서드 참조: 람다가 메서드 하나만 부를 때 더 짧게
        Function<String, Integer> len2 = String::length;     // s -> s.length()
        Consumer<String> printer2 = System.out::println;     // s -> System.out.println(s)
        printer2.accept("메서드 참조");
        System.out.println(len2.apply("abc"));

        int base = 10;
        Function<Integer, Integer> addBase = x -> x + base;  // 바깥 변수 사용 가능
        // base = 20;   // 에러: 람다에서 쓴 지역 변수는 바꿀 수 없음 (effectively final)
        System.out.println(addBase.apply(5));      // 15
    }
}
```

## 4. Stream API

**컬렉션을 "어떻게 반복할지" 대신 "무엇을 할지"로 처리.** Python의 리스트 컴프리헨션·`map`·`filter`에 해당한다.

```
소스        →   중간 연산 (여러 개)        →   최종 연산 (하나)
list.stream()   filter / map / sorted ...     collect / count / sum / forEach
```
**최종 연산을 호출해야 실제로 실행된다**(지연 실행). 스트림은 **한 번 쓰면 끝**이다.

```java
import java.util.*;
import java.util.stream.*;

public class StreamTest {
    record User(String name, int age, String city) { }   // 데이터 묶음 (D6에서 배움)

    public static void main(String[] args) {
        List<Integer> nums = List.of(5, 2, 8, 1, 9, 4);

        // [x * 10 for x in nums if x % 2 == 0]
        List<Integer> r = nums.stream()
                .filter(x -> x % 2 == 0)
                .map(x -> x * 10)
                .collect(Collectors.toList());     // Java 16+: .toList()
        System.out.println(r);                     // [20, 80, 40]

        System.out.println(nums.stream().mapToInt(Integer::intValue).sum());  // 29
        System.out.println(nums.stream().sorted().toList());                  // [1, 2, 4, 5, 8, 9]
        System.out.println(nums.stream().max(Integer::compare).get());        // 9
        System.out.println(nums.stream().anyMatch(x -> x > 8));               // true

        List<User> users = List.of(
            new User("규", 25, "구미"), new User("철수", 30, "서울"), new User("영희", 22, "구미"));

        List<String> names = users.stream()
                .filter(u -> u.city().equals("구미"))
                .map(User::name)
                .toList();
        System.out.println(names);                 // [규, 영희]

        Map<String, Long> byCity = users.stream()
                .collect(Collectors.groupingBy(User::city, Collectors.counting()));
        System.out.println(byCity);                // {서울=1, 구미=2} (순서는 다를 수 있음)

        String joined = users.stream().map(User::name).collect(Collectors.joining(", "));
        System.out.println(joined);                // 규, 철수, 영희

        IntStream.range(0, 3).forEach(i -> System.out.print(i + " "));   // range(0, 3)
        System.out.println();
    }
}
```

**Spring에서 보게 될 모습**: 엔티티 리스트를 DTO 리스트로 바꿀 때

```java
List<UserResponse> result = users.stream().map(UserResponse::from).toList();
```

**백준에서는**: Stream은 for문보다 느리고 디버깅이 어렵다. 코테는 for문, 실무 코드는 Stream.

## 5. Optional

**"값이 있을 수도, 없을 수도 있다"를 타입으로 표현.** `null`을 그대로 돌려줘서 생기는 `NullPointerException`을 줄인다.

```java
import java.util.*;

public class OptionalTest {
    static Map<Long, String> db = Map.of(1L, "규", 2L, "철수");

    static Optional<String> findById(Long id) {
        return Optional.ofNullable(db.get(id));   // 없으면 빈 Optional
    }

    public static void main(String[] args) {
        Optional<String> a = findById(1L);
        Optional<String> b = findById(99L);

        System.out.println(a.isPresent());                 // true
        System.out.println(b.isEmpty());                   // true
        System.out.println(b.orElse("없음"));              // 없음
        a.ifPresent(name -> System.out.println("찾음: " + name));
        System.out.println(a.map(String::length).orElse(0));  // 1

        try {
            String name = b.orElseThrow(() -> new IllegalArgumentException("회원 없음"));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());            // 회원 없음
        }
        // b.get();   // 값이 없으면 NoSuchElementException. get()은 피하자
    }
}
```

**Spring에서 거의 매일 쓰는 모양**:

```java
User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다. id=" + id));
```

Optional은 **반환 타입에만** 쓴다. 필드나 매개변수에는 쓰지 않는 게 관례다.

---

## 자주 하는 실수

| 실수 | 증상 | 고치기 |
|---|---|---|
| `List<Integer>`에서 `remove(3)` | 값 3이 아니라 인덱스 3이 지워짐 | `remove(Integer.valueOf(3))` |
| `Integer`끼리 `==` | 127까지는 true, 128부터 false | `equals` |
| `List.of(...)`에 `add` | `UnsupportedOperationException` | `new ArrayList<>(List.of(...))` |
| for-each 도중 `list.remove` | `ConcurrentModificationException` | `list.removeIf(x -> ...)` |
| `Optional.get()` 남발 | 값 없으면 예외 | `orElse`, `orElseThrow` |
| 스트림 두 번 사용 | `IllegalStateException` | 스트림을 다시 만들기 |
| 비교 람다에 `a - b`로 큰 수 | 오버플로로 정렬이 틀림 | `Integer.compare(a, b)` |

---

## 면접 질문

1. **ArrayList와 LinkedList의 차이는?**
   ArrayList는 배열 기반이라 인덱스 조회가 O(1)이고 중간 삽입·삭제가 느리다. LinkedList는 노드 연결 구조라 조회가 O(n)이다. 대부분 ArrayList를 쓴다.
2. **HashMap은 어떻게 동작하나요?**
   키의 hashCode로 저장할 버킷을 정하고, 같은 버킷에 여러 개가 있으면 equals로 비교해 찾는다. 평균 O(1)로 조회한다. (충돌이 많으면 Java 8부터 버킷 안을 트리로 바꾼다)
3. **Comparable과 Comparator의 차이는?**
   Comparable은 클래스 안에 정의하는 기본 정렬 기준(compareTo), Comparator는 밖에서 상황마다 따로 주는 정렬 기준(compare).
4. **람다와 함수형 인터페이스란?**
   추상 메서드가 하나인 인터페이스를 함수형 인터페이스라 하고, 람다는 그 하나의 메서드를 익명으로 짧게 구현하는 문법이다.
5. **Stream의 특징은?**
   원본을 바꾸지 않고, 중간 연산은 최종 연산이 호출될 때 한꺼번에 실행되며(지연 실행), 한 번 사용하면 재사용할 수 없다.
6. **Optional을 왜 쓰나요?**
   값이 없을 수 있다는 걸 반환 타입으로 드러내서, 호출하는 쪽이 null 처리를 잊지 않게 하기 위해.
7. **int와 Integer의 차이는?**
   int는 기본형으로 값 자체를 저장하고 null이 될 수 없다. Integer는 객체라 컬렉션에 넣을 수 있고 null을 가질 수 있다.

---

## 오늘의 실습

- [ ] 강의 코드 따라 치기 (`src/day05/`)
- [ ] 단어 빈도수 세기: 문장을 `split(" ")`로 나눠 `HashMap`으로 개수 세고, 많은 순으로 출력
- [ ] `Student` 리스트를 점수 내림차순·이름 오름차순으로 정렬
- [ ] 위 코드를 Stream으로 다시 쓰기 (`filter`, `map`, `sorted`, `groupingBy`)
- [ ] D4의 `MemoryRepository`에 `Optional<T> findFirst(Predicate<T> cond)` 추가
- [ ] 연습문제 3개: [problems/day05.md](problems/day05.md) (`src/practice/`에 풀기)
- [ ] 여유 있으면 SWEA·프로그래머스에서 1~2문제 더
- [ ] 커밋: `day05: 컬렉션·람다·Stream·Optional / 빈도수·정렬 실습`
