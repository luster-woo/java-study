package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class D4P2 {

    // 제네릭 스택 : 어떤 타입이든 담을 수 있다
    static class MyStack<T> {
        // new T[2]는 불가 → Object 배열을 만들어 T[]로 형변환 (경고는 무시)
        @SuppressWarnings("unchecked")
        private T[] data = (T[]) new Object[2];
        private int size = 0;

        void push(T x) {
            if (size == data.length) {
                data = Arrays.copyOf(data, data.length * 2);   // 꽉 차면 2배로
            }
            data[size++] = x;           // 넣고 나서 증가 (후위)
        }

        T pop() {
            if (isEmpty()) return null;
            T x = data[--size];         // 먼저 줄이고 꺼내기 (전위)
            data[size] = null;          // 다 쓴 칸 비우기 → GC가 정리할 수 있게
            return x;
        }

        T top() {
            return isEmpty() ? null : data[size - 1];
        }

        int size()        { return size; }
        boolean isEmpty() { return size == 0; }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine().trim());

        MyStack<String> stack = new MyStack<>();   // T = String
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            switch (st.nextToken()) {
                case "push"  -> stack.push(st.nextToken());
                case "pop"   -> {
                    String x = stack.pop();        // 제네릭 덕분에 형변환 없이 String
                    sb.append(x == null ? "EMPTY" : x).append('\n');
                }
                case "top"   -> {
                    String x = stack.top();
                    sb.append(x == null ? "EMPTY" : x).append('\n');
                }
                case "size"  -> sb.append(stack.size()).append('\n');
                case "empty" -> sb.append(stack.isEmpty() ? 1 : 0).append('\n');
            }
        }
        System.out.print(sb);
    }
}
