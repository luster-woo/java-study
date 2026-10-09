package day02;

/**
 * 구체화: 설계도(Person)로 실제 객체(인스턴스)를 만들어 사용한다.
 */
public class PersonTest {
    public static void main(String[] args) {
        // 1. 객체 생성 : new 클래스이름()
        Person p1 = new Person();
        Person p2 = new Person();

        // 2. 생성 직후 값 확인 : 멤버 변수는 기본값으로 자동 초기화
        System.out.println("[생성 직후] " + p1.name + ", " + p1.age + ", " + p1.isHungry);

        // 3. 속성 값 넣기 : 참조변수.멤버변수
        p1.name = "홍길동";
        p1.age = 20;
        p1.isHungry = true;

        p2.name = "임꺽정";
        p2.age = 30;
        p2.isHungry = false;

        // 4. 기능 사용 : 참조변수.메서드()
        p1.eat();
        p2.work();

        // 5. 결과 확인 : 각 객체는 자기만의 값을 가진다
        System.out.println(p1.name + " : " + p1.age + "살, 배고픔 " + p1.isHungry);
        System.out.println(p2.name + " : " + p2.age + "살, 배고픔 " + p2.isHungry);

        // 6. 두 객체의 관계 : 같은 설계도로 만들었지만 서로 다른(독립적인) 객체
        System.out.println("p1 == p2 ? " + (p1 == p2));
    }
}
