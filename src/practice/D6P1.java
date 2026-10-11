package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class D6P1 {

    // 사용자 정의 예외 (practice 패키지의 다른 클래스와 이름이 겹치지 않게 안쪽에 static으로)
    static class DivideByZero extends RuntimeException { }
    static class UnknownOp extends RuntimeException { }

    static long calc(String aStr, String op, String bStr) {
        // 검사 순서 : 숫자 → 연산자 → 0 나누기
        long a = Long.parseLong(aStr);                   // 실패하면 NumberFormatException
        long b = Long.parseLong(bStr);
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;                      // long이라 10억 × 10억도 OK
            case "/":
                if (b == 0) throw new DivideByZero();
                return a / b;                            // 정수 나눗셈 (소수점 버림, -7/2 = -3)
            default:
                throw new UnknownOp();
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine().trim());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            String[] t = br.readLine().trim().split("\\s+");   // 공백 여러 개여도 OK
            try {
                sb.append(calc(t[0], t[1], t[2]));
            } catch (NumberFormatException e) {
                sb.append("INVALID NUMBER");
            } catch (UnknownOp e) {
                sb.append("UNKNOWN OP");
            } catch (DivideByZero e) {
                sb.append("DIVIDE BY ZERO");
            }
            sb.append('\n');
        }
        System.out.print(sb);
    }
}
