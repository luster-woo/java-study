# Stream API

> 교재: 15기_자바_live_08_collection_2_0126_오전 (전공)

> [← 표준함수형 인터페이스](06_표준함수형_인터페이스.md) · [day05 목록](../README.md) · [Wrapper 클래스 →](08_Wrapper_클래스.md)

### Stream이란

- JDK 8에서 추가된 `java.util.stream` 패키지
- 배열·Collection의 요소를 **하나씩 참조해서 처리**하는 목적
- 람다와 **내부 반복자**로 컬렉션 다루는 코드를 간결하게

### Stream API의 역할 및 특징

- 예: 글자 수가 3자를 넘는 요소들의 글자 수 평균 → 결과는 같지만 스타일이 다름

```java
import java.util.*;

public class StreamStyle {
    public static void main(String[] args) {
        List<String> heroes = Arrays.asList("아이언맨", "스파이더맨", "헐크", "캡틴아메리카", "토르");

        // 기존 방식: 외부 반복
        double sum = 0;
        int cnt = 0;
        for (String hero : heroes) {
            int length = hero.length();   // 글자 수로 변환
            if (length > 3) {             // 필터링
                sum += length;            // 합계·개수
                cnt++;
            }
        }
        System.out.println(sum / cnt);    // 5.0

        // Stream 방식
        double avg = heroes.stream()
                .mapToInt(String::length) // 글자 수로 변환
                .filter(len -> len > 3)   // 필터링
                .average()                // 평균
                .getAsDouble();           // 결과
        System.out.println(avg);          // 5.0
    }
}
```

- 컬렉션·배열 등 데이터 소스에 **공통된 접근 방식** 제공

| 소스 | 기존 정렬 방식 | Stream |
|---|---|---|
| `List<String>` | `Collections.sort(List)` | `sorted()` 하나로 통일 |
| `String[]` | `Arrays.sort(String[])` | `sorted()` |
| `Set<String>` | `TreeSet<String>`에 담기 | `sorted()` |

- **손쉬운 병렬 처리**: `stream()` → `parallelStream()`만 바꾸면 됨 (내부적으로 ForkJoinPool 활용)

```java
set.stream().forEach(System.out::println);          // 순차 처리
set.parallelStream().forEach(System.out::println);  // 병렬 처리 (출력 순서 보장 안 됨)
```

### 맵/리듀스 모델

- 대량의 컬렉션 처리를 위한 방식
  - **맵**: 데이터를 작은 단위(chunk)로 나누어(splitting) 지정 함수를 적용(mapping)
  - **리듀스**: 결과를 모아 최종 결과 생성
- **중간 처리**와 **최종 처리**를 조합
  - 중간 처리: 매핑·필터링·정렬 등 **가공** — 여러 개 가능
  - 최종 처리: 반복·카운팅·평균·총합 등 **집계** — 1개

```text
Stream Source        Stream 생성   중간 처리    중간 처리   중간 처리    최종 처리
(컬렉션, 배열, 파일) ──────────▶  필터링  ──▶ 정렬  ──▶ 매핑  ──▶  평균은?
                                  └──── 여러 개 가능 ────┘       └ 1개 ┘
```

- 중간 처리는 **새로운 스트림을 리턴** → chaining 패턴, 기존 스트림 내용은 수정하지 않음
- 최종 처리는 원하는 값(void 포함) 반환, **최종 처리가 끝난 스트림은 재사용 불가** (`IllegalStateException`)

### Stream의 종류

- `java.util.stream` 패키지
  - `Stream`: 객체 요소 처리
  - `IntStream`, `LongStream`, `DoubleStream`: 각각 int, long, double 처리
- 다루는 데이터 타입이 달라 메서드는 조금씩 다르지만 목적은 같음

```text
BaseStream
  ├ Stream
  ├ IntStream
  ├ LongStream
  └ DoubleStream
```

### Stream의 획득

