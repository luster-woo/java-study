# D6 · 예외·입출력·JSON·String·record

## 오늘 볼 강의

| 순서 | 반 | 강의 |
|---|---|---|
| 1 | 전공 | [예외] 예외의 처리 |
| 2 | 전공 | [예외] try_catch_finally |
| 3 | 전공 | [예외] throws 활용 |
| 4 | 전공 | [예외] 사용자 정의 예외 |
| 5 | 전공 | [예외] APPENDIX_부적절한 예외 처리 |
| 6 | 전공 | [파일 입출력] 노드스트림 |
| 7 | 전공 | [파일 입출력] 노드스트림 활용 |
| 8 | 전공 | [파일 입출력] 보조스트림 |
| 9 | 전공 | [파일 입출력] 보조스트림 활용 |
| 10 | 전공 | [XML/Json] JSON |
| 11 | 읽기 | TCP School — String 클래스 |
| 12 | 읽기 | 점프 투 자바 — StringBuffer |
| 13 | 읽기 | Oracle — Record Classes |

---

## 1. 예외 (Exception)

### 계층 구조

```
Throwable
├─ Error                        시스템 문제 (OutOfMemoryError 등). 잡지 않음
└─ Exception
   ├─ IOException, SQLException ...       ← Checked: 처리 안 하면 컴파일 에러
   └─ RuntimeException                    ← Unchecked: 처리 안 해도 컴파일됨
      ├─ NullPointerException
      ├─ ArrayIndexOutOfBoundsException
      ├─ IllegalArgumentException
      └─ ...
```

| | Checked | Unchecked |
|---|---|---|
| 해당 | `Exception` 중 `RuntimeException`이 **아닌** 것 | `RuntimeException`과 그 자식 |
| 컴파일러 | **반드시** try-catch 또는 throws | 강제 안 함 |
| 예 | `IOException`, `SQLException` | `NullPointerException`, `IllegalArgumentException` |
| 성격 | 외부 요인 (파일 없음, 네트워크) | 대부분 코드 실수 |

Python은 Checked 개념이 없다. 모든 예외가 Unchecked처럼 동작한다.

### try-catch-finally

| Python | Java |
|---|---|
| `try:` | `try {` |
| `except ValueError as e:` | `} catch (NumberFormatException e) {` |
| `finally:` | `} finally {` |
| `raise ValueError("x")` | `throw new IllegalArgumentException("x");` |

```java
public class TryTest {
    public static void main(String[] args) {
        String[] inputs = {"10", "abc", "0"};
        for (String s : inputs) {
            try {
                int n = Integer.parseInt(s);
                System.out.println(100 / n);
            } catch (NumberFormatException e) {
                System.out.println("숫자가 아님: " + e.getMessage());
            } catch (ArithmeticException e) {
                System.out.println("0으로 나눌 수 없음");
            } finally {
                System.out.println("항상 실행");   // 예외가 나든 안 나든
            }
        }

        try {
            int[] a = new int[1];
            a[5] = 1;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {   // 멀티 catch
            System.out.println("잡음: " + e.getClass().getSimpleName());
        }
    }
}
```
**catch 순서**: 자식 예외를 먼저, 부모(`Exception`)를 나중에. 반대로 쓰면 컴파일 에러.

### throws — 처리를 호출한 쪽에 넘기기

```java
import java.io.*;

public class ThrowsTest {
    // 이 메서드는 IOException을 직접 처리하지 않고 호출한 쪽에 넘긴다
    static String readFirstLine(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {   // try-with-resources
            return br.readLine();
        }   // br.close()가 자동으로 호출됨
    }

    public static void main(String[] args) {
        try {
            System.out.println(readFirstLine("없는파일.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("파일 없음");
        } catch (IOException e) {
            System.out.println("읽기 실패");
        }
    }
}
```
**try-with-resources** = Python의 `with open(...) as f:`. 괄호 안에서 연 자원(파일·DB 연결)을 자동으로 닫아 준다.

