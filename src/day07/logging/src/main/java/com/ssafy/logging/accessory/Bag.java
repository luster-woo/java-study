package com.ssafy.logging.accessory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/*
 * TODO: 05 빈 생성 및 설정 / TODO: 07 @Qualifier
 *
 *  Bag ──has-a──> Hat, Watch, SmartWatch
 *
 *  Watch 타입 빈 후보 = watch, smartWatch (2개!)
 *   → @Qualifier 없으면 NoUniqueBeanDefinitionException
 *   → @Qualifier("watch") 로 이름을 콕 집어 해결
 */
@Component
public class Bag {
    private final Hat hat;          // 생성자 주입 → final 가능 (불변)
    private final Watch watch;
    private SmartWatch smartWatch;  // setter 주입 → final 불가

    @Autowired   // 생성자가 1개라 생략 가능
    public Bag(Hat hat, @Qualifier("watch") Watch watch) {   // @Qualifier는 생성자 X, 파라미터 O
        this.hat = hat;
        this.watch = watch;
    }

    @Autowired   // setter 주입 - SmartWatch 타입 빈은 smartWatch 하나뿐이라 충돌 없음
    public void setSmartWatch(SmartWatch smartWatch) {
        this.smartWatch = smartWatch;
    }

    public Hat getHat() { return hat; }
    public Watch getWatch() { return watch; }
    public SmartWatch getSmartWatch() { return smartWatch; }
}
