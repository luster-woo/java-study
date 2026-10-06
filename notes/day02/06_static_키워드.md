# static 키워드

> 교재: 15기_자바비전공_Java_04_0122_객체배열관리 (비전공) — JVM·static 단원

> [← JVM 메모리구조](05_JVM_메모리구조.md) · [day02 목록](../README.md)

## 학습 목표

- static 키워드를 사용한 변수, 메서드, 블록의 특징을 설명할 수 있다.

---

## static

- **클래스 수준에서 공유**되는 멤버(필드, 메서드, 중첩 클래스)를 정의하는 데 사용
- static을 붙이면 객체(인스턴스)와 무관하게 **클래스 자체에 속함**
- 특정 객체에 속하지 않고 **모든 객체가 동일한 static 멤버를 참조**
- **클래스 이름**으로 직접 호출 가능
- JVM **메서드 영역**에 저장

## static 필드 (클래스 변수)

- 정적 필드 = 클래스 수준에서 **공유되는 변수**
- 객체마다 값이 다른 인스턴스 필드와 달리 **모든 인스턴스가 공유**
- **클래스 이름으로 호출하는 것을 권장**

```java
public class Person {
    static int pCount;   // 클래스 변수 (모든 Person이 공유)
    String name;         // 인스턴스 변수
    int age;
    String hobby;
}
```

```java
public class PersonTest {
    public static void main(String[] args) {
        Person p = new Person();
        p.name = "Kim";

        Person.pCount++;   // 권장: 클래스 이름으로
        p.pCount++;        // 오류는 나지 않지만 경고

        Person p2 = new Person();
        Person.pCount++;
        System.out.println(Person.pCount);  // 3
        System.out.println(p2.pCount);      // 3 (어느 객체로 봐도 같은 값)
    }
}
```

| | static 필드 (클래스 변수) | 인스턴스 필드 |
|---|---|---|
| 소속 | 클래스 | 객체 |
| 개수 | 클래스당 1개 | 객체마다 1개 |
| 접근 | `클래스이름.필드` (권장) | `객체.필드` |
| 저장 | 메서드 영역 (교재 기준) | 힙 (객체 안) |

## static 메서드

- 정적 메서드 = **클래스 이름으로 직접 호출**할 수 있는 메서드
- 객체와 무관하게 동작 → **전역적인 작업**에 적합 (보통 알고리즘 문제 풀 때 사용)

```java
class Calc {
    static int max(int a, int b) {
        return a > b ? a : b;
    }
}
```

```java
System.out.println(Calc.max(3, 7));   // 7 (객체 생성 없이 호출)
```

## static 블록 (초기화 블록)

- 정적 블록 = **클래스 로드 시 한 번만 실행**되는 초기화 블록
- **정적 필드 초기화**에 사용

```java
class Counter {
    // static 변수
    public static int sCount;
    // non-static 변수
    public int iCount;
    // 초기화 블록
    static {
        sCount = 10;
    }
}
```

```java
System.out.println(Counter.sCount);   // 10
```

## static 주의 사항

- **static 영역에서는 non-static 영역에 직접 접근할 수 없음**
  - static 멤버는 클래스 로드 시 이미 있지만, non-static 멤버는 객체를 만들어야 생김

```java
public class Main {
    String str = "문장";               // non-static

    public static void main(String[] args) {
        System.out.println(str);       // 컴파일 오류: non-static variable str cannot be referenced from a static context
    }
}
```

- **non-static 영역에서는 static 영역에 접근 가능**

```java
public class Main {
    static String str = "문장";        // static

    public void print() {
        System.out.println(str);       // 문장
    }
}
```

| 어디서 \ 무엇을 | static 멤버 | non-static 멤버 |
|---|---|---|
| static 메서드 | 바로 접근 O | 직접 접근 X (객체 생성 후 `객체.멤버`) |
| non-static 메서드 | 바로 접근 O | 바로 접근 O |

---

## 참고

- 교재는 static 변수를 "메서드 영역에 저장"으로 설명. 같은 주 전공반 교재(추상화)는 "클래스 로딩 시 구성, 값은 heap 영역에"로 설명함. JVM 명세상 개념 위치는 메서드 영역이고, HotSpot JVM(Java 8+)은 실제 값을 힙의 클래스 객체에 둠 — 교재의 "JVM 버전에 따라 달라질 수 있다"에 해당
