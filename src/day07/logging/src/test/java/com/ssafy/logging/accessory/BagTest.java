package com.ssafy.logging.accessory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

// TODO: 06 테스트 - 컨테이너를 직접 만들지 않고, 테스트 클래스에도 @Autowired로 주입받음
@ExtendWith(SpringExtension.class)                // JUnit + Spring 연결
@ContextConfiguration(classes = BagConfig.class)  // 사용할 설정 파일
class BagTest {

    @Autowired
    Bag bag;

    @Autowired
    ApplicationContext ctx;

    @Test
    @DisplayName("Bag 빈이 주입된다")
    void bagTest() {
        assertNotNull(bag);
    }

    @Test
    @DisplayName("생성자/setter 주입 모두 들어왔다")
    void injectionTest() {
        assertNotNull(bag.getHat());
        assertNotNull(bag.getWatch());
        assertNotNull(bag.getSmartWatch());
    }

    @Test
    @DisplayName("@Qualifier(\"watch\") → SmartWatch가 아니라 Watch가 들어간다")
    void qualifierTest() {
        assertEquals(Watch.class, bag.getWatch().getClass());
        assertNotSame(bag.getWatch(), bag.getSmartWatch());
    }

    @Test
    @DisplayName("기본 빈 이름은 클래스 이름의 첫 글자를 소문자로")
    void beanNameTest() {
        assertTrue(ctx.containsBean("hat"));
        assertTrue(ctx.containsBean("watch"));
        assertTrue(ctx.containsBean("smartWatch"));
        assertTrue(ctx.containsBean("bag"));
    }

    @Test
    @DisplayName("빈은 singleton - 컨테이너의 hat과 bag 안의 hat은 같은 객체")
    void singletonTest() {
        assertSame(ctx.getBean(Hat.class), bag.getHat());
    }
}
