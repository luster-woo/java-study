package day06.a_exception;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*
 * 예외의 처리 = 예외가 나도 프로그램이 "비정상 종료되지 않게" 수습하는 것
 *
 *  Throwable
 *   ├─ Error              : 메모리 부족 등 → 코드로 수습 X
 *   └─ Exception
 *       ├─ IOException ...      → checked   : 처리 안 하면 컴파일 에러
 *       └─ RuntimeException ... → unchecked : 처리 안 해도 컴파일 O (터지면 종료)
 *
 *   try { 예외가 날 수 있는 코드 } catch (XXException e) { 예외가 났을 때 할 일 }
 *
 *  - 예외가 나면 그 줄 아래 try 코드는 건너뛰고 바로 catch로 점프
 *  - catch는 위에서부터 검사 → 자식 예외를 먼저, 부모(Exception)는 맨 마지막
 *
 * [실행 결과]
 * --- 1. 처리 안 하면 ---
 * (주석 처리해 둠)
 * --- 2. try-catch 흐름 ---
 * code1 code3 code4
 * code1 code2 code4
 * --- 3. 예외 객체 정보 ---
 * getMessage   : Index 2 out of bounds for length 1
 * toString     : java.lang.ArrayIndexOutOfBoundsException: Index 2 out of bounds for length 1
 * 클래스 이름  : ArrayIndexOutOfBoundsException
 * --- 4. 다중 catch ---
 * 0 : 배열 범위 초과
 * 1 : null 참조
 * 2 : 멀티 catch → NumberFormatException
 * 3 : 멀티 catch → ArithmeticException
 * --- 5. checked 예외 ---
 * 파일이 없어요
 * --- 6. 부모 타입으로 잡기 ---
 * RuntimeException으로 받음 : ClassCastException
 * 프로그램 정상 종료
 */
public class ExceptionTest {
    public static void main(String[] args) {

        System.out.println("--- 1. 처리 안 하면 ---");
        int[] arr = {10};
        // System.out.println(arr[2]);                   // 주석 풀면 여기서 프로그램 종료 (아래 줄 실행 X)
        System.out.println("(주석 처리해 둠)");

        System.out.println("--- 2. try-catch 흐름 ---");
        run(0);                                          // 예외 O : code1 → code3 → code4
        run(1);                                          // 예외 X : code1 → code2 → code4

        System.out.println("--- 3. 예외 객체 정보 ---");
        try {
            System.out.println(arr[2]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("getMessage   : " + e.getMessage());
            System.out.println("toString     : " + e);                     // 클래스 이름 + 메시지
            System.out.println("클래스 이름  : " + e.getClass().getSimpleName());
            // e.printStackTrace();                       // 실제로는 이걸 제일 많이 씀 (어디서 터졌는지 추적)
        }

        System.out.println("--- 4. 다중 catch ---");
        String str = null;
        for (int i = 0; i < 4; i++) {
            try {
                if (i == 0) System.out.println(arr[5]);
                if (i == 1) System.out.println(str.length());
                if (i == 2) System.out.println(Integer.parseInt("abc"));
                if (i == 3) System.out.println(10 / 0);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(i + " : 배열 범위 초과");
            } catch (NullPointerException e) {
                System.out.println(i + " : null 참조");
            } catch (NumberFormatException | ArithmeticException e) {      // 처리가 같으면 | 로 묶기 (멀티 catch)
                System.out.println(i + " : 멀티 catch → " + e.getClass().getSimpleName());
            } catch (Exception e) {                                         // 나머지 전부 (반드시 맨 마지막!)
                System.out.println(i + " : 기타 " + e);
            }
            // 순서를 바꿔서 Exception을 맨 위에 두면? → 아래 catch에 도달할 수 없어서 컴파일 에러
        }

        System.out.println("--- 5. checked 예외 ---");
        // new FileReader("없는파일.txt");              // ❌ 컴파일 에러 : unreported exception FileNotFoundException
        try {
            FileReader fr = new FileReader("없는파일.txt");
            fr.read();
            fr.close();
        } catch (FileNotFoundException e) {             // 자식 먼저
            System.out.println("파일이 없어요");
        } catch (IOException e) {                       // 부모 나중 (read, close에서 나는 예외)
            System.out.println("읽다가 오류");
        }

        System.out.println("--- 6. 부모 타입으로 잡기 ---");
        try {
            Object o = "hello";
            Integer n = (Integer) o;                     // ClassCastException
        } catch (RuntimeException e) {                   // 다형성 : 자식 예외는 부모 타입으로 받을 수 있다
            System.out.println("RuntimeException으로 받음 : " + e.getClass().getSimpleName());
        }

        System.out.println("프로그램 정상 종료");
    }

    static void run(int num) {
        try {
            System.out.print("code1 ");
            int i = 1 / num;
            System.out.print("code2 ");                  // 예외가 나면 건너뜀
        } catch (ArithmeticException e) {
            System.out.print("code3 ");
        }
        System.out.println("code4");
    }
}
