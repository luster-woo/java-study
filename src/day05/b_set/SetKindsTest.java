package day05.b_set;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/*
 * Set 구현체 3가지 비교
 *
 *  구현체         | 내부 구조           | 순서       | 속도
 *  ---------------|--------------------|-----------|------------
 *  HashSet        | 해시 테이블         | 보장 X     | 가장 빠름 (기본)
 *  LinkedHashSet  | 해시 + 연결 리스트  | 넣은 순서  | 빠름
 *  TreeSet        | 레드블랙 트리       | 정렬       | 조금 느림 (대신 범위 검색 가능)
 *
 * [실행 결과]  (HashSet 순서는 환경마다 다를 수 있음)
 * --- 1. 같은 값을 넣으면 ---
 * HashSet       : [date, banana, cherry, apple]
 * LinkedHashSet : [cherry, apple, date, banana]
 * TreeSet       : [apple, banana, cherry, date]
 * --- 2. TreeSet 탐색 기능 ---
 * 전체 : [10, 20, 30, 40, 50]
 * first / last      : 10 / 50
 * floor(25)   (≤)   : 20
 * ceiling(25) (≥)   : 30
 * lower(30)   (<)   : 20
 * higher(30)  (>)   : 40
 * floor(5)          : null
 * headSet(30) (<30) : [10, 20]
 * tailSet(30) (≥30) : [30, 40, 50]
 * subSet(20,40)     : [20, 30]
 * 내림차순 보기       : [50, 40, 30, 20, 10]
 * pollFirst → 10, 남은 것 : [20, 30, 40, 50]
 * --- 3. TreeSet 정렬 기준 바꾸기 ---
 * 내림차순 : [5, 4, 3, 2, 1]
 * 길이순   : [kiwi, apple, banana]
 */
public class SetKindsTest {
    public static void main(String[] args) {
        List<String> input = List.of("cherry", "apple", "date", "banana", "apple", "cherry");

        System.out.println("--- 1. 같은 값을 넣으면 ---");
        Set<String> hash = new HashSet<>(input);
        Set<String> linked = new LinkedHashSet<>(input);
        Set<String> tree = new TreeSet<>(input);
        System.out.println("HashSet       : " + hash);     // 순서 X (해시값 기준)
        System.out.println("LinkedHashSet : " + linked);   // 처음 넣은 순서
        System.out.println("TreeSet       : " + tree);     // 사전순 정렬

        System.out.println("--- 2. TreeSet 탐색 기능 ---");
        // 이 메서드들은 Set 인터페이스엔 없음 → 변수 타입을 TreeSet(또는 NavigableSet)으로
        TreeSet<Integer> ts = new TreeSet<>(List.of(30, 10, 50, 20, 40));
        System.out.println("전체 : " + ts);
        System.out.println("first / last      : " + ts.first() + " / " + ts.last());
        System.out.println("floor(25)   (≤)   : " + ts.floor(25));     // 25 이하 중 가장 큰 값
        System.out.println("ceiling(25) (≥)   : " + ts.ceiling(25));   // 25 이상 중 가장 작은 값
        System.out.println("lower(30)   (<)   : " + ts.lower(30));     // 30 미만 중 가장 큰 값
        System.out.println("higher(30)  (>)   : " + ts.higher(30));    // 30 초과 중 가장 작은 값
        System.out.println("floor(5)          : " + ts.floor(5));      // 없으면 null
        System.out.println("headSet(30) (<30) : " + ts.headSet(30));
        System.out.println("tailSet(30) (≥30) : " + ts.tailSet(30));
        System.out.println("subSet(20,40)     : " + ts.subSet(20, 40)); // 20 이상 40 미만
        System.out.println("내림차순 보기       : " + ts.descendingSet());
        Integer polled = ts.pollFirst();                                // 꺼내면서 삭제
        System.out.println("pollFirst → " + polled + ", 남은 것 : " + ts);

        System.out.println("--- 3. TreeSet 정렬 기준 바꾸기 ---");
        TreeSet<Integer> desc = new TreeSet<>(Comparator.reverseOrder());
        desc.addAll(List.of(3, 1, 5, 2, 4));
        System.out.println("내림차순 : " + desc);

        // 길이 기준 정렬 (⚠️ 길이가 같으면 "같은 값"으로 보고 버려지니 주의 → 실무에선 2차 기준 추가)
        TreeSet<String> byLen = new TreeSet<>(Comparator.comparingInt(String::length));
        byLen.addAll(List.of("banana", "kiwi", "apple"));
        System.out.println("길이순   : " + byLen);
    }
}
