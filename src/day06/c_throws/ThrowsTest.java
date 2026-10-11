package day06.c_throws;

import java.io.FileNotFoundException;
import java.io.IOException;

/*
 * throw  : 예외를 직접 "발생"     → throw new XXException("메시지");
 * throws : 예외를 호출한 쪽에 "위임" → void method() throws XXException
 *
 *  - checked 예외  : 직접 try-catch 하거나 throws로 넘기거나 둘 중 하나는 필수
 *  - unchecked 예외 : throws 안 써도 알아서 호출한 쪽으로 올라감
 *  - 재정의 메서드는 부모보다 "큰" checked 예외를 throws 할 수 없다
 *  - 예외 변환(Chaining) : 하위 예외를 원인(cause)으로 담아 상위 계층에 맞는 예외로 바꿔 던지기
 *
 * [실행 결과]
 * --- 1. throw : 직접 예외 발생 ---
 * 나이 설정 : 25
 * 잡음 : 나이는 음수일 수 없음 : -5
 * --- 2. 예외 전파 : a → b → c ---
 * main에서 잡음 : c에서 터짐
 * 호출 스택 : c ← b ← a ← main ← JVM
 * --- 3. checked 예외 위임 ---
 * 설정 로딩 실패 : app.properties 을(를) 찾을 수 없음
 * --- 4. 예외 변환 (Chaining) ---
 * 고객이 받은 예외 : 주문 실패
 * 원인(cause)     : 배송 기사 없음
 * 원인 타입       : DeliveryException
 */

class Parent {
    void read() throws IOException {}
}

class Child extends Parent {
    @Override
    void read() throws FileNotFoundException {}          // OK : IOException의 자식 (더 작은 예외)
    // void read() throws Exception {}                    // ❌ 컴파일 에러 : 부모보다 큰 예외
}

class DeliveryException extends Exception {
    DeliveryException(String msg) { super(msg); }
}

class OrderException extends Exception {
    OrderException(String msg, Throwable cause) { super(msg, cause); }   // 원인을 같이 받는 생성자
}

public class ThrowsTest {
    public static void main(String[] args) {

        System.out.println("--- 1. throw : 직접 예외 발생 ---");
        try {
            setAge(25);
            setAge(-5);
            setAge(30);                                  // 실행 X
        } catch (IllegalArgumentException e) {
            System.out.println("잡음 : " + e.getMessage());
        }

        System.out.println("--- 2. 예외 전파 : a → b → c ---");
        try {
            a();
        } catch (RuntimeException e) {
            System.out.println("main에서 잡음 : " + e.getMessage());
            System.out.print("호출 스택 : ");
            for (StackTraceElement s : e.getStackTrace()) {   // printStackTrace가 보여주는 그 정보
                System.out.print(s.getMethodName() + " ← ");
            }
            System.out.println("JVM");
        }

        System.out.println("--- 3. checked 예외 위임 ---");
        try {
            loadConfig("app.properties");
        } catch (IOException e) {                         // 위임받은 쪽에서 결국 처리
            System.out.println("설정 로딩 실패 : " + e.getMessage());
        }

        System.out.println("--- 4. 예외 변환 (Chaining) ---");
        try {
            order();
        } catch (OrderException e) {
            System.out.println("고객이 받은 예외 : " + e.getMessage());
            System.out.println("원인(cause)     : " + e.getCause().getMessage());
            System.out.println("원인 타입       : " + e.getCause().getClass().getSimpleName());
            // e.printStackTrace() 하면 맨 아래에 "Caused by: DeliveryException" 이 찍힘 → 디버깅 시작점
        }
    }

    static void setAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("나이는 음수일 수 없음 : " + age);
        }
        System.out.println("나이 설정 : " + age);
    }

    static void a() { b(); }
    static void b() { c(); }
    static void c() { throw new IllegalStateException("c에서 터짐"); }   // unchecked → throws 없이도 올라감

    // 여기서 처리하지 않고 호출한 쪽에 맡긴다 (어떻게 대처할지는 호출한 쪽이 더 잘 앎)
    static void loadConfig(String file) throws IOException {
        if (!file.endsWith(".json")) {
            throw new FileNotFoundException(file + " 을(를) 찾을 수 없음");
        }
    }

    static void deliver() throws DeliveryException {
        throw new DeliveryException("배송 기사 없음");
    }

    static void order() throws OrderException {
        try {
            deliver();
        } catch (DeliveryException d) {
            throw new OrderException("주문 실패", d);       // 고객 입장에서 의미 있는 예외로 바꾸고, 원인은 보관
        }
    }
}
