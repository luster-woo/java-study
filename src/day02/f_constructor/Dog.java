package day02.f_constructor;

/*
 * 생성자 (Constructor)
 * - new 할 때 딱 한 번 호출되어 객체를 "초기화"하는 특별한 메서드
 * - 규칙 : 이름 = 클래스 이름, 리턴 타입 없음 (void도 안 씀)
 * - 생성자를 하나도 안 만들면 컴파일러가 기본 생성자를 자동으로 넣어준다
 *   → 하지만 생성자를 하나라도 직접 만들면 기본 생성자는 자동으로 안 생긴다!
 */
public class Dog {
    String name;
    int age;

    // 1. 기본 생성자 : 파라미터 없음
    public Dog() {
        this("멍멍이", 1);   // 아래 파라미터 생성자를 호출 (this()는 반드시 첫 줄)
        System.out.println("기본 생성자 호출");
    }

    // 2. 파라미터 있는 생성자 : 만들 때 값을 바로 넣는다
    public Dog(String name, int age) {
        this.name = name;    // this.name = 멤버 변수, name = 파라미터
        this.age = age;
        System.out.println("파라미터 생성자 호출 : " + name + ", " + age);
    }

    void info() {
        System.out.println("이름 : " + name + ", 나이 : " + age);
    }
}
