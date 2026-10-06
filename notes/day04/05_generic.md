# generic

> 교재: 15기_자바_live_06_Generic_0123 (전공)

> [← default_static_private](04_default_static_private.md) · [day04 목록](../README.md) · [enum →](06_enum.md)

## Generics

### 장바구니 정리하기

| 박스 | 담을 때 | 뺄 때 |
| --- | --- | --- |
| 뭐든지 담을 수 있는 박스 | 편함 | 번거로움 — 장난감인지 식료품인지 확인해야 함 |
| 담을 것을 한정한 박스(장난감 전용, 식료품 전용) | 번거로움 — 정해진 것만 담아야 함 | 편함 — 무엇이 나올지 앎 |

### Generics의 의미

- Generics: 포괄적인, 통칭의
- 다양한 타입의 객체를 다루는 메서드·컬렉션 클래스에서 **컴파일 시에 타입 체크**
  - 사용할 타입을 미리 명시해서 형 변환을 하지 않아도 되게 함
  - 객체 타입의 **안전성 향상**, **형 변환의 번거로움 감소**
- 이전에는 타입을 런타임에 체크했음 → 오류 검출은 빠를수록(컴파일 타임) 좋음

### Generic Type

- 클래스 또는 인터페이스 선언 시 `< >`에 **타입 파라미터** 표시

```java
public interface Interface_Name<T> {}
public class Class_Name<T> {}
```

| 용어 | 예 |
| --- | --- |
| 무인자 자료형(Raw Type) | `Class_Name` |
| 형인자(Type Parameter) | `T` |
| 형인자 자료형(Parameterized Type) | `Class_Name<T>` |

- 형인자(Type Parameter)
  - 임의의 참조형 타입을 말함
  - 성격에 따라 이름을 정함

| 이름 | 의미 |
| --- | --- |
| `T` | reference Type |
| `E` | Element |
| `K` | Key |
| `V` | Value |

```java
public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable { ... }
public class HashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>, Cloneable, Serializable { ... }
```

- 객체 생성
  - 변수 쪽과 생성 쪽의 타입 인자는 **반드시 같아야 함**(상속 관계 등 X)

```java
ClassName<Number> generic = new ClassName<Number>();
ClassName<Number> generic2 = new ClassName<>();   // 생성 쪽 생략 가능(다이아몬드)
ClassName rawType = new ClassName();              // raw type
```

### 클래스에 선언된 Generic

| `NormalBox` | `GenericBox<T>` |
| --- | --- |
| `Object`를 속성으로 사용 → 어떤 객체든 수용 | "어떤 타입" `T`를 속성으로 사용 → 타입 선언 시 결정 |
| 꺼낼 때 `instanceof` 확인·형 변환 필요 (**런타임** 확인) | 넣을 때 컴파일러가 확인, 꺼낼 때 형 변환 없음 (**컴파일 타임** 확인) |

- 컴파일 타임에 타입 파라미터가 대입된 타입으로 대체됨

```java
public class BoxTest {
    public static void main(String[] args) {
        // NormalBox: 무엇이든 담김 → 꺼낼 때 런타임 확인 필요
        NormalBox nBox = new NormalBox();
        nBox.setSome(new Grocery());          // "Toy만 넣어 주세요"라는 약속은 주석뿐
        Object some = nBox.getSome();
        if (some instanceof Toy) {
            Toy toy = (Toy) some;
            System.out.println("장난감");
        } else {
            System.out.println("알 수 없음");  // 알 수 없음
        }

        // GenericBox<Toy>: 담을 때 컴파일러가 확인 → 꺼낼 때 형 변환 없음
        GenericBox<Toy> gBox1 = new GenericBox<>();
        gBox1.setSome(new Toy());
        // gBox1.setSome(new Grocery());      // 컴파일 에러
        Toy toy = gBox1.getSome();
        System.out.println(toy);              // Toy

        GenericBox<Grocery> gBox2 = new GenericBox<>();
        gBox2.setSome(new Grocery());
        Grocery grocery = gBox2.getSome();
        System.out.println(grocery);          // Grocery
    }
}

class Toy { public String toString() { return "Toy"; } }
class Grocery { public String toString() { return "Grocery"; } }

class NormalBox {
    private Object some;                      // 어떤 객체든 수용
    public Object getSome() { return some; }
    public void setSome(Object some) { this.some = some; }
}

class GenericBox<T> {
    private T some;                           // 타입은 선언할 때 결정
    public GenericBox() {}
    public GenericBox(T some) { this.some = some; }
    public T getSome() { return some; }
    public void setSome(T some) { this.some = some; }
}
```

