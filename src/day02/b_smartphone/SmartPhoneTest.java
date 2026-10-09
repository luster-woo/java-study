package day02.b_smartphone;

/*
 * [실행 결과]
 * 1.0 : 010
 * 띠
 * 띠
 * 띠
 * 1.0 : 010-111-1111
 * 2.0 : 010
 *
 * 메모리 동작 그림은 notes/day02/SmartPhone_메모리.md 참고
 */
public class SmartPhoneTest {
    public static void main(String[] args) {
        // ① 클래스 로딩 : SmartPhone.osVer = 1.0 이 Static 영역에 생성 (객체 만들기 전부터 존재)

        // ② Heap에 객체(0x100) 생성, number = null(기본값) / Stack의 sphone에 주소 0x100 저장
        SmartPhone sphone = new SmartPhone();
        sphone.number = "010";
        System.out.println(sphone.osVer + " : " + sphone.number);   // 1.0 : 010

        // ③ call() 프레임이 Stack에 쌓임 (to="011", msg="띠", i) → 띠 3번 출력 → 프레임 제거
        sphone.call("011");

        // ④ Heap에 새 객체(0x200) 생성 / sphone2에 0x200 저장 → sphone과 다른 객체
        SmartPhone sphone2 = new SmartPhone();
        sphone2.number = "010-111-1111";
        System.out.println(sphone2.osVer + " : " + sphone2.number); // 1.0 : 010-111-1111

        // ⑤ Static 영역의 osVer 하나를 2.0으로 변경 → 모든 객체에 반영
        SmartPhone.osVer = 2.0;
        System.out.println(sphone.osVer + " : " + sphone.number);   // 2.0 : 010
    }
}
