package day02.c_method;

/*
 * 메서드 = 선언부 + 구현부
 *
 *   [제한자] 리턴타입 메서드이름(파라미터 목록) { ← 선언부
 *       ...비즈니스 로직...                      ← 구현부
 *       return 값;
 *   }
 */
public class MethodTest {

    // ===================== 선언부 : 리턴 타입 =====================

    // 리턴 타입 int : 호출한 곳으로 int 값을 돌려준다
    public int add(int a, int b) {
        return a + b;
    }

    // 리턴 타입 void : 아무것도 돌려주지 않는다 → return 생략 가능
    public void sayHello() {
        System.out.println("Hello");
        // return;   // 써도 되고 안 써도 됨
    }

    // ===================== 선언부 : 파라미터 목록 =====================

    // 파라미터 long : int 값을 넘겨도 묵시적 형변환(int → long)으로 받을 수 있다
    // (같은 이름 add지만 파라미터 타입이 달라서 공존 가능 = 오버로딩)
    public long add(long a, long b) {
        return a + b;
    }

    // ===================== 구현부 : return =====================

    // 값 반환 시 묵시적 형변환 : int 결과를 double로 돌려줌
    public double half(int a) {
        return a / 2;   // int(정수 나눗셈) → double로 자동 변환해서 반환
    }

    // 조건문으로 return 할 때는 "모든 경로"에서 return 해야 한다
    /*  컴파일 에러 버전 : '-' 일 때 return이 없음 → "missing return statement"
    public double calc(double a, double b, char oper) {
        if (oper == '+') {
            return a + b;
        } else if (oper == '-') {

        } else {
            return 0;
        }
    }
    */

    // 해결 1 : 모든 분기에 return 작성
    public double calc(double a, double b, char oper) {
        if (oper == '+') {
            return a + b;
        } else if (oper == '-') {
            return a - b;
        } else {
            return 0;
        }
    }

    // 해결 2 : 결과 변수에 담고 마지막에 한 번만 return (권장)
    public double calc2(double a, double b, char oper) {
        double result = 0;
        if (oper == '+') {
            result = a + b;
        } else if (oper == '-') {
            result = a - b;
        }
        return result;
    }

    public static void main(String[] args) {
        MethodTest mt = new MethodTest();

        // 1. 결과를 받을 때 묵시적 형변환
        int result1 = mt.add(100, 200);       // int → int
        double result2 = mt.add(100, 200);    // int → double 자동 변환
        System.out.println("result1 = " + result1);   // 300
        System.out.println("result2 = " + result2);   // 300.0

        // 2. void 메서드는 돌려주는 값이 없다
        mt.sayHello();
        // System.out.println(mt.sayHello());  // 컴파일 에러 : void는 출력할 값이 없음

        // 3. 파라미터 전달 시 묵시적 형변환
        long r3 = mt.add(100L, 200L);         // long 버전 호출
        System.out.println("r3 = " + r3);     // 300
        // mt.add(1.1, 2.2);  // 컴파일 에러 : double → int/long 은 자동 변환 불가(큰 → 작은)

        // 4. 반환 시 묵시적 형변환
        System.out.println("half(7) = " + mt.half(7));   // 3.0 (7/2=3 → 3.0)

        // 5. 조건문 return
        System.out.println("calc  = " + mt.calc(10, 3, '-'));   // 7.0
        System.out.println("calc2 = " + mt.calc2(10, 3, '+'));  // 13.0
        System.out.println("calc2 = " + mt.calc2(10, 3, '*'));  // 0.0 (해당 없음)
    }
}