### 주의사항 1

- raw type을 쓰면 경고 → `@SuppressWarnings({"rawtypes", "unused"})`로 끌 수 있지만 남발하지 말 것

```java
@SuppressWarnings({"rawtypes", "unused"})
GenericBox box = new GenericBox();
```

- 헷갈리는 사용법
  - `GenericBox<Person>`은 Person 타입을 담는 박스 → `SpiderMan`(Person 자손) 객체도 담을 수 있음
  - 하지만 **Person을 담는 박스와 SpiderMan을 담는 박스는 상속 관계가 없음**

```java
GenericBox<Person> pbox = new GenericBox<>();
pbox.setSome(new Person());
pbox.setSome(new SpiderMan());          // OK
// pbox = new GenericBox<SpiderMan>();  // 컴파일 에러: GenericBox<SpiderMan> cannot be converted to GenericBox<Person>
```

### 주의사항 2

- 타입 파라미터는 **인스턴스 레벨**에서 결정됨 → 클래스 레벨의 static 멤버에서는 사용 불가

```java
class GenericBox<T> {
    // static T item;   // 컴파일 에러: non-static type variable T cannot be referenced from a static context
}
```

- Generic은 컴파일 타임에 지정한 타입으로 존재 → 런타임에는 타입 정보 소거(**Type Erasure**: 단순 `Object`로 관리)
  - 컴파일러가 이미 타입을 체크했으므로 런타임에는 자유롭게 사용
  - 런타임에 동작하는 `new`, `instanceof`에 타입 파라미터 사용 불가

```java
// T t = new T();                                    // 컴파일 에러

GenericBox<SpiderMan> obj = new GenericBox<>();
// if (obj instanceof GenericBox<String>) {}         // 컴파일 에러
if (obj instanceof GenericBox) {                     // 에러는 아니지만 raw type → 타입에 안전하지 않음
    // ((GenericBox) obj).setSome("Hello");          // SpiderMan 박스에 String이 들어갈 수 있음
}
if (obj instanceof GenericBox<?>) {                  // 이것이 최선: 타입이 훼손될 일은 없음
    System.out.println("이것이 최선: 타입이 훼손될 일은 없다.");
}
```

- `?`는 **wild card**

### 주의사항 3

- Generic 타입의 **배열 생성 불가**
  - 배열은 런타임에 객체 정보를 유지하고 같은 타입의 객체만 처리함
  - `GenericBox<T>[]`가 된다고 가정하면 런타임에는 `GenericBox[]`로 바뀜 → T가 Integer였어도 런타임에는 무엇이든 들어가 버림
  - 대신 `List<GenericBox<T>>` 사용
- raw 타입 객체의 형 변환 주의
  - 실제 메모리에는 모든 객체를 담을 수 있으므로 타입이 보장되지 않음

