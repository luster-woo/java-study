package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class D1P1 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine().trim());
        StringTokenizer st = new StringTokenizer(br.readLine());

        long sum = 0;   // 최대 1e9 * 1e5 = 1e14 → int 범위(약 21억) 초과, long 필요
        long max = 0;
        for (int i = 0; i < n; i++) {
            long x = Long.parseLong(st.nextToken());
            sum += x;
            if (x > max) max = x;
        }

        System.out.println(sum);
        System.out.println(max);
        System.out.println(String.format("%.2f", (double) sum / n));
    }
}
