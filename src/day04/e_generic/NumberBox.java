package day04.e_generic;

import java.util.List;

/*
 * ③ 한정된 타입 파라미터 (bounded type) : <T extends 상위타입>
 *    - T에 아무 타입이나 못 오게 제한 → Number와 그 자식(Integer, Double...)만 허용
 *    - 제한한 덕분에 Number의 메서드(doubleValue())를 T에서 쓸 수 있다
 *    - 인터페이스로 제한할 때도 extends, 여러 개면 & 로 연결 : <T extends Number & Comparable<T>>
 */
public class NumberBox<T extends Number> {
    private List<T> numbers;

    NumberBox(List<T> numbers) {
        this.numbers = numbers;
    }

    double sum() {
        double total = 0;
        for (T n : numbers) {
            total += n.doubleValue();   // T가 Number라는 보장이 있어서 가능
        }
        return total;
    }
}