- Collection, 배열, File, Random, Stream 클래스의 static·default 메서드로 생성

| 소스 | 메서드 |
|---|---|
| 컬렉션 | `default Stream<E> stream()`, `default Stream<E> parallelStream()` |
| 배열 | `Arrays.stream(T[] array)`, `Stream.of(T... values)` |
| 기본형 배열 | `Arrays.stream(int[])` / `IntStream.of(int...)` (long, double도 같은 형태) |

```java
String[] heroes = {"아이언맨", "스파이더맨", "헐크", "토르"};
Stream<String> fromArray = Arrays.stream(heroes);
List<String> heroList = Arrays.asList(heroes);
Stream<String> fromList = heroList.stream();
IntStream ints = IntStream.of(1, 2, 3);
```

### 단계별 주요 처리 메서드

| 중간 처리 | 메서드 |
|---|---|
| 필터링 | `distinct()`, `filter()` |
| 자르기 | `skip()`, `limit()` |
| 정렬 | `sorted()` |
| 매핑(변환) | `map()`, `mapToInt()`, `mapToLong()`, `mapToDouble()`, `flatMap()`, `flatMapToInt()` 등, `asDoubleStream()`, `asLongStream()` |
| 조회 | `peek()` |

| 최종 처리 | 메서드 |
|---|---|
| 매칭 | `allMatch()`, `anyMatch()`, `noneMatch()` |
| 수집 | `collect()` |
| 루핑 | `forEach()` |
| 집계 | `count()`, `findFirst()`, `max()`, `min()`, `average()`, `sum()`, `reduce()` |

- **중간 처리는 최종 처리가 실행될 때까지 지연(LAZY)** 됨

```java
List<String> list = Arrays.asList("ab", "cde");

list.stream().mapToInt(data -> {
    System.out.println("문자열: " + data);
    return data.length();
});                     // 최종 처리가 없으므로 출력 없음

int total = list.stream().mapToInt(data -> {
    System.out.println("문자열: " + data);
    return data.length();
}).sum();               // 최종 처리 시 중간 처리 일괄 진행
System.out.println(total);
/* 출력
문자열: ab
문자열: cde
5
*/
```

### 중간 처리: 필터링 · 자르기

| 메서드 | 설명 |
|---|---|
| `Stream<T> distinct()` | 중복을 제거한 스트림 |
| `Stream<T> filter(Predicate<? super T>)` | `test()`가 true인 요소만으로 구성된 스트림 |
| `Stream<T> limit(long maxSize)` | maxSize 개까지만 |
| `Stream<T> skip(long n)` | n개를 버리고 이후 요소로 |

### 최종 처리: 반복 forEach · 집계 count, sum

- `void forEach(Consumer<? super T> action)`: 요소를 순차적으로 소비
- `long count()`: 요소 개수 / `p sum()`: 기본형 스트림의 총합

### 중간 처리: 정렬

| 메서드 | 설명 |
|---|---|
| `Stream<T> sorted()` | 요소가 Comparable일 때 그 `compareTo()`로 정렬 |
| `Stream<T> sorted(Comparator<? super T>)` | Comparator로 정렬 |

### 중간 처리: 매핑(변환)

- 스트림의 요소를 다른 요소로 대체, `Function<T, R>` 계열을 파라미터로 받음

| 메서드 | 설명 |
|---|---|
| `<R> Stream<R> map(Function<? super T, ? extends R>)` | 요소 T와 **1:1** 대응하는 R의 스트림 |
| `PStream mapToP(ToPFunction<? super T>)` | map에서 R이 기본형 p로 한정, 별도 형변환 불필요 |
| `<R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>>)` | 요소 T가 **1:n** 으로 해체된 R의 스트림 |
| `PStream flatMapToP(Function<? super T, ? extends PStream>)` | flatMap에서 R이 p로 한정 |

