# throws 활용

> 교재: 15기_자바_live_09_exception_0126_오후 (전공)

> [← try_catch_finally](02_try_catch_finally.md) · [day06 목록](../README.md) · [사용자 정의 예외 →](04_사용자_정의_예외.md)

### throws 키워드를 통한 처리 위임

- 메서드에서 처리해야 할 하나 이상의 예외를 **호출한 곳으로 전달**(처리 위임)
    - 예외가 없어지는 것이 아니라 단순히 전달됨
    - 전달받은 메서드에 다시 예외 처리 책임이 생김
- 처리하려는 예외의 **조상 타입**으로 throws 가능

```java
void exceptionMethod() throws Exception1, Exception2 {   // ① 예외 발생
    // 예외 발생 코드
}
void methodCaller() {
    try {
        exceptionMethod();                               // ② 전달됨
    } catch (Exception e) { }                            // ③ 예외 처리 책임
}
```

### checked exception과 throws

- checked exception 은 반드시 `try~catch` 또는 `throws` 필요
- 필요한 곳(처리할 수 있는 곳)에서 try~catch

```java
public class CheckedThrowsTest {
    public static void main(String[] args) {
        CheckedThrowsTest t = new CheckedThrowsTest();
        try {
            t.method1();
        } catch (ClassNotFoundException e) {
            System.out.printf("예외 처리: %s%n", e.getMessage());   // 예외 처리: Some Class
        }
        System.out.println("프로그램 종료");                        // 프로그램 종료
    }
    public void method1() throws ClassNotFoundException {
        method2();                              // 처리하지 않고 다시 위임
    }
    public void method2() throws ClassNotFoundException {
        Class.forName("Some Class");            // ClassNotFoundException 발생
    }
}
```

### unchecked exception과 throws

- unchecked exception 은 throws 하지 않아도 호출한 곳으로 전달됨
- 하지만 결국은 어딘가에서 try~catch 로 처리해야 함

```java
public class RuntimeThrowsTest {
    public static void main(String[] args) {
        RuntimeThrowsTest t = new RuntimeThrowsTest();
        try {
            t.method1();
        } catch (ArithmeticException e) {
            System.out.printf("예외 처리: %s%n", e.getMessage());   // 예외 처리: / by zero
        }
        System.out.println("프로그램 종료");                        // 프로그램 종료
    }
    public void method1() { method2(); }        // throws 없어도 전파
    public void method2() { int i = 1 / 0; }    // ArithmeticException 발생
}
```

### 로그 분석과 예외의 추적

- `printStackTrace()` 로 최초 호출 메서드부터 예외 발생 메서드까지의 스택 정보 확인
- 꼭 확인할 정보

| 질문 | 의미 |
| --- | --- |
| 어떤 예외인가? | 예외 종류 |
| 예외 객체의 메시지는? | 예외 원인 |
| 어디서 발생했는가? | 디버깅 출발점 |

- **직접 작성한 코드**를 디버깅 대상으로 삼고, 참조하는 라이브러리(`java.xx` 등)는 과감히 건너뛰기

```
java.lang.ClassNotFoundException: Some Class                       ← 예외 종류 : 예외 원인
	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(...)  ┐
	at java.base/java.lang.ClassLoader.loadClass(...)                   ├ 라이브러리 — 건너뛰기
	at java.base/java.lang.Class.forName(Class.java:315)                ┘
	at CheckedThrowsTest.method2(CheckedThrowsTest.java:16)            ← 디버깅 출발점
	at CheckedThrowsTest.method1(CheckedThrowsTest.java:13)            ┐ 호출 경로
	at CheckedThrowsTest.main(CheckedThrowsTest.java:5)                ┘ (아래에서 위로 전파)
```

### throws의 목적과 API 활용

