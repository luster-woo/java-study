package day02.d_callby;

/*
 * Java는 call by value! value의 정체는?
 *  - 기본형(int 등) 변수 → 값 자체가 복사된다
 *  - 참조형(객체) 변수   → "주소값"이 복사된다
 *
 * [실행 결과]
 * change1 호출 전 memberVar: 5
 * change1 : 15
 * change1 호출 후 memberVar: 5      ← 복사본(var)만 바뀜
 * change2 : 105
 * change2 호출 후 memberVar: 105    ← 같은 객체를 가리키는 주소가 복사됨
 * change3 : 999
 * change3 호출 후 memberVar: 105    ← (보너스) 주소를 바꿔도 원본 변수는 그대로
 */
public class CallByTest {
    int memberVar = 10;

    // var 에는 memberVar의 "값 5"가 복사되어 들어온다
    static void change1(int var) {
        var += 10;                                   // 복사본만 15가 됨
        System.out.printf("change1 : %d%n", var);
    }   // 메서드 종료 → var 소멸, 원본은 5 그대로

    // cbt1 에는 cbt의 "주소값(예: 0x100)"이 복사되어 들어온다
    static void change2(CallByTest cbt1) {
        cbt1.memberVar += 100;                       // 주소를 따라가서 Heap의 객체를 직접 수정
        System.out.printf("change2 : %d%n", cbt1.memberVar);
    }   // cbt1(주소 복사본)은 사라지지만, 객체는 이미 바뀜

    // (보너스) 복사된 주소 자체를 새 객체로 바꾸면? → 원본 cbt와 연결이 끊김
    static void change3(CallByTest cbt1) {
        cbt1 = new CallByTest();                     // cbt1만 새 객체(0x200)를 가리킴
        cbt1.memberVar = 999;
        System.out.printf("change3 : %d%n", cbt1.memberVar);
    }

    public static void main(String[] args) {
        CallByTest cbt = new CallByTest();           // Heap 0x100, memberVar = 10
        cbt.memberVar = 5;
        System.out.printf("change1 호출 전 memberVar: %d%n", cbt.memberVar);
        change1(cbt.memberVar);                      // 값 5를 복사해서 전달
        System.out.printf("change1 호출 후 memberVar: %d%n", cbt.memberVar);
        change2(cbt);                                // 주소 0x100을 복사해서 전달
        System.out.printf("change2 호출 후 memberVar: %d%n", cbt.memberVar);

        change3(cbt);
        System.out.printf("change3 호출 후 memberVar: %d%n", cbt.memberVar);
    }
}
