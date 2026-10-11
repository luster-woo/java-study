package day04.c_need;

/*
 * 인터페이스의 필요성 ①② : 구현의 강제로 표준화 → 손쉬운 모듈 교체
 *
 *  - Printer 인터페이스가 print()를 "강제" → 어떤 프린터든 print()는 반드시 있다 (표준화)
 *  - Office는 Printer 인터페이스만 알고, 실제 프린터가 뭔지는 모른다
 *  - 그래서 프린터를 바꿔도 Office 코드는 한 줄도 안 바뀐다 (모듈 교체)
 *
 *  실제 예 : JDBC
 *   - 자바 프로그램은 JDBC 인터페이스만 사용
 *   - MySQL, Oracle 회사가 각자 구현 클래스(드라이버)를 제공
 *   - DB를 바꿔도 드라이버만 갈아 끼우면 됨
 *
 * [실행 결과]
 * --- 잉크젯 사용 ---
 * [잉크젯] 보고서.pdf 출력 (컬러)
 * --- 레이저로 교체 ---
 * [레이저] 보고서.pdf 출력 (고속)
 * --- 3D 프린터로 교체 ---
 * [3D] 보고서.pdf 입체 출력
 */

interface Printer {
    void print(String file);   // 모든 프린터가 반드시 가져야 할 기능 (표준)
}

// ❌ print()를 안 만들면 컴파일 에러 → 표준을 안 지킨 프린터는 존재할 수 없다
// class BrokenPrinter implements Printer { }

class InkjetPrinter implements Printer {
    @Override
    public void print(String file) {
        System.out.println("[잉크젯] " + file + " 출력 (컬러)");
    }
}

class LaserPrinter implements Printer {
    @Override
    public void print(String file) {
        System.out.println("[레이저] " + file + " 출력 (고속)");
    }
}

class Printer3D implements Printer {     // 나중에 새로 산 프린터
    @Override
    public void print(String file) {
        System.out.println("[3D] " + file + " 입체 출력");
    }
}

// 사용하는 쪽 : Printer 인터페이스에만 의존 (has a)
class Office {
    private Printer printer;

    Office(Printer printer) {
        this.printer = printer;
    }

    void setPrinter(Printer printer) {   // 프린터 교체
        this.printer = printer;
    }

    void work() {
        printer.print("보고서.pdf");      // 어떤 프린터인지 몰라도 된다
    }
}

public class PrinterTest {
    public static void main(String[] args) {
        Office office = new Office(new InkjetPrinter());
        System.out.println("--- 잉크젯 사용 ---");
        office.work();

        System.out.println("--- 레이저로 교체 ---");
        office.setPrinter(new LaserPrinter());   // 갈아 끼우기만
        office.work();                           // Office 코드는 그대로

        System.out.println("--- 3D 프린터로 교체 ---");
        office.setPrinter(new Printer3D());      // 새 제품이 나와도 OK
        office.work();
    }
}
