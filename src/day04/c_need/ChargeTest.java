package day04.c_need;

/*
 * 인터페이스의 필요성 ③ : 상속 관계 없는 클래스에 관계 부여 → 다형성 확장
 *
 *  - HandPhone(부모 Phone)과 ElectricShaver(부모 Appliance)는 공통 조상이 Object뿐
 *  - badCase  : Object[]에 담으면 타입마다 instanceof + 형변환 → 타입이 늘수록 if도 늘어남
 *  - goodCase : 둘 다 Chargeable을 구현 → Chargeable[] 하나로 깔끔하게
 *
 * [실행 결과]
 * --- badCase ---
 * 휴대폰 충전
 * 면도기 충전
 * 무선 이어폰 충전
 * --- goodCase ---
 * 휴대폰 충전
 * 면도기 충전
 * 무선 이어폰 충전
 */

interface Chargeable {
    void charge();
}

class Phone {}
class HandPhone extends Phone implements Chargeable {
    @Override public void charge() { System.out.println("휴대폰 충전"); }
}

class Appliance {}
class ElectricShaver extends Appliance implements Chargeable {
    @Override public void charge() { System.out.println("면도기 충전"); }
}

class Earbuds implements Chargeable {     // 부모도 없는 새 제품
    @Override public void charge() { System.out.println("무선 이어폰 충전"); }
}

public class ChargeTest {
    public static void main(String[] args) {
        badCase();
        goodCase();
    }

    // ❌ 공통 조상이 Object뿐 → 타입마다 확인하고 형변환
    static void badCase() {
        System.out.println("--- badCase ---");
        Object[] objs = { new HandPhone(), new ElectricShaver(), new Earbuds() };
        for (Object obj : objs) {
            // obj.charge();   // ❌ Object 타입엔 charge()가 없음
            if (obj instanceof HandPhone phone) {
                phone.charge();
            } else if (obj instanceof ElectricShaver shaver) {
                shaver.charge();
            } else if (obj instanceof Earbuds earbuds) {   // 제품이 늘 때마다 계속 추가...
                earbuds.charge();
            }
        }
    }

    // ⭕ Chargeable로 관계 부여 → 한 타입으로 처리
    static void goodCase() {
        System.out.println("--- goodCase ---");
        Chargeable[] objs = { new HandPhone(), new ElectricShaver(), new Earbuds() };
        for (Chargeable obj : objs) {
            obj.charge();                 // 제품이 100개로 늘어도 이 한 줄
        }
    }
}