```java
public class CaveatTest {
    public static void main(String[] args) {
        // GenericBox<String>[] boxes1 = new GenericBox<>[3];   // 컴파일 에러
        @SuppressWarnings("unchecked")
        GenericBox<Person>[] boxes3 = new GenericBox[3];       // raw 배열로 우회 (경고)
        boxes3[0] = new GenericBox<Person>();
        GenericBox<String> box = new GenericBox<>("Hello");
        @SuppressWarnings({"rawtypes", "unchecked"})
        GenericBox<Person> forced = (GenericBox) box;          // raw로 형 변환하면 막을 수 없음
        boxes3[1] = forced;
        System.out.println(boxes3[1].getSome());               // Hello  (Person 박스에 String)
    }
}

class Person {}

class GenericBox<T> {
    private T some;
    public GenericBox() {}
    public GenericBox(T some) { this.some = some; }
    public T getSome() { return some; }
    public void setSome(T some) { this.some = some; }
}
```

### 한정형 형인자 (bounded type parameter)

- 필요에 따라 타입을 구체적으로 제한
  - 예) 계산기 구현 시 `Number` 이하 타입(`Byte`, `Short`, `Integer`...)으로만 제한
- 타입 파라미터 선언 뒤 `extends`와 함께 상위 타입 명시
- 제한하면 상위 타입의 메서드(`doubleValue()` 등) 사용 가능

```java
public class NumberBoxTest {
    public static void main(String[] args) {
        NumberBox<Number> numBox = new NumberBox<>();
        numBox.addSomes(1.5, 5, 4L);          // 총 합은: 10.5
        NumberBox<Integer> intBox = new NumberBox<>();
        intBox.addSomes(1, 2, 3);             // 총 합은: 6.0
        // NumberBox<String> strBox = new NumberBox<>();   // 컴파일 에러: String은 Number가 아님
    }
}

class NumberBox<T extends Number> {           // T는 Number를 상속받아야 함
    public void addSomes(T... ts) {
        double d = 0;
        for (T t : ts) {
            d += t.doubleValue();             // Number의 메서드 사용 가능
        }
        System.out.println("총 합은: " + d);
    }
}
```

- 인터페이스로 제한할 때도 `extends` 사용
- 클래스와 인터페이스 제약을 함께 쓰면 `&`로 연결

```java
class TypeRestrict1<T extends Cloneable> {}
class TypeRestrict2<T extends Number & Cloneable & Comparable<String>> {}
```

### Generic Method

- 파라미터와 리턴 타입으로 타입 파라미터를 갖는 메서드
  - 메서드 **리턴 타입 앞**에 타입 파라미터 변수 선언

```java
[제한자] <타입_파라미터, [...]> 리턴_타입 메서드_이름(파라미터) {
    // do something
}
```

| 타입 파라미터 | 결정 시점 |
| --- | --- |
| 클래스 레벨 `T` | 객체 생성 시점 |
| 메서드 레벨 `P` | 메서드 호출 시점 (명시적 `obj.<Long>method(...)` 또는 묵시적 추론) |

```java
public class TypeParameterMethodTest<T> {
    T some;

    public TypeParameterMethodTest(T some) {
        this.some = some;
    }

    public <P> P method(P p) {
        System.out.printf("클래스 레벨의 T: %s%n", some.getClass().getSimpleName());
        System.out.printf("파라미터 레벨의 P: %s%n", p.getClass().getSimpleName());
        return p;
    }

    public static void main(String[] args) {
        TypeParameterMethodTest<String> tpmt = new TypeParameterMethodTest<>("Hello"); // T 결정
        tpmt.<Long>method(20L);               // 명시적으로 P 결정
        tpmt.method(10);                      // 묵시적으로 P 결정 (Integer)
    }
}

/* 출력
클래스 레벨의 T: String
파라미터 레벨의 P: Long
클래스 레벨의 T: String
파라미터 레벨의 P: Integer
*/
```

### 와일드 카드(?) 자료형

- 제네릭 타입 변수 선언 시 실제 타입 파라미터가 무엇인지 모르거나 신경 쓰고 싶지 않을 때 사용
- wildcard를 쓰면 API가 유연해짐

