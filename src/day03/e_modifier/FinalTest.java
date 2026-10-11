package day03.e_modifier;

/*
 * 접근 제한자 외의 제한자 (활용 제한자)
 *  - static   : 클래스 소속, 모든 객체가 공유 (Day02에서 배움)
 *  - final    : "마지막" → 더 이상 바꿀 수 없음
 *                 final 변수   : 값 변경 불가 (상수)
 *                 final 메서드 : 오버라이딩 불가
 *                 final 클래스 : 상속 불가 (예 : String)
 *  - abstract : 미완성 → 자식이 완성해야 함 (Day04에서 배움)
 *  - sealed / non-sealed : 상속할 수 있는 자식을 지정 (d_sealed 참고)
 */
public class FinalTest {
    // final 변수 : 선언과 동시에 또는 생성자에서 딱 한 번 값을 넣고, 이후 변경 불가
    final int MAX = 100;
    static final double PI = 3.14;      // static final = 클래스 상수 (이름은 대문자 관례)

    void change() {
        // MAX = 200;                   // ❌ final 변수는 값 변경 불가
        final int local = 10;
        // local = 20;                  // ❌ 지역 변수도 final이면 변경 불가
        System.out.println("MAX = " + MAX + ", PI = " + PI + ", local = " + local);
    }
}

class Car {
    final void brake() {                // final 메서드
        System.out.println("브레이크는 바꿀 수 없음");
    }
}

class SportsCar extends Car {
    // @Override void brake() {}        // ❌ final 메서드는 오버라이딩 불가
}

final class Engine {}                   // final 클래스
// class TurboEngine extends Engine {} // ❌ final 클래스는 상속 불가

// class MyString extends String {}    // ❌ String도 final 클래스라 상속 불가
