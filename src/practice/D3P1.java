package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class D3P1 {

    // 부모 : 모든 도형의 공통 모양 (abstract는 D4에서 → 오늘은 기본값을 돌려주게 둠)
    static class Shape {
        double area() { return 0; }
        String name() { return "SHAPE"; }
    }

    // 자식들 : area()와 name()을 각자 방식으로 오버라이딩
    static class Circle extends Shape {
        int r;
        Circle(int r) { this.r = r; }

        @Override double area() { return Math.PI * r * r; }
        @Override String name() { return "CIRCLE"; }
    }

    static class Rect extends Shape {
        int w, h;
        Rect(int w, int h) { this.w = w; this.h = h; }

        @Override double area() { return (double) w * h; }   // 1000*1000은 int도 되지만 습관적으로 double
        @Override String name() { return "RECT"; }
    }

    static class Triangle extends Shape {
        int b, h;
        Triangle(int b, int h) { this.b = b; this.h = h; }

        @Override double area() { return b * h / 2.0; }      // 2가 아니라 2.0 → 실수 나눗셈 (1*1/2 = 0 방지)
        @Override String name() { return "TRIANGLE"; }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine().trim());

        Shape[] shapes = new Shape[n];      // 다형성 배열 : 부모 타입 하나에 여러 자식
        for (int i = 0; i < n; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            String type = st.nextToken();
            int a = Integer.parseInt(st.nextToken());
            switch (type) {
                case "C" -> shapes[i] = new Circle(a);                                    // 업캐스팅 (자동)
                case "R" -> shapes[i] = new Rect(a, Integer.parseInt(st.nextToken()));
                case "T" -> shapes[i] = new Triangle(a, Integer.parseInt(st.nextToken()));
            }
        }

        StringBuilder sb = new StringBuilder();
        double total = 0;
        for (Shape s : shapes) {
            double area = s.area();         // 동적 바인딩 : 실제 객체의 area() 실행
            total += area;
            sb.append(s.name()).append(' ').append(String.format("%.2f", area)).append('\n');
        }
        sb.append("TOTAL ").append(String.format("%.2f", total));   // 반올림 전 값으로 합산
        System.out.println(sb);
    }
}
