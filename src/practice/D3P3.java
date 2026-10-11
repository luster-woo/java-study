package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class D3P3 {

    static class Point {
        final int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // 내용(x, y)이 같으면 같은 점
        @Override
        public boolean equals(Object o) {           // 파라미터는 반드시 Object
            if (this == o) return true;             // 같은 객체면 당연히 같음
            if (!(o instanceof Point p)) return false;   // Point가 아니면(null 포함) 다름
            return x == p.x && y == p.y;
        }

        // equals를 재정의하면 hashCode도 짝을 맞춘다 (이 문제에선 안 쓰지만 규칙)
        @Override
        public int hashCode() {
            return 31 * x + y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine().trim());

        Point[] uniq = new Point[n];
        int cnt = 0;
        for (int i = 0; i < n; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            Point p = new Point(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));

            boolean dup = false;
            for (int j = 0; j < cnt; j++) {
                if (uniq[j].equals(p)) {            // == 였다면 new로 만든 점은 전부 다르게 나옴
                    dup = true;
                    break;
                }
            }
            if (!dup) uniq[cnt++] = p;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(cnt).append('\n');
        for (int i = 0; i < cnt; i++) {
            sb.append(uniq[i]).append('\n');        // toString() 자동 호출
        }
        System.out.print(sb);
    }
}
