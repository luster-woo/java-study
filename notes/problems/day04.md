# D4 연습문제 · 인터페이스·제네릭·enum

그날 배운 문법으로 푸는 문제 3개. 직접 만든 문제라 다른 사이트에는 없다.

- **파일 위치**: `src/practice/D4P1.java` 처럼 문제마다 클래스 하나 (`practice` 패키지)
- **실행**: ▶ 실행 → 아래 콘솔에 예제 입력을 붙여넣고 Enter
- **채점**: 예제와 "추가 테스트"의 출력이 **글자 하나까지 같으면** 통과
- **검토**: 다 풀면 "D4P1 검토해줘"라고 말하기


---

## D4P1 · 결제 수단

**써야 할 문법**: `interface Payment`, 구현 클래스 3개, `enum PayType`

결제 N건을 처리하라. **`Payment` 인터페이스(`long pay(long amount)`)와 결제 수단별 구현 클래스**로 풀고, 결제 수단 구분은 **enum**으로 할 것.

| 수단 | 실제 결제 금액 |
|---|---|
| `CARD` | 금액 + 수수료 3% (수수료는 소수점 버림) |
| `CASH` | 10원 단위 절사 (예: 1,237 → 1,230) |
| `POINT` | 남은 포인트에서 차감하고 금액 그대로 출력. 포인트가 부족하면 `FAIL` (차감 안 함) |

**입력**
- 첫째 줄: 처음 포인트 P (0 ≤ P ≤ 1,000,000,000)
- 둘째 줄: N (1 ≤ N ≤ 1,000)
- 다음 N줄: `수단 금액` (1 ≤ 금액 ≤ 1,000,000,000)

**출력**: 건마다 실제 결제 금액 또는 `FAIL`, 마지막 줄 `POINT LEFT 남은포인트`

**예제 입력 1**

```
5000
4
CARD 10000
CASH 1237
POINT 3000
POINT 2500
```

**예제 출력 1**

```
10300
1230
3000
FAIL
POINT LEFT 2000
```

<details>
<summary>힌트</summary>

`PayType.valueOf("CARD")`로 문자열을 enum으로. POINT 구현 클래스는 남은 포인트를 필드로 가진다. `1000000000 * 3`은 int 범위를 넘는다.

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
0
2
POINT 1
CARD 33
```

출력

```
FAIL
33
POINT LEFT 0
```

**테스트 2** 입력

```
1000000000
3
POINT 1000000000
CARD 1000000000
CASH 9
```

출력

```
1000000000
1030000000
0
POINT LEFT 0
```

**테스트 3** 입력

```
10
2
CASH 10
POINT 10
```

출력

```
10
10
POINT LEFT 0
```

</details>


---

## D4P2 · 제네릭 스택 직접 만들기

**써야 할 문법**: `class MyStack<T>`, 배열 기반, 꽉 차면 2배로 늘리기

**`java.util`의 Stack·Deque·List를 쓰지 않고** 배열로 `MyStack<T>`를 직접 만들어 명령을 처리하라. 값은 문자열이다.

| 명령 | 출력 |
|---|---|
| `push X` | 없음 |
| `pop` | 맨 위 값을 꺼내 출력, 비어 있으면 `EMPTY` |
| `top` | 맨 위 값 출력(꺼내지 않음), 비어 있으면 `EMPTY` |
| `size` | 개수 |
| `empty` | 비어 있으면 `1`, 아니면 `0` |

**입력**
- 첫째 줄: N (1 ≤ N ≤ 100,000)
- 다음 N줄: 명령 (X는 공백 없는 문자열, 길이 10 이하)

**예제 입력 1**

```
7
push java
push spring
top
size
pop
pop
pop
```

**예제 출력 1**

```
spring
2
spring
java
EMPTY
```

<details>
<summary>힌트</summary>

제네릭 배열은 `new T[10]`이 안 된다. `(T[]) new Object[10]`으로 만들고 경고는 무시해도 된다. 늘릴 때는 `Arrays.copyOf`.

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
3
empty
push a
empty
```

출력

```
1
0
```

**테스트 2** 입력

```
6
push 1
push 2
push 3
pop
push 4
top
```

출력

```
3
4
```

**테스트 3** 입력

```
2
top
size
```

출력

```
EMPTY
0
```

</details>


---

## D4P3 · N일 뒤 요일

**써야 할 문법**: `enum Day` (필드·생성자·메서드), `values()`, `ordinal()`, `valueOf()`

요일과 N이 주어지면 N일 뒤의 요일을 출력하라. **enum `Day`에 한글 이름 필드와 `isWeekend()` 메서드**를 넣어서 풀 것.

