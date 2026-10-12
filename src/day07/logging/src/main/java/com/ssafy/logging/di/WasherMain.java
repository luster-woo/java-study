package com.ssafy.logging.di;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/*
 * TODO: 03 빈 사용 + 빈 조회
 *
 * [실행 결과]
 * SWasher 생성
 * LWasher 생성
 * --- 1. 빈 사용 ---
 * SWasher로 세탁: 옷
 * 같은 객체? true
 * --- 2. 이름으로 조회 ---
 * myWasherUser == user ? true
 * --- 3. 같은 타입이 여럿일 때 ---
 * 실패 : NoUniqueBeanDefinitionException
 * 타입 + 이름 조회 : true
 */
public class WasherMain {
    public static void main(String[] args) {
        // 설정 정보(WasherConfig)로 스프링 컨테이너 생성 → 이 순간 빈들이 만들어짐
        ApplicationContext ctx = new AnnotationConfigApplicationContext(WasherConfig.class);

        System.out.println("--- 1. 빈 사용 ---");
        Washer sWasher = ctx.getBean(SWasher.class);        // 타입으로 조회
        WasherUser user = ctx.getBean(WasherUser.class);
        user.useWasher("옷");
        System.out.println("같은 객체? " + (sWasher == user.getWasher()));

        System.out.println("--- 2. 이름으로 조회 ---");
        WasherUser byName = ctx.getBean("myWasherUser", WasherUser.class);
        System.out.println("myWasherUser == user ? " + (byName == user));

        System.out.println("--- 3. 같은 타입이 여럿일 때 ---");
        try {
            ctx.getBean(Washer.class);                      // sWasher, lWasher 둘 다 Washer
        } catch (Exception e) {
            System.out.println("실패 : " + e.getClass().getSimpleName());
        }
        Washer myWasher = ctx.getBean("sWasher", Washer.class);  // 타입 + 이름
        System.out.println("타입 + 이름 조회 : " + (sWasher == myWasher));
    }
}
