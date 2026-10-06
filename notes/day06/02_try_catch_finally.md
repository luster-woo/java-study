# try_catch_finally

> 교재: 15기_자바_live_09_exception_0126_오후 (전공)

> [← 예외의 처리](01_예외의_처리.md) · [day06 목록](../README.md) · [throws 활용 →](03_throws_활용.md)

### finally

- **finally 는 예외 발생 여부와 상관없이 언제나 실행**
- try나 catch 중간에 `return` 을 만나도 finally 를 먼저 수행한 뒤 리턴
- 주 용도: try 블록에서 사용한 시스템 자원을 안전하게 원상복구

```java
try {
    // 예외가 발생할 만한 코드 - 시스템 자원 사용
} catch (Exception e) {
    // 예외 처리
} finally {
    // try 블록에서 접근했던 시스템 자원의 안전한 원상복구
}
```

```java
public class FinallyFlow {
    static void run(int num) {
        try {
            System.out.println("code 1, num: " + num);
            int i = 1 / num;
            System.out.println("code 2 - 예외 없음");
            return;
        } catch (ArithmeticException e) {
            System.out.println("code 3 - 예외 처리 완료");
        } finally {
            System.out.println("code 4 - 언제나 실행");
        }
        System.out.println("code 5");
    }
    public static void main(String[] args) {
        run(0);   // code 1 → code 3 → code 4 → code 5
        run(1);   // code 1 → code 2 → code 4  (return 이라 code 5 없음)
    }
}
```

### finally를 이용한 자원 정리

- 설치 프로그램: 복사 → 설치 → (성공이든 실패든) 임시 파일 삭제
- try 끝과 catch 양쪽에 `delete()` 를 쓰면 중복
- try-catch 뒤에 `delete()` 를 두면 catch 안에서 return 하거나 처리하지 못한 예외가 나갈 때 실행되지 않음
- **finally 에 두면 어떤 경우에도 실행**

```java
public class InstallTest {
    static void run(boolean fail) {
        InstallApp app = new InstallApp();
        try {
            app.copy();
            app.install(fail);
        } catch (Exception e) {
            System.out.println("예외: " + e.getMessage());
        } finally {
            app.delete();          // 성공·실패 모두 정리
        }
        System.out.println("설치 종료");
    }
    public static void main(String[] args) {
        run(false);
        run(true);
    }
}

class InstallApp {
    void copy()   { System.out.println("파일 복사"); }
    void install(boolean fail) throws Exception {
        System.out.println("설치");
        if (fail) throw new Exception("설치 실패");   // 직접 예외 발생
    }
    void delete() { System.out.println("파일 삭제"); }
}
/* 출력
파일 복사
설치
파일 삭제
설치 종료
파일 복사
설치
예외: 설치 실패
파일 삭제
설치 종료
*/
```

> 교재는 `Math.random() > 0.5` 일 때 예외를 던진다. 위 코드는 두 경우를 확인하려고 `fail` 인자로 바꾼 것.

### finally의 주요 목적: 리소스 반납

- `new FileInputStream("abc.txt")` 처럼 파일을 열면 OS가 **file descriptor**(열린 파일 목록)로 관리
- OS마다 열 수 있는 파일 개수가 정해져 있음 → 다 쓰면 반납 필요
- 생성한 시스템 자원을 반납하지 않으면 **resource leak** 발생 가능 → `close()` 처리

### finally에서 close를 통한 자원 반납

- finally 블록이 지저분해질 수밖에 없음
    - `close()` 자체도 IOException 을 던질 수 있음 → finally 안에 또 try-catch
    - 생성자에서 예외가 나면 변수가 null → null 확인 필요