**입력**
- 첫째 줄: Q (1 ≤ Q ≤ 100)
- 다음 Q줄: `요일 N` (요일은 MON TUE WED THU FRI SAT SUN, 0 ≤ N ≤ 1,000,000,000)

**출력**: 줄마다 `영문 한글`, 주말(SAT·SUN)이면 뒤에 ` WEEKEND`

**예제 입력 1**

```
3
MON 10
FRI 1
SUN 0
```

**예제 출력 1**

```
THU 목요일
SAT 토요일 WEEKEND
SUN 일요일 WEEKEND
```

<details>
<summary>힌트</summary>

`Day.values()[(d.ordinal() + n) % 7]`

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
2
SAT 7
TUE 1000000000
```

출력

```
SAT 토요일 WEEKEND
MON 월요일
```

**테스트 2** 입력

```
1
WED 4
```

출력

```
SUN 일요일 WEEKEND
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
interface Greet {
    default String hi() { return "Hi"; }
}
class Kor implements Greet {
    public String hi() { return "안녕"; }
}
class Eng implements Greet { }

// main
Greet[] gs = { new Kor(), new Eng() };
for (Greet g : gs) System.out.print(g.hi() + " ");
```

<details>
<summary>정답·해설</summary>

```
안녕 Hi 
```

Kor는 default 메서드를 재정의했고, Eng는 재정의하지 않아서 인터페이스의 default 구현을 그대로 쓴다.

</details>

#### 출력 2

```java
enum Size { S, M, L }

// main
Size s = Size.valueOf("M");
System.out.println(s.ordinal() + " " + s.compareTo(Size.L) + " " + Size.values().length);
```

<details>
<summary>정답·해설</summary>

```
1 -1 3
```

ordinal은 0부터. enum의 compareTo는 ordinal 차이(1 - 2)를 돌려준다.

</details>

#### 출력 3

```java
static <T extends Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) >= 0 ? a : b;
}

// main
System.out.println(max(3, 7));
System.out.println(max("apple", "banana"));
System.out.println(max("b", "B"));
```

<details>
<summary>정답·해설</summary>

```
7
banana
b
```

Integer와 String 모두 Comparable을 구현해서 같은 메서드로 비교된다. 문자열은 사전순(유니코드 값)이라 소문자 b(98)가 대문자 B(66)보다 크다.

</details>

### 버그 찾기

#### 버그 1 · 컴파일 에러

```java
interface Shape {
    double area();
}
class Square implements Shape {
    double side = 2;
    double area() { return side * side; }
}
```

<details>
<summary>정답·해설</summary>

인터페이스 메서드는 자동으로 `public`이다. 구현하면서 접근 범위를 좁힐 수 없어서 에러 ("attempting to assign weaker access privileges"). **`public double area()`**

</details>

#### 버그 2 · 실행 중 에러

```java
List list = new ArrayList();
list.add("a");
list.add(1);
String s = (String) list.get(1);
```

<details>
<summary>정답·해설</summary>

제네릭 타입을 안 쓴 raw type이라 아무거나 들어가고, 꺼낼 때 `ClassCastException`이 난다. **`List<String> list = new ArrayList<>();`**로 쓰면 `list.add(1)`에서 컴파일러가 막아 준다. 제네릭을 쓰는 이유가 이것이다.

</details>

### 빈칸 채우기

#### 빈칸 1 · 제네릭 스택

```java
class MyStack<T> {
    @SuppressWarnings("unchecked")
    private T[] data = (T[]) new Object[2];
    private int size = 0;

    void push(T x) {
        if (size == data.length) data = Arrays.copyOf(data, ____);
        data[____] = x;
    }

    T pop() {
        return data[____];
    }
}
```

<details>
<summary>정답·해설</summary>

`data.length * 2`, `size++`, `--size`

push는 넣고 나서 증가(후위), pop은 먼저 줄이고 꺼내기(전위). 실무 코드라면 pop 전에 비어 있는지 확인하고, 꺼낸 칸을 `null`로 비워 GC가 정리할 수 있게 한다.

</details>

#### 빈칸 2 · 점수 → 등급

```java
enum Grade {
    A(90), B(80), C(0);

    private final int min;

    Grade(int min) { ____ }

    static Grade of(int score) {
        for (Grade g : ____) {
            if (score >= g.min) return g;
        }
        return C;
    }
}
// Grade.of(85) → B
```

<details>
<summary>정답·해설</summary>

`this.min = min;`, `values()`

`values()`는 선언 순서(A, B, C)로 돌려준다. 높은 등급부터 선언해 둔 덕분에 처음 만족하는 것이 답이다.

</details>
