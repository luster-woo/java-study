package day03.a_inheritance.person;

// 부모 클래스 (조상, super class) : 사람이면 누구나 가진 공통 특징
public class Person {
    String name;

    void eat() {
        System.out.println("냠냠");
    }

    void jump() {
        System.out.println("두 다리로 폴짝");
    }
}
