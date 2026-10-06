# polymorphism

> 교재: 15기_자바_live_04_polymorphism_0121_오후 (전공)

> [← encapsulation](05_encapsulation.md) · [day03 목록](../README.md) · [객체의 형변환 →](07_객체의_형변환.md)

### 다형성이란

- **多形性** — 하나의 객체가 **많은 형(타입)**을 가질 수 있는 성질
- 교재 비유: 황금잉어빵 is a 붕어빵 (상속)
  - "붕어빵 주세요"에 황금잉어빵을 줘도 됨 → 다형성이 없다면 붕어빵마다 따로 주문·판매해야 함
- 상속 관계에 있을 때 **조상 클래스의 타입으로 자식 클래스 객체를 참조**할 수 있음

```java
public class PolyTest {
    public static void main(String[] args) {
        SpiderMan onlyOne = new SpiderMan();
        SpiderMan sman = onlyOne;     // SpiderMan 타입으로 참조
        Person person = onlyOne;      // 조상 Person 타입으로 참조
        Object obj = onlyOne;         // 최상위 Object 타입으로 참조
        // Venom venom = onlyOne;     // 컴파일 에러: incompatible types (상속 관계 없음)
        System.out.println(sman == person && person == obj);  // true
    }
}

class Person {
    String name;
    void jump() { System.out.println("두 다리로 힘껏 점프"); }
}

class SpiderMan extends Person {
    boolean isSpider;
    void fireWeb() { System.out.println("거미줄 발사"); }
}

class Venom {}
```

| 교재 질문 | 답 |
|---|---|
| onlyOne은 SpiderMan 타입인가? | O — SpiderMan 타입으로 참조 가능 |
| onlyOne은 Person 타입인가? | O — SpiderMan is a Person |
| onlyOne은 Object 타입인가? | O — 모든 클래스는 Object의 자손 |
| onlyOne은 Venom 타입인가? | X — 상속 관계가 없어 참조 불가 |

### 활용 예 1 — 다른 타입의 객체를 다루는 배열

- 배열은 **같은 타입**의 데이터를 묶음으로 다룸
  - `Person[]`에는 Person만, `SpiderMan[]`에는 SpiderMan만 → 타입마다 배열을 만들어야 함
- 다형성으로 상속 관계의 다른 타입(Person, SpiderMan)을 **하나의 배열**로 관리

```java
// 다형성 이전
Person[] persons = new Person[10];
persons[0] = new Person();
SpiderMan[] spiderMans = new SpiderMan[10];
spiderMans[0] = new SpiderMan();

// 다형성 이후
Person[] persons2 = new Person[10];
persons2[0] = new Person();
persons2[1] = new SpiderMan();   // SpiderMan도 Person
```

- Object는 모든 클래스의 조상 → **Object 배열은 어떤 타입의 객체든** 저장 가능
- 이 특성으로 자바의 자료 구조를 간단히 처리 → **Collection API** 등장
  - 예: `ArrayList` 생성자는 내부에 `new Object[initialCapacity]`를 만듦

```java
// ArrayList 생성자 (JDK 소스)
public ArrayList(int initialCapacity) {
    if (initialCapacity > 0) {
        this.elementData = new Object[initialCapacity];
    } else if (initialCapacity == 0) {
        this.elementData = EMPTY_ELEMENTDATA;
    } else {
        throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
    }
}
```

- 교재 질문: 기본형(int)은 Object를 상속하지 않았는데 Object 배열에 담을 수 있을까? → 아래 `## 참고`

### 활용 예 2 — 매개변수의 다형성

- 메서드를 호출하려면 메서드 이름과 파라미터가 맞아야 함
  - `println(Person p)`, `println(SpiderMan sp)`, ... 타입마다 만들면 끝이 없음
- 실제 `println`은 **Object**를 파라미터로 받음

```java
// PrintStream.println (JDK 소스)
public void println(Object x) {
    String s = String.valueOf(x);
    synchronized (this) {
        print(s);
        newLine();
    }
}
```

- 조상을 파라미터로 처리하면 객체 타입마다 메서드를 만들 필요가 없음
- API에서 파라미터로 Object를 받는다는 것은 **"모든 객체를 처리한다"**는 의도

---

## 참고

- 교재 질문 "기본형은 Object 배열에 담을 수 있을까?" → 담을 수 있음. `Object[] objs = {new Person(), "문자열", 10};`에서 `10`은 **오토박싱**으로 `Integer` 객체가 되어 저장됨.
