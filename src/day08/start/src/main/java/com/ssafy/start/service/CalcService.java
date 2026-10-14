package com.ssafy.start.service;

import org.springframework.stereotype.Service;

// AOP 적용 대상(Target) - 핵심 로직만 있음. 로그 코드는 한 줄도 없음
@Service
public class CalcService {

    public int add(int a, int b) {
        return a + b;
    }

    public int divide(int a, int b) {
        return a / b;   // b == 0 이면 ArithmeticException → @AfterThrowing 확인용
    }
}
