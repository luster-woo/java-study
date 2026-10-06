# Wrapper 클래스

> 출처: [Wrapper 클래스 - TCP School](https://tcpschool.com/java/java_api_wrapper) · [Integer (Java SE 17)](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Integer.html)

> [← Stream API](07_Stream_API.md) · [day05 목록](../README.md) · [Optional →](09_Optional.md)

## 래퍼 클래스(Wrapper class)

### 왜 필요한가
- 기본 타입 값을 **객체로 다뤄야 하는 상황**이 있음
  - 메서드 인수로 객체만 받는 경우, 컬렉션(`List<Integer>`)·제네릭은 기본형을 못 씀
- 기본 타입 8개 각각의 값을 **객체로 포장해 주는 클래스** = 래퍼 클래스
- 모두 `java.lang` 패키지 → import 없이 사용

| 기본 타입 | 래퍼 클래스 |
|---|---|
| byte | Byte |
| short | Short |
| int | **Integer** |
| long | Long |
| float | Float |
| double | Double |
| char | **Character** |
| boolean | Boolean |

- 이름이 기본형과 다른 것은 **Integer, Character** 둘뿐 → 주의

## 박싱과 언박싱

### 박싱(Boxing) / 언박싱(UnBoxing)
- 래퍼 객체는 산술 연산용이 아님 → 저장된 값을 **바꿀 수 없음**(불변). 값이 바뀌면 새 인스턴스
- **박싱**: 기본형 값 → 래퍼 인스턴스
- **언박싱**: 래퍼 인스턴스 → 기본형 값 (`intValue()`, `charValue()` 등)

```
  기본형  int 17   ── 박싱 ──►   Integer 객체 [17]
                  ◄─ 언박싱 ──
```

### 오토 박싱 / 오토 언박싱
- **JDK 1.5부터** 컴파일러가 박싱·언박싱을 자동 처리
  - `new`나 `intValue()` 없이 대입만으로 변환됨
- 래퍼끼리, 래퍼와 기본형 사이의 산술 연산도 가능 → 내부적으로 **언박싱 후 기본형끼리 계산**

```java
public class WrapperTest {
    public static void main(String[] args) {
        // 박싱 / 언박싱 (직접)
        Integer num = Integer.valueOf(17);   // 박싱
        int n = num.intValue();              // 언박싱
        System.out.println(n);                   // 17

        // 오토 박싱 / 오토 언박싱
        Character ch = 'X';                  // 오토 박싱
        char c = ch;                         // 오토 언박싱
        System.out.println(c);                   // X

        // 연산은 언박싱된 기본형끼리
        Integer a = 7, b = 3;
        Integer r1 = a + b;
        int r2 = a * b;
        System.out.println(r1 + " " + r2);       // 10 21

        // == 는 주소 비교, equals 는 값 비교
        Integer x = new Integer(10);         // deprecated 생성자 - 새 객체를 강제로 만들기 위해 사용
        Integer y = new Integer(10);
        System.out.println(x < Integer.valueOf(20)); // true  (언박싱 후 비교)
        System.out.println(x == y);              // false (서로 다른 객체)
        System.out.println(x.equals(y));         // true

        // Integer 캐시: valueOf/오토박싱은 -128~127을 같은 객체로 돌려줌
        Integer i1 = 127, i2 = 127;
        Integer i3 = 128, i4 = 128;
        System.out.println(i1 == i2);            // true
        System.out.println(i3 == i4);            // false
        System.out.println(i3.equals(i4));       // true

        // null 언박싱 -> NPE
        Integer empty = null;
        try {
            int v = empty;
        } catch (NullPointerException e) {
            System.out.println("NPE");           // NPE
        }

        // 문자열 <-> 숫자
        int p = Integer.parseInt("123");
        Integer q = Integer.valueOf("123");
        System.out.println(p + q);               // 246
    }
}
```

## 래퍼 객체의 비교 — `==` vs `equals()`

### 값이 같은지는 equals()
- `<`, `>` 같은 대소 비교는 오토 언박싱 덕분에 그대로 됨
- 하지만 **`==`는 래퍼도 객체라서 주소(참조)를 비교** → 서로 다른 인스턴스면 값이 같아도 `false`
- 저장된 값이 같은지는 반드시 **`equals()`**

| 비교 | `int` | `Integer` |
|---|---|---|
| `==` | 값 비교 | **참조 비교** |
| `equals()` | (없음 — 기본형) | 값 비교 |
| `null` 가능 | X | O |
| 컬렉션·제네릭 | X | O |

### Integer 캐시 — `==`가 "가끔" 맞는 이유
- `Integer.valueOf(int)`(오토 박싱도 이것을 씀)는 **-128 ~ 127** 값을 항상 캐시해 같은 객체를 돌려줌
- 그래서 `127 == 127`은 `true`, `128 == 128`은 `false` → 테스트할 때는 통과하다 운영 데이터에서 깨지는 버그가 됨
- 결론: 래퍼 비교는 범위와 상관없이 `equals()`

### 언박싱과 NullPointerException
- 래퍼는 `null`을 담을 수 있음 → `null`을 기본형에 대입(오토 언박싱)하면 **NPE**
- DB 조회 결과·JSON 값처럼 비어 있을 수 있는 값은 래퍼로 받고, 꺼내기 전에 null 확인

---

## 참고

- `new Integer(...)` 같은 래퍼 생성자는 **Java 9부터 deprecated(삭제 예정)** — `Integer.valueOf()`(박싱) / `Integer.parseInt()`(문자열 → `int`)를 씀. 위 예제의 `new Integer`는 "서로 다른 객체" 상황을 만들기 위해서만 사용

## 면접 포인트

- **int와 Integer의 차이?** `int`는 값 자체를 담는 기본형, `Integer`는 그 값을 감싼 객체라 `null`을 가질 수 있고 컬렉션·제네릭에 쓸 수 있음. 둘 사이 변환은 JDK 1.5부터 오토 박싱/언박싱으로 자동.
- **Integer를 `==`로 비교하면?** 참조 비교라 값이 같아도 `false`가 나올 수 있음. -128~127은 캐시 때문에 `true`가 나와서 버그가 늦게 드러남 → 항상 `equals()`.
- **오토 언박싱의 위험?** `null`인 래퍼를 기본형으로 꺼내면 NPE. 자동이라 코드에 드러나지 않아서 원인을 찾기 어려움.
