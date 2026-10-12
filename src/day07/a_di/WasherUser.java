package day07.a_di;

/*
 * WasherUser ──has-a──> Washer (의존성)
 *
 *  - 필드 washer : 의존성이 들어갈 "자리" (선언만, 직접 new 하지 않음)
 *  - 주입 통로 3가지
 *      ① 필드 직접 할당  user.washer = ...      (같은 패키지라 접근 가능)
 *      ② 생성자          new WasherUser(washer)
 *      ③ setter          user.setWasher(washer)
 */
public class WasherUser {
    Washer washer;   // 접근제어자 생략(default) → 같은 패키지에서 직접 할당 가능

    // 기본 생성자 - 의존성 없이 생성 (나중에 setter 또는 필드로 주입)
    public WasherUser() {
    }

    // 생성자 주입
    public WasherUser(Washer washer) {
        this.washer = washer;
    }

    // 비즈니스 로직 - 어떤 세탁기인지 몰라도 동작
    public void useWasher(String clothes) {
        if (washer == null) {
            System.out.println("세탁기가 없어서 빨래 못 함: " + clothes);
            return;
        }
        washer.wash(clothes);
    }

    // getter / setter (setter 주입)
    public Washer getWasher() {
        return washer;
    }

    public void setWasher(Washer washer) {
        this.washer = washer;
    }
}
