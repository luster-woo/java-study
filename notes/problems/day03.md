# D3 연습문제 · 상속·다형성·equals

그날 배운 문법으로 푸는 문제 3개. 직접 만든 문제라 다른 사이트에는 없다.

- **파일 위치**: `src/practice/D3P1.java` 처럼 문제마다 클래스 하나 (`practice` 패키지)
- **실행**: ▶ 실행 → 아래 콘솔에 예제 입력을 붙여넣고 Enter
- **채점**: 예제와 "추가 테스트"의 출력이 **글자 하나까지 같으면** 통과
- **검토**: 다 풀면 "D3P1 검토해줘"라고 말하기


---

## D3P1 · 도형 넓이

**써야 할 문법**: 부모 `Shape` + 자식 `Circle`·`Rect`·`Triangle`, 오버라이딩, `Shape[]`

도형 N개의 넓이를 각각 출력하고 마지막에 합계를 출력하라. **`Shape` 부모 클래스와 `area()` 오버라이딩**으로 풀 것.

| 입력 | 도형 | 넓이 |
|---|---|---|
| `C r` | 원 | π × r × r (`Math.PI`) |
| `R w h` | 직사각형 | w × h |
| `T b h` | 삼각형 | b × h ÷ 2 |

**입력**
- 첫째 줄: N (1 ≤ N ≤ 100)
- 다음 N줄: 도형 (모든 수는 1 이상 1,000 이하 정수)

**출력**: 도형마다 `CIRCLE 넓이` / `RECT 넓이` / `TRIANGLE 넓이`, 마지막 줄 `TOTAL 합계`. 모두 소수 둘째 자리까지 (`%.2f`)

**예제 입력 1**

```
3
C 1
R 2 3
T 3 3
```

**예제 출력 1**

```
CIRCLE 3.14
RECT 6.00
TRIANGLE 4.50
TOTAL 13.64
```

<details>
<summary>힌트</summary>

`Shape`에 `abstract`를 쓰는 건 D4. 오늘은 부모 `area()`가 0을 반환하게 두고 자식이 오버라이딩해도 된다. 도형 이름도 메서드로.

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
1
C 10
```

출력

```
CIRCLE 314.16
TOTAL 314.16
```

**테스트 2** 입력

```
2
T 1 1
R 1000 1000
```

출력

```
TRIANGLE 0.50
RECT 1000000.00
TOTAL 1000000.50
```

**테스트 3** 입력

```
3
C 2
C 3
T 5 7
```

출력

```
CIRCLE 12.57
CIRCLE 28.27
TRIANGLE 17.50
TOTAL 58.34
```

</details>


---

## D3P2 · 급여 계산

**써야 할 문법**: 부모 `Employee` + 자식 `Manager`·`Intern`, `super(...)`, 오버라이딩, 다형성 배열

직원 N명의 급여를 계산하라. **`Employee` 부모 클래스의 `pay()`를 자식이 오버라이딩**하는 구조로 풀 것.

| 입력 | 직급 | 급여 |
|---|---|---|
| `STAFF 이름 기본급` | 일반 | 기본급 |
| `MANAGER 이름 기본급 팀원수` | 관리자 | 기본급 + 팀원수 × 100,000 |
| `INTERN 이름 기본급` | 인턴 | 기본급 × 80% (소수점 버림) |

**입력**
- 첫째 줄: N (1 ≤ N ≤ 100)
- 다음 N줄: 직원 정보 (기본급 1 ~ 100,000,000, 팀원수 0 ~ 100)

**출력**: 입력 순서대로 `이름 급여`, 마지막 줄 `TOP 급여가 가장 높은 이름` (같으면 먼저 나온 사람)

**예제 입력 1**

```
3
STAFF kim 3000000
MANAGER lee 3500000 4
INTERN park 2000000
```

**예제 출력 1**

```
kim 3000000
lee 3900000
park 1600000
TOP lee
```

<details>
<summary>힌트</summary>

인턴 급여는 `base * 8 / 10` (정수 나눗셈). `0.8`을 곱하면 실수 오차가 생길 수 있다.

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
2
INTERN a 99
STAFF b 79
```

출력

```
a 79
b 79
TOP a
```

**테스트 2** 입력

```
3
STAFF x 100
STAFF y 100
MANAGER z 0 0
```

출력

```
x 100
y 100
z 0
TOP x
```

**테스트 3** 입력

```
1
MANAGER solo 100000000 100
```

출력

```
solo 110000000
TOP solo
```

</details>


---

## D3P3 · 중복 좌표 제거

**써야 할 문법**: `Point`의 `equals`·`toString` 오버라이딩

좌표 N개에서 중복을 제거하라. **`Point` 클래스에 `equals`와 `toString`을 오버라이딩**하고, 이미 담은 점들과 `equals`로 비교해서 풀 것 (HashSet은 쓰지 않는다).

**입력**
- 첫째 줄: N (1 ≤ N ≤ 1,000)
- 다음 N줄: x y (-1,000 ≤ x, y ≤ 1,000)

**출력**
- 첫째 줄: 서로 다른 점의 개수
- 다음 줄부터: 처음 나온 순서대로 `(x, y)` (`toString` 사용)

