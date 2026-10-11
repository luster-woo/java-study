package day05.b_set;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/*
 * Set = 중복을 허용하지 않는 집합 (파이썬의 set)
 *  - 순서(인덱스)가 없음 → get(i) 없음
 *  - 구현체 3가지
 *      HashSet       : 가장 빠름, 순서 보장 X
 *      LinkedHashSet : 넣은 순서 유지
 *      TreeSet       : 자동 정렬
 *  - 중복 판단 : hashCode() 같고 + equals() true → 같은 것 (Day03 Object 메서드 재정의)
 *
 * [실행 결과]  (HashSet 출력 순서는 환경마다 다를 수 있음)
 * --- 1. 기본 ---
 * 사과 추가 성공? true
 * 사과 또 추가? false
 * 크기 : 3
 * 바나나 있음? true
 * 바나나 삭제 후 : 2개
 * --- 2. 리스트 중복 제거 ---
 * 원본 : [3, 1, 3, 2, 1, 2, 3]
 * HashSet       : [1, 2, 3]
 * LinkedHashSet : [3, 1, 2]
 * TreeSet       : [1, 2, 3]
 * --- 3. 집합 연산 ---
 * 합집합 : [1, 2, 3, 4, 5, 6]
 * 교집합 : [3, 4]
 * 차집합 : [1, 2]
 * --- 4. 직접 만든 클래스 ---
 * equals/hashCode 재정의 X : 2개
 * equals/hashCode 재정의 O : 1개
 */
public class SetTest {

    // 재정의 안 함 → Object의 equals(주소 비교) 사용
    static class PointNo {
        int x, y;
        PointNo(int x, int y) { this.x = x; this.y = y; }
    }

    // 재정의 함 → 내용(x, y)으로 비교
    static class Point {
        int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Point p)) return false;
            return x == p.x && y == p.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);    // 필드 여러 개면 Objects.hash
        }
    }

    public static void main(String[] args) {
        System.out.println("--- 1. 기본 ---");
        Set<String> set = new HashSet<>();
        System.out.println("사과 추가 성공? " + set.add("사과"));   // 새로 들어가면 true
        System.out.println("사과 또 추가? " + set.add("사과"));     // 이미 있으면 false (무시됨)
        set.add("바나나");
        set.add("포도");
        System.out.println("크기 : " + set.size());
        System.out.println("바나나 있음? " + set.contains("바나나"));   // List보다 훨씬 빠름
        set.remove("바나나");
        System.out.println("바나나 삭제 후 : " + set.size() + "개");
        // set.get(0);                                       // ❌ 순서가 없어서 get 없음
        // for (String s : set) { ... }                      // 반복은 향상된 for로

        System.out.println("--- 2. 리스트 중복 제거 ---");
        List<Integer> nums = List.of(3, 1, 3, 2, 1, 2, 3);
        System.out.println("원본 : " + nums);
        System.out.println("HashSet       : " + new HashSet<>(nums));        // 순서 보장 X
        System.out.println("LinkedHashSet : " + new LinkedHashSet<>(nums));  // 처음 넣은 순서
        System.out.println("TreeSet       : " + new TreeSet<>(nums));        // 정렬

        System.out.println("--- 3. 집합 연산 ---");
        Set<Integer> a = new TreeSet<>(List.of(1, 2, 3, 4));
        Set<Integer> b = new TreeSet<>(List.of(3, 4, 5, 6));

        Set<Integer> union = new TreeSet<>(a);   // 원본 보존을 위해 복사해서 사용
        union.addAll(b);                         // 합집합 (파이썬 a | b)
        System.out.println("합집합 : " + union);

        Set<Integer> inter = new TreeSet<>(a);
        inter.retainAll(b);                      // 교집합 (파이썬 a & b)
        System.out.println("교집합 : " + inter);

        Set<Integer> diff = new TreeSet<>(a);
        diff.removeAll(b);                       // 차집합 (파이썬 a - b)
        System.out.println("차집합 : " + diff);

        System.out.println("--- 4. 직접 만든 클래스 ---");
        Set<PointNo> s1 = new HashSet<>();
        s1.add(new PointNo(1, 2));
        s1.add(new PointNo(1, 2));               // 내용은 같지만 다른 객체 → 중복으로 안 봄
        System.out.println("equals/hashCode 재정의 X : " + s1.size() + "개");

        Set<Point> s2 = new HashSet<>();
        s2.add(new Point(1, 2));
        s2.add(new Point(1, 2));                 // hashCode 같고 equals true → 중복 → 무시
        System.out.println("equals/hashCode 재정의 O : " + s2.size() + "개");
    }
}
