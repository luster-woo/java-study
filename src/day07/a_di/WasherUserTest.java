package day07.a_di;

/*
 * 의존성 주입 3가지 방법 + DI의 장점 확인
 *
 * [실행 결과]
 * --- 0. 주입 안 함 ---
 * 세탁기가 없어서 빨래 못 함: 셔츠
 * --- 1. 생성자 주입 ---
 * SWasher 생성
 * SWasher로 세탁: 셔츠
 * --- 2. setter 주입 (교체) ---
 * LWasher 생성
 * LWasher로 세탁: 이불
 * --- 3. 필드 직접 할당 ---
 * SWasher 생성
 * SWasher로 세탁: 양말
 * --- 4. 세탁기 하나를 여러 사용자가 공유 ---
 * LWasher 생성
 * LWasher로 세탁: 수건
 * LWasher로 세탁: 바지
 * 같은 세탁기? true
 */
public class WasherUserTest {
    public static void main(String[] args) {
        System.out.println("--- 0. 주입 안 함 ---");
        WasherUser empty = new WasherUser();
        empty.useWasher("셔츠");                 // washer == null

        System.out.println("--- 1. 생성자 주입 ---");
        WasherUser user = new WasherUser(new SWasher());
        user.useWasher("셔츠");

        System.out.println("--- 2. setter 주입 (교체) ---");
        user.setWasher(new LWasher());           // WasherUser 코드는 그대로, 세탁기만 교체
        user.useWasher("이불");

        System.out.println("--- 3. 필드 직접 할당 ---");
        user.washer = new SWasher();             // 같은 패키지라 가능 (private이면 불가)
        user.useWasher("양말");

        System.out.println("--- 4. 세탁기 하나를 여러 사용자가 공유 ---");
        Washer shared = new LWasher();           // 한 번만 생성 → Spring 빈(싱글턴)의 느낌
        WasherUser u1 = new WasherUser(shared);
        WasherUser u2 = new WasherUser(shared);
        u1.useWasher("수건");
        u2.useWasher("바지");
        System.out.println("같은 세탁기? " + (u1.getWasher() == u2.getWasher()));
    }
}
