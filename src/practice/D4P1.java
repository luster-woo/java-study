package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class D4P1 {

    // 결제 수단 구분 : 문자열 대신 enum → 정해진 값만 가능
    enum PayType { CARD, CASH, POINT }

    // 모든 결제 수단이 지켜야 할 약속
    interface Payment {
        long FAIL = -1;                 // 자동으로 public static final (상수)

        long pay(long amount);          // 자동으로 public abstract
    }

    static class CardPayment implements Payment {
        @Override
        public long pay(long amount) {              // public 필수 (인터페이스 메서드는 public)
            return amount + amount * 3 / 100;       // 10억 * 3 → int 범위 초과라 long
        }
    }

    static class CashPayment implements Payment {
        @Override
        public long pay(long amount) {
            return amount / 10 * 10;                // 정수 나눗셈으로 1의 자리 버림
        }
    }

    static class PointPayment implements Payment {
        private long point;                         // 남은 포인트 = 상태 → 객체 하나를 계속 써야 함

        PointPayment(long point) {
            this.point = point;
        }

        @Override
        public long pay(long amount) {
            if (amount > point) return FAIL;        // 부족하면 차감 안 함
            point -= amount;
            return amount;
        }

        long getPoint() { return point; }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PointPayment pointPay = new PointPayment(Long.parseLong(br.readLine().trim()));
        Payment cardPay = new CardPayment();
        Payment cashPay = new CashPayment();

        int n = Integer.parseInt(br.readLine().trim());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            PayType type = PayType.valueOf(st.nextToken());   // 문자열 → enum
            long amount = Long.parseLong(st.nextToken());

            // enum으로 어떤 구현체를 쓸지 고른다 → 사용하는 쪽은 Payment 타입만 앎
            Payment payment = switch (type) {
                case CARD  -> cardPay;
                case CASH  -> cashPay;
                case POINT -> pointPay;
            };

            long result = payment.pay(amount);              // 동적 바인딩
            sb.append(result == Payment.FAIL ? "FAIL" : String.valueOf(result)).append('\n');
        }
        sb.append("POINT LEFT ").append(pointPay.getPoint());
        System.out.println(sb);
    }
}
