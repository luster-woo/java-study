package com.ssafy.logging.di;

// POJO - Spring 관련 코드가 하나도 없음
public class SWasher implements Washer {
    public SWasher() {
        System.out.println("SWasher 생성");
    }

    @Override
    public void wash(String clothes) {
        System.out.println("SWasher로 세탁: " + clothes);
    }
}
