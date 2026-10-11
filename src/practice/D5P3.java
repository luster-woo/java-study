package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

public class D5P3 {

    // record : 필드 + 생성자 + getter(name(), kor()...) + equals/hashCode/toString 자동 생성 (D6에서 배움)
    record Student(String name, int kor, int eng, int math) {
        int total() {                       // 메서드도 추가 가능
            return kor + eng + math;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine().trim());

        List<Student> students = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            students.add(new Student(
                    st.nextToken(),
                    Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken())));
        }

        // 총점 내림차순 → 국어 내림차순 → 이름 사전순
        students.sort(Comparator.comparingInt(Student::total).reversed()
                .thenComparing(Comparator.comparingInt(Student::kor).reversed())
                .thenComparing(Student::name));
        // ⚠️ .thenComparingInt(Student::kor).reversed() 로 쓰면
        //    reversed()가 "앞의 기준 전체"를 뒤집어서 총점까지 오름차순이 돼버림 → 괄호로 따로 감싸야 함

        StringBuilder sb = new StringBuilder();
        for (Student s : students) {
            sb.append(s.name()).append(' ').append(s.total()).append('\n');
        }

        long pass = students.stream()
                .filter(s -> s.total() >= 240)
                .count();                   // count()는 long을 돌려줌
        sb.append("PASS ").append(pass);
        System.out.println(sb);
    }
}
