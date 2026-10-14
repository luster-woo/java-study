package com.ssafy.start.aop;

// Target의 인터페이스 (Proxy 강의의 Person과 같음)
public interface Person {
    int coding();   // 작성한 코드 줄 수를 반환 → @AfterReturning에서 받음
}
