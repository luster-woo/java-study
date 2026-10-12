package com.ssafy.logging.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * TODO: 02 메타 정보의 작성 - "이것들이 빈입니다" 를 명시
 *
 *  메서드          | 빈 타입     | 빈 이름
 *  ----------------|------------|--------------
 *  sWasher()       | SWasher    | sWasher
 *  lWasher()       | LWasher    | lWasher
 *  washerUser()    | WasherUser | myWasherUser (name으로 재정의)
 */
@Configuration                          // 이 클래스는 설정 정보
public class WasherConfig {

    @Bean                               // 메서드 이름 = 빈 이름
    public SWasher sWasher() {
        return new SWasher();
    }

    @Bean
    public LWasher lWasher() {
        return new LWasher();
    }

    @Bean(name = "myWasherUser")        // 빈 이름 재정의
    public WasherUser washerUser() {
        // return new WasherUser(sWasher());     // ① 생성자 주입
        WasherUser washerUser = new WasherUser();
        washerUser.setWasher(sWasher());          // ② setter 주입
        return washerUser;
        // sWasher()를 또 호출해도 새로 만들지 않고 컨테이너의 빈을 돌려줌 (singleton)
    }
}
