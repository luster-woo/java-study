package day03.g_singleton;

/*
 * 싱글톤(Singleton) 패턴 = 객체를 프로그램 전체에서 "딱 하나만" 만들어 같이 쓰는 방법
 *
 * 만드는 법 3단계
 *  1. private static 변수에 자기 자신 객체를 하나 만들어 둔다
 *  2. 생성자를 private으로 막는다 → 밖에서 new 불가
 *  3. public static getInstance()로 그 하나를 꺼내 준다
 *
 * 언제 쓰나 : 하나만 있어야 하는 것 (설정, 로그, DB 연결 관리자, 프린터 등)
 *  → Spring의 Bean도 기본이 싱글톤
 */
public class Printer {
    // 1. 클래스가 로딩될 때 딱 한 번 생성 (static)
    private static Printer instance = new Printer();

    private int printCount = 0;

    // 2. 생성자 private → 외부에서 new Printer() 불가
    private Printer() {
        System.out.println("Printer 객체 생성 (딱 한 번만 출력됨)");
    }

    // 3. 만들어 둔 하나를 돌려준다 (static이라 객체 없이 Printer.getInstance()로 호출)
    public static Printer getInstance() {
        return instance;
    }

    public void print(String text) {
        printCount++;
        System.out.println("[" + printCount + "번째 출력] " + text);
    }
}
