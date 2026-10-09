package day02.b_smartphone;

public class SmartPhone {
    static double osVer = 1.0;   // 클래스(static) 변수 : Method Area(Static 영역)에 1개만 존재, 모든 객체가 공유
    String number;               // 멤버(인스턴스) 변수 : Heap의 각 객체 안에 따로 존재

    void call(String to) {       // to  : 파라미터 변수 (Stack, call() 프레임)
        String msg = "띠";       // msg : 로컬 변수 (Stack, call() 프레임)
        for (int i = 0; i < 3; i++) { // i : 로컬 변수 (for 블록 안에서만 유효)
            System.out.println(msg);
        }
    }   // call() 종료 → to, msg, i 모두 Stack에서 사라짐
}
