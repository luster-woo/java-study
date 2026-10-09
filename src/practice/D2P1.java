package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class D2P1 {

    static class Account {
        private static int count = 0;   // 개설된 계좌 수 : 모든 Account가 공유 (static)

        private final String name;      // private : 밖에서 직접 못 바꾸고 메서드로만 접근
        private long balance;           // 최대 10억 * 1000번 → int 범위 초과, long 필요

        Account(String name, long balance) {
            this.name = name;
            this.balance = balance;
            count++;                    // 계좌가 만들어질 때마다 1 증가
        }

        String getName() { return name; }
        long getBalance() { return balance; }

        void deposit(long amount) {
            balance += amount;
        }

        // 출금 성공하면 true, 잔액 부족이면 false (잔액 그대로)
        boolean withdraw(long amount) {
            if (amount > balance) return false;
            balance -= amount;
            return true;
        }

        static int getCount() { return count; }
    }

    static Account[] accounts = new Account[1000];   // 명령이 최대 1000개 → 계좌도 최대 1000개
    static int size = 0;

    // 이름으로 계좌 찾기 (없으면 null)
    static Account find(String name) {
        for (int i = 0; i < size; i++) {
            if (accounts[i].getName().equals(name)) return accounts[i];   // 문자열 비교는 equals
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int q = Integer.parseInt(br.readLine().trim());
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < q; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            String cmd = st.nextToken();
            String name = st.nextToken();
            Account acc = find(name);

            if (cmd.equals("OPEN")) {
                long amount = Long.parseLong(st.nextToken());
                if (acc != null) {
                    sb.append("EXISTS\n");
                } else {
                    accounts[size++] = new Account(name, amount);
                    sb.append("OK\n");
                }
                continue;
            }

            // DEP, WD, BAL 공통 : 계좌가 없으면 NO ACCOUNT
            if (acc == null) {
                sb.append("NO ACCOUNT\n");
                continue;
            }

            switch (cmd) {
                case "DEP" -> {
                    acc.deposit(Long.parseLong(st.nextToken()));
                    sb.append(acc.getBalance()).append('\n');
                }
                case "WD" -> {
                    if (acc.withdraw(Long.parseLong(st.nextToken()))) {
                        sb.append(acc.getBalance()).append('\n');
                    } else {
                        sb.append("FAIL\n");
                    }
                }
                case "BAL" -> sb.append(acc.getBalance()).append('\n');
            }
        }

        sb.append("TOTAL ").append(Account.getCount());
        System.out.println(sb);
    }
}
