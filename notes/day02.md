# D2 · 클래스와 객체, JVM 메모리, static

## 오늘 볼 강의

| 순서 | 반 | 강의 |
|---|---|---|
| 1 | 전공 | [추상화] 객체지향 프로그래밍 |
| 2 | 전공 | [추상화] 변수 |
| 3 | 전공 | [추상화] 메서드 |
| 4 | 전공 | [추상화] 생성자 |
| 5 | 비전공 | [객체배열관리] JVM 메모리구조 |
| 6 | 비전공 | [객체배열관리] static 키워드 |

---

## 1. 클래스와 객체

- **클래스** = 설계도. "계좌에는 주인과 잔액이 있고, 입금·출금을 할 수 있다."
- **객체(인스턴스)** = 설계도로 찍어 낸 실물. "규의 계좌", "윤우의 계좌"
- 클래스 하나로 객체를 여러 개 만들 수 있고, 각 객체는 **자기 데이터를 따로** 가진다.

| Python | Java |
|---|---|
| `class Account:` | `public class Account {` |
| `def __init__(self, owner):` | `public Account(String owner) {` (생성자) |
| `self.owner = owner` | `this.owner = owner;` |
| `def deposit(self, money):` | `public void deposit(int money) {` |
| `acc = Account("규")` | `Account acc = new Account("규");` |
| 속성을 아무 때나 추가 가능 | **필드는 클래스에 미리 선언**해야 함 |

```java
// Account.java
public class Account {
    // 필드 (= 인스턴스 변수). 객체마다 따로 가짐
    String owner;
    int balance;

    // 생성자: 객체를 만들 때 한 번 실행. 이름이 클래스와 같고 반환 타입이 없음
    public Account(String owner, int balance) {
        this.owner = owner;      // this = 지금 만들어지는 객체 자신 (Python의 self)
        this.balance = balance;
    }

    // 메서드
    public void deposit(int money) {
        balance += money;
    }

    public boolean withdraw(int money) {
        if (money > balance) {
            return false;
        }
        balance -= money;
        return true;
    }

    public int getBalance() {
        return balance;
    }
}
```

```java
// AccountTest.java
public class AccountTest {
    public static void main(String[] args) {
        Account a = new Account("규", 1000);
        Account b = new Account("윤우", 5000);

        a.deposit(500);
        System.out.println(a.getBalance());   // 1500
        System.out.println(b.getBalance());   // 5000 (a와 별개)
        System.out.println(a.withdraw(9999)); // false
    }
}
```

## 2. 메서드

```
접근제한자  반환타입  이름(매개변수타입 이름, ...) {
    ...
    return 값;
}
```

- 반환할 게 없으면 `void`.
- **반환 타입과 매개변수 타입을 꼭 써야** 한다. Python처럼 아무거나 받을 수 없다.

### 오버로딩 (overloading)

**같은 이름, 다른 매개변수**로 메서드를 여러 개 만드는 것. Python에는 없다(기본값 인자로 흉내 냄).

```java
public class Calculator {
    int add(int a, int b) { return a + b; }
    double add(double a, double b) { return a + b; }
    int add(int a, int b, int c) { return a + b + c; }
    // 반환 타입만 다른 건 오버로딩이 아님 (컴파일 에러)

    public static void main(String[] args) {
        Calculator c = new Calculator();
        System.out.println(c.add(1, 2));       // 3
        System.out.println(c.add(1.5, 2.5));   // 4.0
        System.out.println(c.add(1, 2, 3));    // 6
    }
}
```

### 값 전달 — Java는 항상 "값을 복사해서" 넘긴다

```java
public class PassTest {
    static void changeInt(int x) { x = 100; }
    static void changeArr(int[] arr) { arr[0] = 100; }

    public static void main(String[] args) {
        int n = 1;
        changeInt(n);
        System.out.println(n);       // 1 (복사본만 바뀜)

        int[] a = {1, 2, 3};
        changeArr(a);
        System.out.println(a[0]);    // 100 (참조값을 복사해 넘겨서, 같은 배열을 고침)
    }
}
```
기본형은 값이, 참조형은 **주소(참조)가 복사**된다. 그래서 메서드 안에서 배열·객체 내용을 바꾸면 밖에서도 보인다. Python과 결과가 같다.

## 3. 생성자

- 생성자를 하나도 안 만들면 Java가 **매개변수 없는 기본 생성자**를 자동으로 넣어 준다.
- **하나라도 직접 만들면 기본 생성자는 사라진다.** → `new Account()` 하면 에러.
- `this(...)`로 같은 클래스의 다른 생성자를 부를 수 있다 (반드시 첫 줄).

