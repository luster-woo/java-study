package day08.a_proxy;

import java.util.Random;

/*
 * TODO: Proxy 패턴 - 프로그래머와 SSAFY 교육생의 하루
 *
 *  Client ──요청──▶ PersonProxy ──위임──▶ Programmer / Ssafy (Target)
 *                     │
 *                     └─ 앞뒤 공통 일과(부가 기능)는 프록시가 처리
 *
 *  try-catch-finally 위치 = 다음 강의의 Advice 타입
 *   - target 호출 전            → @Before
 *   - 정상 종료 후 (Push)        → @AfterReturning
 *   - 예외 발생 시 (조퇴)        → @AfterThrowing
 *   - finally (마무리)          → @After
 *   - 이 메서드 전체             → @Around
 */
public class ProxyTest {
    public static void main(String[] args) {
        // 클라이언트는 Person 타입으로만 사용 → 프록시인지 진짜인지 모름
        Person p1 = new PersonProxy(new Programmer());
        p1.coding();
        System.out.println("-----");
        Person p2 = new PersonProxy(new Ssafy());
        p2.coding();

        System.out.println("----- 프록시 없이 Target만 -----");
        Person raw = new Ssafy();
        raw.coding();   // 부가 기능 없이 핵심만 실행
    }

    // 프록시와 Target이 함께 구현하는 공통 인터페이스
    interface Person {
        void coding();
    }

    // Target 1: 핵심 로직만 가짐
    static class Programmer implements Person {
        @Override
        public void coding() { System.out.println("[Programmer] 서비스 코드를 작성한다."); }
    }

    // Target 2: 핵심 로직만 가짐
    static class Ssafy implements Person {
        @Override
        public void coding() { System.out.println("[Ssafy] 실습 코드를 작성한다."); }
    }

    static class OuchException extends RuntimeException {
    }

    // Proxy: Target과 같은 인터페이스 + 안에 Target을 품고 있음
    static class PersonProxy implements Person {
        private final Person target;

        PersonProxy(Person target) { this.target = target; }

        @Override
        public void coding() {
            System.out.println("컴퓨터를 부팅한다.");            // @Before
            try {
                target.coding();                                // 핵심 로직은 Target에 위임
                if (new Random().nextBoolean())
                    throw new OuchException();                  // 일부러 문제 발생
                System.out.println("Git에 Push 한다.");          // @AfterReturning
            } catch (OuchException e) {
                System.out.println("조퇴를 한다.");               // @AfterThrowing
            } finally {
                System.out.println("보람찬 하루를 마무리한다.");    // @After
            }
        }
    }
}
/*
 * [실행 결과] (Random이라 실행마다 Push/조퇴가 갈림, 예시)
 * 컴퓨터를 부팅한다.
 * [Programmer] 서비스 코드를 작성한다.
 * 조퇴를 한다.
 * 보람찬 하루를 마무리한다.
 * -----
 * 컴퓨터를 부팅한다.
 * [Ssafy] 실습 코드를 작성한다.
 * Git에 Push 한다.
 * 보람찬 하루를 마무리한다.
 * ----- 프록시 없이 Target만 -----
 * [Ssafy] 실습 코드를 작성한다.
 */
