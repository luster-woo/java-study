package com.ssafy.start.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/*
 * MyAspect의 4개 Advice를 @Around 하나로 변환 (동작 100% 동일 버전)
 *
 *  4개 Advice 버전                         @Around 버전
 *  @Before 메서드                      →   proceed() 전
 *  @AfterReturning(returning = "num") →   proceed()의 반환값을 직접 받음
 *  @AfterThrowing(throwing = "th")    →   catch로 직접 받음
 *  @After 메서드                       →   finally
 *
 *  ※ 지금은 꺼져 있음 (@Component 없음 → 빈이 아니라 Aspect로 인식 안 됨)
 *     켜려면: 여기에 @Component 추가 + MyAspect의 @Component 제거
 *     (둘 다 켜면 같은 문구가 두 번씩 출력됨)
 *
 *  Pointcut은 MyAspect의 named pointcut 재사용 → 다른 클래스라 "풀 패키지명.클래스.메서드명()"
 */
@Aspect
public class AroundAspect {

    @Around("com.ssafy.start.aop.MyAspect.mypt()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("컴퓨터를 부팅한다.");                          // @Before 자리
        try {
            Object result = pjp.proceed();                                // coding() 실행
            int num = (int) result;
            System.out.println("Git Push 한다. : " + num + "줄의 코드를");    // @AfterReturning 자리
            return result;                                                // 반환값 그대로 돌려주기
        } catch (Throwable th) {
            System.out.println("조퇴를 한다.");                             // @AfterThrowing 자리
            if (th instanceof OuchException ouch) {
                ouch.handleException();
            }
            throw th;                                                     // 예외를 다시 던짐 → 원래 동작 유지
        } finally {
            System.out.println("보람찬 하루를 마무리 한다.");                 // @After 자리
        }
    }
}