| 구분 | 표현 | 설명 |
| --- | --- | --- |
| 비한정형 와일드카드(unbounded wildcard type) | `Generic_type<?>` | 타입 제한 없음(Object) |
| 한정형 와일드카드 — upper bounded | `Generic_type<? extends T>` | T 또는 T를 상속받은 타입만 |
| 한정형 와일드카드 — lower bounded | `Generic_type<? super T>` | T 또는 T의 조상 타입만 |

- 상속 관계: `Object` ← `Person` ← `SpiderMan` ← `VenomSpider`

| 매개변수 타입 | `GenericBox<Object>` | `GenericBox<Person>` | `GenericBox<SpiderMan>` |
| --- | --- | --- | --- |
| `GenericBox<Person>` | X | O | X |
| `GenericBox<?>` | O | O | O |
| `GenericBox<? extends Person>` | X | O | O |
| `GenericBox<? super Person>` | O | O | X |

- 허용할 수 있는 공통 요소를 생각하기 (안 되는 상황은 반증법으로)

| 매개변수 타입 | 꺼내면(조회) | 넣을 수 있는 것(저장) |
| --- | --- | --- |
| `GenericBox<?>` | `Object` | 없음(null만) |
| `GenericBox<? extends Person>` | `Person` — 최상위 타입이 Person | 없음 — 실제로는 SpiderMan 박스일 수도 있음 |
| `GenericBox<? super Person>` | `Object`로만 보장 | `Person`과 그 자식 — 최하위 타입이 Person |

### PECS (Producer Extends, Consumer Super)

- **Producer Extends**: 제네릭 타입이 데이터를 **생산**해 외부로 공급 → `<? extends T>` → 코드에서 T로 조회
- **Consumer Super**: 제네릭 타입이 데이터를 **소비**(추가·수정) → `<? super T>` → 코드에서 T의 자식을 저장
- 예) `Collections.copy`: src가 공급한 데이터를 dest가 소비하는 구조

```java
public class Collections {
    public static <T> void copy(List<? super T> dest, List<? extends T> src) { ... }
}
```

```java
import java.util.*;

public class WildCardTest {
    public static void main(String[] args) {
        GenericBox<Object> boxObject = new GenericBox<>(new Object());
        GenericBox<Person> boxPerson = new GenericBox<>(new Person());
        GenericBox<SpiderMan> boxSpiderMan = new GenericBox<>(new SpiderMan());

        notUseWildCardType(boxPerson);          // boxPerson만 가능
        useWildCardType1(boxObject);            // <?> : 모두 가능
        useWildCardType1(boxSpiderMan);
        useWildCardType2(boxPerson);            // <? extends Person>
        useWildCardType2(boxSpiderMan);         // boxObject는 컴파일 에러
        useWildCardType3(boxObject);            // <? super Person>
        useWildCardType3(boxPerson);            // boxSpiderMan은 컴파일 에러

        // PECS: src(extends)가 공급, dest(super)가 소비
        List<Object> dest = new ArrayList<>(Arrays.asList("a", "b"));
        List<String> src = List.of("x");
        Collections.copy(dest, src);
        System.out.println(dest);               // [x, b]
    }

    static void notUseWildCardType(GenericBox<Person> boxPerson) {}

    static void useWildCardType1(GenericBox<?> boxAll) {
        Object o = boxAll.getSome();            // 꺼내면 Object
        // boxAll.setSome(new Person());        // 컴파일 에러: 무엇을 담는 박스인지 모름
    }

    static void useWildCardType2(GenericBox<? extends Person> boxExtendsPerson) {
        Person p = boxExtendsPerson.getSome();  // Producer Extends: Person으로 조회 가능
        // boxExtendsPerson.setSome(new Person());  // 컴파일 에러
        System.out.println("extends: " + p.getClass().getSimpleName());
    }

    static void useWildCardType3(GenericBox<? super Person> boxSuperPerson) {
        boxSuperPerson.setSome(new SpiderMan()); // Consumer Super: Person 자식 저장 가능
        Object o = boxSuperPerson.getSome();     // 꺼내면 Object로만 보장
        System.out.println("super: " + o.getClass().getSimpleName());
    }
}

class Person {}
class SpiderMan extends Person {}

class GenericBox<T> {
    private T some;
    public GenericBox(T some) { this.some = some; }
    public T getSome() { return some; }
    public void setSome(T some) { this.some = some; }
}

/* 출력
extends: Person
extends: SpiderMan
super: SpiderMan
super: SpiderMan
[x, b]
*/
```

