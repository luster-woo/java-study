package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;

public class D5P2 {

    static boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();     // 스택은 Stack 클래스 대신 ArrayDeque

        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);                           // 여는 괄호 → 쌓기
            } else {
                if (stack.isEmpty()) return false;      // 닫는데 열린 게 없음 (예 : "}")
                char open = stack.pop();                 // 가장 최근에 연 괄호
                if (c == ')' && open != '(') return false;
                if (c == ']' && open != '[') return false;
                if (c == '}' && open != '{') return false;
            }
        }
        return stack.isEmpty();                          // 다 끝났는데 남아 있으면 안 닫힌 것 (예 : "((")
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int t = Integer.parseInt(br.readLine().trim());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < t; i++) {
            sb.append(isValid(br.readLine().trim()) ? "YES" : "NO").append('\n');
        }
        System.out.print(sb);
    }
}
