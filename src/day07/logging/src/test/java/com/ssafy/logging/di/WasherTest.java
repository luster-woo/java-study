package com.ssafy.logging.di;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

// TODO: 04 이제 main은 그만 - JUnit으로 검증
class WasherTest {
    ApplicationContext ctx;

    @BeforeEach
    void setUp() {
        ctx = new AnnotationConfigApplicationContext(WasherConfig.class);
    }

    @Test
    @DisplayName("WasherUser 빈을 꺼내서 사용할 수 있다")
    void washerUserTest() {
        WasherUser user = ctx.getBean(WasherUser.class);
        assertNotNull(user);
        assertNotNull(user.getWasher());        // 의존성이 주입됐는가
        user.useWasher("옷");
    }

    @Test
    @DisplayName("빈은 singleton - 주입된 세탁기와 컨테이너의 sWasher는 같은 객체")
    void washerTest() {
        Washer sWasher = ctx.getBean(SWasher.class);
        WasherUser user = ctx.getBean(WasherUser.class);
        assertSame(sWasher, user.getWasher());
    }

    @Test
    @DisplayName("빈 이름은 @Bean(name)으로 바꿀 수 있다")
    void beanNameTest() {
        assertTrue(ctx.containsBean("myWasherUser"));
        assertFalse(ctx.containsBean("washerUser"));   // 메서드 이름은 더 이상 빈 이름 아님
    }

    @Test
    @DisplayName("같은 타입 빈이 2개면 타입만으로는 조회 실패")
    void noUniqueBeanTest() {
        assertThrows(NoUniqueBeanDefinitionException.class, () -> ctx.getBean(Washer.class));
    }

    @Test
    @DisplayName("타입 + 이름으로 조회하면 성공")
    void typeAndNameTest() {
        Washer byType = ctx.getBean(SWasher.class);
        Washer byName = ctx.getBean("sWasher", Washer.class);
        assertSame(byType, byName);
    }
}
