package day03.a_inheritance.person;

/*
 * 클래스 간의 두 가지 관계
 *  1. is a  (상속)   : SpiderMan "은" Person "이다"   → extends Person
 *                     → Person의 name, eat(), jump()를 물려받아 그냥 쓴다
 *  2. has a (포함)   : SpiderMan "은" Spider 를 "가지고 있다" → 멤버 변수 Spider spider
 *                     → Java는 상속을 하나만 할 수 있으니(단일 상속),
 *                       Spider 기능은 객체를 가지고 있다가 시켜서(위임) 쓴다
 */
public class SpiderMan extends Person {     // is a
    Spider spider;                          // has a
    boolean isSpider = true;

    // 기본 생성자
    SpiderMan() {}

    // 파라미터 생성자
    SpiderMan(Spider spider, boolean isSpider) {
        this.spider = spider;
        this.isSpider = isSpider;
    }

    // SpiderMan만의 새 기능 : 거미일 때는 가지고 있는 spider에게 시킨다(위임)
    void fireWeb() {
        if (this.isSpider) {
            spider.fireWeb();
        } else {
            System.out.println("사람일 때는 안되지.");
        }
    }

    // (추가) 메서드 재정의(오버라이딩) : 부모의 jump()를 내 상황에 맞게 다시 정의
    //  → 이게 없으면 sman.jump()는 Person의 "두 다리로 폴짝"이 실행된다
    @Override
    void jump() {
        if (this.isSpider) {
            spider.jump();
        } else {
            super.jump();                   // 부모(Person)의 jump() 호출
        }
    }
}
