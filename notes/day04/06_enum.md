# enum

> 교재: 15기_자바_live_06_Generic_0123 (전공)

> [← generic](05_generic.md) · [day04 목록](../README.md) · [annotation →](07_annotation.md)

## Enum

- **열거형** 데이터 타입
  - 데이터가 몇 가지 한정된 값(주로 상수)만 갖는 경우 (예: 요일, 월, 계절)
  - 내부적으로 `java.lang.Enum`을 상속받은 특별한 클래스 — `class` 대신 `enum` 키워드 사용
- Enum 타입 작성
  - 선언된 값은 **enum 상수** — 일반 상수 naming 룰(대문자, `_`) 따름
- Enum 타입 사용
  - 클래스처럼 변수 선언, 할당할 수 있는 값은 **enum 상수 또는 null**뿐

```java
enum Grade {
    SALES, PART_TIME_JOB, NORMAL
}

Grade grade = Grade.SALES;
System.out.println(grade);                     // SALES
System.out.println(grade instanceof Enum);     // true
System.out.println(grade instanceof Object);   // true
```

### 필요성

- 값 비교 시 값뿐 아니라 **타입까지 체크** → 논리적 오류 방지

| `int` 상수 | `enum` |
| --- | --- |
| `SALES = 1`, `SPRING = 1`처럼 의미가 달라도 값이 같으면 통과 | `Grade`에 선언된 상수만 전달 가능 |
| `calcSalNormal(SPRING)` → 컴파일 OK, 결과도 SALES와 같음 | `calcSalEnum(SPRING)` → 컴파일 에러 |

### Enum을 이용한 연산

- 주로 비교 연산에서 사용: `==`, `equals`로 두 enum 상수가 같은지 비교
- `switch~case`의 case에는 열거형 이름 없이 **상수 이름만** 씀 (`case SPRING:`)

### Enum의 주요 메서드

| 메서드 선언부 | 설명 |
| --- | --- |
| `public final String name()` | enum 상수의 이름을 문자열로 리턴 |
| `public final int ordinal()` | enum 상수의 순서 (0부터) |
| `public final int compareTo(E o)` | 두 enum 상수의 ordinal 차이 리턴 |
| `public static T[] values()` | 선언된 enum 상수를 배열로 리턴 |
| `public static T valueOf(String name)` | 문자열에 매핑된 enum 상수 객체 리턴 |

```java
public class EnumTest {
    private static final int SALES = 1;
    private static final int PART_TIME_JOB = 2;
    private static final int NORMAL = 3;
    private static final int SPRING = 1;       // 다른 의미의 상수인데 값이 같음

    public static void main(String[] args) {
        Grade grade = Grade.SALES;
        System.out.println(grade);                         // SALES
        System.out.println(grade instanceof Enum);         // true
        System.out.println(grade instanceof Object);       // true

        calcSalNormal(SALES);                              // 영업 실적 반영
        calcSalNormal(SPRING);                             // 영업 실적 반영  (int면 언제나 OK)
        calcSalEnum(Grade.NORMAL);                         // 근로 계약 기준
        // calcSalEnum(SPRING);                            // 컴파일 에러: Grade 상수만 가능

        enumSwitch(Season.FALL);                           // 잎이 떨어지는 시기
        System.out.println(Grade.SALES == Grade.SALES);    // true

        for (Grade g : Grade.values()) {
            System.out.printf("name: %s, ordinal: %d%n", g.name(), g.ordinal());
        }
        System.out.println(Grade.NORMAL.compareTo(Grade.SALES));  // 2
        System.out.println(Grade.valueOf("PART_TIME_JOB"));       // PART_TIME_JOB
    }

    static void calcSalNormal(int grade) {
        if (grade == SALES) System.out.println("영업 실적 반영");
        else if (grade == PART_TIME_JOB) System.out.println("근무 시간 반영");
        else if (grade == NORMAL) System.out.println("근로 계약 기준");
    }

    static void calcSalEnum(Grade grade) {
        if (grade.equals(Grade.SALES)) System.out.println("영업 실적 반영");
        else if (grade.equals(Grade.PART_TIME_JOB)) System.out.println("근무 시간 반영");
        else if (grade.equals(Grade.NORMAL)) System.out.println("근로 계약 기준");
    }

    static void enumSwitch(Season season) {
        switch (season) {
            case SPRING:                    // Season.SPRING 이 아니라 상수 이름만
                System.out.println("새 잎이 나는 시기");
                break;
            case SUMMER:
                System.out.println("열매가 무르 익는 시기");
                break;
            case FALL:
                System.out.println("잎이 떨어지는 시기");
                break;
            default:
                System.out.println("겨울잠을 자는 시기");
        }
    }
}

enum Grade {
    SALES, PART_TIME_JOB, NORMAL
}

enum Season {
    SPRING, SUMMER, FALL, WINTER
}

/* values() 반복 출력
name: SALES, ordinal: 0
name: PART_TIME_JOB, ordinal: 1
name: NORMAL, ordinal: 2
*/
```

> 참고: 교재 표는 `values()`, `valueOf(String)`을 `java.lang.Enum`의 메서드로 묶었지만, 이 둘은 컴파일러가 각 enum 타입(`Grade` 등)에 자동으로 만들어 주는 static 메서드임. `java.lang.Enum`에 있는 것은 `valueOf(Class<T>, String)` 형태.

### 자체 멤버 추가

- 일반 클래스처럼 enum에도 멤버 변수·메서드 추가 가능
- 단, enum 상수 선언 끝에 **`;`** 필요

```java
public class Greeting2Test {
    public static void main(String[] args) {
        Greeting g = Greeting.GOOD_AFTERNOON;
        g.setMessage("Hello");
        System.out.println(g.getMessage());       // Hello
    }
}

enum Greeting {
    GOOD_MORNING, GOOD_AFTERNOON, GOOD_EVENING;  // 상수 뒤에 멤버가 오므로 ;

    private String message;

    public void setMessage(String message) { this.message = message; }
    public String getMessage() { return message; }
}
```

### 생성자를 통한 enum 상수 초기화

- enum의 멤버 변수를 생성자로 초기화
  - 생성자는 언제나 **private** — 외부에서 접근 불가
  - 내부에서 enum 상수를 선언할 때 값 할당 `GOOD_MORNING("좋은 아침")`

```java
public class GreetingTest {
    public static void main(String[] args) {
        Greeting g = Greeting.GOOD_AFTERNOON;
        System.out.println(g.getMessage());                       // 오후도 힘내
        // new Greeting("hi");                                    // 컴파일 에러: 외부에서 생성 불가
        System.out.println(Greeting.GOOD_EVENING.getMessage());   // 오늘도 수고했어
    }
}

enum Greeting {
    GOOD_MORNING("좋은 아침"),
    GOOD_AFTERNOON("오후도 힘내"),
    GOOD_EVENING("오늘도 수고했어");

    private String message;

    Greeting(String message) {                    // 생략해도 private
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```
