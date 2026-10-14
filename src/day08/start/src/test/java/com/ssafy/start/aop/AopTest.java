package com.ssafy.start.aop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AopTest {

    @Autowired
    Person person;   // Programmer 대신 프록시가 주입됨

    @Test
    @DisplayName("주입된 Person은 CGLIB 프록시")
    void proxyTest() {
        System.out.println("class = " + person.getClass().getName());   // Programmer$$SpringCGLIB$$0
        assertTrue(person.getClass().getName().contains("CGLIB"));
    }

    @Test
    @DisplayName("5일 동안 출근 - Random이라 Push/조퇴가 갈림")
    void codingTest() {
        for (int day = 1; day <= 5; day++) {
            System.out.println("===== " + day + "일차 =====");
            try {
                person.coding();
            } catch (OuchException e) {
                // MyAspect는 예외를 삼키지 않음 → 호출한 쪽까지 예외가 전파됨
                System.out.println("(테스트) OuchException 받음");
            }
        }
    }
}
