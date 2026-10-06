# D3 · 상속·접근제한자·캡슐화·다형성

## 오늘 볼 강의

| 순서 | 반 | 강의 |
|---|---|---|
| 1 | 전공 | [상속] 상속 |
| 2 | 전공 | [상속] 메서드 재정의 |
| 3 | 전공 | [상속] package_import |
| 4 | 전공 | [상속] 제한자 |
| 5 | 전공 | [다형성] encapsulation |
| 6 | 전공 | [다형성] polymorphism |
| 7 | 전공 | [다형성] 객체의 형변환 |
| 8 | 전공 | [다형성] Object 메서드 재정의 |

---

## 1. 상속

부모 클래스의 필드·메서드를 자식이 물려받는다.

| Python | Java |
|---|---|
| `class Dog(Animal):` | `class Dog extends Animal {` |
| `super().__init__(name)` | `super(name);` (생성자 **첫 줄**) |
| 다중 상속 가능 | **클래스는 하나만 상속** (인터페이스는 여러 개, D4) |
| 모든 클래스의 조상 `object` | 모든 클래스의 조상 `Object` |

```java
class Animal {
    String name;

    Animal(String name) {
        this.name = name;
    }

    void sound() {
        System.out.println("...");
    }
}

class Dog extends Animal {
    Dog(String name) {
        super(name);                 // 부모 생성자 호출. 안 쓰면 super()가 자동으로 들어감
    }

    @Override                        // "부모 메서드를 재정의한다"는 표시. 오타를 컴파일러가 잡아 줌
    void sound() {
        System.out.println(name + ": 멍멍");
    }

    void fetch() {
        System.out.println(name + "가 공을 물어 옴");
    }
}

public class InheritTest {
    public static void main(String[] args) {
        Dog d = new Dog("초코");
        d.sound();   // 초코: 멍멍
        d.fetch();
    }
}
```

**주의**: 부모에 기본 생성자가 없으면(위 `Animal`처럼) 자식 생성자에서 `super(...)`를 **반드시** 직접 호출해야 한다.

## 2. 오버라이딩 vs 오버로딩

| | 오버라이딩 (Overriding) | 오버로딩 (Overloading) |
|---|---|---|
| 뜻 | 부모 메서드를 자식이 **다시 정의** | 같은 이름, **매개변수만 다르게** 여러 개 |
| 관계 | 상속 관계 | 상관없음 (같은 클래스 또는 자식 클래스) |
| 시그니처 | 이름·매개변수 같아야 함. 반환 타입은 같거나 하위 타입, 접근 범위는 같거나 더 넓게 | 이름만 같고 매개변수는 달라야 함 |
| 표시 | `@Override` | 없음 |

면접 단골. 헷갈리면 "덮어쓰기(override) vs 여러 개 싣기(overload)".

## 3. 패키지와 import

```java
package day03;               // 이 파일이 속한 패키지. 파일 맨 위

import java.util.ArrayList;  // 다른 패키지의 클래스 가져오기
import java.util.*;          // java.util 전체
```
- 같은 패키지 안의 클래스는 import 없이 쓴다.
- `java.lang` 패키지(`String`, `Math`, `System`, `Integer`)는 자동 import.
- IntelliJ에서는 `Alt + Enter`로 import가 자동으로 추가된다.

## 4. 제한자 (Modifier)

### 접근 제한자

| 제한자 | 같은 클래스 | 같은 패키지 | 다른 패키지의 자식 | 전체 |
|---|---|---|---|---|
| `public` | O | O | O | O |
| `protected` | O | O | O | X |
| (default, 아무것도 안 씀) | O | O | X | X |
| `private` | O | X | X | X |

Python은 `_name`으로 "건드리지 마세요"라고 **약속**만 하지만, Java `private`은 **컴파일러가 막는다**.

### 그 밖의 제한자

| 제한자 | 붙는 곳 | 뜻 |
|---|---|---|
| `static` | 필드·메서드 | 클래스 소속 (D2) |
| `final` | 변수 | 값 변경 불가 (상수) |
| `final` | 메서드 | 오버라이딩 불가 |
| `final` | 클래스 | 상속 불가 (예: `String`) |
| `abstract` | 클래스·메서드 | 미완성. D4에서 |

