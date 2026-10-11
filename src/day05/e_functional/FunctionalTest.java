package day05.e_functional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/*
 * 표준 함수형 인터페이스 = Java가 java.util.function 에 미리 만들어 둔 함수형 인터페이스
 *  → 람다 예제에서 Calculator, Checker 같은 걸 직접 만들 필요 없이 이걸 쓰면 된다
 *
 *  이름        | 메서드              | 입력 → 출력      | 한 마디
 *  ------------|--------------------|-----------------|---------------------
 *  Consumer<T> | void accept(T)     | T → 없음        | 받아서 쓰기만 (소비)
 *  Supplier<T> | T get()            | 없음 → T        | 만들어서 주기만 (공급)
 *  Function<T,R>| R apply(T)        | T → R           | 바꾸기 (변환)
 *  Predicate<T>| boolean test(T)    | T → boolean     | 검사하기 (조건)
 *  + Operator  | UnaryOperator<T> (T → T), BinaryOperator<T> (T, T → T)
 *  + Bi~       | BiFunction<T,U,R> 처럼 입력이 2개인 버전
 *
 * [실행 결과]
 * --- 1. Consumer : 받아서 쓰기만 ---
 * 안녕, kim
 * --- 2. Supplier : 만들어서 주기만 ---
 * 새 리스트 : []
 * 주사위 : 1~6 사이 OK
 * --- 3. Function : 바꾸기 ---
 * "apple" 길이 : 5
 * 5 → 25
 * 길이의 제곱 (andThen) : 25
 * --- 4. Predicate : 검사하기 ---
 * 10은 짝수? true
 * 10은 짝수이면서 양수? true
 * -4는 짝수이면서 양수? false
 * 7은 짝수가 아님? true
 * --- 5. Operator / Bi ---
 * 대문자 : HELLO
 * 3 + 4 = 7
 * kim 님은 20살
 * --- 6. 실제로 쓰이는 곳 ---
 * forEach(Consumer)     : APPLE KIWI BANANA
 * removeIf(Predicate)   : [apple, banana]
 * replaceAll(UnaryOp)   : [APPLE, BANANA]
 * 직접 만든 메서드        : [5, 6]
 */
public class FunctionalTest {

    // ===== 6. 파라미터를 함수형 인터페이스로 받으면 "동작"을 넘길 수 있다 =====
    // 리스트의 각 값을 어떻게 바꿀지는 호출하는 쪽이 정한다
    static <T, R> List<R> mapAll(List<T> list, Function<T, R> f) {
        List<R> result = new ArrayList<>();
        for (T item : list) {
            result.add(f.apply(item));
        }
        return result;
    }

    public static void main(String[] args) {

        System.out.println("--- 1. Consumer : 받아서 쓰기만 ---");
        Consumer<String> greet = name -> System.out.println("안녕, " + name);
        greet.accept("kim");                                  // 리턴 없음

        System.out.println("--- 2. Supplier : 만들어서 주기만 ---");
        Supplier<List<String>> newList = () -> new ArrayList<>();   // 입력 없음
        System.out.println("새 리스트 : " + newList.get());
        Supplier<Integer> dice = () -> (int) (Math.random() * 6) + 1;
        int d = dice.get();
        System.out.println("주사위 : " + (d >= 1 && d <= 6 ? "1~6 사이 OK" : "오류"));

        System.out.println("--- 3. Function : 바꾸기 ---");
        Function<String, Integer> length = s -> s.length();  // String → Integer
        Function<Integer, Integer> square = n -> n * n;       // Integer → Integer
        System.out.println("\"apple\" 길이 : " + length.apply("apple"));
        System.out.println("5 → " + square.apply(5));
        // andThen : 앞 함수 결과를 뒤 함수에 넘김 (함수 합치기)
        System.out.println("길이의 제곱 (andThen) : " + length.andThen(square).apply("apple"));

        System.out.println("--- 4. Predicate : 검사하기 ---");
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isPositive = n -> n > 0;
        System.out.println("10은 짝수? " + isEven.test(10));
        // and / or / negate 로 조건 조합
        System.out.println("10은 짝수이면서 양수? " + isEven.and(isPositive).test(10));
        System.out.println("-4는 짝수이면서 양수? " + isEven.and(isPositive).test(-4));
        System.out.println("7은 짝수가 아님? " + isEven.negate().test(7));

        System.out.println("--- 5. Operator / Bi ---");
        UnaryOperator<String> upper = s -> s.toUpperCase();  // = Function<String, String>
        BinaryOperator<Integer> add = (a, b) -> a + b;        // = BiFunction<Integer, Integer, Integer>
        BiFunction<String, Integer, String> intro = (name, age) -> name + " 님은 " + age + "살";
        System.out.println("대문자 : " + upper.apply("hello"));
        System.out.println("3 + 4 = " + add.apply(3, 4));
        System.out.println(intro.apply("kim", 20));

        System.out.println("--- 6. 실제로 쓰이는 곳 ---");
        // 컬렉션·Stream 메서드의 파라미터가 전부 이 인터페이스들이다
        List<String> fruits = new ArrayList<>(List.of("apple", "kiwi", "banana"));

        System.out.print("forEach(Consumer)     : ");
        fruits.forEach(f -> System.out.print(f.toUpperCase() + " "));   // forEach(Consumer<T>)
        System.out.println();

        fruits.removeIf(f -> f.length() < 5);                 // removeIf(Predicate<T>)
        System.out.println("removeIf(Predicate)   : " + fruits);

        List<String> copy = new ArrayList<>(fruits);
        copy.replaceAll(String::toUpperCase);                 // replaceAll(UnaryOperator<T>)
        System.out.println("replaceAll(UnaryOp)   : " + copy);

        System.out.println("직접 만든 메서드        : " + mapAll(fruits, String::length));   // Function<T,R>
    }
}
