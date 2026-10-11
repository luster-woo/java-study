package day05.d_lambda;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 * 람다식 (Lambda) = "메서드를 값처럼" 짧게 쓰는 문법   (파이썬의 lambda x: x * 2)
 *
 *   (파라미터) -> { 실행할 코드 }
 *
 *  - 추상 메서드가 "딱 1개"인 인터페이스(함수형 인터페이스)에만 쓸 수 있다
 *    → 메서드가 하나뿐이라 이름을 안 써도 뭘 구현하는지 알 수 있으니까
 *  - 사실은 "인터페이스 구현 객체"를 아주 짧게 만드는 것 (익명 클래스의 축약)
 *
 * [실행 결과]
 * --- 1. 익명 클래스 → 람다 ---
 * 익명 클래스 : 8
 * 람다       : 8
 * --- 2. 점점 줄여 쓰기 ---
 * 15 15 15 15
 * --- 3. 여러 모양 ---
 * Hello!
 * 짝수? true
 * 큰 값 : 9
 * --- 4. 컬렉션과 함께 ---
 * 원본       : [banana, kiwi, apple, cherry]
 * 사전순     : [apple, banana, cherry, kiwi]
 * 길이순     : [kiwi, apple, banana, cherry]
 * 길이 내림차순 : [banana, cherry, apple, kiwi]
 * forEach    : APPLE BANANA CHERRY KIWI
 * 5글자 이하 삭제 : [banana, cherry]
 * --- 5. 메서드 참조 (::) ---
 * banana
 * cherry
 * --- 6. 바깥 변수 사용 ---
 * 10% 할인 : 9000
 */

// ===== 함수형 인터페이스 : 추상 메서드가 딱 1개 =====
@FunctionalInterface                    // 2개 이상 만들면 컴파일 에러로 알려줌 (선택이지만 권장)
interface Calculator {
    int calc(int a, int b);
}

@FunctionalInterface
interface Greeting {
    void say();                         // 파라미터 X, 리턴 X
}

@FunctionalInterface
interface Checker {
    boolean check(int n);               // 파라미터 1개, 리턴 boolean
}

public class LambdaTest {
    public static void main(String[] args) {

        System.out.println("--- 1. 익명 클래스 → 람다 ---");
        // 예전 방식 : 인터페이스 구현 클래스를 이름 없이 그 자리에서 만들기 (익명 클래스)
        Calculator add1 = new Calculator() {
            @Override
            public int calc(int a, int b) {
                return a + b;
            }
        };
        // 람다 : 위 코드에서 "뻔한 부분"을 다 지운 것
        Calculator add2 = (a, b) -> a + b;

        System.out.println("익명 클래스 : " + add1.calc(3, 5));
        System.out.println("람다       : " + add2.calc(3, 5));

        System.out.println("--- 2. 점점 줄여 쓰기 ---");
        Calculator c1 = (int a, int b) -> { return a * b; };   // 다 쓴 형태
        Calculator c2 = (a, b) -> { return a * b; };           // 타입 생략 (인터페이스 보고 추론)
        Calculator c3 = (a, b) -> a * b;                        // 한 줄이면 { } 와 return 생략
        Calculator c4 = Math::multiplyExact;                    // 메서드 참조 (아래 5번)
        System.out.println(c1.calc(3, 5) + " " + c2.calc(3, 5) + " " + c3.calc(3, 5) + " " + c4.calc(3, 5));

        System.out.println("--- 3. 여러 모양 ---");
        Greeting g = () -> System.out.println("Hello!");      // 파라미터 없으면 ()
        g.say();

        Checker isEven = n -> n % 2 == 0;                      // 파라미터 1개면 ( ) 생략 가능
        System.out.println("짝수? " + isEven.check(4));

        Calculator max = (a, b) -> {                           // 여러 줄이면 { } 와 return 필요
            if (a > b) return a;
            return b;
        };
        System.out.println("큰 값 : " + max.calc(9, 2));

        System.out.println("--- 4. 컬렉션과 함께 ---");
        // 직접 인터페이스를 안 만들어도, Java가 미리 만들어 둔 것(Comparator 등)에 람다를 넘긴다
        List<String> fruits = new ArrayList<>(List.of("banana", "kiwi", "apple", "cherry"));
        System.out.println("원본       : " + fruits);

        fruits.sort((a, b) -> a.compareTo(b));                 // Comparator를 람다로
        System.out.println("사전순     : " + fruits);

        fruits.sort((a, b) -> a.length() - b.length());        // 기준만 바꾸면 정렬 방식이 바뀜
        System.out.println("길이순     : " + fruits);

        fruits.sort(Comparator.comparing(String::length).reversed());   // 더 읽기 쉬운 방식
        System.out.println("길이 내림차순 : " + fruits);

        fruits.sort(null);                                     // null = 기본(사전순) 정렬
        System.out.print("forEach    : ");
        fruits.forEach(f -> System.out.print(f.toUpperCase() + " "));   // 하나씩 꺼내서 실행
        System.out.println();

        fruits.removeIf(f -> f.length() <= 5);                 // 조건이 true인 것 삭제
        System.out.println("5글자 이하 삭제 : " + fruits);

        System.out.println("--- 5. 메서드 참조 (::) ---");
        // 람다가 "이미 있는 메서드 하나를 그대로 호출"만 할 때 더 짧게
        fruits.forEach(System.out::println);                   // = f -> System.out.println(f)
        // String::length       = s -> s.length()
        // Integer::parseInt    = s -> Integer.parseInt(s)
        // Math::max            = (a, b) -> Math.max(a, b)

        System.out.println("--- 6. 바깥 변수 사용 ---");
        int rate = 10;                                         // 람다 안에서 쓰는 바깥 지역변수는
        Calculator discount = (price, unused) -> price - price * rate / 100;
        // rate = 20;                                          // ❌ 값을 바꾸면 컴파일 에러 (사실상 final이어야 함)
        System.out.println(rate + "% 할인 : " + discount.calc(10000, 0));
    }
}
