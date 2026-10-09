package day02.f_constructor;

/*
 * [실행 결과]
 * 파라미터 생성자 호출 : 멍멍이, 1
 * 기본 생성자 호출
 * 이름 : 멍멍이, 나이 : 1
 * 파라미터 생성자 호출 : 바둑이, 3
 * 이름 : 바둑이, 나이 : 3
 */
public class DogTest {
    public static void main(String[] args) {
        // 기본 생성자 사용 → 정해둔 기본값으로 초기화
        Dog d1 = new Dog();
        d1.info();

        // 파라미터 생성자 사용 → 원하는 값으로 바로 초기화
        Dog d2 = new Dog("바둑이", 3);
        d2.info();
    }
}
