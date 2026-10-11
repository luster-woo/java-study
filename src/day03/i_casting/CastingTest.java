package day03.i_casting;

/*
 * 객체의 형변환 = 참조 변수의 "타입"을 바꾸는 것 (객체 자체는 그대로!)
 *  - 상속 관계(부모 ↔ 자식)에서만 가능
 *
 *  1. 업캐스팅 (자식 → 부모) : 자동. (Person) 생략 가능
 *       Person p = new SpiderMan();
 *       → 쓸 수 있는 멤버가 "줄어듦" (부모에 있는 것만)
 *
 *  2. 다운캐스팅 (부모 → 자식) : 직접 (SpiderMan) 써야 함
 *       SpiderMan s = (SpiderMan) p;
 *       → 쓸 수 있는 멤버가 "다시 늘어남"
 *       → 실제 객체가 그 자식이 아니면 실행 중 ClassCastException!
 *
 *  3. 그래서 다운캐스팅 전에는 instanceof로 확인
 *
 * [실행 결과]
 * --- 1. 업캐스팅 ---
 * 냠냠
 * 거미 점프
 * --- 2. 다운캐스팅 ---
 * 슉슉~~
 * s == p ? true
 * --- 3. 잘못된 다운캐스팅 ---
 * 에러 : ClassCastException
 * --- 4. instanceof로 안전하게 ---
 * 사람 : 거미줄 못 쏨
 * 스파이더맨 : 슉슉~~
 * --- 5. 주의 : 변수는 타입을 따라감 ---
 * p.name = 사람
 * s.name = 스파이더맨
 */

class Person {
    String name = "사람";

    void eat()  { System.out.println("냠냠"); }
    void jump() { System.out.println("두 다리 점프"); }
}

class SpiderMan extends Person {
    String name = "스파이더맨";          // 부모와 같은 이름의 변수

    @Override
    void jump()    { System.out.println("거미 점프"); }
    void fireWeb() { System.out.println("슉슉~~"); }   // 자식에만 있는 메서드
}

public class CastingTest {
    public static void main(String[] args) {

        System.out.println("--- 1. 업캐스팅 ---");
        SpiderMan sman = new SpiderMan();
        Person p = sman;                  // (Person) 생략 → 자동 형변환
        p.eat();                          // 부모 메서드 OK
        p.jump();                         // 재정의된 메서드 → 실제 객체(SpiderMan)의 jump
        // p.fireWeb();                   // ❌ 컴파일 에러 : Person 타입엔 fireWeb()이 없음

        System.out.println("--- 2. 다운캐스팅 ---");
        SpiderMan s = (SpiderMan) p;      // 직접 써야 함. 실제 객체가 SpiderMan이라 성공
        s.fireWeb();                      // 다시 쓸 수 있음
        System.out.println("s == p ? " + (s == p));   // 같은 객체! 리모컨(타입)만 바꿨을 뿐

        System.out.println("--- 3. 잘못된 다운캐스팅 ---");
        Person justPerson = new Person(); // 실제 객체가 그냥 Person
        try {
            SpiderMan fake = (SpiderMan) justPerson;  // 컴파일은 통과하지만...
            fake.fireWeb();
        } catch (ClassCastException e) {  // 실행 중 에러 (예외 처리는 Day06)
            System.out.println("에러 : ClassCastException");
        }

        System.out.println("--- 4. instanceof로 안전하게 ---");
        Person[] people = { new Person(), new SpiderMan() };
        for (Person each : people) {
            if (each instanceof SpiderMan spider) {   // 확인 + 형변환 한 번에 (Java 16~)
                System.out.print("스파이더맨 : ");
                spider.fireWeb();
            } else {
                System.out.println("사람 : 거미줄 못 쏨");
            }
        }
        // 옛날 방식 (Java 15 이하)
        // if (each instanceof SpiderMan) {
        //     SpiderMan spider = (SpiderMan) each;
        //     spider.fireWeb();
        // }

        System.out.println("--- 5. 주의 : 변수는 타입을 따라감 ---");
        // 메서드는 "실제 객체" 기준 (재정의된 것 실행)
        // 변수(필드)는 "참조 변수 타입" 기준 → 재정의 개념이 없다
        System.out.println("p.name = " + p.name);   // Person 타입 → 사람
        System.out.println("s.name = " + s.name);   // SpiderMan 타입 → 스파이더맨
    }
}
