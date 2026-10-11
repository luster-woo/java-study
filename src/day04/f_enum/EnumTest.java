package day04.f_enum;

/*
 * enum (열거형) = 정해진 값들만 가질 수 있는 타입
 *
 *  - int 상수 대신 쓰면 "값 + 타입"까지 체크 → 엉뚱한 값을 넣는 실수 방지
 *  - 비교는 == 로 (상수는 딱 하나씩만 존재 → 싱글톤처럼)
 *  - switch의 case에는 상수 이름만 (case SPRING:)
 *  - 필드, 생성자, 메서드도 가질 수 있음 (상수 목록 끝에 ; 필요)
 *
 * [실행 결과]
 * --- 1. int 상수의 문제 ---
 * 급여 : 3000000
 * 급여 : 3000000   ← 계절을 넣었는데 통과됨!
 * --- 2. enum 기본 ---
 * 오늘 : SPRING
 * name() : SPRING, ordinal() : 0
 * SPRING == SPRING ? true
 * 봄 : 꽃구경
 * --- 3. values / valueOf ---
 * SPRING SUMMER FALL WINTER
 * valueOf("FALL") : FALL
 * --- 4. 필드 + 생성자 + 메서드 ---
 * STAFF   : 기본급 3000000원
 * MANAGER : 기본급 4500000원
 * INTERN  : 기본급 2000000원
 * MANAGER 보너스 : 900000원
 */

// ===== 기본 enum =====
enum Season {
    SPRING, SUMMER, FALL, WINTER          // 각각이 Season 타입의 객체 (public static final)
}

// ===== 필드 + 생성자 + 메서드를 가진 enum =====
enum Grade {
    STAFF(3_000_000),                     // 상수를 만들 때 생성자 호출
    MANAGER(4_500_000),
    INTERN(2_000_000);                    // 멤버가 뒤에 오면 ; 필수

    private final int baseSalary;

    Grade(int baseSalary) {               // 생성자는 자동으로 private (밖에서 new 불가)
        this.baseSalary = baseSalary;
    }

    int getBaseSalary() { return baseSalary; }

    int bonus() {
        return baseSalary / 5;            // 기본급의 20%
    }
}

public class EnumTest {

    // int 상수 방식
    static final int SALES = 1;
    static final int SPRING_INT = 1;      // 의미는 다른데 값이 같음

    static int calcSalNormal(int dept) {
        return dept == SALES ? 3_000_000 : 0;
    }

    // enum 방식 : Grade 타입만 받음
    static int calcSalEnum(Grade grade) {
        return grade.getBaseSalary();
    }

    static String activity(Season s) {
        switch (s) {
            case SPRING: return "꽃구경";     // Season.SPRING 이 아니라 SPRING만
            case SUMMER: return "물놀이";
            case FALL:   return "단풍놀이";
            default:     return "눈사람";
        }
    }

    public static void main(String[] args) {
        System.out.println("--- 1. int 상수의 문제 ---");
        System.out.println("급여 : " + calcSalNormal(SALES));
        System.out.println("급여 : " + calcSalNormal(SPRING_INT) + "   ← 계절을 넣었는데 통과됨!");
        // calcSalEnum(Season.SPRING);   // ❌ enum은 타입이 달라서 컴파일 에러 → 실수 방지

        System.out.println("--- 2. enum 기본 ---");
        Season today = Season.SPRING;
        // Season s = new Season();      // ❌ enum은 new 불가
        System.out.println("오늘 : " + today);                          // toString() = 이름
        System.out.println("name() : " + today.name() + ", ordinal() : " + today.ordinal());
        System.out.println("SPRING == SPRING ? " + (today == Season.SPRING));   // == 로 비교
        System.out.println("봄 : " + activity(today));

        System.out.println("--- 3. values / valueOf ---");
        for (Season s : Season.values()) {   // 모든 상수를 배열로
            System.out.print(s + " ");
        }
        System.out.println();
        System.out.println("valueOf(\"FALL\") : " + Season.valueOf("FALL"));   // 문자열 → enum
        // Season.valueOf("fall");       // 💥 대소문자 다르면 IllegalArgumentException

        System.out.println("--- 4. 필드 + 생성자 + 메서드 ---");
        for (Grade g : Grade.values()) {
            System.out.printf("%-7s : 기본급 %d원%n", g, calcSalEnum(g));
        }
        System.out.println("MANAGER 보너스 : " + Grade.MANAGER.bonus() + "원");
    }
}
