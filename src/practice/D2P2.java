package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class D2P2 {

    static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // 나(this)와 other 사이 거리의 제곱 (최대 2만² + 2만² = 8억 → int 가능)
        int distSq(Point other) {
            int dx = this.x - other.x;
            int dy = this.y - other.y;
            return dx * dx + dy * dy;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine().trim());

        Point[] points = new Point[n];   // 객체 배열 : 처음엔 전부 null
        for (int i = 0; i < n; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            points[i] = new Point(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
        }

        int best = Integer.MAX_VALUE;
        int bi = 0, bj = 0;
        // i < j 인 모든 쌍 검사. i, j가 작은 순서로 도니까 "더 작을 때만" 갱신하면
        // 같은 거리일 때 먼저 나온 쌍(i가 작은 쌍, 그다음 j가 작은 쌍)이 자동으로 남는다
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int d = points[i].distSq(points[j]);
                if (d < best) {
                    best = d;
                    bi = i;
                    bj = j;
                }
            }
        }

        System.out.println(best);
        System.out.println((bi + 1) + " " + (bj + 1));   // 번호는 1부터
    }
}
