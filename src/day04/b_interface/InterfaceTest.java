package day04.b_interface;

/*
 * 인터페이스 (interface) = "이 기능을 할 수 있다"는 약속(규격)
 *
 *  - 메서드는 자동으로 public abstract  (안 써도 붙음)
 *  - 변수는 자동으로 public static final (상수만 가능)
 *  - 생성자 X, new 불가
 *  - 클래스는 implements로 "구현" → 약속한 메서드를 전부 만들어야 함
 *  - 여러 개 동시에 구현 가능 (클래스 상속은 1개만 되는 것과 차이!)
 *
 *  추상 클래스 vs 인터페이스
 *   - 추상 클래스 : "~이다"   (is a)     Duck은 Animal이다
 *   - 인터페이스  : "~할 수 있다" (can do) Duck은 날 수 있다, 헤엄칠 수 있다
 *
 * [실행 결과]
 * --- 오리 ---
 * 오리 : 꽥꽥
 * 오리 : 날개로 푸드덕
 * 오리 : 발로 헤엄
 * --- 날 수 있는 것들 ---
 * 오리 : 날개로 푸드덕
 * 비행기 : 제트 엔진으로 비행
 * 슈퍼맨 : 망토 펄럭이며 비행
 * --- 상수 ---
 * 최대 고도 : 10000m
 */

// ===== 인터페이스 : 능력(기능)의 약속 =====
interface Flyable {
    int MAX_HEIGHT = 10000;      // = public static final int MAX_HEIGHT

    void fly();                  // = public abstract void fly();
}

interface Swimmable {
    void swim();
}

// ===== 추상 클래스 : 정체성(무엇인가) =====
abstract class Animal {
    String name;
    Animal(String name) { this.name = name; }
    abstract void sound();
}

// ===== 상속은 1개 + 인터페이스는 여러 개 =====
class Duck extends Animal implements Flyable, Swimmable {
    Duck() { super("오리"); }

    @Override void sound()      { System.out.println(name + " : 꽥꽥"); }
    @Override public void fly() { System.out.println(name + " : 날개로 푸드덕"); }  // public 필수!
    @Override public void swim(){ System.out.println(name + " : 발로 헤엄"); }
}

// 동물이 아니어도 "날 수 있다"는 약속만 지키면 같은 타입으로 묶인다
class Airplane implements Flyable {
    @Override
    public void fly() { System.out.println("비행기 : 제트 엔진으로 비행"); }
}

class Superman implements Flyable {
    @Override
    public void fly() { System.out.println("슈퍼맨 : 망토 펄럭이며 비행"); }
}

public class InterfaceTest {

    // 파라미터를 인터페이스 타입으로 → 날 수 있는 거면 뭐든 받는다
    static void takeOff(Flyable f) {
        f.fly();
    }

    public static void main(String[] args) {
        // Flyable f = new Flyable();   // ❌ 인터페이스는 객체 생성 불가

        System.out.println("--- 오리 ---");
        Duck duck = new Duck();
        duck.sound();
        duck.fly();
        duck.swim();

        System.out.println("--- 날 수 있는 것들 ---");
        Flyable[] flyers = { duck, new Airplane(), new Superman() };   // 서로 남남인데 한 배열에!
        for (Flyable f : flyers) {
            takeOff(f);
            // f.swim();                // ❌ Flyable 타입엔 swim()이 없음
        }

        System.out.println("--- 상수 ---");
        System.out.println("최대 고도 : " + Flyable.MAX_HEIGHT + "m");
        // Flyable.MAX_HEIGHT = 20000;  // ❌ 자동으로 final이라 변경 불가
    }
}
