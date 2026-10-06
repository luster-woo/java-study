# Lambda 표현식

> 교재: 15기_자바_live_08_collection_2_0126_오전 (전공)

> [← 정렬](04_정렬.md) · [day05 목록](../README.md) · [표준함수형 인터페이스 →](06_표준함수형_인터페이스.md)

### Lambda 식이란

- 함수적 프로그래밍 형태로 **재사용 가능한 코드 블록**
- 기존 **익명 내부 클래스** 방식을 간결하게 처리
- 예: `Arrays.sort(T[] a, Comparator<? super T> c)` → Comparator 타입 객체가 필요
  - 익명 내부 클래스: 클래스 정의 + 객체 생성 + 메서드 재정의를 모두 작성
  - Lambda: class 형태 없이 **code block만 전달**

```java
String[] langs = {"Java", "C", "Python", "Go"};

// 익명 내부 클래스
Arrays.sort(langs, new Comparator<String>() {
    @Override
    public int compare(String o1, String o2) {
        return o1.compareTo(o2);
    }
});
System.out.println(Arrays.toString(langs)); // [C, Go, Java, Python]

// Lambda 식 (내림차순)
Arrays.sort(langs, (o1, o2) -> o2.compareTo(o1));
System.out.println(Arrays.toString(langs)); // [Python, Java, Go, C]
```

### 타겟 타입과 @FunctionalInterface

- **타겟 타입(함수형 인터페이스)**: Lambda 식이 할당되는 인터페이스
  - **abstract 메서드가 반드시 하나**만 있어야 함 (default, static 메서드는 상관없음)
  - 그 하나의 메서드 구현부가 Lambda 식으로 대체됨
- **@FunctionalInterface**
  - abstract 메서드가 하나만 있는지 **컴파일러가 체크**, 아니면 오류 → 안정적인 프로그래밍을 위한 옵션
- abstract 메서드가 2개 이상이면 여전히 익명 내부 클래스 사용

```java
@FunctionalInterface
public interface Comparator<T> {
    int compare(T o1, T o2);          // 유일한 abstract 메서드
    boolean equals(Object obj);       // Object의 public 메서드 → 개수에 안 셈
    default Comparator<T> reversed() { // default → 상관없음
        return Collections.reverseOrder(this);
    }
}
```

### 구현체 작성법

- 기본형: `(타입 변수명[, ...]) -> { 실행문; }`
  - 선언된 변수(매개변수)로 실행문을 실행하라는 뜻

| 단계 | 형태 |
|---|---|
| 기본 | `(String str) -> { System.out.println(str); }` |
| 매개변수 타입 생략 (대입되는 값으로 자동 인식, 보통 생략) | `(str) -> { System.out.println(str); }` |
| 매개변수·실행문이 하나면 괄호·중괄호·`;` 생략 | `str -> System.out.println(str)` |
| 리턴이 필요하면 `return` | `num -> { System.out.println(num); return num * 2; }` |
| 리턴문 하나뿐이면 중괄호·`return` 생략 | `num -> num * 2` |

```java
public class LambdaForm {
    @FunctionalInterface
    interface Printer { void print(String str); }

    @FunctionalInterface
    interface Doubler { int apply(int num); }

    public static void main(String[] args) {
        Printer p = str -> System.out.println(str);
        p.print("Hello");               // Hello

        Doubler d1 = num -> {
            System.out.println(num);
            return num * 2;
        };
        Doubler d2 = num -> num * 2;
        System.out.println(d1.apply(3)); // 3 출력 후 6
        System.out.println(d2.apply(5)); // 10
    }
}
```

### 타입과 메서드의 추정

- 이름이 없는데 어떻게 알까? `Arrays.sort(strs, (o1, o2) -> o1.compareTo(o2) * -1)`

```text
1. sort의 두 번째 파라미터 자리에는 Comparator만 올 수 있음 → Comparator 타입으로 인정
2. Comparator의 abstract 메서드는 compare 하나뿐
3. compare는 T 타입 2개를 받아 int를 반환 → (o1, o2)는 T, 결과는 int
```

### 메서드 참조

- Lambda 실행문이 **다른 메서드 하나만 호출**하면 `::` 연산자로 기존 메서드를 참조
- 형식: `<소유자>::<메서드>` — 파라미터는 적지 않음

| 종류 | 형식 | 파라미터 전달 |
|---|---|---|
| 파라미터의 인스턴스 메서드 | `클래스::인스턴스메서드` | 첫 번째 파라미터가 메서드의 소유자, 나머지는 메서드 파라미터로 순서대로 |
| 정적 메서드 | `클래스::정적메서드` | 모든 파라미터가 메서드 파라미터로 순서대로 |
| 특정 객체의 인스턴스 메서드 | `객체::인스턴스메서드` | 파라미터가 메서드에 그대로 |
| 생성자 | `클래스명::new` | 파라미터 개수·타입으로 호출할 생성자 결정 |

- 너무 줄이려고만 할 필요는 없음 — Lambda와 메서드 참조는 똑같이 동작

```java
import java.util.*;
import java.util.function.*;

public class MethodRefTest {
    public static void main(String[] args) {
        Integer[] nums = {1, 7, 3, 4, 5};
        Arrays.sort(nums, (n1, n2) -> n1.compareTo(n2));       // n1의 인스턴스 메서드
        Arrays.sort(nums, Integer::compareTo);
        Arrays.sort(nums, (n1, n2) -> Integer.compare(n1, n2)); // Integer의 static 메서드
        Arrays.sort(nums, Integer::compare);
        System.out.println(Arrays.toString(nums));  // [1, 3, 4, 5, 7]

        List<String> names = Arrays.asList("홍길동", "임꺽정");
        names.forEach(item -> System.out.println(item));
        names.forEach(System.out::println);         // 특정 객체(System.out)의 메서드
        /* 출력 (두 번 반복)
        홍길동
        임꺽정
        */

        // 생성자 참조
        Supplier<StringBuilder> s1 = () -> new StringBuilder();
        Supplier<StringBuilder> s2 = StringBuilder::new;            // 파라미터 0개 생성자
        Function<String, StringBuilder> f1 = init -> new StringBuilder(init);
        Function<String, StringBuilder> f2 = StringBuilder::new;   // String 1개 생성자
        System.out.println(s2.get().append("empty").length());     // 5
        System.out.println(f2.apply("hong").reverse());            // gnoh
    }
}
```

> 참고: 교재는 "파라미터의 인스턴스 메서드 참조"의 형식을 `객체::인스턴스 메서드`로 적었으나 예제(`Integer::compareTo`)처럼 앞에는 **클래스 이름**이 온다. 또 교재의 `names.forEach(names::add)`는 순회 중인 리스트 자신에 추가하므로 요소가 있으면 `ConcurrentModificationException`이 난다(다른 리스트에 담을 때만 안전).
