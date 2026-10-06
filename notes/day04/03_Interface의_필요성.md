# Interface의 필요성

> 교재: 15기_자바_live_05_interface_0122 (전공)

> [← Interface](02_Interface.md) · [day04 목록](../README.md) · [default_static_private →](04_default_static_private.md)

## 인터페이스의 필요성

- **구현의 강제**로 표준화 처리 (abstract 메서드 사용)
- 인터페이스를 통한 간접적인 클래스 사용으로 **손쉬운 모듈 교체** 지원
- 서로 상속 관계가 없는 클래스들에게 인터페이스로 관계를 부여해 **다형성 확장**
- 모듈 간 **독립적 프로그래밍** 가능 → 개발 기간 단축

### 구현의 강제로 표준화 → 손쉬운 모듈 교체

- 프린터가 바뀌어도 `print()`는 있음 → 사용하는 쪽 코드는 그대로
- 예) JDBC: 자바 애플리케이션은 JDBC 인터페이스만 사용, DB 벤더가 인터페이스 구현 클래스(드라이버)를 제공

```
JAVA Application
      │  (JDBC interface만 사용)
      ▼
    JDBC  ── interface
      │
      ├── JDBC driver for MySQL   ┐
      ├── JDBC driver for Oracle  ├ 벤더별 interface 구현 class
      └── JDBC driver for MsSql   ┘
```

### 상속 관계 없는 클래스에 관계 부여 → 다형성 확장

- `HandPhone`, `ElectricShaver`는 공통 조상이 `Object`뿐
- badCase: `Object[]`에 담으면 타입마다 `instanceof`로 확인하고 형 변환해야 함 — 타입이 늘수록 분기도 늘어남
- goodCase: 둘 다 `Chargeable`을 구현하게 하면 `Chargeable[]` 하나로 처리

```java
public class ChargeTest {
    public static void main(String[] args) {
        badCase();
        goodCase();
    }

    // 공통 조상이 Object뿐 → 타입마다 확인하고 형 변환
    static void badCase() {
        Object[] objs = { new HandPhone(), new ElectricShaver() };
        for (Object obj : objs) {
            if (obj instanceof HandPhone) {
                ((HandPhone) obj).charge();
            } else if (obj instanceof ElectricShaver) {
                ((ElectricShaver) obj).charge();
            }
        }
    }

    // Chargeable로 관계 부여 → 한 타입으로 처리
    static void goodCase() {
        Chargeable[] objs = { new HandPhone(), new ElectricShaver() };
        for (Chargeable obj : objs) {
            obj.charge();
        }
    }
}

interface Chargeable {
    void charge();
}

class Phone {}
class HandPhone extends Phone implements Chargeable {
    public void charge() { System.out.println("휴대폰 충전"); }
}

class Appliance {}
class ElectricShaver extends Appliance implements Chargeable {
    public void charge() { System.out.println("면도기 충전"); }
}

/* 출력
휴대폰 충전
면도기 충전
휴대폰 충전
면도기 충전
*/
```

- 교재의 badCase는 `if (obj instanceof HandPhone phone) { phone.charge(); }` 처럼 패턴 매칭(Java 16+)으로 형 변환을 줄여 씀 — 위 코드와 동작은 같음

### 독립적인 프로그래밍으로 개발 기간 단축

- 계산기를 두 팀이 나눠 구현
  - A팀: 클라이언트를 위한 UI
  - B팀: 계산 로직 구현
- 진행 순서
  1. 구현할 내용 협의 → `Calculator` 인터페이스 확정 (호출 방식·리턴 타입)
  2. A팀은 대충 구현한 **Stub**으로 UI 개발 (UI가 계산 로직을 has a 관계로 보유)
  3. B팀은 정확한 business logic을 `CalculatorImpl`로 구현
  4. `CalculatorStub`을 `CalculatorImpl`로 교체

```java
public class CalculatorTest {
    public static void main(String[] args) {
        CalculatorClient client = new CalculatorClient(new CalculatorStub());
        client.add(3, 4);   // A팀: Stub으로 UI 흐름 먼저 확인

        client = new CalculatorClient(new CalculatorImpl());
        client.add(3, 4);   // B팀 구현 완료 후 교체
    }
}

interface Calculator {                       // 두 팀이 합의한 약속
    int add(int a, int b);
}

class CalculatorStub implements Calculator { // 호출·리턴 타입만 맞춘 임시 구현
    public int add(int a, int b) {
        System.out.printf("파라미터 확인: %d, %d%n", a, b);
        return 0;
    }
}

class CalculatorImpl implements Calculator { // 실제 비즈니스 로직
    public int add(int a, int b) {
        System.out.printf("파라미터 확인: %d, %d%n", a, b);
        return a + b;
    }
}

class CalculatorClient {                     // has a 관계
    private Calculator calcLogic;

    CalculatorClient(Calculator calcLogic) {
        this.calcLogic = calcLogic;
    }

    public void add(int a, int b) {
        System.out.printf("결과: %d+%d=%d%n", a, b, calcLogic.add(a, b));
    }
}

/* 출력
파라미터 확인: 3, 4
결과: 3+4=0
파라미터 확인: 3, 4
결과: 3+4=7
*/
```

- 교재의 `CalculatorClient`는 `Scanner`로 두 정수를 입력받고 필드에서 `new CalculatorStub()`을 직접 생성함 — 위 코드는 실행 확인을 위해 값을 인자로 받고 구현체를 생성자로 넘기도록 바꿈