```java
FileInputStream fileInput = null;
try {
    fileInput = new FileInputStream("abc.txt");   // FileNotFoundException 가능
    fileInput.read();
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (fileInput != null) {                      // 생성 실패 시 null
        try {
            fileInput.close();                    // 자원 반납 (IOException 가능)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### try-with-resources

- 리소스를 **자동으로 close** (finally 역할)
- try 선언부에 자동으로 닫을 대상을 `;` 로 구분해 등록

```java
try (리소스_타입1 res1 = 초기화; 리소스_타입2 res2 = 초기화; ...) {
    // 예외 발생 코드
} catch (Exception e) {
    // 예외 처리 코드
}

// 바깥에서 만든 변수를 등록하는 형태
리소스_타입1 res1 = 초기화;
리소스_타입2 res2 = 초기화;
try (res1; res2) {
    // 예외 발생 코드
} catch (Exception e) {
    // 예외 처리 코드
}
```

- 등록할 객체는 **AutoCloseable** 인터페이스를 구현해야 함
    - 각종 I/O stream, socket, connection ...
    - `AutoCloseable` ← `Closeable` ← `InputStream` ← `FileInputStream`
- 등록한 객체는 try 블록에서 다시 할당할 수 없음 — final 변수로 간주

```java
try (FileInputStream fileInput = new FileInputStream("abc.txt")) {
    System.out.println("FileInputStream이 생성되었습니다.");
    fileInput.read();
} catch (IOException e) {
    System.out.println("파일 처리 실패");   // abc.txt 가 없으면 이 줄 실행
}
```

```java
try (StringReader r = new StringReader("a")) {
    r = new StringReader("b");   // error: auto-closeable resource r may not be assigned
}
```

### try-with-resources 사용 시 주의점

- 구성 형태
    - 일반 try: `try ~ catch`, `try ~ catch ~ finally`, `try ~ finally`
    - try-with-resources: `try(AutoCloseable) ~ catch` 형태
- **close 시점 주의**
    - 자동 생성 코드는 마법이 아님 → 실제 동작을 정확히 알아야 함
    - try-with-resources 는 **nested try 블록**을 구성 → try 블록이 끝나는 순간 close 됨
    - 그래서 catch 나 finally 에서는 이미 닫힌 자원

```java
public class TwrOrder {
    public static void main(String[] args) {
        MyResource r1 = new MyResource("A");
        MyResource r2 = new MyResource("B");
        try (r1; r2) {
            r1.use(false);
            r2.use(true);
        } catch (IllegalStateException e) {
            System.out.println("catch: " + e.getMessage());   // 이미 close 된 뒤
        } finally {
            System.out.println("finally");
        }
    }
}

class MyResource implements AutoCloseable {
    private final String name;
    MyResource(String name) { this.name = name; System.out.println(name + " 열기"); }
    void use(boolean fail) {
        System.out.println(name + " 사용");
        if (fail) throw new IllegalStateException(name + " 사용 중 오류");
    }
    @Override
    public void close() { System.out.println(name + " close"); }
}
/* 출력
A 열기
B 열기
A 사용
B 사용
B close
A close
catch: B 사용 중 오류
finally
*/
```

- close 는 등록 역순(B → A)이고, catch·finally 보다 먼저 실행됨

| 상황 | 적합한 형태 |
| --- | --- |
| 그냥 쓰고 close 만 하면 됨 | 단순한 try-with-resources (`try (pstmt) { ... }`) |
| catch 에서도 자원을 써야 함 (예: 실패 시 `con.rollback()`) | 일반 `try ~ catch ~ finally` 에서 finally 에 `close()` |

```java
// catch 에서 자원을 써야 한다면 — 일반 try ~ catch ~ finally
Connection con = ...;            // 리소스 초기화
try {
    con.commit();                // 리소스 사용
} catch (Exception e) {
    con.rollback();              // 리소스 사용 (아직 닫히지 않음)
} finally {
    con.close();                 // 자원 반납
}
```

> 참고: try-with-resources 는 catch 없이 `try (res) { ... }` 만 써도 된다(교재의 `try (pstmt)` 예시가 그 형태). `try (res1; res2)` 처럼 바깥 변수를 등록하는 문법은 Java 9부터.
