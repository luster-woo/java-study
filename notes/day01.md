# D1 · 기본 문법·배열

## 오늘 볼 강의

| 순서 | 반 | 강의 |
|---|---|---|
| 1 | 비전공 | Hello Java |
| 2 | 비전공 | 변수와 자료형 |
| 3 | 비전공 | 연산자 |
| 4 | 비전공 | 제어문 |
| 5 | 전공 | 배열 |
| 6 | 전공 | 다차원 배열 |

---

## 1. Java 프로그램이 실행되는 과정

```
HelloJava.java  --(javac 컴파일)-->  HelloJava.class  --(JVM 실행)-->  결과
   소스 코드                            바이트코드
```

- Python도 내부적으로 바이트코드(`.pyc`)로 바꾸지만, 타입 검사 없이 실행하다가 그 줄에서 오류를 만난다.
- Java는 **먼저 컴파일**해서 바이트코드(`.class`)를 만들고, **JVM**이 그걸 실행한다. 컴파일할 때 타입을 전부 검사한다.
- 그래서 타입 오류 같은 실수는 **실행 전에** 빨간 줄로 잡힌다.
- JVM이 OS마다 있어서 같은 `.class` 파일이 Windows·Linux 어디서든 돈다. ("Write once, run anywhere")
- IntelliJ에서 ▶를 누르면 컴파일과 실행을 한 번에 해 준다. `out/` 폴더에 생기는 게 `.class` 파일이다.

| 용어 | 뜻 |
|---|---|
| JDK | 개발 도구 모음 (컴파일러 `javac` + JRE). 우리가 설치한 Temurin 21 |
| JRE | 실행 환경 (JVM + 기본 라이브러리) |
| JVM | `.class`를 실제로 실행하는 가상 머신 |

## 2. 기본 구조

```java
public class HelloJava {                        // 파일 이름 = 클래스 이름
    public static void main(String[] args) {    // 프로그램 시작점
        System.out.println("Hello World!");     // 출력 후 줄바꿈
    }
}
```

| Python | Java |
|---|---|
| 들여쓰기로 블록 구분 | `{ }`로 블록 구분 (들여쓰기는 보기용) |
| 줄 끝에 아무것도 없음 | 문장 끝에 `;` 필수 |
| `print("hi")` | `System.out.println("hi");` |
| 파일에 바로 코드 작성 | 모든 코드는 클래스 안에 |
| `# 주석` | `// 한 줄 주석`, `/* 여러 줄 */` |

## 3. 변수와 자료형

Java는 **변수를 만들 때 타입을 정하고, 그 타입은 바뀌지 않는다.** (정적 타입)

```java
int age = 25;
age = "스물다섯";   // 컴파일 에러. Python에서는 됐던 것
```

### 기본형 8개 (primitive)

| 타입 | 크기 | 범위·예 | 메모 |
|---|---|---|---|
| `byte` | 1B | -128 ~ 127 | 파일·네트워크 데이터 |
| `short` | 2B | 약 ±3만 | 거의 안 씀 |
| **`int`** | 4B | 약 ±21억 | 정수 기본 |
| **`long`** | 8B | 약 ±922경 | 큰 정수. 값 뒤에 `L` (`10000000000L`) |
| `float` | 4B | 소수 7자리 정도 | 값 뒤에 `f` (`3.14f`) |
| **`double`** | 8B | 소수 15자리 정도 | 실수 기본 |
| **`boolean`** | — | `true` / `false` | 소문자. Python은 `True` |
| **`char`** | 2B | `'A'`, `'가'` | 문자 하나. **작은따옴표** |

- **`String`은 기본형이 아니라 클래스(참조형)다.** 큰따옴표 `"hello"`.
- Python `int`는 크기 제한이 없지만, Java `int`는 21억을 넘으면 **조용히 음수로 넘어간다**(오버플로). 백준에서 합이 커질 것 같으면 `long`.

