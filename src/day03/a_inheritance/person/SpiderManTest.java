package day03.a_inheritance.person;

/*
 * [실행 결과]
 * --- 거미 모드 ---
 * 냠냠
 * 자기 키 * 100배 jump
 * 슉슉~~
 * --- 사람 모드 ---
 * 냠냠
 * 두 다리로 폴짝
 * 사람일 때는 안되지.
 */
public class SpiderManTest {
    public static void main(String[] args) {
        // TODO: SpiderMan 객체를 만들고 기능을 사용해보자.

        // ❌ new SpiderMan(new Spider()) → 컴파일 에러
        //    파라미터 1개짜리 생성자가 없다. (Spider, boolean) 두 개를 넘겨야 함
        System.out.println("--- 거미 모드 ---");
        SpiderMan sman = new SpiderMan(new Spider(), true);
        sman.eat();       // Person에게 물려받은 메서드
        sman.jump();      // 재정의한 메서드 → 거미 jump
        sman.fireWeb();   // SpiderMan에 새로 만든 메서드

        System.out.println("--- 사람 모드 ---");
        sman.isSpider = false;
        sman.eat();
        sman.jump();      // super.jump() → 사람 jump
        sman.fireWeb();
        // END
    }
}
