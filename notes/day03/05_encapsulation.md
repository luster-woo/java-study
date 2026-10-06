# encapsulation

> 교재: 15기_자바_live_04_polymorphism_0121_오후 (전공)

> [← 제한자](04_제한자.md) · [day03 목록](../README.md) · [polymorphism →](06_polymorphism.md)

### 객체지향 언어의 특징 — OOP is A P.I.E

| 특성 | 내용 |
|---|---|
| **A**bstraction(추상화) | 현실의 객체를 추상화해서 클래스를 구성함 |
| **P**olymorphism(다형성) | 하나의 객체를 여러 가지 타입(형)으로 참조할 수 있음 |
| **I**nheritance(상속) | 부모 클래스의 자산을 물려받아 자식을 정의 → 코드 재사용 |
| **E**ncapsulation(데이터 은닉과 보호) | 데이터를 외부에 직접 노출하지 않고 메서드로 보호함 |

### 누군가 내 정보를 마음대로 바꾼다면?

```java
public class UnbelievableTest {
    public static void main(String[] args) {
        UnbelievableUserInfo info = new UnbelievableUserInfo();
        System.out.printf("사용자 정보:%s, %d%n", info.name, info.account);  // 사용자 정보:홍길동, 10000
        info.name = null;
        info.account = -1000;
        System.out.printf("사용자 정보:%s, %d%n", info.name, info.account);  // 사용자 정보:null, -1000
    }
}

class UnbelievableUserInfo {
    public String name = "홍길동";   // 이름은 not null
    public int account = 10000;      // 계좌는 0보다 커야 함
}
```

- 보호되지 못하는 이유: 외부에서 변수에 **직접 접근**하기 때문
- 대책
  - 변수는 **`private`** 접근으로 막음
  - 공개된 메서드로 접근 통로 마련: **setter / getter**
  - 메서드 안에 **정보 보호 로직** 작성

### 데이터가 보호되도록 고치기

```java
public class BelievableTest {
    public static void main(String[] args) {
        BelievableUserInfo info = new BelievableUserInfo();
        // info.name = null;   // 컴파일 에러: private
        System.out.printf("사용자 정보:%s, %d%n", info.getName(), info.getAccount());
        info.setName(null);
        info.setAccount(-10000);
        System.out.printf("사용자 정보:%s, %d%n", info.getName(), info.getAccount());
    }
}

class BelievableUserInfo {
    private String name = "홍길동";
    private int account = 10000;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        } else {
            System.out.println("부적절한 name 할당 시도 무시: " + name);
        }
    }

    public int getAccount() {
        return this.account;
    }

    public void setAccount(int account) {
        if (account > 0) {
            this.account = account;
        } else {
            System.out.println("부적절한 account 할당 시도 무시: " + account);
        }
    }
}
/* 출력
사용자 정보:홍길동, 10000
부적절한 name 할당 시도 무시: null
부적절한 account 할당 시도 무시: -10000
사용자 정보:홍길동, 10000
*/
```

### 객체의 생성 제어

- 객체 생성을 제한해야 하는 경우
  - 여러 개의 객체가 필요 없는 경우
    - 객체를 **구별할 필요가 없는** 경우 = 수정 가능한 멤버 변수(상태, state)가 없고 **기능만** 있는 경우
    - 이런 객체를 **stateless**한 객체라고 함 (홍길동·성춘향·이몽룡처럼 구별되는 객체와 대비)
  - 객체를 계속 생성/삭제하는 데 비용이 많이 들어 **재사용이 유리**한 경우

### Singleton 디자인 패턴

- 작성 방법
  1. 외부에서 생성자 접근 금지 → 생성자의 접근 제한자를 **`private`**
  2. 내부에서는 private에 접근 가능 → 생성자를 호출해 객체 생성, 멤버 변수이므로 `private`
  3. 외부에서 private 멤버에 접근할 **getter** 생성 (setter는 불필요)
  4. 객체 없이 외부에서 접근하도록 getter와 변수에 **`static`** 추가
- 외부에서는 언제나 getter로 객체를 참조 → **하나의 객체 재사용**

```java
public class SingletonTest {
    public static void main(String[] args) {
        // SingletonClass sc = new SingletonClass();  // 컴파일 에러: 생성자가 private
        SingletonClass sc1 = SingletonClass.getInstance();
        SingletonClass sc2 = SingletonClass.getInstance();
        System.out.printf("두 객체는 같은가? %b%n", sc1 == sc2);  // 두 객체는 같은가? true
        sc1.sayHello();                                          // Hello
    }
}

class SingletonClass {
    private static SingletonClass instance = new SingletonClass();  // 2. 내부에서 생성, 4. static

    private SingletonClass() {}                                     // 1. 외부 생성 금지

    public static SingletonClass getInstance() {                    // 3. getter, 4. static
        return instance;
    }

    public void sayHello() {
        System.out.println("Hello");
    }
}
```
