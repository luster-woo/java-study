package day04;

/*
 * 추상 클래스 (abstract class) = "미완성 설계도"
 *
 *  - abstract 메서드 : 몸통 { } 없이 선언만 → "자식아, 이건 네가 꼭 만들어라"
 *  - abstract 메서드가 하나라도 있으면 클래스도 abstract 필수
 *  - new로 객체 생성 불가 (미완성이니까)
 *  - 자식은 abstract 메서드를 "반드시" 오버라이딩 (안 하면 자식도 abstract가 돼야 함)
 *  - 일반 필드, 일반 메서드, 생성자는 가질 수 있음 → 공통 기능은 물려줌
 *
 * D3P1 도형 문제의 Shape.area()가 "return 0" 이었던 걸 떠올리면 :
 *  → 도형 넓이 0은 의미가 없다. 사실 "자식마다 반드시 다르게 만들어야 하는 것"
 *  → 이걸 문법으로 강제하는 게 abstract
 *
 * [실행 결과]
 * 멍멍이 : 숨을 쉰다
 * 멍멍이 : 멍멍
 * 나비 : 숨을 쉰다
 * 나비 : 야옹
 */

abstract class Animal {
    String name;

    Animal(String name) {                 // 생성자 가능 (자식이 super(name)으로 호출)
        this.name = name;
    }

    // 일반 메서드 : 모든 동물 공통 → 그대로 물려줌
    void breathe() {
        System.out.println(name + " : 숨을 쉰다");
    }

    // 추상 메서드 : 동물마다 다름 → 몸통 없이 ; 로 끝
    abstract void sound();
}

class Dog extends Animal {
    Dog(String name) { super(name); }

    @Override
    void sound() {                        // 안 만들면 컴파일 에러
        System.out.println(name + " : 멍멍");
    }
}

class Cat extends Animal {
    Cat(String name) { super(name); }

    @Override
    void sound() {
        System.out.println(name + " : 야옹");
    }
}

// ❌ 추상 메서드를 구현 안 하면 컴파일 에러
// class Fish extends Animal {
//     Fish(String name) { super(name); }
// }

// ⭕ 구현 안 하려면 자식도 abstract로 (손자에게 떠넘기기)
abstract class Bird extends Animal {
    Bird(String name) { super(name); }
}

public class AbstractTest {
    public static void main(String[] args) {
        // Animal a = new Animal("동물");   // ❌ 추상 클래스는 객체 생성 불가

        Animal[] animals = { new Dog("멍멍이"), new Cat("나비") };   // 타입으로는 사용 가능 (다형성)
        for (Animal a : animals) {
            a.breathe();                  // 부모가 만든 공통 기능
            a.sound();                    // 자식이 완성한 기능 (동적 바인딩)
        }
    }
}
