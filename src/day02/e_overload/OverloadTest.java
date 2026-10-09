package day02.e_overload;

/*
 * 메서드 오버로딩 (Overloading)
 * - 같은 이름의 메서드를 "파라미터만 다르게" 여러 개 정의하는 것
 * - 조건 : 이름 같음 + 파라미터의 개수 / 타입 / 순서 중 하나 이상 다름
 * - 리턴 타입만 다른 건 오버로딩 X (컴파일 에러)
 * - 장점 : 같은 기능을 하는 메서드를 이름 하나로 쓸 수 있다 (예: println(int), println(String) ...)
 *
 * [실행 결과]
 * [int,int] 3
 * [int,int,int] 6
 * [double,double] 4.0
 * [String,String] HelloJava
 * [double,double] 3.5
 * [double,double] 15.0
 * int, String : 1번
 * String, int : 번호1
 */
public class OverloadTest {

    // 1. 기본
    static int add(int a, int b) {
        System.out.print("[int,int] ");
        return a + b;
    }

    // 2. 파라미터 "개수"가 다름
    static int add(int a, int b, int c) {
        System.out.print("[int,int,int] ");
        return a + b + c;
    }

    // 3. 파라미터 "타입"이 다름
    static double add(double a, double b) {
        System.out.print("[double,double] ");
        return a + b;
    }

    static String add(String a, String b) {
        System.out.print("[String,String] ");
        return a + b;
    }

    // 4. 파라미터 "순서"가 다름
    static void print(int a, String b) {
        System.out.println("int, String : " + a + b);
    }

    static void print(String a, int b) {
        System.out.println("String, int : " + a + b);
    }

    // ❌ 리턴 타입만 다르면 오버로딩 안 됨 → 컴파일 에러
    // static double add(int a, int b) { return a + b; }
    //   add(1, 2) 를 호출했을 때 어느 쪽인지 구분할 수 없기 때문

    // ❌ 파라미터 "이름"만 다른 것도 안 됨 (타입이 같으면 같은 메서드)
    // static int add(int x, int y) { return x + y; }

    public static void main(String[] args) {
        System.out.println(add(1, 2));            // 딱 맞는 add(int,int)
        System.out.println(add(1, 2, 3));         // 개수로 구분
        System.out.println(add(1.5, 2.5));        // 타입으로 구분
        System.out.println(add("Hello", "Java")); // 타입으로 구분

        // 딱 맞는 게 없으면 묵시적 형변환이 가능한 메서드를 찾는다
        System.out.println(add(1, 2.5));          // int → double 변환 → add(double,double)
        long x = 10;
        System.out.println(add(x, 5));            // long은 int로 못 줄임 → add(double,double)

        print(1, "번");                           // 순서로 구분
        print("번호", 1);
    }
}
