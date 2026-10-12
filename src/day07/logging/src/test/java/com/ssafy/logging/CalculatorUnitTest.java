package com.ssafy.logging;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class CalculatorUnitTest {
    private static final Logger log = LoggerFactory.getLogger(CalculatorUnitTest.class);
    private Calculator calc;

    // ===== life cycle =====
    @BeforeAll
    static void setupAll() {
        log.debug("@BeforeAll - 모든 테스트에서 공통으로 사용할 리소스 초기화");
    }

    @AfterAll
    static void tearDownAll() {
        log.debug("@AfterAll - 모든 테스트 종료 후 1번");
    }

    @BeforeEach
    void setup() {
        log.debug("@BeforeEach - 개별 테스트에서 필요한 리소스 초기화");
        calc = new Calculator();
    }

    @AfterEach
    void tearDown() {
        log.debug("@AfterEach - 개별 테스트 종료");
    }

    // ===== 기본 테스트 =====
    @Test
    @DisplayName("두 수를 더해서 나오는 합을 검증: ex) 1 + 2 = 3")
    void 두_수를_더해서_나오는_합을_검증() {
        int result = calc.add(10, 20);
        assertEquals(30, result);
    }

    // ===== standard assertion =====
    @Test
    @DisplayName("standard assertion: 하나가 실패하면 이후의 검증은 취소된다.")
    void standardAssertionTest() {
        assertNotNull(calc);
        assertEquals(2, calc.divide(10, 5));
        assertNotEquals(3, calc.divide(10, 5));
        assertTrue(calc.divide(10, 5) == 2);
    }

    // ===== equals vs same =====
    @Test
    @DisplayName("같은 객체를 참조하는가? 같은 내용인가")
    void assertEqualsSameTest() {
        String str1 = new String("Hello");
        String str2 = new String("Hello");
        assertEquals(str1, str2);    // 내용 같음
        assertNotSame(str1, str2);   // 다른 객체
    }

    // ===== 배열 =====
    @Test
    @DisplayName("배열의 내용이 같은가?")
    void assertArray() {
        char[] chars1 = "HelloJUnit".toCharArray();
        char[] chars2 = "HelloJUnit".toCharArray();
        assertNotEquals(chars1, chars2);    // 주소 비교 → 다름
        assertArrayEquals(chars1, chars2);  // 내용 비교 → 같음
    }

    // ===== 예외 =====
    @Test
    @DisplayName("예외 상황에 대한 assertion")
    void assertThrowsTest() {
        ArithmeticException e =
            assertThrows(ArithmeticException.class, () -> calc.divide(10, 0));
        assertEquals("/ by zero", e.getMessage());
    }

    // ===== grouped assertion (일부러 실패) =====
    @Test
    @DisplayName("grouped assertion: 하나가 실패하더라도 모든 검증을 실행한다.")
    void groupedAssertionTest() {
        assertAll(
            () -> assertEquals(100, calc.divide(10, 5)),   // 실패
            () -> assertNotEquals(2, calc.divide(10, 5)),  // 실패
            () -> assertNotNull(calc),                     // 통과
            () -> assertTrue(calc.divide(10, 5) == 2)      // 통과
        );
    }

    // ===== Given-When-Then =====
    @Test
    @DisplayName("Given-When-Then 패턴")
    void successNew() {
        // given
        int a = 10;
        int b = 5;
        // when
        int result = calc.divide(a, b);
        // then
        assertEquals(10 / 5, result);
    }
}
