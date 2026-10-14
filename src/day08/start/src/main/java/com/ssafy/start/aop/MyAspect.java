package com.ssafy.start.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/*
 * Annotation으로 Aspect 작성 (강의노트 "Spring AOP 시작하기")
 *
 *  ① @Component : Aspect도 빈이어야 함
 *  ② @Aspect    : 이 빈이 Aspect임을 선언
 *  ③ @Pointcut + Advice 타입 : 어디에(Pointcut) 언제·무엇을(Advice)
 *
 *  PersonProxy 코드 위치  →  Advice
 *   try 전 "부팅"          →  @Before
 *   정상 후 "Git Push"      →  @AfterReturning
 *   catch "조퇴"           →  @AfterThrowing
 *   finally "마무리"        →  @After
 *
 *  ※ AroundAspect와 동시에 켜면 같은 문구가 두 번씩 나옴 → 둘 중 하나만 @Component
 */
@Component
@Aspect
public class MyAspect {

    // named pointcut: com.ssafy.start.aop 패키지 모든 클래스의 coding() (파라미터 없음)
    @Pointcut("execution(* com.ssafy.start.aop.*.coding())")
    public void mypt() {
    }

    @Before("mypt()")
    public void before(JoinPoint jp) {
        System.out.println("컴퓨터를 부팅한다. (" + jp.getSignature().getName() + " 시작 전)");
    }

    // returning 이름(num) == 파라미터 이름(num)
    @AfterReturning(value = "mypt()", returning = "num")
    public void afterReturning(int num) {
        System.out.println("Git Push 한다. : " + num + "줄의 코드를");
    }

    // throwing 이름(th) == 파라미터 이름(th)
    @AfterThrowing(value = "mypt()", throwing = "th")
    public void afterThrowing(Throwable th) {
        System.out.println("조퇴를 한다.");
        if (th instanceof OuchException ouch) {
            ouch.handleException();
        }
    }

    @After("mypt()")
    public void after() {
        System.out.println("보람찬 하루를 마무리 한다.");
    }
}
