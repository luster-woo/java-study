package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class D4P3 {

    enum Day {
        MON("월요일"), TUE("화요일"), WED("수요일"), THU("목요일"),
        FRI("금요일"), SAT("토요일"), SUN("일요일");     // 멤버가 뒤에 오니까 ; 필수

        private final String korean;

        Day(String korean) {            // 자동으로 private
            this.korean = korean;
        }

        String getKorean() { return korean; }

        boolean isWeekend() {
            return this == SAT || this == SUN;     // enum은 == 로 비교
        }

        // n일 뒤 요일 : 선언 순서(ordinal)를 7로 나눈 나머지로 순환
        Day after(int n) {
            return values()[(ordinal() + n) % 7];  // 10억 + 6 은 int 범위 안
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int q = Integer.parseInt(br.readLine().trim());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < q; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            Day day = Day.valueOf(st.nextToken());          // "MON" → Day.MON
            Day result = day.after(Integer.parseInt(st.nextToken()));

            sb.append(result).append(' ').append(result.getKorean());   // result = name()
            if (result.isWeekend()) sb.append(" WEEKEND");
            sb.append('\n');
        }
        System.out.print(sb);
    }
}
