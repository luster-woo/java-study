package day05.c_map;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
 * Map = key → value 쌍으로 저장 (파이썬의 dict)
 *  - key는 중복 불가 (같은 key로 put하면 값이 덮어써짐), value는 중복 가능
 *  - key의 중복 판단은 Set과 같음 (hashCode + equals)
 *  - 사실 HashSet은 내부적으로 HashMap의 key만 쓰는 구조
 *
 *  구현체         | 순서            | 특징
 *  ---------------|----------------|-----------------------
 *  HashMap        | 보장 X          | 가장 빠름 (기본)
 *  LinkedHashMap  | 넣은 순서       | 순서 유지
 *  TreeMap        | key 기준 정렬   | 범위 검색 (TreeSet과 같은 기능)
 *
 * [실행 결과]  (HashMap 순서는 환경마다 다를 수 있음)
 * --- 1. HashMap 기본 ---
 * {kim=90, lee=85, park=70}
 * kim 점수 : 90
 * 없는 key : null
 * 없으면 기본값 : 0
 * kim 덮어쓰기 → 이전 값 90 반환, 지금 : 95
 * lee 있음? true / 점수 70 있음? true
 * park 삭제 후 : {kim=95, lee=85}
 * --- 2. 반복 ---
 * kim : 95
 * lee : 85
 * keySet : [kim, lee]
 * values : [95, 85]
 * --- 3. 개수 세기 ---
 * getOrDefault : {a=3, b=2, c=1}
 * merge        : {a=3, b=2, c=1}
 * --- 4. 같은 데이터, 3가지 Map ---
 * HashMap       : {banana=2, cherry=3, apple=1}
 * LinkedHashMap : {cherry=3, apple=1, banana=2}
 * TreeMap       : {apple=1, banana=2, cherry=3}
 * --- 5. TreeMap 탐색 기능 ---
 * 점수표 : {60=D, 70=C, 80=B, 90=A}
 * 85점 등급 : B
 * 72점 등급 : C
 * firstKey / lastKey : 60 / 90
 * 80점 이상 : {80=B, 90=A}
 * 내림차순 : {90=A, 80=B, 70=C, 60=D}
 */
public class MapKindsTest {
    public static void main(String[] args) {

        System.out.println("--- 1. HashMap 기본 ---");
        Map<String, Integer> score = new HashMap<>();   // <key 타입, value 타입>
        score.put("kim", 90);
        score.put("lee", 85);
        score.put("park", 70);
        System.out.println(new TreeMap<>(score));       // (출력 순서 고정용으로 TreeMap에 담아 출력)

        System.out.println("kim 점수 : " + score.get("kim"));
        System.out.println("없는 key : " + score.get("choi"));               // 없으면 null
        System.out.println("없으면 기본값 : " + score.getOrDefault("choi", 0));

        Integer old = score.put("kim", 95);              // 같은 key → 덮어쓰기, 이전 값 반환
        System.out.println("kim 덮어쓰기 → 이전 값 " + old + " 반환, 지금 : " + score.get("kim"));

        System.out.println("lee 있음? " + score.containsKey("lee")
                + " / 점수 70 있음? " + score.containsValue(70));
        score.remove("park");
        System.out.println("park 삭제 후 : " + new TreeMap<>(score));

        System.out.println("--- 2. 반복 ---");
        Map<String, Integer> sorted = new TreeMap<>(score);
        for (Map.Entry<String, Integer> e : sorted.entrySet()) {   // key와 value 같이 (가장 많이 씀)
            System.out.println(e.getKey() + " : " + e.getValue());
        }
        System.out.println("keySet : " + sorted.keySet());          // key만
        System.out.println("values : " + sorted.values());          // value만

        System.out.println("--- 3. 개수 세기 ---");
        List<String> words = List.of("a", "b", "a", "c", "b", "a");

        Map<String, Integer> count1 = new TreeMap<>();
        for (String w : words) {
            count1.put(w, count1.getOrDefault(w, 0) + 1);   // 없으면 0에서 시작
        }
        System.out.println("getOrDefault : " + count1);

        Map<String, Integer> count2 = new TreeMap<>();
        for (String w : words) {
            count2.merge(w, 1, Integer::sum);               // 없으면 1, 있으면 기존값 + 1
        }
        System.out.println("merge        : " + count2);

        System.out.println("--- 4. 같은 데이터, 3가지 Map ---");
        Map<String, Integer> hash = new HashMap<>();
        Map<String, Integer> linked = new LinkedHashMap<>();
        Map<String, Integer> tree = new TreeMap<>();
        for (Map<String, Integer> m : List.of(hash, linked, tree)) {   // 다형성 : 셋 다 Map
            m.put("cherry", 3);
            m.put("apple", 1);
            m.put("banana", 2);
        }
        System.out.println("HashMap       : " + hash);     // 순서 X
        System.out.println("LinkedHashMap : " + linked);   // 넣은 순서
        System.out.println("TreeMap       : " + tree);     // key 정렬

        System.out.println("--- 5. TreeMap 탐색 기능 ---");
        TreeMap<Integer, String> grade = new TreeMap<>();
        grade.put(90, "A");
        grade.put(80, "B");
        grade.put(70, "C");
        grade.put(60, "D");
        System.out.println("점수표 : " + grade);
        // floorEntry(점수) : 그 점수 이하인 key 중 가장 큰 것 → 구간 찾기에 딱
        System.out.println("85점 등급 : " + grade.floorEntry(85).getValue());
        System.out.println("72점 등급 : " + grade.floorEntry(72).getValue());
        System.out.println("firstKey / lastKey : " + grade.firstKey() + " / " + grade.lastKey());
        System.out.println("80점 이상 : " + grade.tailMap(80));
        System.out.println("내림차순 : " + grade.descendingMap());
    }
}