```text
map     : "Hello", "Stream"  →  [H,e,l,l,o], [S,t,r,e,a,m]   (요소 2개 → 2개)
flatMap : "Hello", "Stream"  →  H, e, l, l, o, S, t, r, e, a, m   (요소 2개 → 11개)
```

### 최종 처리: 매칭

| 메서드 | 설명 |
|---|---|
| `boolean allMatch(Predicate)` | 모든 요소가 조건을 만족하면 true |
| `boolean anyMatch(Predicate)` | 하나라도 만족하면 true |
| `boolean noneMatch(Predicate)` | 하나도 만족하지 않으면 true |

### 최종 처리: 집계(통계)

| 메서드 | 설명 |
|---|---|
| `long count()` | 요소 개수 |
| `Optional<T> min(Comparator)` / `OptionalP min()` | 최솟값 (객체 스트림 / 기본형 스트림) |
| `Optional<T> max(Comparator)` / `OptionalP max()` | 최댓값 |
| `OptionalDouble average()` | 기본형 스트림의 평균 |
| `p sum()` | 기본형 스트림의 총합 |

### 최종 처리: 사용자 정의 집계 reduce()

- `sum()`, `average()` 등과 달리 **사용자가 작성한 연산**으로 집계 (sum 등은 reduce의 특별한 형태)
- `BinaryOperator`로 처음 두 요소의 연산 결과를 다음 요소와 다시 연산 → 반복 후 결과 리턴

| 메서드 | 설명 |
|---|---|
| `Optional<T> reduce(BinaryOperator<T>)` | 결과를 Optional로 (요소가 없을 수 있으므로) |
| `T reduce(T identity, BinaryOperator<T>)` | 초기값 identity부터 연산 → Optional이 아닌 T 리턴 |
| `<U> U reduce(U identity, BiFunction<U, ? super T, U>, BinaryOperator<U> combiner)` | 요소 타입(T)과 결과 타입(U)이 다를 때. combiner는 병렬 스트림에서 부분 결과를 합침 |

### 최종 처리: 조사 findFirst() · findAny()

| 메서드 | 설명 |
|---|---|
| `Optional<T> findFirst()` | 맨 처음 요소를 담은 Optional (없으면 빈 Optional) |
| `Optional<T> findAny()` | 아무 요소나 담은 Optional (없으면 빈 Optional) |

### 최종 처리: 결과 모으기 collect()

- 필터링·매핑된 요소들을 수집 → 필요한 요소만으로 컬렉션 구성

| 메서드 | 설명 |
|---|---|
| `<R, A> R collect(Collector<? super T, A, R>)` | Collector로 요소를 새 컬렉션 R에 담아 리턴 |
| `<R> R collect(Supplier<R>, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner)` | supplier가 만든 컨테이너 R에 accumulator로 값을 저장, combiner는 병렬 스트림에서 스레드별 컨테이너를 합칠 때만 호출 |

- `Collector`는 직접 구현하기보다 **`Collectors`의 static 메서드**로 얻어 씀

| Collectors 메서드 | 설명 |
|---|---|
| `toList()` | 요소를 `List<T>`에 저장 |
| `toSet()` | 요소를 `Set<T>`에 저장 |
| `toMap(keyMapper, valueMapper [, mergeFunction])` | 요소를 K, U로 변환해 Map에 저장. key가 중복되면 mergeFunction이 value 처리 방법 결정 (중복이 없으면 생략 가능) |

### 중간·최종 처리 종합 예제

