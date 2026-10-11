package day05.f_stream;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/*
 * Stream = 컬렉션 데이터를 "공장 컨베이어 벨트"처럼 흘려보내며 처리
 *
 *   list.stream()          ① 벨트에 올리기 (시작)
 *       .filter(...)       ② 중간 작업 : 거르기   ┐ 몇 개든 이어 붙일 수 있음
 *       .map(...)          ② 중간 작업 : 바꾸기   ┘
 *       .toList();         ③ 최종 작업 : 결과 받기 (이게 있어야 실제로 실행됨)
 *
 *  파이썬으로 치면 : [x * 2 for x in nums if x > 3]
 *  자바 Stream     : nums.stream().filter(x -> x > 3).map(x -> x * 2).toList()
 *
 *  - 원본 리스트는 바뀌지 않는다 (새 결과를 만듦)
 *  - 한 번 쓴 stream은 다시 못 씀 (벨트는 일회용)
 *
 * [실행 결과]
 * --- 1. for문 vs Stream ---
 * for문  : [8, 12, 16]
 * Stream : [8, 12, 16]
 * --- 2. 중간 작업 ---
 * filter (짝수)       : [2, 4, 6, 8, 10]
 * map (제곱)          : [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]
 * sorted (내림차순)    : [10, 9, 8, 7, 6, 5, 4, 3, 2, 1]
 * distinct (중복제거)  : [1, 2, 3]
 * limit (앞 3개)       : [1, 2, 3]
 * skip (앞 7개 건너뜀) : [8, 9, 10]
 * --- 3. 최종 작업 ---
 * count   : 10
 * sum     : 55
 * average : 5.5
 * max     : 10
 * 5보다 큰 게 하나라도? true
 * 전부 양수?           true
 * 처음 나오는 짝수      : 2
 * --- 4. 객체 리스트 ---
 * 80점 이상 이름 : [kim, park, choi]
 * 점수 높은 순   : [choi, kim, park, lee, jung]
 * 1등           : choi (95)
 * 평균 점수      : 81.0
 * 이름 이어 붙이기 : kim, lee, park, choi, jung
 * --- 5. 그룹 만들기 ---
 * 반별 학생 : {1반=[kim, park, jung], 2반=[lee, choi]}
 * 반별 평균 : {1반=78.33333333333333, 2반=85.0}
 */
public class StreamTest {

    static class Student {
        String name;
        int score;
        String group;

        Student(String name, int score, String group) {
            this.name = name;
            this.score = score;
            this.group = group;
        }

        String getName()  { return name; }
        int getScore()    { return score; }
        String getGroup() { return group; }
    }

    public static void main(String[] args) {
        List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        System.out.println("--- 1. for문 vs Stream ---");
        // 문제 : 3보다 크고 짝수인 것만 골라서 2배 → 앞에서 3개
        List<Integer> result1 = new ArrayList<>();
        for (int n : nums) {
            if (n > 3 && n % 2 == 0) {
                result1.add(n * 2);
                if (result1.size() == 3) break;
            }
        }
        System.out.println("for문  : " + result1);

        List<Integer> result2 = nums.stream()
                .filter(n -> n > 3 && n % 2 == 0)   // 거르기
                .map(n -> n * 2)                    // 바꾸기
                .limit(3)                           // 앞에서 3개
                .toList();                          // 결과 받기
        System.out.println("Stream : " + result2);
        // → "무엇을 할지"가 한 줄씩 보여서 읽기 쉽다

        System.out.println("--- 2. 중간 작업 ---");
        System.out.println("filter (짝수)       : " + nums.stream().filter(n -> n % 2 == 0).toList());
        System.out.println("map (제곱)          : " + nums.stream().map(n -> n * n).toList());
        System.out.println("sorted (내림차순)    : " + nums.stream().sorted(Comparator.reverseOrder()).toList());
        System.out.println("distinct (중복제거)  : " + List.of(1, 1, 2, 3, 3).stream().distinct().toList());
        System.out.println("limit (앞 3개)       : " + nums.stream().limit(3).toList());
        System.out.println("skip (앞 7개 건너뜀) : " + nums.stream().skip(7).toList());

        System.out.println("--- 3. 최종 작업 ---");
        System.out.println("count   : " + nums.stream().count());
        // 숫자 계산은 mapToInt로 IntStream으로 바꾼 뒤 sum / average / max
        System.out.println("sum     : " + nums.stream().mapToInt(n -> n).sum());
        System.out.println("average : " + nums.stream().mapToInt(n -> n).average().orElse(0));
        System.out.println("max     : " + nums.stream().mapToInt(n -> n).max().orElse(0));
        System.out.println("5보다 큰 게 하나라도? " + nums.stream().anyMatch(n -> n > 5));
        System.out.println("전부 양수?           " + nums.stream().allMatch(n -> n > 0));
        System.out.println("처음 나오는 짝수      : " + nums.stream().filter(n -> n % 2 == 0).findFirst().orElse(-1));
        // findFirst / average / max는 결과가 없을 수도 있어서 Optional로 나옴 → orElse(기본값)

        System.out.println("--- 4. 객체 리스트 ---");
        List<Student> students = List.of(
                new Student("kim", 90, "1반"),
                new Student("lee", 75, "2반"),
                new Student("park", 80, "1반"),
                new Student("choi", 95, "2반"),
                new Student("jung", 65, "1반")
        );

        List<String> good = students.stream()
                .filter(s -> s.getScore() >= 80)
                .map(Student::getName)              // Student → 이름(String)으로 바꾸기
                .toList();
        System.out.println("80점 이상 이름 : " + good);

        List<String> ranking = students.stream()
                .sorted(Comparator.comparing(Student::getScore).reversed())
                .map(Student::getName)
                .toList();
        System.out.println("점수 높은 순   : " + ranking);

        Student top = students.stream()
                .max(Comparator.comparing(Student::getScore))
                .orElseThrow();
        System.out.println("1등           : " + top.getName() + " (" + top.getScore() + ")");

        System.out.println("평균 점수      : " + students.stream().mapToInt(Student::getScore).average().orElse(0));

        String names = students.stream()
                .map(Student::getName)
                .collect(Collectors.joining(", "));  // 문자열로 이어 붙이기 (파이썬 ", ".join)
        System.out.println("이름 이어 붙이기 : " + names);

        System.out.println("--- 5. 그룹 만들기 ---");
        // groupingBy : 기준이 같은 것끼리 Map으로 묶기 (SQL의 GROUP BY)
        Map<String, List<String>> byGroup = students.stream()
                .collect(Collectors.groupingBy(
                        Student::getGroup,                                    // 묶는 기준 (key)
                        TreeMap::new,                                         // 결과 Map 종류 (정렬용)
                        Collectors.mapping(Student::getName, Collectors.toList())));   // value
        System.out.println("반별 학생 : " + byGroup);

        Map<String, Double> avgByGroup = students.stream()
                .collect(Collectors.groupingBy(
                        Student::getGroup,
                        TreeMap::new,
                        Collectors.averagingInt(Student::getScore)));
        System.out.println("반별 평균 : " + avgByGroup);
    }
}