**예제 입력 1**

```
5
1 2
3 4
1 2
-1 0
3 4
```

**예제 출력 1**

```
3
(1, 2)
(3, 4)
(-1, 0)
```

<details>
<summary>힌트</summary>

`Point[] uniq` 배열과 개수 `cnt`를 두고, 새 점마다 `uniq[0..cnt-1]` 중 `equals`인 게 있는지 확인.

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
1
0 0
```

출력

```
1
(0, 0)
```

**테스트 2** 입력

```
4
2 1
1 2
2 1
1 2
```

출력

```
2
(2, 1)
(1, 2)
```

**테스트 3** 입력

```
3
-1000 1000
1000 -1000
-1000 1000
```

출력

```
2
(-1000, 1000)
(1000, -1000)
```

</details>

---

# 코드 읽기 문제

면접 질문과 필기(IT 직무상식)는 코드를 **읽고** 답하는 형식이 많다. IntelliJ로 실행하지 말고 먼저 손으로 답을 적은 뒤, 정답을 열거나 직접 실행해서 확인한다.

- **출력 예측**: 실행 결과를 정확히 적기 (공백·줄바꿈까지)
- **버그 찾기**: 무엇이 잘못됐는지, 어떻게 고치는지
- **빈칸 채우기**: `____`에 들어갈 코드

### 출력 예측

#### 출력 1

```java
class A {
    void hello() { System.out.print("A "); }
    void call() { hello(); }
}
class B extends A {
    @Override
    void hello() { System.out.print("B "); }
}

// main
A x = new B();
x.call();
new A().call();
```

<details>
<summary>정답·해설</summary>

```
B A 
```

`call()`은 A에만 있지만, 그 안에서 부르는 `hello()`는 **실제 객체**의 것이 실행된다. x는 실제로 B라서 `B`. 이게 다형성의 핵심.

</details>

#### 출력 2

```java
class P { P() { System.out.print("P "); } }
class C extends P { C() { System.out.print("C "); } }
class G extends C { G() { super(); System.out.print("G "); } }

// main
new G();
```

<details>
<summary>정답·해설</summary>

```
P C G 
```

자식 생성자의 첫 줄에는 항상 `super()`가 있다(안 써도 자동으로). 그래서 가장 위 부모부터 실행된다.

</details>

#### 출력 3

```java
class Pt {
    int x;
    Pt(int x) { this.x = x; }
}

// main
Pt p = new Pt(1);
Pt q = new Pt(1);
System.out.println(p == q);
System.out.println(p.equals(q));
List<Pt> list = new ArrayList<>(List.of(p));
System.out.println(list.contains(q));
```

<details>
<summary>정답·해설</summary>

```
false
false
false
```

`equals`를 재정의하지 않으면 Object의 equals는 `==`와 같다. `contains`도 내부에서 equals를 쓰기 때문에 false. 내용으로 비교하려면 equals(와 hashCode)를 재정의해야 한다.

</details>

### 버그 찾기

#### 버그 1 · Dog가 안 나온다

```java
class Animal {
    public String toString() { return "Animal"; }
}
class Dog extends Animal {
    public String tostring() { return "Dog"; }
}

// main
System.out.println(new Dog());   // Animal 출력
```

<details>
<summary>정답·해설</summary>

`tostring`(소문자 s) 오타라 재정의가 아니라 새 메서드가 생겼다. 에러도 안 난다. **`@Override`를 붙이면** 컴파일러가 "재정의할 메서드가 없다"고 바로 잡아 준다. 메서드 이름을 `toString`으로.

</details>

#### 버그 2 · 실행 중 에러

```java
Animal a = new Cat();
Dog d = (Dog) a;
d.fetch();
```

<details>
<summary>정답·해설</summary>

실제 객체가 Cat인데 Dog로 다운캐스팅해서 실행 중 `ClassCastException`. 컴파일은 통과한다. 캐스팅 전에 확인:

```java
if (a instanceof Dog dog) {
    dog.fetch();
}
```

</details>

### 빈칸 채우기

#### 빈칸 1 · equals 재정의

```java
class Point {
    final int x, y;
    Point(int x, int y) { this.x = x; this.y = y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return ____;
        if (!(o instanceof Point p)) return ____;
        return ____;
    }

    @Override
    public int hashCode() { return Objects.hash(x, y); }
}
```

<details>
<summary>정답·해설</summary>

`true`, `false`, `x == p.x && y == p.y`

</details>

#### 빈칸 2 · 관리자 급여

```java
class Employee {
    protected String name;
    protected int base;
    Employee(String name, int base) { this.name = name; this.base = base; }
    int pay() { return base; }
}

class Manager extends Employee {
    int team;
    Manager(String name, int base, int team) {
        ____;
        this.team = team;
    }

    @Override
    int pay() { return ____ + team * 100000; }
}
```

<details>
<summary>정답·해설</summary>

`super(name, base)`, `super.pay()` (또는 `base`)

Employee에 기본 생성자가 없어서 `super(name, base)`를 빼면 컴파일 에러. `super.pay()`를 쓰면 나중에 부모 급여 계산이 바뀌어도 자동으로 따라간다.

</details>