## 5. 캡슐화 (Encapsulation)

필드는 `private`으로 숨기고, **메서드를 통해서만** 바꾸게 한다. 잘못된 값이 들어가는 걸 막을 수 있다.

```java
public class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        setAge(age);
    }

    public String getName() { return name; }

    public int getAge() { return age; }

    public void setAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("나이는 0 이상");  // D6 예외에서 자세히
        }
        this.age = age;
    }

    public static void main(String[] args) {
        Person p = new Person("규", 25);
        // p.age = -5;        // 같은 클래스 안이라 되지만, 다른 클래스에서는 컴파일 에러
        p.setAge(26);
        System.out.println(p.getName() + " " + p.getAge());
    }
}
```
getter/setter는 `Alt + Insert` → Getter and Setter. 나중에 Lombok의 `@Getter`가 이걸 대신 만들어 준다(D7).

## 6. 다형성 (Polymorphism)

**부모 타입 변수로 자식 객체를 가리킬 수 있다.** 그리고 메서드를 호출하면 **실제 객체(자식)의 메서드**가 실행된다.

위 `InheritTest`와 같은 패키지에 두면 `Animal`·`Dog` 이름이 겹치므로, 여기서는 클래스 **안에** 넣었다(`static class`, 중첩 클래스).

```java
public class PolyTest {
    static class Animal {
        void sound() { System.out.println("..."); }
    }

    static class Dog extends Animal {
        @Override
        void sound() { System.out.println("멍멍"); }
        void fetch() { System.out.println("공 물어 오기"); }
    }

    static class Cat extends Animal {
        @Override
        void sound() { System.out.println("야옹"); }
    }

    public static void main(String[] args) {
        Animal[] animals = { new Dog(), new Cat(), new Dog() };   // 부모 타입 배열에 자식들
        for (Animal a : animals) {
            a.sound();               // 멍멍 / 야옹 / 멍멍  ← 실제 객체의 메서드가 불림
        }

        Animal a = new Dog();        // 업캐스팅 (자동)
        // a.fetch();                // 컴파일 에러: Animal 타입에는 fetch가 없음

        if (a instanceof Dog) {      // 진짜 Dog인지 확인하고
            Dog d = (Dog) a;         // 다운캐스팅 (명시적)
            d.fetch();
        }

        // Java 16+ 패턴 매칭: 확인과 변환을 한 번에
        if (a instanceof Dog dog) {
            dog.fetch();
        }

        // Animal x = new Cat();
        // Dog wrong = (Dog) x;      // 실행 중 ClassCastException
    }
}
```

| 용어 | 방향 | 자동? |
|---|---|---|
| 업캐스팅 | 자식 → 부모 타입 | 자동 |
| 다운캐스팅 | 부모 → 자식 타입 | `(Dog)` 명시 필요. 실제 객체가 아니면 `ClassCastException` |

**규칙 두 줄**:
- **무엇을 호출할 수 있나**는 **변수 타입**(왼쪽)이 정한다.
- **어떤 코드가 실행되나**는 **실제 객체**(오른쪽)가 정한다.

**왜 중요한가?** Spring 전체가 이 원리로 돌아간다. `UserRepository repo`(부모·인터페이스 타입)에 실제로는 Spring이 만든 구현 객체가 들어온다. D4 인터페이스와 D7 DI에서 다시 나온다.

## 7. Object 클래스의 메서드

모든 클래스는 `Object`를 상속한다. 그중 자주 재정의하는 세 개.

| 메서드 | 기본 동작 | 재정의 이유 |
|---|---|---|
| `toString()` | `클래스명@해시값` 출력 | `println(obj)` 했을 때 보기 좋게 (Python의 `__str__`) |
| `equals(Object o)` | `==`와 같음 (같은 객체인지) | **내용이 같으면 같다**고 판단하게 (Python의 `__eq__`) |
| `hashCode()` | 객체마다 다른 값 | `equals`를 바꾸면 **반드시 같이** 바꿔야 함 (HashMap·HashSet에서 사용) |

