package com.ssafy.start.aop;

public class OuchException extends RuntimeException {
    public void handleException() {
        System.out.println("병원에 간다.");
    }
}
