package day02;

/**
 * 추상화: 사람의 공통 특징을 뽑아 "설계도(클래스)"로 만든다.
 * UML의 ~ 기호 = default 접근 제한자 (아무것도 안 붙임, 같은 패키지에서만 접근 가능)
 */
public class Person {
    // 속성 (멤버 변수, 필드) : 객체가 "가진 것"
    String name;
    int age;
    boolean isHungry;

    // 기능 (메서드) : 객체가 "하는 것"
    void eat() {
        System.out.println(name + "이(가) 밥을 먹습니다.");
        isHungry = false;
    }

    void work() {
        System.out.println(name + "이(가) 일을 합니다.");
        isHungry = true;
    }
}
