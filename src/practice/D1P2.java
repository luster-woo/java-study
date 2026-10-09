package practice;

import java.util.Scanner;

public class D1P2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        StringBuilder sb = new StringBuilder();

        // i = 0 ~ 2n-2, 가운데 줄(i = n-1)에서 별이 가장 많다
        for (int i = 0; i < 2 * n - 1; i++) {
            int dist = Math.abs(n - 1 - i);      // 가운데 줄로부터의 거리
            int spaces = dist;
            int stars = 2 * (n - dist) - 1;
            for (int s = 0; s < spaces; s++) sb.append(' ');
            for (int s = 0; s < stars; s++) sb.append('*');
            sb.append('\n');                      // 별 뒤에는 공백 없이 바로 줄바꿈
        }
        System.out.print(sb);
    }
}
