# abstract class

> 교재: 15기_자바_live_05_interface_0122 (전공)

> [day04 목록](../README.md) · [Interface →](02_Interface.md)

## 추상 클래스

### 차량 관제 시스템 — 문제 상황

- 관리 대상: **디젤차**, **전기차**
- 공통 기능: 연료 주입(`addFuel`), 현재 위치 파악(`reportPosition`)
- 두 클래스를 따로 만들면 `curX`, `curY`, `reportPosition()`이 그대로 중복됨

```java
public class DieselSUV {
    private int curX, curY;
    public void reportPosition() { System.out.printf("현재 위치: (%d, %d)%n", curX, curY); }
    public void addFuel() { System.out.println("주유소에서 급유"); }
}

public class ElectricCar {
    private int curX, curY;                    // 중복
    public void reportPosition() { System.out.printf("현재 위치: (%d, %d)%n", curX, curY); }  // 중복
    public void addFuel() { System.out.println("급속 충전"); }
}
```

### 상속으로 정비

- 공통 모듈을 조상 클래스 `Vehicle`로 올리고 자손은 `addFuel()`만 재정의(override)
- 상속으로 코드 재사용성 향상
- `Vehicle[]`에 담아 반복문 하나로 처리 가능(다형성)

```java
class Vehicle {
    private int curX, curY;
    public void reportPosition() { System.out.printf("현재 위치: (%d, %d)%n", curX, curY); }
    public void addFuel() {
        System.out.println("모든 운송 수단은 연료가 필요");   // 실제로는 아무도 쓰지 않는 코드
    }
}
```

### 조상의 addFuel()은 무의미

- `addFuel()`은 공통 모듈이라 조상 `Vehicle`에 정의하고, 자손이 필요할 때 재정의할 예정
- 그런데 자손이 **반드시 재정의**하므로 `Vehicle`의 구현은 아무도 사용하지 않음
- 그렇다고 `Vehicle`에서 지우면 `Vehicle` 타입으로 `v.addFuel()` 호출 불가 → 다형성 활용 불가
- 해결: 선언부만 남기고 구현부를 없앰
  1. 구현부 `{ }`를 세미콜론 `;`으로 대체
  2. 메서드 선언부에 **`abstract`** 추가 → 추상 메서드
  3. 객체를 만들 수 없는 클래스라는 뜻으로 클래스 선언부에도 `abstract` 추가 → **abstract class**
- 교재에서는 이 형태를 abstract method design pattern이라 부름

```java
public class VehicleTest {
    public static void main(String[] args) {
        // Vehicle v = new Vehicle();  // 컴파일 에러: abstract 클래스는 객체 생성 불가
        Vehicle[] vehicles = { new DieselSUV(), new ElectricCar() };  // 조상 타입으로 참조는 가능
        for (Vehicle v : vehicles) {
            v.addFuel();
            v.reportPosition();
        }
    }
}

abstract class Vehicle {
    private int curX, curY;

    public void reportPosition() {
        System.out.printf("현재 위치: (%d, %d)%n", curX, curY);
    }

    public abstract void addFuel();   // 선언부만 남기고 ; 로 끝냄
}

class DieselSUV extends Vehicle {
    @Override
    public void addFuel() {
        System.out.println("주유소에서 급유");
    }
}

class ElectricCar extends Vehicle {
    @Override
    public void addFuel() {
        System.out.println("급속 충전");
    }
}

/* 출력
주유소에서 급유
현재 위치: (0, 0)
급속 충전
현재 위치: (0, 0)
*/
```

### 추상 클래스의 특징

- abstract 클래스는 **상속 전용** 클래스
  - 자식은 abstract 메서드를 재정의할 책임이 있음
  - 구현부 없는 메서드가 있으므로 **객체 생성 불가**
  - 조상 타입으로 자식 객체를 참조하는 것은 가능(다형성 적용)
- 상속받은 abstract 메서드를 재정의하지 않으면 → 자식 안에 abstract 메서드가 남으므로 자식도 abstract 클래스로 선언해야 함
- UML에서는 abstract를 *이탤릭*으로 표현

```java
// Vehicle v = new Vehicle();   // abstract 클래스는 객체 생성 불가
Vehicle v = new DieselSUV();    // 자식을 참조하는 것은 문제 없음
```

### 추상 클래스를 사용하는 이유

- **구현의 강제**로 프로그램 안정성 향상

| `Vehicle`의 `addFuel()` | `class HorseCart extends Vehicle {}` |
| --- | --- |
| 일반 메서드 | 조상의 `addFuel()`을 그대로 사용 — 재정의를 잊어도 모름 |
| abstract 메서드 | 컴파일 에러 — 반드시 `addFuel()` 재정의 필요 |

```java
// 컴파일 에러: HorseCart is not abstract and does not override abstract method addFuel() in Vehicle
class HorseCart extends Vehicle {}
```

- interface의 메서드 중 구현할 수 있는 것은 abstract 클래스에서 미리 구현해 두어 개발 편의 지원
