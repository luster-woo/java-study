# D6 연습문제 · 예외·String·record

그날 배운 문법으로 푸는 문제 3개. 직접 만든 문제라 다른 사이트에는 없다.

- **파일 위치**: `src/practice/D6P1.java` 처럼 문제마다 클래스 하나 (`practice` 패키지)
- **실행**: ▶ 실행 → 아래 콘솔에 예제 입력을 붙여넣고 Enter
- **채점**: 예제와 "추가 테스트"의 출력이 **글자 하나까지 같으면** 통과
- **검토**: 다 풀면 "D6P1 검토해줘"라고 말하기


---

## D6P1 · 안전한 계산기

**써야 할 문법**: `try-catch`, `NumberFormatException`, 사용자 정의 예외(`DivideByZero`·`UnknownOp`)

`a 연산자 b` 형식의 식 N개를 계산하라. 잘못된 입력은 **예외로 처리**해서 아래 문구를 출력한다. 프로그램이 중간에 멈추면 안 된다.

| 상황 | 출력 |
|---|---|
| a 또는 b가 정수가 아님 | `INVALID NUMBER` |
| 연산자가 `+ - * /`가 아님 | `UNKNOWN OP` |
| 0으로 나누기 | `DIVIDE BY ZERO` |
| 정상 | 결과 (나눗셈은 Java 정수 나눗셈: 소수점 아래 버림) |

검사 순서: 숫자 → 연산자 → 0 나누기.

**입력**
- 첫째 줄: N (1 ≤ N ≤ 100)
- 다음 N줄: `a op b` (공백 구분, 정상 숫자는 -1,000,000,000 ~ 1,000,000,000)

**예제 입력 1**

```
5
10 + 20
7 / 2
3 % 2
12x * 3
5 / 0
```

**예제 출력 1**

```
30
3
UNKNOWN OP
INVALID NUMBER
DIVIDE BY ZERO
```

<details>
<summary>힌트</summary>

`Long.parseLong`이 실패하면 `NumberFormatException`. `long` 곱셈이라 10억 × 10억도 담긴다.

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
4
-7 / 2
1000000000 * 1000000000
- - 1
0 / -5
```

출력

```
-3
1000000000000000000
INVALID NUMBER
0
```

**테스트 2** 입력

```
2
3.5 + 1
-0 - 0
```

출력

```
INVALID NUMBER
0
```

</details>


---

## D6P2 · 문자열 압축과 복원

**써야 할 문법**: `StringBuilder`, `charAt`, `Character.isDigit`

첫째 줄 문자열을 압축하고, 둘째 줄 압축 문자열을 복원하라.

- 압축: 같은 문자가 연속되면 `문자+개수`로. 1개여도 개수를 쓴다. 예: `aaabcc` → `a3b1c2`
- 복원: 그 반대. 개수는 여러 자리일 수 있다. 예: `x12y1` → `xxxxxxxxxxxxy`

**입력**
- 첫째 줄: 영문 소문자 문자열 (길이 1 ~ 100,000)
- 둘째 줄: 압축 문자열 (복원 결과 길이 100,000 이하)

**출력**: 첫째 줄 압축 결과, 둘째 줄 복원 결과

**예제 입력 1**

```
aaabcc
x12y1
```

**예제 출력 1**

```
a3b1c2
xxxxxxxxxxxxy
```

<details>
<summary>힌트</summary>

문자열을 `+=`로 붙이면 길이 10만에서 매우 느리다. 반드시 `StringBuilder`.

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
z
a1
```

출력

```
z1
a
```

**테스트 2** 입력

```
abab
b2a3b1
```

출력

```
a1b1a1b1
bbaaab
```

**테스트 3** 입력

```
kkkkkkkkkkkk
q10
```

출력

```
k12
qqqqqqqqqq
```

</details>


---

## D6P3 · 로그 분석

**써야 할 문법**: `record LogEntry`, `split(" ", 4)`, 잘못된 줄은 예외로 건너뛰기, 문자열 비교 `compareTo`

서버 로그 N줄을 분석하라. 정상 줄 형식은 `YYYY-MM-DD HH:MM:SS 레벨 메시지`이고, 레벨은 `INFO`, `WARN`, `ERROR` 중 하나다. 메시지에는 공백이 들어갈 수 있다. 로그는 **시간순이 아닐 수 있다.**

각 줄을 **`record LogEntry(String time, String level, String message)`**로 바꿔서 처리하고, 칸이 4개보다 적거나 레벨이 이상한 줄은 건너뛰고 센다.

**출력** (순서 고정)
```
INFO 개수
WARN 개수
ERROR 개수
SKIPPED 건너뛴 줄 수
LAST ERROR: 가장 늦은 시각의 ERROR 메시지 (없으면 NONE)
```

**입력**
- 첫째 줄: N (1 ≤ N ≤ 1,000)
- 다음 N줄: 로그

**예제 입력 1**

```
5
2026-10-06 09:00:01 INFO server started
2026-10-06 09:05:10 ERROR db connection failed
2026-10-06 09:03:00 WARN slow query
broken line
2026-10-06 08:59:59 ERROR old error
```

**예제 출력 1**

```
INFO 1
WARN 1
ERROR 2
SKIPPED 1
LAST ERROR: db connection failed
```

<details>
<summary>힌트</summary>

