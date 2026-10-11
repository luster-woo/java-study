package day06.e_bad;

import java.util.InputMismatchException;
import java.util.Scanner;

/*
 * 부적절한 예외 처리 3가지 (APPENDIX)
 *
 *  1. 잡고 모르쇠    : catch (Exception e) { }  → 무슨 일이 있었는지 아무도 모름
 *  2. 예외로 흐름 제어 : 배열 끝을 ArrayIndexOutOfBounds로 감지 → 느리고 읽기 어려움
 *  3. 상태 확인 없이 호출 : nextInt() 전에 hasNextInt()로 먼저 확인
 *
 *  ※ RuntimeException은 "잡을 대상"이기보다 "고칠 대상(버그)"인 경우가 많다
 *     → try-catch 대신 if로 상태를 먼저 확인
 *
 * [실행 결과]
 * --- 1. 잡고 모르쇠 ---
 * (bad1 : 조용히 끝남...)
 * name이 null
 * 0으로 나눌 수 없음
 * 길이 : 4
 * 10 / 2 = 5
 * --- 2. 예외로 흐름 제어 ---
 * 나쁜 예 합계 : 15
 * 좋은 예 합계 : 15
 * --- 3. 상태 종속적 코드 ---
 * 나쁜 예 : 중간에 터짐, 합계 30
 * 좋은 예 : 합계 30
 * 숫자 아님, 건너뜀 : abc
 * 건너뛰기 : 합계 60
 */
public class BadExceptionTest {
    public static void main(String[] args) {

        System.out.println("--- 1. 잡고 모르쇠 ---");
        bad1(null, 0);                                   // 아무것도 출력 안 됨 → 뭐가 문제인지 모름
        System.out.println("(bad1 : 조용히 끝남...)");
        good1(null, 0);
        good1("java", 2);

        System.out.println("--- 2. 예외로 흐름 제어 ---");
        int[] nums = {1, 2, 3, 4, 5};
        int sum1 = 0;
        try {
            for (int i = 0; ; i++) sum1 += nums[i];      // ❌ 끝을 예외로 알아챔
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("나쁜 예 합계 : " + sum1);
        }
        int sum2 = 0;
        for (int i = 0; i < nums.length; i++) sum2 += nums[i];   // ✅ 길이로 확인
        System.out.println("좋은 예 합계 : " + sum2);

        System.out.println("--- 3. 상태 종속적 코드 ---");
        Scanner bad = new Scanner("10 20 abc 30");
        int s1 = 0;
        try {
            while (true) s1 += bad.nextInt();            // ❌ abc에서 InputMismatchException
        } catch (InputMismatchException e) {
            System.out.println("나쁜 예 : 중간에 터짐, 합계 " + s1);
        }

        Scanner good = new Scanner("10 20 abc 30");
        int s2 = 0;
        while (good.hasNextInt()) s2 += good.nextInt();  // ✅ 먼저 확인 → 숫자가 아니면 멈춤
        System.out.println("좋은 예 : 합계 " + s2);

        Scanner skip = new Scanner("10 20 abc 30");
        int s3 = 0;
        while (skip.hasNext()) {
            if (skip.hasNextInt()) s3 += skip.nextInt();
            else System.out.println("숫자 아님, 건너뜀 : " + skip.next());
        }
        System.out.println("건너뛰기 : 합계 " + s3);
    }

    static void bad1(String name, int divisor) {
        try {
            System.out.println(name.length());
            System.out.println(10 / divisor);
        } catch (Exception e) {
            // 쉿!!!  ← 절대 이러지 말기 (최소한 로그라도)
        }
    }

    static void good1(String name, int divisor) {
        if (name != null) System.out.println("길이 : " + name.length());
        else System.out.println("name이 null");
        if (divisor != 0) System.out.println("10 / " + divisor + " = " + 10 / divisor);
        else System.out.println("0으로 나눌 수 없음");
    }
}
