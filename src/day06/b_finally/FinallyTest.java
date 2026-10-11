package day06.b_finally;

/*
 * finally = 예외가 나든 안 나든 "항상" 실행되는 블록
 *  - try에서 return을 만나도 finally는 실행된 뒤에 메서드가 끝난다
 *  - 주 용도 : 자원 반납 (파일, 네트워크, DB 연결 닫기)
 *
 * try-with-resources (Java 7+)
 *   try (자원 선언) { ... }   → 블록이 끝나면 close()를 자동 호출
 *  - 자원은 AutoCloseable 구현 클래스여야 함
 *  - 여러 개면 ; 로 구분, 닫히는 순서는 선언의 "역순"
 *  - close는 try 블록이 끝나는 순간 → catch / finally보다 "먼저"
 *
 * [실행 결과]
 * --- 1. finally 기본 ---
 * try 결과=5 finally 끝
 * try catch finally 끝
 * --- 2. try 안에서 return ---
 * try에서 return 직전
 * 그래도 finally 실행
 * 반환값 : try의 값
 * --- 3. 퀴즈 : 반환값은? ---
 * finally에서 x = 20
 * 반환값 : 10
 * --- 4. 예전 방식 : finally에서 close ---
 *   A 열기
 *   A 사용
 *   catch : A 사용 중 오류
 *   A 닫기
 * --- 5. try-with-resources ---
 *   B 열기
 *   B 사용
 *   B 닫기
 * --- 6. 예외가 나면 close 시점은? ---
 *   C 열기
 *   C 사용
 *   C 닫기
 *   catch : C 사용 중 오류
 *   finally
 * --- 7. 자원 여러 개 ---
 *   D 열기
 *   E 열기
 *   D 사용
 *   E 사용
 *   E 닫기
 *   D 닫기
 */

// 연습용 자원 : 열고, 쓰고, 닫는 과정이 출력된다
class MyResource implements AutoCloseable {
    private final String name;

    MyResource(String name) {
        this.name = name;
        System.out.println("  " + name + " 열기");
    }

    void use(boolean fail) {
        System.out.println("  " + name + " 사용");
        if (fail) throw new IllegalStateException(name + " 사용 중 오류");
    }

    @Override
    public void close() {
        System.out.println("  " + name + " 닫기");
    }
}

public class FinallyTest {
    public static void main(String[] args) {

        System.out.println("--- 1. finally 기본 ---");
        divide(10, 2);
        divide(10, 0);

        System.out.println("--- 2. try 안에서 return ---");
        System.out.println("반환값 : " + returnTest());

        System.out.println("--- 3. 퀴즈 : 반환값은? ---");
        System.out.println("반환값 : " + quiz());

        System.out.println("--- 4. 예전 방식 : finally에서 close ---");
        MyResource r = null;
        try {
            r = new MyResource("A");
            r.use(true);
        } catch (IllegalStateException e) {
            System.out.println("  catch : " + e.getMessage());
        } finally {
            if (r != null) r.close();                    // null 체크까지 직접 → 길고 실수하기 쉬움
        }

        System.out.println("--- 5. try-with-resources ---");
        try (MyResource res = new MyResource("B")) {
            res.use(false);
        }                                                // catch 없이도 OK, 여기서 자동 close

        System.out.println("--- 6. 예외가 나면 close 시점은? ---");
        try (MyResource res = new MyResource("C")) {
            res.use(true);
        } catch (IllegalStateException e) {
            System.out.println("  catch : " + e.getMessage());   // 이미 닫힌 뒤!
        } finally {
            System.out.println("  finally");
        }

        System.out.println("--- 7. 자원 여러 개 ---");
        try (MyResource d = new MyResource("D");
             MyResource e = new MyResource("E")) {
            d.use(false);
            e.use(false);
        }                                                // 역순으로 닫힘 : E → D
    }

    static void divide(int a, int b) {
        try {
            System.out.print("try ");
            System.out.print("결과=" + (a / b) + " ");
        } catch (ArithmeticException e) {
            System.out.print("catch ");
        } finally {
            System.out.print("finally ");
        }
        System.out.println("끝");
    }

    static String returnTest() {
        try {
            System.out.println("try에서 return 직전");
            return "try의 값";
        } finally {
            System.out.println("그래도 finally 실행");
        }
    }

    static int quiz() {
        int x = 10;
        try {
            return x;                                    // 이 순간의 값 10이 반환값으로 "저장"됨
        } finally {
            x = 20;                                      // 변수만 바뀌고 저장된 반환값은 그대로
            System.out.println("finally에서 x = " + x);
        }
        // ⚠ finally 안에서 return 하면 예외까지 삼켜버리므로 절대 금지
    }
}
