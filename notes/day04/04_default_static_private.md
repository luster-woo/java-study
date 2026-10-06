# default_static_private

> 교재: 15기_자바_live_05_interface_0122 (전공)

> [← Interface의 필요성](03_Interface의_필요성.md) · [day04 목록](../README.md) · [generic →](05_generic.md)

## default method

- 인터페이스에 선언된 **구현부가 있는 일반 메서드**
  - 메서드 선언부에 `default` 제한자를 붙이고 구현부 작성
  - 접근 제한자는 `public`으로 한정(생략 가능)
- 필요성
  - 기존에 인터페이스 기반으로 동작하던 라이브러리의 인터페이스에 새 기능을 추가해야 하는 상황 (예: 에어컨에 "무풍" 기능 추가)
  - abstract 메서드로 추가하면 → 기존 구현체(OldClass 1, 2, 3...)가 모두 override 해야 함
  - default 메서드는 abstract가 아니므로 → 반드시 구현할 필요 없음

```java
public class DefaultMethodTest {
    public static void main(String[] args) {
        new OldClass().defaultMethod();     // 이것은 기본 메서드입니다.
        new NewClass().defaultMethod();     // 새 클래스는 기능을 재정의
    }
}

interface DefaultMethodInterface {
    void abstractMethod();

    default void defaultMethod() {          // public 생략 가능
        System.out.println("이것은 기본 메서드입니다.");
    }
}

class OldClass implements DefaultMethodInterface {   // 기존 구현체: 수정 없이 컴파일됨
    public void abstractMethod() {}
}

class NewClass implements DefaultMethodInterface {
    public void abstractMethod() {}

    @Override
    public void defaultMethod() {
        System.out.println("새 클래스는 기능을 재정의");
    }
}
```

### default method의 충돌

- JDK 1.7 이하: 인터페이스 메서드에 구현부가 없으므로 충돌 없음
- 1.8부터: default 메서드가 생기면서 같은 이름의 구현부 있는 메서드끼리 충돌
- 메서드 우선순위

| 상황 | 결과 |
| --- | --- |
| super class가 구체적인 메서드를 가짐 | **super class의 메서드 우선** — default 메서드는 무시 |
| 한 인터페이스는 default 제공, 다른 인터페이스에도 같은 이름 메서드(default 유무 무관) | sub class가 **반드시 override** 해서 충돌 해결 |

```java
public class ConflictTest {
    public static void main(String[] args) {
        new Child().hello();     // 조상 클래스의 hello
        new Both().hello();      // A의 hello / 직접 해결
    }
}

interface A {
    default void hello() { System.out.println("A의 hello"); }
}

interface B {
    void hello();                // default 유무와 무관하게 이름이 겹치면 충돌
}

class Parent {
    public void hello() { System.out.println("조상 클래스의 hello"); }
}

// 1) super class 우선: Parent의 hello가 있으므로 A의 default는 무시
class Child extends Parent implements A {}

// 2) interface 간 충돌: 반드시 override (안 하면 컴파일 에러)
class Both implements A, B {
    @Override
    public void hello() {
        A.super.hello();         // 필요하면 특정 인터페이스의 default 호출
        System.out.println("직접 해결");
    }
}

/* 출력
조상 클래스의 hello
A의 hello
직접 해결
*/
```

## static method

- 인터페이스에 선언된 static 메서드
  - 일반 static 메서드처럼 별도 객체가 필요 없음
  - 구현 클래스 없이 **인터페이스 이름으로 바로** 접근해서 사용

```java
public class StaticMethodTest {
    public static void main(String[] args) {
        StaticMethodInterface.staticMethod();   // Static 메서드
    }
}

interface StaticMethodInterface {
    static void staticMethod() {
        System.out.println("Static 메서드");
    }
}
```

## private method (Java 9~)

- 인터페이스에 body를 가진 메서드(default, static)가 생기면서 **공통으로 처리할 모듈**이 생김
  - 외부에 공개할 필요 없는 메서드를 지정하려고 private 메서드 추가
  - `default` 키워드는 쓰지 않으며 `static` 처리 가능
  - static 메서드에서 부르려면 private **static** 이어야 함

```java
public class MyMathTest {
    public static void main(String[] args) {
        MyMath.add(3, 4);                 // 3 + 4 = 7
        new MyMath() {}.multi(3, 4);      // 3 * 4 = 12  (default 메서드는 구현 객체로 호출)
        // MyMath.print(1, 2, '+', 3);    // 컴파일 에러: private
    }
}

interface MyMath {
    static void add(int a, int b) {
        int result = a + b;
        print(a, b, '+', result);
    }

    default void multi(int a, int b) {
        print(a, b, '*', a * b);
    }

    // 공통 처리 모듈 — 외부에 공개하지 않음
    private static void print(int num1, int num2, char oper, int result) {
        System.out.println("%d %c %d = %d".formatted(num1, oper, num2, result));
    }
}
```
