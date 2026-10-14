package com.ssafy.start.aop;

import org.springframework.stereotype.Component;

import java.util.Random;

/*
 * Target - 핵심 로직(coding)만 있음
 *  Proxy 강의의 PersonProxy에 있던 "부팅 / Push / 조퇴 / 마무리"는 하나도 없음
 *  → 그 부가 기능은 MyAspect가 끼워 넣음
 *
 *  AOP는 빈에만 적용되므로 @Component 필수
 */
@Component
public class Programmer implements Person {

    @Override
    public int coding() {
        System.out.println("[Programmer] 열심히 코딩한다.");
        if (new Random().nextBoolean()) {
            throw new OuchException();          // 랜덤으로 문제 발생 → @AfterThrowing
        }
        return new Random().nextInt(100) + 1;  // 작성한 코드 줄 수 → @AfterReturning
    }
}