```java
public class OverflowTest {
    public static void main(String[] args) {
        int max = Integer.MAX_VALUE;   // 2147483647
        System.out.println(max + 1);   // -2147483648 (에러 없이 넘어감)

        long big = 2147483647L + 1;
        System.out.println(big);       // 2147483648
    }
}
```

### 형 변환

| 종류 | 방향 | 예 |
|---|---|---|
| 자동 (묵시적) | 작은 타입 → 큰 타입 | `long l = 10;` `double d = 3;` |
| 강제 (명시적) | 큰 타입 → 작은 타입. 값이 잘릴 수 있음 | `int i = (int) 3.99;` → `3` |

```java
public class CastTest {
    public static void main(String[] args) {
        int a = 7, b = 2;
        System.out.println(a / b);           // 3   (정수끼리 나누면 정수. Python의 // 와 같음)
        System.out.println((double) a / b);  // 3.5 (하나만 double로 바꾸면 실수 나눗셈)
        System.out.println(a % b);           // 1

        char c = 'A';
        System.out.println(c + 1);           // 66 (char는 숫자로 계산됨)
        System.out.println((char) (c + 1));  // B

        String s = "123";
        int n = Integer.parseInt(s);         // 문자열 → 정수. Python의 int("123")
        String t = String.valueOf(n);        // 정수 → 문자열. Python의 str(123)
        System.out.println(n + 1);           // 124
        System.out.println(t + 1);           // 1231 (문자열 + 숫자 = 문자열 이어붙이기)
    }
}
```

### 상수

```java
final int MAX_SIZE = 100;   // 한 번 넣으면 못 바꿈. 이름은 대문자_스네이크
```

### var (Java 10+)

```java
var list = new java.util.ArrayList<String>();  // 오른쪽을 보고 타입 추론. 그래도 타입은 고정
```
처음엔 타입을 직접 쓰는 습관부터 들이는 게 좋다.

## 4. 연산자

Python과 거의 같고, 다른 것만 정리.

| Python | Java | 메모 |
|---|---|---|
| `and`, `or`, `not` | `&&`, `\|\|`, `!` | |
| `7 // 2` | `7 / 2` (둘 다 int일 때) | |
| `7 / 2` → 3.5 | `7.0 / 2` 또는 `(double) 7 / 2` | |
| `2 ** 10` | `Math.pow(2, 10)` | 결과가 `double` |
| 없음 | `i++`, `i--` | 1 증가·감소 |
| `x if cond else y` | `cond ? x : y` | 삼항 연산자 |

### ++i 와 i++

```java
public class IncrementTest {
    public static void main(String[] args) {
        int i = 5;
        int a = i++;   // a에 5를 넣고, 그다음 i가 6
        int b = ++i;   // i를 7로 만들고, b에 7
        System.out.println(a + " " + b + " " + i);  // 5 7 7
    }
}
```
헷갈리면 `i++`를 **한 줄에 혼자** 쓰는 습관을 들이면 된다.

### 단축 평가

`&&`는 왼쪽이 `false`면 오른쪽을 아예 실행하지 않는다. 그래서 이런 코드가 안전하다.

```java
if (arr != null && arr.length > 0) { ... }
```

## 5. 제어문

### if

```java
if (score >= 90) {
    grade = 'A';
} else if (score >= 80) {     // Python의 elif
    grade = 'B';
} else {
    grade = 'C';
}
```
조건은 반드시 **괄호 안**, 그리고 **boolean이어야** 한다. Python처럼 `if (list)`, `if (0)`은 안 된다.

### switch