`yyyy-MM-dd HH:mm:ss` 형식은 문자열 비교(`compareTo`)만으로 시간 순서가 맞다. 레벨 검사는 `Level.valueOf`(enum)가 실패하면 `IllegalArgumentException`.

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
2
2026-01-01 00:00:00 DEBUG hi
2026-01-01 00:00:00 INFO
```

출력

```
INFO 0
WARN 0
ERROR 0
SKIPPED 2
LAST ERROR: NONE
```

**테스트 2** 입력

```
3
2026-12-31 23:59:59 ERROR last one
2026-01-01 00:00:00 ERROR first one
2025-06-01 12:00:00 ERROR older
```

출력

```
INFO 0
WARN 0
ERROR 3
SKIPPED 0
LAST ERROR: last one
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
static int test() {
    try {
        System.out.print("try ");
        return 1;
    } finally {
        System.out.print("finally ");
    }
}

// main
System.out.println(test());
```

<details>
<summary>정답·해설</summary>

```
try finally 1
```

return이 있어도 메서드를 빠져나가기 전에 finally가 실행된다. 그다음에 반환값 1이 출력된다.

</details>

#### 출력 2

```java
try {
    System.out.print("A ");
    Integer.parseInt("x");
    System.out.print("B ");
} catch (NumberFormatException e) {
    System.out.print("C ");
} catch (Exception e) {
    System.out.print("D ");
} finally {
    System.out.print("E ");
}
System.out.println("F");
```

<details>
<summary>정답·해설</summary>

```
A C E F
```

예외가 나면 try의 나머지(B)는 건너뛴다. 처음 맞는 catch 하나만 실행되고(D는 안 함), finally 후 다음 줄로 계속 간다.

</details>

#### 출력 3

```java
String s = "abc";
s.concat("def");
s.toUpperCase();
StringBuilder sb = new StringBuilder("abc");
sb.append("def").reverse();
System.out.println(s + " " + sb + " " + sb.length());
```

<details>
<summary>정답·해설</summary>

```
abc fedcba 6
```

String은 불변이라 `concat`·`toUpperCase`는 새 문자열을 만들 뿐, 대입하지 않으면 s는 그대로. StringBuilder는 자기 자신을 바꾼다.

</details>

#### 출력 4

```java
record P(int x, int y) { }

// main
P a = new P(1, 2);
P b = new P(1, 2);
System.out.println(a == b);
System.out.println(a.equals(b));
System.out.println(a);
```

<details>
<summary>정답·해설</summary>

```
false
true
P[x=1, y=2]
```

record는 equals·hashCode·toString을 필드 값 기준으로 자동 생성한다. 그래도 `==`는 여전히 같은 객체인지 비교.

</details>

### 버그 찾기

#### 버그 1 · 문제 세 개

```java
BufferedReader br = new BufferedReader(new FileReader("data.txt"));
try {
    String line = br.readLine();
    int n = Integer.parseInt(line);
} catch (Exception e) {
}
```

<details>
<summary>정답·해설</summary>

1. **빈 catch**: 예외를 삼켜서 실패해도 아무도 모른다.
2. **파일을 안 닫음**: `br.close()`가 없다.
3. **너무 넓은 catch**: `Exception`으로 다 잡아서 무슨 문제인지 구분이 안 된다. (그리고 `new FileReader`는 try 밖이라 파일이 없으면 여기서 잡히지도 않는다. 메서드에 `throws`가 없으면 애초에 컴파일 에러)

```java
try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
    int n = Integer.parseInt(br.readLine());
} catch (IOException e) {
    System.out.println("파일 읽기 실패: " + e.getMessage());
} catch (NumberFormatException e) {
    System.out.println("숫자가 아님");
}
```

</details>

#### 버그 2 · 컴파일 에러

```java
try {
    Files.readString(Path.of("a.txt"));
} catch (Exception e) {
    System.out.println("에러");
} catch (IOException e) {
    System.out.println("파일 에러");
}
```

<details>
<summary>정답·해설</summary>

`IOException`은 `Exception`의 자식이라 앞의 catch에서 이미 다 잡힌다. 뒤 catch는 절대 실행될 수 없어서 컴파일 에러("exception has already been caught"). **자식 예외를 먼저** 쓴다.

</details>

### 빈칸 채우기

#### 빈칸 1 · 파일 한 줄씩 읽기

```java
try (BufferedReader br = ____) {
    String line;
    while ((line = ____) != null) {
        System.out.println(line);
    }
}
```

<details>
<summary>정답·해설</summary>

`new BufferedReader(new FileReader("a.txt"))`, `br.readLine()`

괄호 안에서 연 자원은 블록이 끝날 때 자동으로 닫힌다. catch가 없으므로 이 코드가 든 메서드에는 `throws IOException`이 필요하다.

</details>

#### 빈칸 2 · 사용자 정의 예외

```java
class InsufficientBalanceException extends ____ {
    InsufficientBalanceException(int shortage) {
        ____("잔액이 " + shortage + "원 부족합니다");
    }
}
```

<details>
<summary>정답·해설</summary>

`RuntimeException`, `super`

Unchecked로 만들면 호출하는 쪽마다 throws를 달지 않아도 되고, Spring `@Transactional`도 기본으로 롤백한다. 메시지는 부모 생성자에 넘겨야 `getMessage()`로 꺼낼 수 있다.

</details>