### 사용자 정의 예외

```java
class InsufficientBalanceException extends RuntimeException {   // Unchecked로 만드는 게 요즘 관례
    private final int shortage;

    InsufficientBalanceException(int shortage) {
        super("잔액이 " + shortage + "원 부족합니다");
        this.shortage = shortage;
    }

    int getShortage() { return shortage; }
}

class Account {
    private int balance;
    Account(int balance) { this.balance = balance; }

    void withdraw(int money) {
        if (money > balance) {
            throw new InsufficientBalanceException(money - balance);
        }
        balance -= money;
    }
}

public class CustomExceptionTest {
    public static void main(String[] args) {
        Account acc = new Account(1000);
        try {
            acc.withdraw(3000);
        } catch (InsufficientBalanceException e) {
            System.out.println(e.getMessage());     // 잔액이 2000원 부족합니다
        }
    }
}
```
**Spring에서는**: Service에서 이런 예외를 던지고, `@RestControllerAdvice`가 한 곳에서 잡아 `400 Bad Request` 같은 응답으로 바꾼다. Checked 예외를 쓰면 모든 메서드에 `throws`가 줄줄이 붙어서 Spring은 Unchecked를 쓴다. 그리고 **`@Transactional`은 기본적으로 Unchecked 예외에서만 롤백**한다(D10).

### 하면 안 되는 예외 처리

```java
try { ... } catch (Exception e) { }                       // ❌ 예외를 삼킴. 문제가 생겨도 아무도 모름
try { ... } catch (Exception e) { e.printStackTrace(); }  // △ 연습 때만. 실무는 로그(log.error)
```

## 2. 파일 입출력

### 스트림의 두 종류

| | 바이트 스트림 | 문자 스트림 |
|---|---|---|
| 단위 | byte (이미지, 아무 파일) | char (텍스트) |
| 입력 | `InputStream` → `FileInputStream` | `Reader` → `FileReader` |
| 출력 | `OutputStream` → `FileOutputStream` | `Writer` → `FileWriter` |

- **노드 스트림**: 실제 데이터 소스(파일 등)에 직접 연결 (`FileReader`)
- **보조 스트림**: 노드 스트림을 감싸서 기능 추가 (`BufferedReader` = 버퍼로 빠르게 + 한 줄씩 읽기)
- **`InputStreamReader`**: 바이트 → 문자 변환 다리. 백준 틀의 `new BufferedReader(new InputStreamReader(System.in))`이 바로 "키보드(바이트) → 문자 → 버퍼" 조합이다.

```java
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class FileTest {
    public static void main(String[] args) throws IOException {
        // 쓰기
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("memo.txt", StandardCharsets.UTF_8))) {
            bw.write("첫 줄");
            bw.newLine();
            bw.write("둘째 줄");
        }

        // 읽기 (한 줄씩)
        try (BufferedReader br = new BufferedReader(new FileReader("memo.txt", StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {   // 파일 끝이면 null
                System.out.println(line);
            }
        }

        // 최신 방식 (java.nio.file.Files): 짧은 파일은 이게 편함
        List<String> lines = Files.readAllLines(Path.of("memo.txt"));
        System.out.println(lines);
        Files.writeString(Path.of("memo2.txt"), "한 번에 쓰기");
        Files.delete(Path.of("memo.txt"));
        Files.delete(Path.of("memo2.txt"));
    }
}
```

**객체 직렬화**(`Serializable`): 객체를 바이트로 바꿔 파일·네트워크로 보내는 것. 요즘은 직렬화 대신 JSON을 쓴다.

## 3. JSON

```json
{
  "id": 1,
  "name": "규",
  "skills": ["Java", "Spring"],
  "active": true
}
```
- Python `dict`와 거의 같은 모양. 웹 API에서 데이터를 주고받는 표준 형식.
- Java에서는 Jackson, Gson 같은 라이브러리로 **객체 ↔ JSON**을 변환한다.
- **Spring Boot는 Jackson이 기본으로 들어 있어서**, Controller가 객체를 반환하면 자동으로 JSON이 되고, 요청 JSON은 `@RequestBody`로 자동으로 객체가 된다. 그래서 직접 파싱할 일은 거의 없다. 강의의 Gson 예제는 원리만 이해하면 충분하다.