```java
public class SwitchTest {
    public static void main(String[] args) {
        int month = 2;

        // 예전 방식: break를 빼먹으면 아래 case로 계속 내려간다 (fall-through)
        switch (month) {
            case 12:
            case 1:
            case 2:
                System.out.println("겨울");
                break;
            default:
                System.out.println("겨울 아님");
        }

        // Java 14+ 새 방식: break 필요 없음, 값을 바로 돌려받을 수 있음
        String season = switch (month) {
            case 12, 1, 2 -> "겨울";
            case 3, 4, 5 -> "봄";
            case 6, 7, 8 -> "여름";
            default -> "가을";
        };
        System.out.println(season);
    }
}
```

### 반복문

| Python | Java |
|---|---|
| `for i in range(5):` | `for (int i = 0; i < 5; i++)` |
| `for i in range(4, -1, -1):` | `for (int i = 4; i >= 0; i--)` |
| `for x in arr:` | `for (int x : arr)` (향상된 for문) |
| `while cond:` | `while (cond)` |
| 없음 | `do { } while (cond);` (최소 1번 실행) |

```java
public class LoopTest {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            if (i == 1) continue;   // 이번 회차 건너뛰기
            if (i == 4) break;      // 반복 끝내기
            System.out.print(i + " ");
        }
        System.out.println();       // 0 2 3

        // 이중 for문을 한 번에 빠져나가기: 라벨
        outer:
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i * j == 2) break outer;
                System.out.print(i + "" + j + " ");
            }
        }
        System.out.println();       // 00 01 02 10 11
    }
}
```

## 6. 배열

Python 리스트와 가장 다른 점: **크기가 고정**되고, **한 가지 타입만** 담는다.

```java
import java.util.Arrays;

public class ArrayTest {
    public static void main(String[] args) {
        int[] a = new int[5];          // [0, 0, 0, 0, 0] (기본값으로 채워짐)
        int[] b = {3, 1, 2};           // 선언과 동시에 값 넣기

        a[0] = 10;
        System.out.println(a.length);  // 5  (length는 괄호 없음! 메서드 아님)
        System.out.println(b[2]);      // 2
        // System.out.println(b[3]);   // ArrayIndexOutOfBoundsException
        // b[-1] 도 에러. Python처럼 뒤에서부터 접근 불가

        System.out.println(b);                   // [I@1b6d3586 같은 주소값이 나옴
        System.out.println(Arrays.toString(b));  // [3, 1, 2]  ← 출력은 이렇게

        Arrays.sort(b);                          // 오름차순 정렬 (원본이 바뀜)
        System.out.println(Arrays.toString(b));  // [1, 2, 3]

        int[] c = b;                   // 같은 배열을 가리킴 (복사 아님!)
        c[0] = 99;
        System.out.println(b[0]);      // 99

        int[] d = Arrays.copyOf(b, b.length);  // 진짜 복사
        d[0] = 0;
        System.out.println(b[0]);      // 99 (그대로)
    }
}
```

기본값: `int` → `0`, `double` → `0.0`, `boolean` → `false`, `char` → `'\u0000'`, `String` 같은 참조형 → `null`.

### 2차원 배열

```java
import java.util.Arrays;

public class Array2DTest {
    public static void main(String[] args) {
        int[][] grid = new int[3][4];      // 3행 4열, 전부 0
        grid[1][2] = 7;

        int[][] m = {
            {1, 2, 3},
            {4, 5, 6}
        };
        System.out.println(m.length);      // 2 (행 수)
        System.out.println(m[0].length);   // 3 (열 수)

        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println(Arrays.deepToString(m));  // [[1, 2, 3], [4, 5, 6]]

        // 델타 탐색 (BFS·시뮬레이션 문제 단골)
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        int r = 1, c = 1;
        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d], nc = c + dc[d];
            if (nr < 0 || nr >= 3 || nc < 0 || nc >= 4) continue;
            grid[nr][nc] = 1;
        }
        System.out.println(Arrays.deepToString(grid));
    }
}
```

---

## 7. 백준 입출력 틀

`Scanner`는 편하지만 느려서 입력이 많으면 시간 초과가 난다. 처음부터 `BufferedReader`에 익숙해지자.

