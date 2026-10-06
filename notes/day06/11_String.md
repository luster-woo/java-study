# String

> 출처: [String 클래스 - TCP School](https://www.tcpschool.com/java/java_api_string) · [String (Java SE 17)](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html) · [JLS 17 §3.10.5 String Literals](https://docs.oracle.com/javase/specs/jls/se17/html/jls-3.html#jls-3.10.5)

> [← JSON](10_JSON.md) · [day06 목록](../README.md) · [StringBuffer_StringBuilder →](12_StringBuffer_StringBuilder.md)

## java.lang.String 클래스

### 문자열 전용 클래스
- C는 문자열을 `char` 배열로 다루지만 자바는 **String 클래스**를 따로 제공
- `"abc"` 같은 **문자열 리터럴은 모두 String 인스턴스**
- `java.lang` 패키지 → import 불필요

### 불변 객체(immutable object)
- String 인스턴스는 한 번 만들어지면 **읽기만 가능, 변경 불가**
- `+`로 결합하면 기존 문자열이 바뀌는 것이 아니라 **합쳐진 새 String 인스턴스가 생성**됨
- `toUpperCase()`, `concat()`, `trim()` 등도 원본은 그대로 두고 **새 String을 반환**
  - 결과를 쓰려면 반드시 다시 대입: `s = s.toUpperCase();`
- 불변이라 **여러 곳에서 안전하게 공유**할 수 있음(공식 문서) → 아래 String Pool의 전제

```
String t = "abc";
t = t.toUpperCase();

 t ──X──► "abc"     (그대로 남아 있음)
   └────► "ABC"     (새 인스턴스)
```

## String Pool — 리터럴은 같은 인스턴스를 공유

### 리터럴과 new String()
- 같은 내용의 **문자열 리터럴은 항상 같은 String 인스턴스**를 가리킴 (JLS §3.10.5)
  - 리터럴과 **상수식**(`"hel" + "lo"`처럼 컴파일 때 값이 정해지는 식)의 결과는 "interned" — 하나의 인스턴스를 공유
  - 이 공유 인스턴스들을 모아 두는 곳을 흔히 **String Pool**(문자열 상수 풀)이라 부름
- **실행 중에 계산된 문자열**(`"hel" + lo`, `lo`는 변수)은 새로 만들어진 별개의 객체
- `new String("hello")`는 항상 **새 객체**
- `intern()`: 같은 내용의 공유 인스턴스를 돌려줌 → 리터럴과 `==` 비교 시 true

| 코드 | `s1("hello" 리터럴) == ?` |
|---|---|
| `"hello"` (다른 리터럴) | true |
| `"hel" + "lo"` (상수식) | true |
| `"hel" + lo` (변수 포함) | false |
| `new String("hello")` | false |
| `new String("hello").intern()` | true |

```
        String Pool
        ┌──────────┐
 s1 ──► │ "hello"  │ ◄── s2, s5, s3.intern()
        └──────────┘
 s3 ──► [ "hello" ]   new String() — 별도 객체
 s4 ──► [ "hello" ]   실행 중 결합 — 별도 객체
```

### 그래서 문자열 비교는 equals()
- `==`는 **참조(주소) 비교** → 리터럴끼리는 우연히 true지만 입력값·결합 결과와는 false
- 내용 비교는 항상 **`equals()`** (대소문자 무시는 `equalsIgnoreCase()`)

## 주요 메서드

### charAt()
- 지정 인덱스의 문자 반환
- 범위를 벗어난 인덱스(음수, 길이 이상)면 **IndexOutOfBoundsException**

### compareTo()
- 사전 순 비교, **대소문자 구분**
- 같으면 0, 내가 작으면 음수, 크면 양수 (예제는 문자 코드 차이가 나옴)
- 대소문자 무시: `compareToIgnoreCase()`

### concat()
- 뒤에 인수 문자열을 붙인 **새 문자열** 반환, 인수 길이가 0이면 자기 자신 반환

### indexOf()
- 문자/문자열이 처음 나오는 인덱스, 없으면 **-1**, 대소문자 구분
- `lastIndexOf()`: 마지막 위치

### trim(), toLowerCase(), toUpperCase()
- `trim()`: 앞뒤 공백 문자 제거 (가운데 공백은 그대로)
- 대·소문자 변환

```java
public class StringTest {
    public static void main(String[] args) {
        String str = "Java";
        for (int i = 0; i < str.length(); i++) {
            System.out.print(str.charAt(i) + " ");
        }
        System.out.println();                            // J a v a

        String abcd = "abcd";
        System.out.println(abcd.compareTo("bcef"));      // -1
        System.out.println(abcd.compareTo("abcd"));      // 0
        System.out.println(abcd.compareTo("Abcd"));      // 32  ('a'(97) - 'A'(65))
        System.out.println(abcd.compareToIgnoreCase("Abcd")); // 0

        System.out.println(str.concat("수업"));           // Java수업
        System.out.println(str);                         // Java  (원본 그대로 - 불변)

        String oj = "Oracle Java";
        System.out.println(oj.indexOf('o'));             // -1 (대소문자 구분)
        System.out.println(oj.indexOf('a'));             // 2
        System.out.println(oj.indexOf("Java"));          // 7

        String sp = " Java ";
        System.out.println(sp + "|");                    //  Java |
        System.out.println(sp.trim() + "|");             // Java|

        System.out.println(str.toLowerCase() + " " + str.toUpperCase()); // java JAVA
        System.out.println(String.join(",", "a,b,c".split(",")));        // a,b,c
        System.out.println(oj.substring(7) + " " + oj.substring(0, 6));  // Java Oracle

        // String Pool
        String s1 = "hello";
        String s2 = "hello";
        String s3 = new String("hello");
        String lo = "lo";
        String s4 = "hel" + lo;          // 실행 중 계산 -> 새 객체
        String s5 = "hel" + "lo";        // 상수식 -> 컴파일 때 "hello"
        System.out.println(s1 == s2);            // true
        System.out.println(s1 == s3);            // false
        System.out.println(s1.equals(s3));       // true
        System.out.println(s1 == s4);            // false
        System.out.println(s1 == s5);            // true
        System.out.println(s1 == s3.intern());   // true

        // 불변: 메서드는 새 String을 돌려줄 뿐
        String t = "abc";
        t.toUpperCase();
        System.out.println(t);                   // abc
        t = t.toUpperCase();
        System.out.println(t);                   // ABC
    }
}
```

### 대표 메서드 정리

| 메서드 | 하는 일 |
|---|---|
| `char charAt(int index)` | 인덱스의 문자 |
| `int compareTo(String)` / `compareToIgnoreCase(String)` | 사전 순 비교 |
| `String concat(String)` | 뒤에 붙인 새 문자열 |
| `int indexOf(...)` / `lastIndexOf(...)` | 처음/마지막 위치, 없으면 -1 (시작 인덱스 지정 버전 있음) |
| `String[] split(String regex)` | 정규식 기준으로 나눔 |
| `String substring(int begin)` / `substring(int begin, int end)` | 부분 문자열 (`end`는 미포함) |
| `toLowerCase()` / `toUpperCase()` | 대소문자 변환 |
| `String trim()` | 앞뒤 공백 제거 |
| `int length()` | 길이 |
| `boolean isEmpty()` | 길이가 0이면 true |

---

## 참고

- TCP School 표의 `substring(int begin, int end)` 설명("마지막 인덱스까지")은 **`end` 직전까지**가 정확함 — 예제의 `"Oracle Java".substring(0, 6)`이 `"Oracle"`(인덱스 0~5)
- TCP School 예제는 `new String("Java")`로 시작하지만, 공식 문서는 String이 불변이라 이 복사 생성자가 **보통 필요 없다**고 함 → 리터럴로 쓰면 됨
- Java 17 공식 문서 메서드 목록에는 TCP School 표에 없는 `isBlank()`, `strip()`, `repeat()`, `equalsIgnoreCase()` 등도 있음

## 면접 포인트

- **String이 불변인 이유/장점?** 한 번 만든 값이 바뀌지 않으니 여러 참조가 같은 인스턴스를 안전하게 공유할 수 있고, 이것이 리터럴 공유(String Pool)의 전제가 됨. 대신 변경할 때마다 새 객체가 생김.
- **String Pool이란?** 같은 내용의 문자열 리터럴·상수식 결과를 하나의 인스턴스로 공유(intern)하는 것. `new String()`이나 실행 중 결합 결과는 별개 객체라서 `==`가 false.
- **`==`와 `equals()` 차이?** `==`는 참조 비교, `equals()`는 내용 비교. 문자열 내용 비교는 항상 `equals()`.
- **반복 결합은?** `+`마다 새 String이 생기므로 반복문 안의 결합은 StringBuilder를 씀 → [다음 노트](12_StringBuffer_StringBuilder.md).
