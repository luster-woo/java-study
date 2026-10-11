package day03.c_structure;                 // ① 패키지 선언부 : 이 파일이 속한 폴더(패키지). 파일 맨 첫 줄, 하나만

import java.io.*;                          // ② 외부 패키지 import : 다른 패키지의 클래스를 이름만으로 쓰기 위해
                                           //    * = 그 패키지의 모든 클래스 (하위 패키지는 포함 X)
                                           //    java.lang(String, System 등)은 import 없이 자동 사용

/*
 * 일반적인 클래스 레이아웃 (순서)
 *  ① package → ② import → ③ class 선언부
 *     { ④ 멤버 변수 → ⑤ 초기화 블록 → ⑥ 생성자 → ⑦ 멤버 메서드 }
 *
 *  - ①②③ 순서는 문법상 "반드시" 지켜야 함
 *  - 클래스 { } 안의 ④~⑦ 순서는 자유지만, 위 순서로 쓰는 게 관례 (읽기 쉬움)
 *
 * [실행 결과]
 * 초기화 블록 실행 : name = andy
 * 생성자 실행 전 name = andy
 * 생성자 실행 후 name = hong, age = 10
 * setName 후 name = gildong
 */
public class ClassStructure {              // ③ class 선언부 : public 클래스 이름 = 파일 이름

    // ④ 멤버 변수 : 객체의 "상태"
    String name;
    int age;

    // ⑤ 초기화 블록 : 생성자보다 "먼저", 객체 만들 때마다 실행
    //    → 모든 생성자에 공통으로 들어갈 초기화 코드를 한 곳에
    {
        name = "andy";
        System.out.println("초기화 블록 실행 : name = " + name);
    }

    // ⑥ 생성자 : 객체 초기화
    public ClassStructure(String name, int age) {
        System.out.println("생성자 실행 전 name = " + this.name);  // 초기화 블록이 이미 andy로 넣어둠
        this.name = name;
        this.age = age;
        System.out.println("생성자 실행 후 name = " + this.name + ", age = " + this.age);
    }

    // ⑦ 멤버 메서드 : 객체의 "동작"
    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        ClassStructure cs = new ClassStructure("hong", 10);
        cs.setName("gildong");
        System.out.println("setName 후 name = " + cs.name);
    }
}