```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class StreamMiddleTest {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("stream", "java", "lambda", "java", "list", "map", "stream", "set");

        // 필터링·자르기
        System.out.println("중복 제거 결과: " + words.stream().distinct().count()); // 중복 제거 결과: 6
        words.stream().skip(2).limit(3).forEach(System.out::println);              // lambda / java / list

        // 정렬: 역순으로 3개
        words.stream().distinct().sorted((a, b) -> a.compareTo(b) * -1)
             .limit(3)
             .forEach(System.out::println);                                       // stream / set / map

        // forEach
        String[] strs = {"Hi", "C", "Java", "Zoo", "World"};
        Arrays.stream(strs).sorted()
              .forEach(data -> System.out.println(data + " : 길이: " + data.length()));
        /* 출력
        C : 길이: 1
        Hi : 길이: 2
        Java : 길이: 4
        World : 길이: 5
        Zoo : 길이: 3
        */

        // map(1:1) · flatMap(1:n)
        List<String> list = Arrays.asList("Hello", "Stream");
        list.stream().map(item -> item.toCharArray())
            .forEach(data -> System.out.println(data.length));                   // 5 / 6
        IntStream.range(0, 5).mapToObj(num -> "제곱: " + num * num)
                 .forEach(System.out::println);                                   // 제곱: 0 / 1 / 4 / 9 / 16
        list.stream().flatMap(item -> Arrays.stream(item.split("")))
            .forEach(System.out::print);                                          // HelloStream
        System.out.println();

        // 매칭
        boolean anyMatch = words.stream().distinct().anyMatch(w -> w.length() >= 5);
        boolean allMatch = words.stream().allMatch(w -> w.length() >= 5);
        boolean noneMatch = words.stream().noneMatch(String::isEmpty);
        System.out.println(anyMatch + " " + allMatch + " " + noneMatch);         // true false true

        // 집계
        int[] nums = {3, 1, 4, 1, 5};
        System.out.println(Arrays.stream(nums).count());                 // 5
        System.out.println(Arrays.stream(nums).sum());                   // 14
        System.out.println(Arrays.stream(nums).max().getAsInt());        // 5
        System.out.println(Arrays.stream(nums).average().getAsDouble()); // 2.8

        // reduce
        int sum = IntStream.of(1, 2, 3, 4, 5).reduce(0, (acc, num) -> acc + num);
        System.out.println(sum);                                         // 15
        Optional<Integer> product = Stream.of(1, 2, 3, 4).reduce((a, b) -> a * b);
        System.out.println(product.get());                               // 24

        // findFirst · findAny
        System.out.println(Arrays.stream(strs).findFirst().orElse("none")); // Hi
        System.out.println(Arrays.stream(strs).filter(s -> s.length() > 10)
                                 .findAny().orElse("none"));                // none

        // collect(supplier, accumulator, combiner)
        Supplier<List<String>> s = ArrayList::new;                          // 최종 반환할 컬렉션
        BiConsumer<List<String>, String> a = (l, str) -> l.add(str);        // 하나씩 누적
        BiConsumer<List<String>, List<String>> c = (l1, l2) -> l1.addAll(l2); // 병렬 시 병합
        List<String> picked = words.stream().filter(str -> str.length() >= 5)
                .distinct().sorted().limit(5).collect(s, a, c);
        System.out.println(picked);                                      // [lambda, stream]

        // collect(Collectors)
        List<String> toList = words.stream().filter(w -> w.length() == 4).collect(Collectors.toList());
        System.out.println(toList);                                      // [java, java, list]
        Map<String, Integer> toMap = words.stream()
                .collect(Collectors.toMap(w -> w, String::length, (v1, v2) -> v1)); // key 중복 시 기존 값 유지
        System.out.println(new TreeMap<>(toMap));                        // {java=4, lambda=6, list=4, map=3, set=3, stream=6}
    }
}
```

> 참고: 교재 `sorted()` 설명의 "요소의 compare() 메서드"는 Comparable의 `compareTo()`이다. `findFirst()`·`findAny()`는 요소가 없을 때 "null을 갖는 Optional"이 아니라 빈 Optional(`Optional.empty()`)을 돌려준다.

---

## 참고

- `Collectors.toMap`에서 mergeFunction 없이 key가 중복되면 `IllegalStateException`(Duplicate key) 발생 — 실행 확인함
- `findAny()`는 순차 스트림에서는 보통 첫 요소를 주지만, 병렬 스트림에서는 어떤 요소가 나올지 보장되지 않음
