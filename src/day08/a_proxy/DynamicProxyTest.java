package day08.a_proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/*
 * TODO (추가): JDK Dynamic Proxy - 프록시 클래스를 직접 안 만들고 "실행 중에" 자동 생성
 *
 *  ProxyTest의 PersonProxy는 손으로 만든 프록시 → 인터페이스마다 프록시 클래스를 또 만들어야 함
 *  Dynamic Proxy는 부가 기능(InvocationHandler) 하나로 어떤 인터페이스든 프록시를 만들어 줌
 *  → 스프링 AOP가 자동으로 프록시를 만드는 원리 (인터페이스 기반 방식)
 *
 *  | 방식              | 만드는 법             | 조건            |
 *  | CGLIB (Boot 기본)  | Target 클래스 상속     | 인터페이스 불필요 |
 *  | JDK Dynamic Proxy | Target 인터페이스 구현  | 인터페이스 필요   |
 */
public class DynamicProxyTest {
    public static void main(String[] args) {
        Person p = createProxy(new Programmer(), Person.class);
        p.coding();
        p.review("PR #12");

        System.out.println("--- 진짜 클래스 이름 ---");
        System.out.println(p.getClass().getName());   // $Proxy0 처럼 실행 중에 만들어진 클래스
    }

    interface Person {
        void coding();
        void review(String pr);
    }

    static class Programmer implements Person {
        public void coding() { System.out.println("[Programmer] 코딩한다."); }
        public void review(String pr) { System.out.println("[Programmer] " + pr + " 리뷰한다."); }
    }

    // 부가 기능을 한 곳에 정의 → 모든 메서드 호출이 여기로 들어옴 (= @Around Advice)
    @SuppressWarnings("unchecked")
    static <T> T createProxy(T target, Class<T> type) {
        InvocationHandler handler = (proxy, method, args) -> {
            System.out.println(">> " + method.getName() + " 시작");      // @Before
            long start = System.nanoTime();
            Object result = method.invoke(target, args);                 // Target에 위임 (= proceed())
            System.out.println("<< " + method.getName() + " 끝 ("
                    + (System.nanoTime() - start) / 1000 + "µs)");       // @After
            return result;
        };
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler);
    }
}
/*
 * [실행 결과] (시간은 실행마다 다름)
 * >> coding 시작
 * [Programmer] 코딩한다.
 * << coding 끝 (120µs)
 * >> review 시작
 * [Programmer] PR #12 리뷰한다.
 * << review 끝 (35µs)
 * --- 진짜 클래스 이름 ---
 * day08.a_proxy.$Proxy0
 *
 * 메서드가 늘어나도 프록시 코드는 그대로 → 손으로 만든 PersonProxy와의 차이
 */
