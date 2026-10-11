package day04.c_need;

/*
 * 인터페이스의 필요성 ④ : 모듈 간 독립적 프로그래밍 → 개발 기간 단축
 *
 *  계산기를 두 팀이 나눠 개발
 *   - A팀 : 화면(UI)       → CalculatorClient
 *   - B팀 : 계산 로직      → CalculatorImpl
 *
 *  순서
 *   1. 두 팀이 Calculator 인터페이스를 먼저 합의 (메서드 이름, 파라미터, 리턴 타입)
 *   2. A팀은 B팀을 기다리지 않고, 대충 만든 Stub(가짜 구현)으로 UI 개발
 *   3. B팀은 진짜 로직 CalculatorImpl 구현
 *   4. 완성되면 Stub → Impl 로 교체 (A팀 코드는 그대로)
 *
 * [실행 결과]
 * --- A팀 : Stub으로 UI 먼저 개발 ---
 * 파라미터 확인: 3, 4
 * 결과: 3+4=0
 * 파라미터 확인: 10, 5
 * 결과: 10-5=0
 * --- B팀 완성 후 Impl로 교체 ---
 * 결과: 3+4=7
 * 결과: 10-5=5
 */

// 1. 두 팀이 합의한 약속
interface Calculator {
    int add(int a, int b);
    int sub(int a, int b);
}

// 2. A팀용 임시 구현 : 호출 방식과 리턴 타입만 맞춘 가짜
class CalculatorStub implements Calculator {
    @Override
    public int add(int a, int b) {
        System.out.printf("파라미터 확인: %d, %d%n", a, b);
        return 0;                         // 계산은 안 함
    }

    @Override
    public int sub(int a, int b) {
        System.out.printf("파라미터 확인: %d, %d%n", a, b);
        return 0;
    }
}

// 3. B팀의 진짜 로직
class CalculatorImpl implements Calculator {
    @Override public int add(int a, int b) { return a + b; }
    @Override public int sub(int a, int b) { return a - b; }
}

// A팀이 만드는 화면 : Calculator 인터페이스만 알고 있다 (has a)
class CalculatorClient {
    private Calculator calcLogic;

    CalculatorClient(Calculator calcLogic) {
        this.calcLogic = calcLogic;
    }

    void add(int a, int b) {
        System.out.printf("결과: %d+%d=%d%n", a, b, calcLogic.add(a, b));
    }

    void sub(int a, int b) {
        System.out.printf("결과: %d-%d=%d%n", a, b, calcLogic.sub(a, b));
    }
}

public class CalculatorTest {
    public static void main(String[] args) {
        System.out.println("--- A팀 : Stub으로 UI 먼저 개발 ---");
        CalculatorClient client = new CalculatorClient(new CalculatorStub());
        client.add(3, 4);
        client.sub(10, 5);

        System.out.println("--- B팀 완성 후 Impl로 교체 ---");
        client = new CalculatorClient(new CalculatorImpl());   // 여기만 바뀜
        client.add(3, 4);
        client.sub(10, 5);
    }
}
