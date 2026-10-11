package day06.j_record;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * record (Java 16+) = "데이터만 담는 클래스"를 한 줄로 만드는 문법
 *
 *   record Point(int x, int y) { }
 *
 *  컴파일러가 자동으로 만들어 주는 것
 *   - private final 필드 (x, y)          → 값 변경 불가 (불변)
 *   - 모든 필드를 받는 생성자
 *   - 접근자 x(), y()                      ← getX()가 아니라 필드 이름 그대로!
 *   - toString(), equals(), hashCode()    → Day03에서 직접 재정의하던 그것들
 *
 *  못 하는 것 : setter 없음, 다른 클래스 상속 X (인터페이스 구현은 O), 인스턴스 필드 추가 X
 *  주 용도 : DTO, 메서드에서 값 여러 개 리턴, Map의 key
 *
 * [실행 결과]
 * --- 1. class vs record ---
 * PointClass[x=3, y=4] / x = 3
 * Point[x=3, y=4] / x = 3
 * --- 2. equals / hashCode 자동 ---
 * p == p2      : false
 * p.equals(p2) : true
 * Set 크기     : 2
 * --- 3. 컴팩트 생성자 (검증) ---
 * Member[name=윤우, age=25] / 성인? true
 * Member[name=guest, age=0]
 * 생성 실패 : 나이는 음수 불가 : -1
 * --- 4. 값 바꾸고 싶으면? → 새로 만든다 ---
 * 원본 : Member[name=윤우, age=25] → 새 객체 : Member[name=윤우, age=26]
 * --- 5. 메서드에서 값 2개 리턴 ---
 * 최소 2, 최대 9
 */

// ===== 일반 클래스로 쓰면 : 이만큼 써야 함 =====
class PointClass {
    private final int x;
    private final int y;

    PointClass(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PointClass p)) return false;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() { return 31 * x + y; }

    @Override
    public String toString() { return "PointClass[x=" + x + ", y=" + y + "]"; }
}

// ===== record로 쓰면 : 위와 같은 기능이 한 줄 =====
record Point(int x, int y) { }

// ===== 검증 + 메서드 추가 =====
record Member(String name, int age) {
    // 컴팩트 생성자 : 파라미터 목록 생략, 필드 대입도 자동 → 검증만 쓰면 됨
    Member {
        if (age < 0) throw new IllegalArgumentException("나이는 음수 불가 : " + age);
        name = name.trim();                          // 대입 전에 값 다듬기도 가능
    }

    boolean isAdult() {                              // 일반 메서드 추가 O
        return age >= 20;
    }

    static Member guest() {                          // static 메서드도 O
        return new Member("guest", 0);
    }
}

public class RecordTest {
    public static void main(String[] args) {

        System.out.println("--- 1. class vs record ---");
        PointClass pc = new PointClass(3, 4);
        Point p = new Point(3, 4);
        System.out.println(pc + " / x = " + pc.getX());
        System.out.println(p + " / x = " + p.x());                 // getX() 아니고 x()
        // p.x = 10;                                               // ❌ final이라 변경 불가

        System.out.println("--- 2. equals / hashCode 자동 ---");
        Point p2 = new Point(3, 4);
        System.out.println("p == p2      : " + (p == p2));          // 다른 객체
        System.out.println("p.equals(p2) : " + p.equals(p2));      // 값이 같으니 true
        Set<Point> set = new HashSet<>(List.of(p, p2, new Point(1, 1)));
        System.out.println("Set 크기     : " + set.size());         // 중복 제거됨 → 2

        System.out.println("--- 3. 컴팩트 생성자 (검증) ---");
        Member m = new Member("  윤우  ", 25);
        System.out.println(m + " / 성인? " + m.isAdult());          // 앞뒤 공백 제거됨
        System.out.println(Member.guest());
        try {
            new Member("kim", -1);
        } catch (IllegalArgumentException e) {
            System.out.println("생성 실패 : " + e.getMessage());
        }

        System.out.println("--- 4. 값 바꾸고 싶으면? → 새로 만든다 ---");
        Member older = new Member(m.name(), m.age() + 1);
        System.out.println("원본 : " + m + " → 새 객체 : " + older);

        System.out.println("--- 5. 메서드에서 값 2개 리턴 ---");
        MinMax r = minMax(List.of(7, 2, 9, 4));
        System.out.println("최소 " + r.min() + ", 최대 " + r.max());
    }

    record MinMax(int min, int max) { }              // 클래스 안에 선언도 가능 (잠깐 쓰는 용도로 편함)

    static MinMax minMax(List<Integer> nums) {
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int n : nums) {
            min = Math.min(min, n);
            max = Math.max(max, n);
        }
        return new MinMax(min, max);
    }
}