```java
import java.util.Objects;

public class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point p = (Point) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public static void main(String[] args) {
        Point a = new Point(1, 2);
        Point b = new Point(1, 2);
        System.out.println(a);            // Point(1, 2)
        System.out.println(a == b);       // false (다른 객체)
        System.out.println(a.equals(b));  // true  (내용이 같음)
    }
}
```
`Alt + Insert` → `equals() and hashCode()`, `toString()`으로 자동 생성할 수 있다.

**`==` vs `equals()`**: `==`는 **같은 객체인지**(주소 비교), `equals()`는 **내용이 같은지**. 문자열 비교에 `==`를 쓰면 안 되는 이유가 이것이다.

---

## 자주 하는 실수

| 실수 | 증상 | 고치기 |
|---|---|---|
| `@Override` 안 붙이고 이름 오타 | 재정의가 안 되고 새 메서드가 생김. 에러도 안 남 | 항상 `@Override` |
| 부모에 기본 생성자 없음 | 자식 클래스에 빨간 줄 | 자식 생성자 첫 줄에 `super(...)` |
| 부모 타입 변수로 자식 전용 메서드 호출 | 컴파일 에러 | `instanceof` 확인 후 다운캐스팅 |
| `equals`만 재정의 | HashSet에 같은 값이 두 번 들어감 | `hashCode`도 같이 |
| 한 파일에 `public class` 두 개 | 컴파일 에러 | public 클래스는 파일당 하나, 나머지는 `public` 빼기 |

---

## 면접 질문

1. **오버라이딩과 오버로딩의 차이는?**
   오버라이딩은 상속받은 메서드를 자식이 같은 시그니처로 다시 정의하는 것, 오버로딩은 같은 이름의 메서드를 매개변수만 다르게 여러 개 정의하는 것.
2. **다형성이란?**
   부모(또는 인터페이스) 타입의 변수로 여러 자식 객체를 다룰 수 있고, 호출하면 실제 객체의 재정의된 메서드가 실행되는 성질. 코드를 바꾸지 않고 구현을 갈아 끼울 수 있다.
3. **캡슐화를 왜 하나요?**
   필드를 private으로 숨기고 메서드로만 접근하게 해서, 잘못된 값이 들어오는 걸 막고 내부 구현을 바꿔도 사용하는 쪽 코드는 그대로 두기 위해.
4. **`==`와 `equals()`의 차이는?**
   `==`는 참조형에서 같은 객체인지(주소)를 비교하고, `equals()`는 재정의된 기준에 따라 내용이 같은지를 비교한다.
5. **`equals`를 재정의하면 왜 `hashCode`도 재정의해야 하나요?**
   HashMap·HashSet은 hashCode로 먼저 위치를 찾고 equals로 비교한다. equals가 같은데 hashCode가 다르면 같은 객체를 다른 것으로 취급한다.
6. **Java가 클래스 다중 상속을 막는 이유는?**
   두 부모에 같은 메서드가 있으면 어느 쪽을 물려받을지 모호해지는 문제(다이아몬드 문제)를 피하려고. 대신 인터페이스는 여러 개 구현할 수 있다.

---

## 오늘의 실습

- [ ] 강의 코드 따라 치기 (`src/day03/`)
- [ ] `Animal` → `Dog`, `Cat`, `Bird`를 만들고 `Animal[]` 배열에 담아 `sound()` 호출
- [ ] D2의 `Account` 필드를 `private`으로 바꾸고 getter만 열기 (balance에 setter는 만들지 않기)
- [ ] `Point`에 `equals`·`hashCode`·`toString` 재정의하고 `==`와 `equals` 결과 비교
- [ ] 연습문제 3개: [problems/day03.md](problems/day03.md) (`src/practice/`에 풀기)
- [ ] 여유 있으면 SWEA·프로그래머스에서 1~2문제 더
- [ ] 커밋: `day03: 상속·접근제한자·다형성 / Animal·Point 실습`
