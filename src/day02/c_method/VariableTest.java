package day02.c_method;

/*
 * Variable arguments (가변 인자)
 * - 같은 타입의 인자가 몇 개 들어올지 모를 때 사용
 * - 타입... 이름  → 호출 시 넘긴 개수만큼 자동으로 배열 생성 및 초기화
 * - 메서드 안에서 params는 그냥 int[] 배열이다
 * - 규칙 : 파라미터 목록의 "맨 마지막"에 "하나만" 올 수 있다
 */
public class VariableTest {

    public static void main(String[] args) {
        VariableTest vt = new VariableTest();
        vt.addAll(1, 2, 3);          // 6
        vt.addAll(1, 2, 3, 4, 5);    // 15
        vt.addAll(1, 2);             // 3
        vt.addAll();                 // 0  (0개도 가능 → 길이 0인 배열)

        // 비교 : 배열 파라미터로 받으면 호출 전에 배열을 직접 만들어야 해서 번거롭다
        vt.addAllArray(new int[]{1, 2, 3});   // 6
    }

    public void addAll(int... params) {
        int sum = 0;
        for (int i : params) {       // 향상된 for문 : 배열의 값을 하나씩 꺼냄
            sum += i;
        }
        System.out.println(sum + " (인자 " + params.length + "개)");
    }

    public void addAllArray(int[] params) {
        int sum = 0;
        for (int i : params) {
            sum += i;
        }
        System.out.println(sum + " (배열 버전)");
    }
}