```java
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(br.readLine());             // 한 줄에 숫자 하나

        StringTokenizer st = new StringTokenizer(br.readLine());  // 한 줄에 여러 개 (공백 구분)
        int a = Integer.parseInt(st.nextToken());
        int b = Integer.parseInt(st.nextToken());

        StringBuilder sb = new StringBuilder();              // 출력이 많으면 모아서 한 번에
        for (int i = 0; i < n; i++) {
            sb.append(a + b).append('\n');
        }
        System.out.print(sb);
    }
}
```

| Python | Java |
|---|---|
| `input()` | `br.readLine()` |
| `int(input())` | `Integer.parseInt(br.readLine())` |
| `map(int, input().split())` | `StringTokenizer` + `Integer.parseInt(st.nextToken())` |
| `print()` 여러 번 | `StringBuilder`에 모아서 `System.out.print(sb)` 한 번 |

백준 제출 시: **클래스 이름은 `Main`**, 맨 위 `package ...;` 줄은 **지우고** 제출.

---

## 자주 하는 실수

| 실수 | 증상 | 고치기 |
|---|---|---|
| 문장 끝 `;` 빠짐 | 빨간 줄 | `Alt + Enter` |
| 파일 이름 ≠ 클래스 이름 | 컴파일 에러 | 둘을 똑같이 |
| 문자열을 `==`로 비교 | 같은 글자인데 `false`가 나옴 | `s1.equals(s2)` (D6에서 자세히) |
| `char`에 큰따옴표 | `char c = "A";` 에러 | `'A'` |
| `arr.length()` | 에러 | 배열은 `arr.length`, 문자열은 `s.length()` |
| 정수 나눗셈 | `1 / 2`가 `0` | `1.0 / 2` |
| `int` 오버플로 | 큰 수가 음수로 | `long` |
| 클래스 이름에 `_` | 동작은 하지만 관례 위반 | `Hello_Java` → `HelloJava` (PascalCase) |

### 이름 규칙

| 대상 | 규칙 | 예 |
|---|---|---|
| 클래스 | PascalCase | `HelloJava`, `ArrayTest` |
| 변수·메서드 | camelCase | `maxValue`, `getName()` |
| 상수 | UPPER_SNAKE | `MAX_SIZE` |
| 패키지 | 소문자 | `day01`, `boj` |

Python은 변수에 `snake_case`를 쓰지만 Java는 `camelCase`다.

---

## 면접 질문

1. **JDK, JRE, JVM 차이는?**
   JVM은 바이트코드를 실행하는 가상 머신, JRE는 JVM과 실행에 필요한 라이브러리, JDK는 JRE에 컴파일러 같은 개발 도구를 더한 것.
2. **Java가 플랫폼에 독립적인 이유는?**
   소스를 OS에 맞는 기계어가 아니라 바이트코드로 컴파일하고, 각 OS의 JVM이 그 바이트코드를 실행하기 때문.
3. **기본형과 참조형의 차이는?**
   기본형(8개)은 변수에 값 자체가 들어가고, 참조형(String·배열·객체)은 변수에 객체가 있는 위치(참조)가 들어간다. 그래서 배열을 `c = b`로 대입하면 같은 배열을 가리킨다.
4. **`i++`와 `++i` 차이는?**
   `i++`는 현재 값을 쓰고 나서 증가, `++i`는 먼저 증가시키고 그 값을 쓴다.

---

## 오늘의 실습

- [ ] 오늘 강의 코드 따라 치기 (`src/day01/`)
- [ ] 연습문제 3개: [problems/day01.md](problems/day01.md) (`src/practice/`에 풀기)
- [ ] 여유 있으면 SWEA·프로그래머스에서 1~2문제 더
- [ ] 커밋: `day01: 변수·연산자·제어문·배열 / 연습문제 D1P1~P3`