### API의 와일드카드 읽기 1 — `java.util.List.addAll`

```java
public interface List<E> extends Collection<E> {
    boolean addAll(int index, Collection<? extends E> c);
}
```

- `List<Person>`에 다른 요소들을 추가할 때 Collection의 타입 파라미터가

| 만약 | 의미 |
| --- | --- |
| `E`였다면 | `List<Person>`만 처리 — `List<SpiderMan>`을 추가할 수 없음 → 여러 타입을 받으려면 와일드카드 필요 |
| `<?>`였다면 | 모든 대상을 받겠다는 이야기 (Object도 들어옴) |
| `<? super E>`였다면 | Object(Person의 조상)를 받겠다는 이야기 |
| `<? extends E>` (실제) | Person과 그 자손 컬렉션을 받음 |

### API의 와일드카드 읽기 2 — `java.util.Arrays.compare`

```java
public static <T> int compare(T[] a, T[] b, Comparator<? super T> cmp) {}
```

- T의 **조상으로 구성된 Comparator**로 비교 가능
- SpiderMan 배열은 `Comparator<Person>`(조상 기준)으로 비교 가능, `Comparator<VenomSpider>`(자손 기준)로는 불가

| 만약 | 의미 |
| --- | --- |
| `T`였다면 | SpiderMan만 처리 — `Comparator<SpiderMan>`이 없으면 처리 불가 → 여러 타입을 받으려면 와일드카드 필요 |
| `<?>`였다면 | 모두의 기준을 받게 됨 — 처리할 수 없음 |
| `<? extends T>`였다면 | `Comparator<VenomSpider>`를 받겠다는 이야기 — SpiderMan은 비교 불가 |

```java
import java.util.*;

public class ApiWildCardTest {
    public static void main(String[] args) {
        // addAll(int, Collection<? extends E>) : E의 자손 컬렉션도 추가 가능
        List<Person> people = new ArrayList<>();
        List<SpiderMan> spiders = List.of(new SpiderMan("피터"));
        people.addAll(0, spiders);
        System.out.println(people.size());                      // 1

        // compare(T[], T[], Comparator<? super T>) : 조상 기준 Comparator로 비교 가능
        Comparator<Person> byName = Comparator.comparing(p -> p.name);
        SpiderMan[] a = { new SpiderMan("마일스") };
        SpiderMan[] b = { new SpiderMan("피터") };
        System.out.println(Arrays.compare(a, b, byName) < 0);   // true
    }
}

class Person {
    String name;
    Person(String name) { this.name = name; }
}

class SpiderMan extends Person {
    SpiderMan(String name) { super(name); }
}
```

> 참고: 교재 `Arrays.compare` 슬라이드의 마지막 질문은 `<? extends E>`로 적혀 있지만 이 메서드의 타입 파라미터는 `T`이므로 `<? extends T>`가 맞음.

---

## 참고

- 교재 주의사항 2의 `static I item;` 코드는 타입 파라미터 이름이 `I`인데 에러 메시지는 `T`로 되어 있음 — 같은 내용(타입 파라미터는 static에서 사용 불가)
- Java 16부터 `instanceof`는 안전하게 확인 가능한 경우에 한해 제네릭 타입도 허용함(예: `List<String>` 변수를 `ArrayList<String>`으로 확인). 위 `GenericBox<SpiderMan>`을 `GenericBox<String>`으로 확인하는 것은 여전히 컴파일 에러
