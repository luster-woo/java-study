package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class D6P2 {

    // aaabcc → a3b1c2
    static String compress(String s) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            int j = i;
            while (j < s.length() && s.charAt(j) == c) j++;   // 같은 문자가 끝나는 곳까지
            sb.append(c).append(j - i);                      // 문자 + 개수
            i = j;
        }
        return sb.toString();
    }

    // x12y1 → xxxxxxxxxxxxy
    static String restore(String s) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i++);                          // 문자 하나
            int count = 0;
            while (i < s.length() && Character.isDigit(s.charAt(i))) {
                count = count * 10 + (s.charAt(i++) - '0');  // 여러 자리 숫자 : "12" → 1*10 + 2
            }
            sb.append(String.valueOf(c).repeat(count));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String raw = br.readLine().trim();
        String packed = br.readLine().trim();
        System.out.println(compress(raw));
        System.out.println(restore(packed));
    }
}
