package day03.b_overriding;

import java.io.FileNotFoundException;

/*
 * 메서드 오버라이딩 (overriding) = 부모에게 물려받은 메서드를 자식이 "다시 정의"
 *
 * 오버라이딩의 조건
 *  1. 메서드 이름이 같아야 한다
 *  2. 매개 변수의 개수, 타입, 순서가 같아야 한다
 *  3. 리턴 타입이 같아야 한다
 *  4. 접근 제한자는 부모보다 범위가 넓거나 같아야 한다
 *  5. 조상보다 더 큰 예외를 던질 수 없다
 *
 * super 키워드 = "내 안의 부모 부분"   (this = 나 자신)
 *  - super.메서드() : 재정의하기 전 "부모의 원래 메서드" 호출
 *  - super.변수     : 이름이 겹칠 때 "부모의 변수"
 *  - super(...)     : 부모의 생성자 호출 (생성자 첫 줄에서만)
 */
public class SpiderMan2 extends Person {
    Spider spider = new Spider();
    boolean isSpider;
    String power = "거미의 힘";              // 부모와 같은 이름의 변수

    // ===== super() : 부모 생성자 호출 =====
    SpiderMan2() {
        // super();   ← 안 써도 컴파일러가 자동으로 넣어준다 → Person() 실행
        System.out.println("SpiderMan2() 생성자");
    }

    SpiderMan2(String name, boolean isSpider) {
        super(name);                         // Person(String) 호출 → name 초기화는 부모에게 맡김
        this.isSpider = isSpider;            // 내 것은 내가 초기화
        System.out.println("SpiderMan2(String, boolean) 생성자");
    }

    void fireWeb() {
        System.out.println("슉슉~~");
    }

    // ===== super.메서드() : 부모의 원래 메서드 =====
    @Override
    void jump() {
        if (isSpider) {
            spider.jump();                   // 거미일 때 : 탐나는 Spider의 jump
        } else {
            super.jump();                    // 사람일 때 : 부모(Person)의 jump 그대로
            // jump();                       // ❌ 이렇게 쓰면 자기 자신(SpiderMan2.jump)을 다시 호출 → 무한 반복
        }
    }

    // super.메서드()로 부모 기능 + 내 기능 덧붙이기
    void powerJump() {
        super.jump();                        // 부모 점프 먼저
        System.out.println("+ 거미줄 반동으로 한 번 더!");
    }

    // ===== super.변수 : 이름이 겹칠 때 =====
    void showPower() {
        System.out.println("power       : " + power);         // 내 것 (this.power)
        System.out.println("this.power  : " + this.power);    // 내 것
        System.out.println("super.power : " + super.power);   // 부모 것
        System.out.println("name        : " + name);          // 겹치지 않으면 그냥 써도 부모 것
    }

    // ===== 조건 4 : 접근 제한자는 같거나 넓게 =====
    @Override
    public int getAge() {                    // protected → public (넓힘) ⭕
        return super.getAge() + 5;           // 부모 값(20)을 활용해서 25
    }
    // ❌ private int getAge()              // 좁힘 → 컴파일 에러
    // ❌ int getAge()                      // default는 protected보다 좁음 → 컴파일 에러

    // ===== 조건 5 : 부모보다 큰 예외는 못 던짐 =====
    @Override
    void read() throws FileNotFoundException {   // IOException의 자식 예외 ⭕
        System.out.println("거미줄 위에서 책을 읽는다");
    }
    // ❌ void read() throws Exception       // Exception은 IOException보다 큼 → 컴파일 에러

    // ===== @Override가 잡아주는 실수들 =====
    // @Override void jump(int height) {}  // 파라미터 다름 → 오버로딩이 됨 → 에러로 알려줌
    // @Override void jmup() {}            // 오타 → 부모에 없는 메서드 → 에러로 알려줌
    // @Override int jump() { return 0; }  // 리턴 타입 다름 → 에러
}
