package day05;

import java.util.ArrayList;
import java.util.List;

/*
 * List = 순서가 있고, 중복을 허용하는 "크기가 자동으로 늘어나는 배열"
 *
 *  - 선언은 인터페이스(List)로, 생성은 구현체(ArrayList)로 → 다형성 (나중에 LinkedList로 교체 쉬움)
 *  - <String> 제네릭으로 담을 타입 지정 → 기본형은 안 되고 Integer 같은 래퍼 클래스 사용
 *
 *  배열과 비교
 *   - 배열 : 크기 고정 (new String[3]), 중간 삽입·삭제 직접 구현
 *   - List : 크기 자동, add/remove 메서드 제공
 *
 * [실행 결과]
 * --- 1. 추가 ---
 * [사과, 바나나, 포도, 사과]
 * 크기 : 4
 * --- 2. 조회 ---
 * 0번 : 사과
 * 바나나 있음? true
 * 사과 위치 : 0 (처음), 3 (마지막)
 * --- 3. 수정 / 중간 삽입 ---
 * [사과, 딸기, 바나나, 멜론, 사과]
 * --- 4. 삭제 ---
 * [딸기, 바나나, 멜론, 사과]
 * [딸기, 멜론, 사과]
 * --- 5. 반복 ---
 * 0 : 딸기
 * 1 : 멜론
 * 2 : 사과
 * 딸기 멜론 사과
 * --- 6. Integer 리스트 주의 ---
 * [10, 30]
 * [30]
 * --- 7. 비우기 ---
 * [] 비었음? true
 */
public class ListTest {
    public static void main(String[] args) {
        List<String> fruits = new ArrayList<>();   // 왼쪽 = 인터페이스, 오른쪽 = 구현체

        System.out.println("--- 1. 추가 ---");
        fruits.add("사과");
        fruits.add("바나나");
        fruits.add("포도");
        fruits.add("사과");                          // 중복 허용
        System.out.println(fruits);                 // toString()이 재정의돼 있어서 보기 좋게 출력
        System.out.println("크기 : " + fruits.size());   // 배열의 length 대신 size()

        System.out.println("--- 2. 조회 ---");
        System.out.println("0번 : " + fruits.get(0));                  // 배열의 [0] 대신 get(0)
        System.out.println("바나나 있음? " + fruits.contains("바나나"));  // 내부에서 equals로 비교
        System.out.println("사과 위치 : " + fruits.indexOf("사과") + " (처음), "
                + fruits.lastIndexOf("사과") + " (마지막)");
        // fruits.get(10);                          // 💥 IndexOutOfBoundsException

        System.out.println("--- 3. 수정 / 중간 삽입 ---");
        fruits.set(2, "멜론");                       // 2번 칸을 멜론으로 바꿈 (포도 → 멜론)
        fruits.add(1, "딸기");                       // 1번 자리에 끼워 넣음 → 뒤가 한 칸씩 밀림
        System.out.println(fruits);

        System.out.println("--- 4. 삭제 ---");
        fruits.remove(0);                           // 인덱스로 삭제 → 뒤가 한 칸씩 당겨짐
        System.out.println(fruits);
        fruits.remove("바나나");                     // 값으로 삭제 (처음 만나는 것 하나)
        System.out.println(fruits);

        System.out.println("--- 5. 반복 ---");
        for (int i = 0; i < fruits.size(); i++) {   // 인덱스가 필요할 때
            System.out.println(i + " : " + fruits.get(i));
        }
        for (String f : fruits) {                   // 값만 필요할 때 (향상된 for)
            System.out.print(f + " ");
        }
        System.out.println();
        // for (String f : fruits) fruits.remove(f);   // 💥 반복 중 삭제 → ConcurrentModificationException

        System.out.println("--- 6. Integer 리스트 주의 ---");
        List<Integer> nums = new ArrayList<>(List.of(10, 20, 30));   // List.of로 초기값 한 번에
        // List<int> x;                             // ❌ 기본형 불가
        nums.remove(1);                             // ⚠️ int 1 → "1번 인덱스" 삭제 (20 삭제)
        System.out.println(nums);
        nums.remove(Integer.valueOf(10));           // Integer 객체 → "값 10" 삭제
        System.out.println(nums);

        System.out.println("--- 7. 비우기 ---");
        fruits.clear();
        System.out.println(fruits + " 비었음? " + fruits.isEmpty());
    }
}
