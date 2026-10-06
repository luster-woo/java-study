# Interface

> 교재: 15기_자바_live_05_interface_0122 (전공)

> [← abstract class](01_abstract_class.md) · [day04 목록](../README.md) · [Interface의 필요성 →](03_Interface의_필요성.md)

## 인터페이스

### 인터페이스란?

- 예) GUI(Graphic User Interface): 프로그램과 사용자 사이의 접점
- 인터페이스를 사이에 두고
  - 한쪽은 **사용**에 관심: 저장 버튼을 누르면 저장되겠지 — 어떻게(How)는 궁금하지 않음
  - 한쪽은 **구현**에 관심: 누르면 실제로 저장해야 함
- 대응 관계

| 화면 | 코드 |
| --- | --- |
| 동작 | 메서드 |
| 누르기 | 실행(호출) |
| 결과 | 리턴 |

- 사용하는 쪽에는 **선언부만** 필요

```java
public abstract void save();          // 사용하는 쪽이 아는 것

public void save() {                  // 구현하는 쪽이 채우는 것
    // 1. 변경 정보 확인
    // 2. OutputStream을 통해 내용 저장
    // 3. 화면상에 변경 완료 표시
}
```

### 인터페이스 작성

- **최고 수준의 추상화** 단계: 일반 메서드는 모두 abstract 형태
  - JDK 8에서 default method, static method 추가
- 형태: 클래스와 비슷하게 `interface`로 선언
- 멤버 구성

| 멤버 | 실제 제한자 | 생략 |
| --- | --- | --- |
| 멤버 변수 | `public static final` | 생략 가능 |
| 메서드 | `public abstract` | 생략 가능 |

```java
public interface MyInterface {
    public static final int MEMBER1 = 10;
    int MEMBER2 = 10;                          // public static final 생략
    public abstract void method1(int param);
    void method2(int param);                   // public abstract 생략
}
```

### 인터페이스 상속

- 클래스처럼 `extends`로 상속 가능
- 클래스와 다른 점: 인터페이스는 **다중 상속 가능**
  - 헷갈릴 메서드 구현 자체가 없기 때문

```java
interface Fightable {
    int fire();
}

interface Transformable {
    void changeShape(boolean isHeroMode);
}

public interface Heroable extends Fightable, Transformable {
    void upgrade();
}
```

### 인터페이스 구현과 객체 참조

- 클래스는 `implements`로 인터페이스 구현
- implements 한 클래스는
  - 모든 abstract 메서드를 override해서 구현하거나
  - 구현하지 않으면 abstract 클래스로 표시
- 여러 인터페이스를 동시에 implements 가능
- **다형성**은 조상 클래스뿐 아니라 **조상 인터페이스**에도 적용

```java
public class IronManTest {
    public static void main(String[] args) {
        IronMan iman = new IronMan();
        Object obj = iman;               // 모든 클래스의 조상
        Heroable hero = iman;            // 조상 인터페이스로 참조 가능
        Fightable fight = iman;
        Transformable trans = iman;

        System.out.println(fight.fire());   // 빔 발사: 100만큼의 데미지를 가함 / 100
        trans.changeShape(true);            // 장갑 장착
        hero.upgrade();                     // 무기 성능 개선
        System.out.println(obj instanceof Fightable);  // true
    }
}

interface Fightable {
    int fire();                      // public abstract 생략
}

interface Transformable {
    void changeShape(boolean isHeroMode);
}

interface Heroable extends Fightable, Transformable {   // 인터페이스는 다중 상속 가능
    void upgrade();
}

class IronMan implements Heroable {
    int weaponDamage = 100;

    @Override
    public int fire() {
        System.out.printf("빔 발사: %d만큼의 데미지를 가함%n", weaponDamage);
        return this.weaponDamage;
    }

    @Override
    public void changeShape(boolean isHeroMode) {
        String status = isHeroMode ? "장착" : "제거";
        System.out.printf("장갑 %s%n", status);
    }

    @Override
    public void upgrade() {
        System.out.println("무기 성능 개선");
    }
}
```

> 참고: 교재 `fire()`의 `printf("빔 발사: %d만큼의 데미지를 가함%n")`는 `%d`에 넣을 값이 없어 실행 시 `MissingFormatArgumentException`이 남. `weaponDamage`를 인자로 넘겨야 함. `upgrade()`의 닫는 따옴표도 `”`(전각)로 되어 있어 컴파일 에러 — `"`로 고침.

- 인터페이스와의 관계도 is a 관계이지만 더 세부적으로 **is able to** 라고도 함
  - ~할 수 있는 기능을 가지게 되는 관계
  - 예) `Serializable`, `Cloneable`, `Comparable`

---

## 참고

### abstract class vs interface (교재 내용 정리)

| 구분 | abstract class | interface |
| --- | --- | --- |
| 선언 | `abstract class` | `interface` |
| 객체 생성 | 불가 | 불가 |
| 멤버 변수 | 일반 필드 가능 | `public static final`만 |
| 메서드 | 일반 메서드 + abstract 메서드 | abstract + default·static(8~) + private(9~) |
| 사용 | `extends` (단일 상속) | `implements` (여러 개 가능), 인터페이스끼리는 `extends` 다중 상속 |
| 관계 | is a | is able to |

- 교재가 abstract method design pattern이라 부른 형태는 GoF 패턴 목록에 같은 이름이 없음. 조상이 공통 기능을 갖고 일부 단계만 자손에게 맡기는 구조는 템플릿 메서드(Template Method) 패턴과 가까움
- `String.formatted()`는 Java 15부터 쓸 수 있음. 그 전 버전에서는 `String.format("%d %c %d = %d", ...)` 사용
