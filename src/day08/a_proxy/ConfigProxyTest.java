package day08.a_proxy;

import java.util.HashMap;
import java.util.Map;

/*
 * TODO: @Configuration 프록시 - @Bean 메서드를 여러 번 불러도 싱글톤이 유지되는 이유
 *
 *  @Configuration
 *  public class AppConfig {
 *      @Bean public KeyBoard keyboard() { return new KeyBoard(); }
 *      @Bean public Developer developerA() { return new Developer(keyboard()); }
 *      @Bean public Developer developerB() { return new Developer(keyboard()); }
 *  }
 *
 *  스프링은 AppConfig를 상속한 프록시(AppConfig$$SpringCGLIB$$0)를 만들어서
 *  keyboard() 호출을 가로챔
 *   ├─ 컨테이너에 이미 있으면 → 등록된 빈 반환
 *   └─ 없으면 → super.keyboard() (진짜 new) → 등록 후 반환
 *
 *  AOP 프록시와 차이: 별도 target 객체 없이 super.method()로 부모 메서드를 직접 호출
 */
public class ConfigProxyTest {
    public static void main(String[] args) {
        System.out.println("--- 1. 프록시 없이 (proxyBeanMethods = false 와 같음) ---");
        AppConfig plain = new AppConfig();
        Developer a1 = plain.developerA();
        Developer b1 = plain.developerB();
        System.out.println("같은 키보드? " + (a1.keyBoard == b1.keyBoard));   // false

        System.out.println("--- 2. 프록시 적용 (@Configuration 기본) ---");
        AppConfig proxy = new AppConfigProxy();
        Developer a2 = proxy.developerA();
        Developer b2 = proxy.developerB();
        System.out.println("같은 키보드? " + (a2.keyBoard == b2.keyBoard));   // true
    }

    static class KeyBoard {
        KeyBoard() { System.out.println("KeyBoard 생성"); }
    }

    static class Developer {
        final KeyBoard keyBoard;
        Developer(KeyBoard keyBoard) { this.keyBoard = keyBoard; }
    }

    // 내가 작성한 설정 클래스
    static class AppConfig {
        public KeyBoard keyboard() { return new KeyBoard(); }
        public Developer developerA() { return new Developer(keyboard()); }
        public Developer developerB() { return new Developer(keyboard()); }
    }

    // 스프링이 CGLIB로 만드는 프록시를 흉내: AppConfig 상속 + 컨테이너(Map) 확인
    static class AppConfigProxy extends AppConfig {
        private final Map<String, Object> container = new HashMap<>();   // 스프링 컨테이너 역할

        @Override
        public KeyBoard keyboard() {
            if (!container.containsKey("keyboard")) {
                container.put("keyboard", super.keyboard());   // 처음 한 번만 진짜 생성
            }
            return (KeyBoard) container.get("keyboard");       // 이후엔 등록된 빈 반환
        }
    }
}
/*
 * [실행 결과]
 * --- 1. 프록시 없이 (proxyBeanMethods = false 와 같음) ---
 * KeyBoard 생성
 * KeyBoard 생성
 * 같은 키보드? false
 * --- 2. 프록시 적용 (@Configuration 기본) ---
 * KeyBoard 생성
 * 같은 키보드? true
 *
 * day07 WasherConfig에서 sWasher()를 두 번 불러도 같은 객체였던 이유가 이것
 */
