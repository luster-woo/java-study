# D1 연습문제 · 기본 문법·배열

그날 배운 문법으로 푸는 문제 3개. 직접 만든 문제라 다른 사이트에는 없다.

- **파일 위치**: `src/practice/D1P1.java` 처럼 문제마다 클래스 하나 (`practice` 패키지)
- **실행**: ▶ 실행 → 아래 콘솔에 예제 입력을 붙여넣고 Enter
- **채점**: 예제와 "추가 테스트"의 출력이 **글자 하나까지 같으면** 통과
- **검토**: 다 풀면 "D1P1 검토해줘"라고 말하기


---

## D1P1 · 합계, 최댓값, 평균

**써야 할 문법**: `long`, 반복문, `String.format("%.2f", ...)`

N개의 양의 정수가 주어진다. 합계, 최댓값, 평균을 출력하라.

**입력**
- 첫째 줄: N (1 ≤ N ≤ 100,000)
- 둘째 줄: 정수 N개 (1 ≤ 각 수 ≤ 1,000,000,000), 공백으로 구분

**출력**
- 첫째 줄: 합계
- 둘째 줄: 최댓값
- 셋째 줄: 평균 (소수점 셋째 자리에서 반올림해 둘째 자리까지)

**예제 입력 1**

```
5
1 2 3 4 5
```

**예제 출력 1**

```
15
5
3.00
```

**예제 입력 2**

```
3
1000000000 1000000000 1000000000
```

**예제 출력 2**

```
3000000000
1000000000
1000000000.00
```

<details>
<summary>힌트</summary>

합계가 21억을 넘을 수 있다. 어떤 타입을 써야 할까?

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
4
7 1 9 2
```

출력

```
19
9
4.75
```

**테스트 2** 입력

```
3
1 1 2
```

출력

```
4
2
1.33
```

**테스트 3** 입력

```
6
10 20 30 40 50 61
```

출력

```
211
61
35.17
```

**테스트 4** 입력

```
1
999999999
```

출력

```
999999999
999999999
999999999.00
```

</details>


---

## D1P2 · 다이아몬드

**써야 할 문법**: 이중 `for`문, `StringBuilder`

N이 주어지면 높이 2N-1의 다이아몬드를 `*`로 출력하라. **줄 끝에 공백을 출력하지 않는다.**

**입력**: N (1 ≤ N ≤ 50)

**출력**: 다이아몬드

**예제 입력 1**

```
3
```

**예제 출력 1**

```
  *
 ***
*****
 ***
  *
```

<details>
<summary>힌트</summary>

i번째 줄의 공백 개수와 별 개수를 i로 표현해 보자.

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
1
```

출력

```
*
```

**테스트 2** 입력

```
2
```

출력

```
 *
***
 *
```

**테스트 3** 입력

```
4
```

출력

```
   *
  ***
 *****
*******
 *****
  ***
   *
```

</details>


---

## D1P3 · 이웃 합 격자

**써야 할 문법**: 2차원 배열, 델타 배열 `dr`·`dc`, 범위 검사

R행 C열 격자가 주어진다. 각 칸에 대해 **상하좌우 이웃 칸의 값의 합**(자기 자신 제외)을 구해 같은 모양으로 출력하라. 격자 밖은 없는 칸이다.

**입력**
- 첫째 줄: R C (1 ≤ R, C ≤ 50)
- 다음 R줄: 각 줄에 C개의 정수 (0 ≤ 값 ≤ 100)

**출력**: R줄, 각 줄에 C개의 정수를 공백으로 구분

**예제 입력 1**

```
3 3
1 2 3
4 5 6
7 8 9
```

**예제 출력 1**

```
6 9 8
13 20 17
12 21 14
```

<details>
<summary>힌트</summary>

