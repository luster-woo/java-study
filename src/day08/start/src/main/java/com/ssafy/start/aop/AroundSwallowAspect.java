package com.ssafy.start.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/*
 * 강의노트 버전의 @Around - 예외를 "삼키는" 버전
 *
 *  AroundAspect와 차이: catch에서 throw 하지 않음
 *   → 예외가 나도 호출한 쪽은 예외를 못 받고 0을 받음
 *   → 이렇게 흐름(예외·반환값)을 바꿀 수 있는 게 @Around만의 힘 = "가장 강력"
 *   → 반대로 실수로 throw / return을 빼먹으면 원래 동작이 바뀌니 주의
 *
 *  ※ 지금은 꺼져 있음. 켜려면 @Component 추가 + 다른 Aspect의 @Component 제거
 */
@Aspect
public class AroundSwallowAspect {

    @Around("com.ssafy.start.aop.MyAspect.mypt()")
    public Object around(ProceedingJoinPoint pjp) {
        int num = 0;
        System.out.println("컴퓨터를 부팅한다.");                          // @Before 자리
        try {
            num = (int) pjp.proceed();                                    // Target 실행
            System.out.println("Git Push 한다. : " + num + "줄의 코드를");    // @AfterReturning 자리
        } catch (Throwable th) {
            System.out.println("조퇴를 한다.");                             // @AfterThrowing 자리
            if (th instanceof OuchException ouch) {
                ouch.handleException();
            }
            // throw 없음 → 예외를 여기서 삼킴
        } finally {
            System.out.println("보람찬 하루를 마무리 한다.");                 // @After 자리
        }
        return num;                                                       // 예외가 났으면 0
    }
}
