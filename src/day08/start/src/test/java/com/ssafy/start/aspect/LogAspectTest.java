package com.ssafy.start.aspect;

import com.ssafy.start.service.CalcService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class LogAspectTest {

    @Autowired
    CalcService calc;   // 실제로는 프록시가 주입됨

    @Test
    @DisplayName("주입된 빈은 CGLIB 프록시")
    void proxyTest() {
        log.info("class = {}", calc.getClass().getName());   // CalcService$$SpringCGLIB$$0
        Assertions.assertTrue(calc.getClass().getName().contains("CGLIB"));
    }

    @Test
    @DisplayName("정상 종료: Before → AfterReturning → After → Around")
    void successTest() {
        Assertions.assertEquals(30, calc.add(10, 20));
    }

    @Test
    @DisplayName("예외 발생: Before → AfterThrowing → After")
    void exceptionTest() {
        Assertions.assertThrows(ArithmeticException.class, () -> calc.divide(10, 0));
    }
}
