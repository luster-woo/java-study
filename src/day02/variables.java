package day02;

class Student {

    // 멤버 변수
    String name;          // ① 인스턴스 변수
    static int count = 0; // ② 클래스 변수

    void study(int hours) {  // ④ 매개변수
        int score = 100;     // ③ 지역 변수

        System.out.println(name);
        System.out.println(hours);
        System.out.println(score);
    }
}

public class variables {
    public static void main(String[] args) {

        Student s1 = new Student();

        s1.name = "철수";
        s1.study(3);
    }
}