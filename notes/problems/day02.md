# D2 연습문제 · 클래스와 객체, static

그날 배운 문법으로 푸는 문제 3개. 직접 만든 문제라 다른 사이트에는 없다.

- **파일 위치**: `src/practice/D2P1.java` 처럼 문제마다 클래스 하나 (`practice` 패키지)
- **실행**: ▶ 실행 → 아래 콘솔에 예제 입력을 붙여넣고 Enter
- **채점**: 예제와 "추가 테스트"의 출력이 **글자 하나까지 같으면** 통과
- **검토**: 다 풀면 "D2P1 검토해줘"라고 말하기


---

## D2P1 · 은행 창구

**써야 할 문법**: `Account` 클래스(private 필드·생성자·메서드), `static` 카운터, 객체 배열

은행 명령 Q개를 처리하라. **`Account` 클래스를 직접 만들어서** 풀어야 한다 (이름·잔액은 private 필드, 입금·출금은 메서드).

| 명령 | 동작 | 출력 |
|---|---|---|
| `OPEN 이름 금액` | 계좌 개설 | `OK`, 이미 있으면 `EXISTS` |
| `DEP 이름 금액` | 입금 | 입금 후 잔액 |
| `WD 이름 금액` | 출금 | 출금 후 잔액, 잔액보다 크면 `FAIL` (잔액 그대로) |
| `BAL 이름` | 조회 | 잔액 |

없는 이름으로 DEP·WD·BAL을 하면 `NO ACCOUNT`. 모든 명령을 처리한 뒤 마지막 줄에 `TOTAL 개설된 계좌 수`를 출력한다 (**`Account`의 static 변수로 셀 것**).

**입력**
- 첫째 줄: Q (1 ≤ Q ≤ 1,000)
- 다음 Q줄: 명령. 이름은 영문 소문자 10자 이하, 금액은 1 이상 1,000,000,000 이하

**예제 입력 1**

```
7
OPEN gyu 1000
DEP gyu 500
WD gyu 2000
WD gyu 300
BAL kim
OPEN gyu 5
BAL gyu
```

**예제 출력 1**

```
OK
1500
FAIL
1200
NO ACCOUNT
EXISTS
1200
TOTAL 1
```

<details>
<summary>힌트</summary>

HashMap은 D5에서 배운다. 지금은 `Account[]` 배열에 넣고 이름으로 하나씩 찾자. 잔액이 21억을 넘을 수 있다.

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
5
OPEN a 1000000000
DEP a 1000000000
DEP a 1000000000
OPEN b 1
BAL a
```

출력

```
OK
2000000000
3000000000
OK
3000000000
TOTAL 2
```

**테스트 2** 입력

```
4
BAL x
OPEN x 10
WD x 10
BAL x
```

출력

```
NO ACCOUNT
OK
0
0
TOTAL 1
```

**테스트 3** 입력

```
6
OPEN p 5
OPEN q 7
WD p 6
DEP q 3
OPEN p 1
BAL q
```

출력

```
OK
OK
FAIL
10
EXISTS
10
TOTAL 2
```

</details>


---

## D2P2 · 가장 가까운 두 점

**써야 할 문법**: `Point` 클래스, 인스턴스 메서드 `distSq(Point other)`

평면 위의 점 N개 중 **거리가 가장 가까운 두 점**을 찾아라. **`Point` 클래스와 `int distSq(Point other)` 메서드**(거리의 제곱)를 만들어서 쓴다.

**입력**
- 첫째 줄: N (2 ≤ N ≤ 1,000)
- 다음 N줄: x y (-10,000 ≤ x, y ≤ 10,000)

**출력**
- 첫째 줄: 가장 가까운 거리의 **제곱**
- 둘째 줄: 그 두 점의 번호 i j (입력 순서, 1부터, i < j). 같은 거리가 여러 쌍이면 i가 작은 쌍, 그것도 같으면 j가 작은 쌍

**예제 입력 1**

```
4
0 0
5 5
1 1
10 0
```

**예제 출력 1**

```
2
1 3
```

<details>
<summary>힌트</summary>

이중 for문 `for (int j = i + 1; ...)`. 거리 제곱은 최대 8억이라 int로 충분하다.

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
2
-10000 -10000
10000 10000
```

출력

```
800000000
1 2
```

**테스트 2** 입력

```
5
0 0
3 4
0 0
7 7
3 4
```

출력

```
0
1 3
```

**테스트 3** 입력

```
3
1 2
4 6
1 5
```

출력

```
9
1 3
```

</details>


---

## D2P3 · 시계

**써야 할 문법**: `Time` 클래스, 생성자, `plus(int minutes)`, `toString()`, `String.format("%02d")`

시작 시각과 Q개의 경과 시간(분)이 주어진다. 경과 시간을 **차례로 누적해서** 더한 시각을 매번 출력하라. 24시를 넘으면 다음 날로 넘어간다(시각만 출력).