## 4. String — 불변(immutable) 객체

```java
public class StringTest {
    public static void main(String[] args) {
        String a = "java";
        String b = "java";             // 문자열 리터럴은 String Pool에서 같은 객체를 재사용
        String c = new String("java"); // new는 Heap에 새 객체

        System.out.println(a == b);       // true  (같은 객체)
        System.out.println(a == c);       // false (다른 객체)
        System.out.println(a.equals(c));  // true  ← 문자열 비교는 항상 equals

        String s = "hello";
        s.toUpperCase();                  // 원본은 안 바뀜 (불변)
        System.out.println(s);            // hello
        s = s.toUpperCase();              // 새 문자열을 다시 대입해야 함
        System.out.println(s);            // HELLO

        String t = "  Hello, Java  ";
        System.out.println(t.trim());                  // Hello, Java     ← strip()
        System.out.println(t.length());                // 15              ← len()
        System.out.println(t.charAt(2));               // H               ← t[2]
        System.out.println(t.contains("Java"));        // true            ← "Java" in t
        System.out.println(t.indexOf("Java"));         // 9               ← find()
        System.out.println(t.trim().substring(0, 5));  // Hello           ← t[0:5]
        System.out.println(t.replace("Java", "Spring"));
        System.out.println(String.join("-", "a", "b", "c"));  // a-b-c   ← "-".join(...)
        System.out.println("a,b,c".split(",").length);        // 3
        System.out.println("".isEmpty() + " " + "  ".isBlank());  // true true
    }
}
```

### StringBuilder — 문자열을 많이 붙일 때

`String`은 불변이라 `+=`를 반복하면 **매번 새 객체**가 생긴다. 반복문 안에서는 `StringBuilder`.

```java
public class BuilderTest {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i).append(",");
        }
        sb.setLength(sb.length() - 1);       // 마지막 쉼표 제거
        System.out.println(sb);              // 0,1,2,3,4
        System.out.println(sb.reverse());    // 4,3,2,1,0
        String result = sb.toString();
    }
}
```

| | `String` | `StringBuilder` | `StringBuffer` |
|---|---|---|---|
| 변경 | 불가 (불변) | 가능 | 가능 |
| 스레드 안전 | 안전 (불변이라서) | **안전하지 않음** | 안전 (동기화) |
| 속도 | 붙이기 느림 | **가장 빠름** | 조금 느림 |
| 언제 | 일반 문자열 | 반복문에서 붙이기 (백준 출력) | 여러 스레드가 같이 쓸 때 |

## 5. record (Java 16+)

**값을 담기만 하는 클래스를 한 줄로.** 필드·생성자·getter·`equals`·`hashCode`·`toString`이 자동으로 생긴다.

```java
public class RecordTest {
    record UserResponse(Long id, String name, String email) { }

    record Point(int x, int y) {
        Point {                                   // 검증용 생성자
            if (x < 0 || y < 0) throw new IllegalArgumentException("음수 불가");
        }
        double dist() { return Math.sqrt(x * x + y * y); }   // 메서드 추가 가능
    }

    public static void main(String[] args) {
        UserResponse u = new UserResponse(1L, "규", "gyu@example.com");
        System.out.println(u.name());             // 규  ← getName()이 아니라 name()
        System.out.println(u);                    // UserResponse[id=1, name=규, email=gyu@example.com]
        System.out.println(u.equals(new UserResponse(1L, "규", "gyu@example.com")));  // true

        System.out.println(new Point(3, 4).dist());   // 5.0
    }
}
```
- 필드는 전부 `private final` → **값 변경 불가**
- 상속 불가
- Python의 `@dataclass(frozen=True)`와 비슷
- **Spring에서는 DTO(요청·응답 객체)로 많이 쓴다.** 단, JPA 엔티티는 값이 바뀌어야 하고 기본 생성자가 필요해서 **record로 만들 수 없다.**

