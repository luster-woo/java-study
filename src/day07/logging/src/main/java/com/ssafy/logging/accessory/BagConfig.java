package com.ssafy.logging.accessory;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

// @Bean 메서드가 하나도 없음! → 이 패키지에서 @Component 붙은 클래스를 찾아서 빈으로 등록
@Configuration
@ComponentScan("com.ssafy.logging.accessory")   // 생략하면 이 클래스의 패키지(+하위)를 scan
public class BagConfig {
}
