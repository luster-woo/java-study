package com.ssafy.logging.di;

// POJO - 4번(a_di)과 똑같음. Spring을 몰라도 됨
public class WasherUser {
    private Washer washer;

    public WasherUser() {
    }

    public WasherUser(Washer washer) {      // 생성자 주입용
        this.washer = washer;
    }

    public void useWasher(String clothes) {
        washer.wash(clothes);
    }

    public Washer getWasher() {
        return washer;
    }

    public void setWasher(Washer washer) {  // setter 주입용
        this.washer = washer;
    }
}
