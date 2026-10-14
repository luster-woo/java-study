package com.ssafy.start.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/*
 * Advice Type - 5가지 메서드 정의
 *
 *  [필수] @Component + @Aspect  → 빈이어야 스프링이 Aspect로 인식
 *  [필수] build.gradle: spring-boot-starter-aspectj
 *
 *  실행 순서 (정상)  : @Around 전 → @Before → 핵심 로직 → @AfterReturning → @After → @Around 후
 *  실행 순서 (예외)  : @Around 전 → @Before → 핵심 로직 → @AfterThrowing  → @After → (예외 전파)
 */
@Slf4j
@Component
@Aspect
public class LogAspect {

    // 공통 Pointcut: service 패키지의 모든 클래스의 모든 메서드 (named pointcut으로 재사용)
    @Pointcut("execution(* com.ssafy.start.service.*.*(..))")
    public void serviceLayer() {
    }

    // ① @Before — 대상 메서드 실행 직전
    @Before("serviceLayer()")
    public void logBefore(JoinPoint jp) {
        log.info(">>> {}", jp.getSignature());
    }

    // ② @After — 종료 후 (성공·실패 무관, finally)
    @After("serviceLayer()")
    public void logAfter(JoinPoint jp) {
        log.info("<<< {}", jp.getSignature());
    }

    // ③ @AfterReturning — 정상 종료 후 (반환값 접근)
    //    returning = "ret" 의 이름과 파라미터 이름(ret)이 같아야 함
    @AfterReturning(pointcut = "serviceLayer()", returning = "ret")
    public void logReturn(JoinPoint jp, Object ret) {
        log.info("return = {}", ret);
    }

    // ④ @AfterThrowing — 예외가 던져졌을 때만
    //    throwing = "ex" 의 이름과 파라미터 이름(ex)이 같아야 함
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void logThrow(JoinPoint jp, Exception ex) {
        log.error("error = {}", ex.getMessage());
    }

    // ⑤ @Around — 실행 전후 모두 + 흐름 가로채기 (가장 강력)
    //    ProceedingJoinPoint + proceed() 호출 + 반환값 return + throws Throwable 필수
    @Around("serviceLayer()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        long t = System.currentTimeMillis();
        Object ret = pjp.proceed();                                    // 원본 메서드 호출
        log.info("took {}ms", System.currentTimeMillis() - t);
        return ret;                                                    // 안 하면 호출한 쪽은 null(0)을 받음
    }
}