```java
public class Member {
    String name;
    int age;

    public Member() {
        this("이름없음", 0);         // 아래 생성자 호출
    }

    public Member(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

## 4. JVM 메모리 구조

| 영역 | 무엇이 들어가나 | 언제 사라지나 |
|---|---|---|
| **Method(메서드) 영역** | 클래스 정보, **static 변수**, 메서드 코드 | 프로그램 끝날 때 |
| **Heap** | `new`로 만든 **객체와 배열** | 아무도 참조하지 않으면 GC가 정리 |
| **Stack** | 메서드 호출마다 생기는 칸 (지역 변수, 매개변수) | 메서드가 끝나면 바로 |

```java
Account a = new Account("규", 1000);
```
- `new Account(...)` → **Heap**에 객체가 생김
- `a` → **Stack**에 있는 지역 변수. 안에는 Heap 객체의 **주소**가 들어 있음

그래서 `Account c = a;` 하면 객체가 복사되는 게 아니라 **같은 객체를 가리키는 변수가 하나 더** 생긴다.

> 위 표는 JVM 명세의 **개념상** 구분이다. 실제 HotSpot JVM(Java 8+)에서는 클래스 정보가 **Metaspace**에, static 변수 값은 **Heap**(Class 객체 쪽)에 저장된다. 면접에서 "Java 8부터 PermGen이 Metaspace로 바뀌었다"까지 말하면 플러스.

**GC(Garbage Collector)**: Heap에서 더 이상 아무 변수도 가리키지 않는 객체를 자동으로 지워 준다. Python도 비슷하게 자동으로 메모리를 관리한다.

## 5. static

`static`이 붙으면 **객체가 아니라 클래스에 속한다.** 객체를 몇 개 만들든 **하나만** 존재한다.

```java
public class Counter {
    static int total = 0;   // 클래스 변수: 모든 객체가 공유
    int count = 0;          // 인스턴스 변수: 객체마다 따로

    public Counter() {
        total++;
        count++;
    }

    static void printTotal() {           // static 메서드: 객체 없이 호출
        System.out.println("total = " + total);
        // System.out.println(count);    // 에러! static 메서드는 인스턴스 변수를 못 씀
    }

    public static void main(String[] args) {
        Counter c1 = new Counter();
        Counter c2 = new Counter();
        Counter c3 = new Counter();
        System.out.println(c1.count);    // 1
        System.out.println(c3.count);    // 1
        Counter.printTotal();            // total = 3
    }
}
```

| | 인스턴스 변수 | static 변수 |
|---|---|---|
| 소속 | 객체 | 클래스 |
| 개수 | 객체마다 1개 | 딱 1개 |
| 메모리 | Heap | Method 영역 (개념상) |
| 접근 | `객체.변수` | `클래스.변수` |
| Python으로 치면 | `self.x` | 클래스 본문에 쓴 `x = 0` |

**왜 `main`은 static인가?** 프로그램이 시작할 때는 아직 아무 객체도 없다. 객체 없이 바로 호출할 수 있어야 하니까 static이다.

**언제 static을 쓰나?** `Math.max()`, `Integer.parseInt()`처럼 객체 상태와 상관없는 도구 메서드, 그리고 상수(`static final`).

---

## 자주 하는 실수

| 실수 | 증상 | 고치기 |
|---|---|---|
| `main`(static)에서 인스턴스 메서드 바로 호출 | `Non-static method cannot be referenced from a static context` | 객체를 만들어서 `obj.method()`, 또는 메서드를 `static`으로 |
| 생성자 만든 뒤 `new Account()` | 기본 생성자 없음 에러 | 매개변수 없는 생성자도 직접 만들기 |
| 생성자에 `void`를 붙임 | 생성자가 아니라 일반 메서드가 돼 버림 | 생성자는 반환 타입 없음 |
| `this.` 빼먹기 | `owner = owner;` → 필드에 안 들어감 | `this.owner = owner;` |
| `null`인 변수로 메서드 호출 | `NullPointerException` | `new`로 객체를 만들었는지 확인 |

**팁**: 생성자·getter는 `Alt + Insert`로 자동 생성.

---

## 면접 질문

1. **클래스와 객체(인스턴스)의 차이는?**
   클래스는 객체를 만들기 위한 설계도이고, 객체는 그 설계도로 메모리(Heap)에 만들어진 실체다.
2. **JVM 메모리 구조를 설명해 주세요.**
   클래스 정보와 static 변수가 있는 Method 영역, `new`로 만든 객체가 있는 Heap, 메서드 호출마다 지역 변수가 쌓이는 Stack으로 나뉜다. Heap은 GC가 정리한다. (HotSpot에서는 Java 8부터 Method 영역이 Metaspace로 구현되고, static 변수 값은 Heap에 있다)
3. **static은 언제 쓰나요?**
   객체마다 다를 필요가 없는 값이나, 객체 상태 없이 동작하는 유틸 메서드에 쓴다. 객체 없이 클래스 이름으로 접근하고, 모든 객체가 하나를 공유한다.
4. **오버로딩이란?**
   같은 이름의 메서드를 매개변수의 개수나 타입을 다르게 해서 여러 개 정의하는 것.
5. **Java는 Call by Value인가요?**
   그렇다. 항상 값을 복사해 넘긴다. 참조형은 참조(주소)값이 복사되기 때문에 메서드 안에서 객체 내용을 바꾸면 원본에 반영된다.

---

## 오늘의 실습

- [ ] 강의 코드 따라 치기 (`src/day02/`)
- [ ] `Account` 클래스: 필드(주인·잔액), 생성자, `deposit`, `withdraw`(잔액 부족이면 false), `getBalance`
- [ ] `AccountTest`에서 계좌 3개 만들고 입출금해 보기
- [ ] `Account`에 `static int count`를 넣어서 만든 계좌 수 세기
- [ ] 연습문제 3개: [problems/day02.md](problems/day02.md) (`src/practice/`에 풀기)
- [ ] 여유 있으면 SWEA·프로그래머스에서 1~2문제 더
- [ ] 커밋: `day02: 클래스·생성자·static / Account 실습`
