package com.ssafy.start;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class StartApplicationTests {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${manager.id}")
    private String id;

    @Value("${manager.age}")
    private int age;                 // "30" -> 30 자동 변환

    @Value("${manager.roles}")
    private List<String> roles;      // "admin, manager, guest" -> List

    @Test
    void valueTest() {
        Assertions.assertEquals(30, age);
        Assertions.assertEquals(List.of("admin", "manager", "guest"), roles);
        log.debug("id: {}, age: {}, roles: {}", id, age, roles);
    }
}