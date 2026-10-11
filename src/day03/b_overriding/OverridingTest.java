package day03.b_overriding;

import java.io.IOException;

/*
 * [실행 결과]
 * --- 1. super() : 생성자 ---
 * Person() 생성자
 * SpiderMan2() 생성자
 * Person(String) 생성자 : 피터
 * SpiderMan2(String, boolean) 생성자
 * --- 2. super.메서드() ---
 * 두 다리로 힘껏 점프
 * 키 * 1000만큼 엄청난 점프
 * 두 다리로 힘껏 점프
 * + 거미줄 반동으로 한 번 더!
 * --- 3. super.변수 ---
 * power       : 거미의 힘
 * this.power  : 거미의 힘
 * super.power : 평범한 사람
 * name        : 피터
 * --- 4. 조건 4, 5 ---
 * 25
 * 거미줄 위에서 책을 읽는다
 * --- 5. 부모 타입으로 받아도? ---
 * 키 * 1000만큼 엄청난 점프
 */
public class OverridingTest {
    public static void main(String[] args) throws IOException {
        System.out.println("--- 1. super() : 생성자 ---");
        SpiderMan2 s1 = new SpiderMan2();                  // 부모 생성자가 항상 먼저 실행
        SpiderMan2 sman = new SpiderMan2("피터", false);

        System.out.println("--- 2. super.메서드() ---");
        sman.jump();                 // 사람 모드 → super.jump()
        sman.isSpider = true;
        sman.jump();                 // 거미 모드 → spider.jump()
        sman.powerJump();            // super.jump() + 추가 기능

        System.out.println("--- 3. super.변수 ---");
        sman.showPower();

        System.out.println("--- 4. 조건 4, 5 ---");
        System.out.println(sman.getAge());
        sman.read();

        // 미리보기 (다형성) : Person 타입 변수에 담아도 실제 객체가 SpiderMan2면
        //                    재정의된 jump()가 실행된다
        System.out.println("--- 5. 부모 타입으로 받아도? ---");
        Person p = sman;
        p.jump();
    }
}
