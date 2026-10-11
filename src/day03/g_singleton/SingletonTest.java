package day03.g_singleton;

/*
 * [실행 결과]
 * Printer 객체 생성 (딱 한 번만 출력됨)
 * [1번째 출력] 보고서
 * [2번째 출력] 이력서
 * [3번째 출력] 영수증
 * p1 == p2 ? true
 * p2 == p3 ? true
 */
public class SingletonTest {
    public static void main(String[] args) {
        // Printer p = new Printer();   // ❌ 생성자가 private이라 컴파일 에러

        Printer p1 = Printer.getInstance();
        Printer p2 = Printer.getInstance();
        Printer p3 = Printer.getInstance();

        p1.print("보고서");
        p2.print("이력서");          // 같은 객체라 카운트가 이어진다
        p3.print("영수증");

        System.out.println("p1 == p2 ? " + (p1 == p2));   // 같은 주소
        System.out.println("p2 == p3 ? " + (p2 == p3));
    }
}
