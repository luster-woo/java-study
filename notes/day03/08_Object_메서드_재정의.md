# Object 메서드 재정의

> 교재: 15기_자바_live_04_polymorphism_0121_오후 (전공)

> [← 객체의 형변환](07_객체의_형변환.md) · [day03 목록](../README.md)

### Object 클래스

- 가장 최상위 클래스로 **모든 클래스의 조상**
  - Object의 멤버는 모든 클래스의 멤버 → `toString()`, `equals()`, `hashCode()`, `getClass()` ...
- 필요하면 **재정의**해서 사용

```java
System.out.println(person.toString());      // Object의 toString 사용 (재정의 안 함)
System.out.println(spiderMan.toString());   // SpiderMan의 toString 사용 (재정의함)
```

### toString()

- 객체를 **문자열로** 바꾸는 메서드
- Object의 `toString()`은 `클래스이름@해시코드16진수` — Object 입장에서는 최선
- 정작 궁금한 것은 주소값 같은 것이 아니라 **내용** → 재정의

```java
@Override
public String toString() {
    return "SpiderMan [isSpider=" + isSpider + ", name=" + name + "]";
}
```

### equals()

- 두 객체가 **같은지** 비교하는 메서드
- Object의 `equals()`는 `==`로 두 객체의 **주소값**을 비교

```java
// Object의 equals
public boolean equals(Object obj) {
    return (this == obj);
}
```

```java
Object obj1 = new Object();
Object obj2 = new Object();
Object obj3 = obj2;
System.out.printf("obj1 == obj2: %b%n", obj1 == obj2);           // false
System.out.printf("obj1 equals obj2: %b%n", obj1.equals(obj2));  // false
System.out.printf("obj2 == obj3: %b%n", obj2 == obj3);           // true
System.out.printf("obj2 equals obj3: %b%n", obj2.equals(obj3));  // true
```

```
 obj1 ─► 0x100 (Object)
 obj2 ─► 0x200 (Object)
 obj3 ─► 0x200          <- obj2와 같은 객체
```

### equals 재정의 — 내용 비교

- 우리가 비교할 것은 정말 주소값인가? → 두 객체의 **내용**을 비교하도록 재정의

| 비교 대상 | 방법 |
|---|---|
| 객체의 주소 | `==` |
| 객체의 내용 | `equals` 재정의 |

```java
public class EqualsTest {
    public static void main(String[] args) {
        String s1 = new String("Hello");
        String s2 = new String("Hello");
        System.out.println((s1 == s2) + " : " + s1.equals(s2));  // false : true  (String은 equals 재정의됨)

        Phone p1 = new Phone("0100000000");
        Phone p2 = new Phone("0100000000");
        System.out.println((p1 == p2) + " : " + p1.equals(p2));  // false : true
    }
}

class Phone {
    String number;

    public Phone(String number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Phone casted) {
            return number.equals(casted.number);
        }
        return false;
    }
}
```

- Integer에서의 equals

```java
Integer[] is = {1, 1, 128, 128};
for (int i = 0; i < is.length; i += 2) {
    System.out.printf("%d, ==? %b, equals? %b%n", i, is[i] == is[i + 1], is[i].equals(is[i + 1]));
}
/* 출력
0, ==? true, equals? true
2, ==? false, equals? true
*/
```

- `1`끼리는 `==`도 true지만 `128`끼리는 false → 래퍼 객체는 `==`가 아니라 **equals**로 비교해야 함

### hashCode()

- 객체의 해시 코드: 시스템에서 **객체를 구별**하기 위해 쓰는 정수 값
- **equals를 재정의할 때는 반드시 hashCode도 재정의**할 것
  - API 문서 규약: equals로 같은 두 객체는 hashCode도 같은 정수를 내야 함
- `HashSet`, `HashMap` 등에서 객체의 동일성 확인에 사용

```
 Set.add(s1) / Set.add(s2)    (s1, s2 = new String("Hello"))
   s1.equals(s2) && s1.hashCode() == s2.hashCode()  →  같은 객체로 보고 {s1} 하나만 관리
```

- Object에 정의된 hashCode: `public native int hashCode();`
  - 직접 계산하기보다 미리 작성된 **String이나 Number 등에서 재정의된 hashCode** 활용 권장

### Object의 메서드 재정의 — Product

```java
import java.util.HashSet;
import java.util.Set;

public class ProductTest {
    public static void main(String[] args) {
        Product product1 = new Product("123");
        Product product2 = new Product("123");

        System.out.println(product1);                    // Product [sn=123]
        System.out.println(product1.equals(product2));   // true
        System.out.printf("hashCode: %d, %d%n", product1.hashCode(), product2.hashCode());  // hashCode: 48690, 48690

        Set<Product> set = new HashSet<>();
        set.add(product1);
        set.add(product2);
        System.out.println(set.size());                  // 1
    }
}

class Product {
    String sn;

    public Product(String sn) {
        this.sn = sn;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Product casted) {
            return sn.equals(casted.sn);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return sn.hashCode();   // String이 재정의한 hashCode 활용
    }

    @Override
    public String toString() {
        return "Product [sn=" + sn + "]";
    }
}
```

| 재정의 상태 | `println(product1)` | `equals` | `hashCode` 두 값 | HashSet 크기 |
|---|---|---|---|---|
| 아무것도 재정의 안 함 | `Product@251a69d7` 형태 | false | 서로 다름 | 2 |
| toString·equals만 재정의 | `Product [sn=123]` | true | 서로 다름 | 2 |
| hashCode까지 재정의 | `Product [sn=123]` | true | 같음(48690) | 1 |

### 정리 — OOP is A P.I.E

| 특성 | 이번 교재에서 |
|---|---|
| Abstraction | 현실의 객체를 클래스로 |
| Polymorphism | 조상 타입으로 자식 객체 참조, 동적 바인딩 |
| Inheritance | (이전 교재) extends, 오버라이딩 |
| Encapsulation | private + getter/setter, Singleton |

---

## 참고

- `Integer` `==` 결과가 1과 128에서 다른 이유: `Integer.valueOf`는 **-128~127** 값을 캐시해 같은 객체를 돌려주므로 1끼리는 `==`가 true, 128은 매번 새 객체라 false.
- `obj != null && obj instanceof Phone casted`에서 `instanceof`는 `null`이면 항상 false라 `obj != null`은 없어도 결과가 같음(교재 코드대로 둠).
- `obj instanceof Person casted` 같은 패턴 매칭은 Java 16 이상에서 컴파일됨. Java 11 이하에서는 `if (obj instanceof Person) { Person casted = (Person) obj; ... }`로 씀.
- 교재 Product 출력 예시의 hashCode `48721`은 노트 예제(`"123"`)의 실제 값 `48690`과 다름 — `String.hashCode()`는 문자열 내용으로 정해지므로 같은 sn이면 항상 같은 값이 나옴.