- API 메서드는 발생할 수 있는 예외를 선언부에 명시해 **프로그래머가 대처하도록 강요**
    - 예: `public FileInputStream(String name) throws FileNotFoundException`
    - 문서의 Throws 항목: 파일이 없거나, 디렉터리이거나, 읽을 수 없으면 FileNotFoundException

| API 안에서 처리해 버리면 | API가 throws 로 전파하면 |
| --- | --- |
| 예외가 API 안에서 끝남 | 예외가 개발자 코드로 전파 |
| 개발자 코드는 상황을 인지 못함 | 개발자 코드가 상황 인지 → 적절한 예외 처리 |

### 메서드 재정의와 throws

- 재정의 메서드는 조상 메서드가 던지는 예외보다 **더 큰(부모) 예외를 던질 수 없음**
- "부모가 치지 않은 사고를 자식이 칠 수 없다"

```java
class Parent {
    void methodA() throws IOException {}
    void methodB() throws ClassNotFoundException {}
}
public class OverridingTest extends Parent {
    @Override
    void methodA() throws FileNotFoundException {}   // OK: IOException 의 자식
    @Override
    void methodB() throws Exception {}               // 컴파일 오류
    // error: methodB() in OverridingTest cannot override methodB() in Parent
    //        overridden method does not throw Exception
}
```

> 참고: 이 제한은 checked exception 에만 적용된다. RuntimeException 계열은 재정의 메서드에 자유롭게 선언할 수 있다.

### 예외 변환과 Exception Chaining

- 하위 계층에서 난 예외는 **상위 계층에 맞는 예외로 바꿔서** 던져야 함
    - 고객이 주문 → 주문이 배송 요청 → 배송 실패
    - 고객 입장에서는 "배송 실패"가 아니라 "주문 실패"가 의미 있음
- **Exception Chaining**: 하위 계층 예외를 **원인(cause)** 으로 담아 상위 계층 예외를 발생
    - 하위 예외 정보가 상위 예외 디버깅에 유용할 때 사용

```
고객 ──주문──→ 주문 ──배송 요청──→ 배송
      ←throws── OrderException(d) ←throws── DeliveryException("배송 문제")
```

```java
public class ChainingTest {
    static void deliver() throws DeliveryException {
        throw new DeliveryException("배송 문제");
    }
    static void order() throws OrderException {
        try {
            deliver();
        } catch (DeliveryException d) {
            throw new OrderException("주문 실패", d);   // 하위 예외를 원인으로
        }
    }
    public static void main(String[] args) {
        try {
            order();
        } catch (OrderException e) {
            System.out.println("고객이 받은 예외: " + e.getMessage());
            System.out.println("원인: " + e.getCause().getMessage());
            e.printStackTrace();
        }
    }
}
class DeliveryException extends Exception {
    DeliveryException(String msg) { super(msg); }
}
class OrderException extends Exception {
    OrderException(String msg, Throwable cause) { super(msg, cause); }
}
/* 출력
고객이 받은 예외: 주문 실패
원인: 배송 문제
OrderException: 주문 실패
	at ChainingTest.order(ChainingTest.java:9)
	at ChainingTest.main(ChainingTest.java:14)
Caused by: DeliveryException: 배송 문제
	at ChainingTest.deliver(ChainingTest.java:3)
	at ChainingTest.order(ChainingTest.java:7)
	... 1 more
*/
```

- `Caused by:` 아래가 원래 원인 → 디버깅은 여기서 시작

### 예: 서블릿에서의 예외 변환

- `HttpServlet.doGet()` 은 `throws ServletException, IOException` 으로 선언됨
- 재정의한 `doGet()` 은 다른 checked 예외(`SomeCheckedException`)를 throws 할 수 없음
- → catch 해서 `ServletException` 으로 감싸 던짐 → 컨테이너가 에러 페이지로 연결

```java
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            throw new SomeCheckedException();
        } catch (SomeCheckedException e) {
            throw new ServletException(e);      // 원인을 담아 변환
        }
    }
}
```
