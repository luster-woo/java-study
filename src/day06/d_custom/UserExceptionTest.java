package day06.d_custom;

/*
 * 사용자 정의 예외가 필요한 이유 - 예외 없이 boolean 리턴으로 작성한 경우
 *
 *  - getFruit1은 성공하면 true, 없으면 false
 *  - 문제점 : 호출할 때마다 if(!result)로 결과를 직접 확인해야 함
 *             → 확인을 깜빡하면 실패를 모르고 그냥 지나감
 *             → false만으로는 "왜" 실패했는지 알 수 없음
 *  - 해결 : "과일이 없는 상태"를 예외로 만든다 → FruitExceptionTest 참고
 *
 * [실행 결과]
 * 사과는 없습니다.
 * 창고 관리 끝~
 *  (첫 번째 호출 : 사과를 꺼냄 → true, 두 번째 호출 : 이미 null → false)
 */
public class UserExceptionTest {
    private static String[] fruits = {"사과", "오렌지", "토마토"};

    public static void main(String[] args) {
        boolean result = getFruit1("사과");
        if (!result) {
            System.out.println("사과는 없습니다.");
        }
        result = getFruit1("사과");
        if (!result) {
            System.out.println("사과는 없습니다.");
        }
        System.out.println("창고 관리 끝~");
    }

    private static boolean getFruit1(String name) {
        for (int i = 0; i < fruits.length; i++) {
            if (fruits[i] != null && fruits[i].equals(name)) {
                fruits[i] = null;                        // 꺼냈으니 빈 칸으로
                return true;
            }
        }
        return false;
    }
}
