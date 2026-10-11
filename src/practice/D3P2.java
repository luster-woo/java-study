package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class D3P2 {

    static class Employee {
        protected String name;
        protected long base;

        Employee(String name, long base) {
            this.name = name;
            this.base = base;
        }

        long pay() { return base; }         // STAFF는 이걸 그대로 사용
    }

    static class Manager extends Employee {
        int team;

        Manager(String name, long base, int team) {
            super(name, base);              // Employee에 기본 생성자가 없으니 필수
            this.team = team;
        }

        @Override
        long pay() { return super.pay() + team * 100_000L; }   // 부모 급여 + 팀원 수당
    }

    static class Intern extends Employee {
        Intern(String name, long base) {
            super(name, base);
        }

        @Override
        long pay() { return base * 8 / 10; }    // 0.8 곱하면 실수 오차 → 정수 나눗셈으로 버림
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine().trim());

        Employee[] emps = new Employee[n];
        for (int i = 0; i < n; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            String type = st.nextToken();
            String name = st.nextToken();
            long base = Long.parseLong(st.nextToken());
            emps[i] = switch (type) {               // switch expression으로 바로 대입
                case "MANAGER" -> new Manager(name, base, Integer.parseInt(st.nextToken()));
                case "INTERN"  -> new Intern(name, base);
                default        -> new Employee(name, base);   // STAFF
            };
        }

        StringBuilder sb = new StringBuilder();
        Employee top = emps[0];
        for (Employee e : emps) {
            sb.append(e.name).append(' ').append(e.pay()).append('\n');
            if (e.pay() > top.pay()) top = e;       // ">" 이라 같으면 먼저 나온 사람 유지
        }
        sb.append("TOP ").append(top.name);
        System.out.println(sb);
    }
}