`int[] dr = {-1, 1, 0, 0}; int[] dc = {0, 0, -1, 1};`

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
1 1
5
```

출력

```
0
```

**테스트 2** 입력

```
2 4
1 0 0 1
0 1 1 0
```

출력

```
0 2 2 0
2 1 1 2
```

**테스트 3** 입력

```
1 5
3 1 4 1 5
```

출력

```
1 7 2 9 1
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
int a = 7, b = 2;
double c = a / b;
System.out.println(c);
System.out.println(a / (double) b);
System.out.println(a % b + a / b);
```

<details>
<summary>정답·해설</summary>

```
3.0
3.5
4
```

`a / b`는 정수끼리라 3이 먼저 계산되고, 그다음 double에 들어가 `3.0`. 하나라도 double로 바꾸면 실수 나눗셈. 마지막은 `1 + 3`.

</details>

#### 출력 2

```java
int i = 3;
int j = i++ + ++i;
System.out.println(i + " " + j);
```

<details>
<summary>정답·해설</summary>

```
5 8
```

`i++`는 3을 쓰고 i가 4가 된다. `++i`는 i를 5로 만들고 5를 쓴다. j = 3 + 5.

</details>

#### 출력 3

```java
String s = "Java";
System.out.println(s + 1 + 2);
System.out.println(1 + 2 + s);
System.out.println('A' + 1);
System.out.println("" + 'A' + 1);
```

<details>
<summary>정답·해설</summary>

```
Java12
3Java
66
A1
```

`+`는 왼쪽부터 계산한다. 문자열이 먼저 나오면 그 뒤는 전부 이어붙이기, 숫자끼리 먼저 만나면 덧셈. `char`는 숫자(A=65)로 계산된다.

</details>

#### 출력 4

```java
int x = 2;
switch (x) {
    case 1: System.out.print("one ");
    case 2: System.out.print("two ");
    case 3: System.out.print("three ");
        break;
    default: System.out.print("other ");
}
```

<details>
<summary>정답·해설</summary>

```
two three 
```

`case 2`에 `break`가 없어서 `case 3`까지 내려간다(fall-through). `->` 문법을 쓰면 이런 실수가 없다.

</details>

### 버그 찾기

#### 버그 1 · 평균 구하기

```java
int[] arr = {1, 2, 3, 4};
int sum = 0;
for (int i = 0; i <= arr.length; i++) {
    sum += arr[i];
}
System.out.println("평균: " + sum / arr.length);
```

<details>
<summary>정답·해설</summary>

1. `i <= arr.length` → 마지막에 `arr[4]`를 읽어서 `ArrayIndexOutOfBoundsException`. **`i < arr.length`**
2. `sum / arr.length`는 정수 나눗셈이라 2.5가 아니라 `2`. **`(double) sum / arr.length`**

</details>

#### 버그 2 · 입력 비교

```java
Scanner sc = new Scanner(System.in);
String answer = sc.next();
if (answer == "yes") {
    System.out.println("확인");
}
```

<details>
<summary>정답·해설</summary>

`yes`를 입력해도 `확인`이 안 나온다. 입력받은 문자열은 새 객체라 `==`(같은 객체인지)로 비교하면 false. **`answer.equals("yes")`** 또는 `"yes".equals(answer)`(answer가 null이어도 안전).

</details>

### 빈칸 채우기

#### 빈칸 1 · 배열 뒤집기 (새 배열 없이)

```java
int[] a = {1, 2, 3, 4, 5};
for (int i = 0; i < ____; i++) {
    int tmp = a[i];
    a[i] = a[____];
    a[____] = tmp;
}
// a = [5, 4, 3, 2, 1]
```

<details>
<summary>정답·해설</summary>

`a.length / 2`, `a.length - 1 - i`, `a.length - 1 - i`

절반까지만 돌아야 한다. 끝까지 돌면 두 번 바꿔서 원래대로 돌아온다.

</details>

#### 빈칸 2 · 행렬 전치 (행과 열 바꾸기)

```java
int[][] m = {{1, 2, 3}, {4, 5, 6}};      // 2행 3열
int[][] t = new int[____][____];         // 3행 2열
for (int i = 0; i < m.length; i++) {
    for (int j = 0; j < m[i].length; j++) {
        ____ = m[i][j];
    }
}
```

<details>
<summary>정답·해설</summary>

`m[0].length`, `m.length`, `t[j][i]`

</details>
