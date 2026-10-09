package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class D2P3 {

    static class Time {
        private static final int DAY = 24 * 60;   // 하루 = 1440분
        private int hour;
        private int minute;

        Time(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }

        // "HH:MM" 문자열로 만드는 생성자 → 위 생성자 호출
        Time(String text) {
            this(Integer.parseInt(text.split(":")[0]), Integer.parseInt(text.split(":")[1]));
        }

        // 자정부터 몇 분인지로 바꿔서 더한 뒤, 1440으로 나눈 나머지로 다시 시·분 계산
        void plus(int minutes) {
            int total = (hour * 60 + minute + minutes) % DAY;
            hour = total / 60;
            minute = total % 60;
        }

        // println(객체)를 하면 자동으로 호출되는 메서드
        @Override
        public String toString() {
            return String.format("%02d:%02d", hour, minute);   // %02d : 두 자리, 빈 자리는 0
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Time time = new Time(br.readLine().trim());
        int q = Integer.parseInt(br.readLine().trim());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < q; i++) {
            time.plus(Integer.parseInt(br.readLine().trim()));   // 누적
            sb.append(time).append('\n');                         // toString() 자동 호출
        }
        System.out.print(sb);
    }
}