---

## 자주 하는 실수

| 실수 | 증상 | 고치기 |
|---|---|---|
| Checked 예외 처리 안 함 | "Unhandled exception" 빨간 줄 | `Alt + Enter` → try-catch 또는 throws |
| `catch (Exception e)` 먼저 | 뒤 catch가 "already caught" 에러 | 자식 예외부터 |
| 빈 catch 블록 | 에러가 조용히 사라짐 | 최소한 메시지 출력 |
| 파일을 안 닫음 | 파일 잠김·자원 누수 | try-with-resources |
| 문자열 `==` 비교 | 같은 글자인데 false | `equals` |
| 반복문에서 `String +=` | 백준 시간 초과 | `StringBuilder` |
| Windows에서 한글 깨짐 | 콘솔 출력이 깨지거나, 메모장(MS949)으로 저장한 파일을 읽을 때 깨짐 | Java 18부터 기본 인코딩은 UTF-8. 읽을 파일의 인코딩을 명시하고, IntelliJ File Encodings를 UTF-8로 |
| record에 `getName()` 호출 | 에러 | `name()` |

---

## 면접 질문

1. **Checked와 Unchecked 예외의 차이는?**
   Checked는 RuntimeException이 아닌 Exception으로, 컴파일러가 처리(try-catch나 throws)를 강제한다. Unchecked는 RuntimeException 계열로 처리를 강제하지 않는다.
2. **Spring에서 Unchecked 예외를 주로 쓰는 이유는?**
   Checked 예외는 모든 호출 계층에 throws를 퍼뜨려 코드를 오염시키고, 대부분 복구할 수도 없다. 또 @Transactional은 기본적으로 Unchecked 예외에서 롤백한다.
3. **throw와 throws의 차이는?**
   throw는 예외를 실제로 발생시키는 문장, throws는 메서드가 그 예외를 호출한 쪽에 넘긴다고 선언하는 것.
4. **finally와 try-with-resources는?**
   finally는 예외 여부와 상관없이 실행되는 블록이고, try-with-resources는 AutoCloseable 자원을 블록이 끝날 때 자동으로 닫아 준다.
5. **String이 불변인 이유와 장점은?**
   String Pool에서 같은 문자열을 공유하려면 바뀌면 안 되고, 불변이라 여러 스레드에서 안전하게 쓸 수 있으며 HashMap 키로도 안전하다.
6. **String, StringBuilder, StringBuffer 차이는?**
   String은 불변, StringBuilder와 StringBuffer는 가변이다. StringBuffer는 동기화돼서 스레드 안전하지만 느리고, 단일 스레드에서는 StringBuilder를 쓴다.
7. **record는 언제 쓰나요?**
   값을 담아 전달만 하는 불변 객체, 예를 들어 DTO에. 생성자·접근자·equals·hashCode·toString을 자동으로 만들어 준다.

---

## 오늘의 실습

- [ ] 강의 코드 따라 치기 (`src/day06/`)
- [ ] D2·D3의 `Account`에 `InsufficientBalanceException` 붙이기 (잔액 부족이면 예외)
- [ ] 계좌 목록을 `accounts.txt`에 한 줄씩 저장하고 다시 읽어서 `Account` 객체로 복원
- [ ] 계좌 정보 응답용 `record AccountResponse(String owner, int balance)` 만들기
- [ ] 연습문제 3개: [problems/day06.md](problems/day06.md) (`src/practice/`에 풀기)
- [ ] 여유 있으면 SWEA·프로그래머스에서 1~2문제 더
- [ ] 커밋: `day06: 예외·입출력·String·record / Account 예외·파일 저장`
- [ ] **1주차 끝!** 이번 주 노트의 면접 질문을 소리 내서 답해 보기