`Time` 클래스(시·분 필드, `plus` 메서드, `HH:MM` 형식의 `toString`)를 만들어서 풀 것.

**입력**
- 첫째 줄: `HH:MM` (00:00 ~ 23:59)
- 둘째 줄: Q (1 ≤ Q ≤ 100)
- 다음 Q줄: 경과 분 (0 ≤ 분 ≤ 100,000)

**출력**: Q줄, `HH:MM`

**예제 입력 1**

```
23:30
3
20
15
1440
```

**예제 출력 1**

```
23:50
00:05
00:05
```

<details>
<summary>힌트</summary>

시각을 '자정부터 몇 분'으로 바꿔서 계산하면 쉽다. `"10:05".split(":")`

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
00:00
2
0
59
```

출력

```
00:00
00:59
```

**테스트 2** 입력

```
12:45
2
100000
75
```

출력

```
23:25
00:40
```

**테스트 3** 입력

```
09:05
1
1435
```

출력

```
09:00
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
class Counter {
    static int total = 0;
    int count = 0;
    Counter() { total++; count++; }
}

// main
Counter a = new Counter();
Counter b = new Counter();
Counter c = b;
c.count += 10;
System.out.println(a.count + " " + b.count + " " + Counter.total);
```

<details>
<summary>정답·해설</summary>

```
1 11 2
```

`c = b`는 객체 복사가 아니라 **같은 객체를 가리키는 변수가 하나 더** 생긴 것. 그래서 b.count가 11. `new`는 두 번뿐이라 total은 2.

</details>

#### 출력 2

```java
static void change(int[] arr, int n) {
    arr[0] = 99;
    n = 99;
    arr = new int[]{0, 0, 0};
    arr[1] = 99;
}

// main
int[] a = {1, 2, 3};
int n = 1;
change(a, n);
System.out.println(Arrays.toString(a) + " " + n);
```

<details>
<summary>정답·해설</summary>

```
[99, 2, 3] 1
```

참조값이 복사돼 넘어가서 `arr[0] = 99`는 원본을 바꾼다. 하지만 `arr = new int[]...`는 **지역 변수 arr만** 새 배열을 가리키게 할 뿐 원본 a와는 무관. 기본형 n은 복사본만 바뀐다.

</details>

#### 출력 3

```java
class Box {
    int w, h;
    Box() { this(1); System.out.print("A"); }
    Box(int s) { this(s, s); System.out.print("B"); }
    Box(int w, int h) { this.w = w; this.h = h; System.out.print("C"); }
}

// main
Box b = new Box();
System.out.println(" " + b.w * b.h);
```

<details>
<summary>정답·해설</summary>

```
CBA 1
```

`this(...)` 호출이 먼저 끝까지 내려간 뒤에 각 생성자의 나머지 줄이 실행된다. 가장 안쪽(C)부터 출력. 마지막은 `" " + (1 * 1)` — `*`가 `+`보다 먼저라 " 1".

</details>

### 버그 찾기

#### 버그 1 · 이름이 저장되지 않음

```java
class Member {
    String name;
    Member(String name) {
        name = name;
    }
}

// main
System.out.println(new Member("규").name);   // null 출력
```

<details>
<summary>정답·해설</summary>

`name = name;`은 매개변수 name에 자기 자신을 넣는 코드라 필드는 그대로 null. **`this.name = name;`**

</details>

#### 버그 2 · 컴파일 에러

```java
public class Calc {
    int add(int a, int b) { return a + b; }

    public static void main(String[] args) {
        System.out.println(add(1, 2));
    }
}
```

<details>
<summary>정답·해설</summary>

static인 main에서 인스턴스 메서드 `add`를 객체 없이 호출할 수 없다. 둘 중 하나:
- `static int add(...)`로 바꾸기
- `new Calc().add(1, 2)`

</details>

### 빈칸 채우기

#### 빈칸 1 · 두 점 거리의 제곱

```java
class Point {
    int x, y;
    Point(int x, int y) { this.x = x; this.y = y; }

    int distSq(Point other) {
        int dx = ____;
        int dy = ____;
        return ____;
    }
}
```

<details>
<summary>정답·해설</summary>

`this.x - other.x` (또는 `x - other.x`), `this.y - other.y`, `dx * dx + dy * dy`

</details>

#### 빈칸 2 · 회원 번호 자동 발급

```java
class Member {
    private static int nextId = 1;
    private final int id;

    Member() {
        this.id = ____;
    }
    int getId() { return id; }
}
// new Member()를 세 번 하면 id가 1, 2, 3
```

<details>
<summary>정답·해설</summary>

`nextId++`

후위 증가라 현재 값을 id에 넣고 나서 nextId가 1 늘어난다. static이라 모든 Member가 같은 nextId를 공유한다.

</details>
