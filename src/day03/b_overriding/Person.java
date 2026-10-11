package day03.b_overriding;

import java.io.IOException;

public class Person {
    String name;
    String power = "평범한 사람";     // 자식에도 같은 이름의 변수를 만들어서 super.power 확인용

    Person() {
        System.out.println("Person() 생성자");
    }

    Person(String name) {
        this.name = name;
        System.out.println("Person(String) 생성자 : " + name);
    }

    void jump() {
        System.out.println("두 다리로 힘껏 점프");    // 물려받은 jump(). 성능이 좋지 않다.
    }

    // 오버라이딩 조건 확인용 메서드들
    protected int getAge() {
        return 20;
    }

    void read() throws IOException {
        System.out.println("책을 읽는다");
    }
}
