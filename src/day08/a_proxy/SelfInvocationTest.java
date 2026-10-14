package day08.a_proxy;

/*
 * TODO: 내부 호출(Self-Invocation)은 프록시를 거치지 않는다
 *
 *  스프링 컨테이너가 주입해 주는 것 = proxy
 *  proxy.registerMember() → target.registerMember() → this.saveMemberData()
 *                                                       └ this는 target! → [TX] 없음
 *
 *  CGLIB 프록시 개념: Target 클래스를 "상속" + 실제 일은 target 객체에 "위임"
 */
public class SelfInvocationTest {
    public static void main(String[] args) {
        MemberServiceImpl target = new MemberServiceImpl();
        MemberServiceImpl proxy = new MemberServiceImplProxy(target);   // 컨테이너가 주입해 주는 것

        System.out.println("--- 1. 외부에서 직접 호출 ---");
        proxy.saveMemberData();          // 프록시를 거침 → [TX] 적용

        System.out.println("--- 2. 내부 호출 ---");
        proxy.registerMember();          // 안에서 this.saveMemberData() → [TX] 미적용

        System.out.println("--- 3. 진짜 클래스 이름 ---");
        System.out.println(proxy.getClass().getSimpleName());   // 스프링이라면 MemberServiceImpl$$SpringCGLIB$$0
    }

    // Target
    static class MemberServiceImpl {
        public void registerMember() {
            System.out.println("registerMember 핵심 로직");
            saveMemberData();            // = this.saveMemberData(), this는 target
        }

        public void saveMemberData() {   // @Transactional이 붙었다고 가정
            System.out.println("saveMemberData 핵심 로직");
        }
    }

    // Proxy (CGLIB가 만드는 것을 흉내): Target 상속 + target에 위임
    static class MemberServiceImplProxy extends MemberServiceImpl {
        private final MemberServiceImpl target;

        MemberServiceImplProxy(MemberServiceImpl target) { this.target = target; }

        @Override
        public void registerMember() {   // 부가 기능 없는 메서드: 위임만
            target.registerMember();
        }

        @Override
        public void saveMemberData() {   // 부가 기능(트랜잭션)이 걸린 메서드
            System.out.println("[TX] 시작");
            target.saveMemberData();
            System.out.println("[TX] 커밋");
        }
    }
}
/*
 * [실행 결과]
 * --- 1. 외부에서 직접 호출 ---
 * [TX] 시작
 * saveMemberData 핵심 로직
 * [TX] 커밋
 * --- 2. 내부 호출 ---
 * registerMember 핵심 로직
 * saveMemberData 핵심 로직      ← 내부 호출이라 [TX] 없음
 * --- 3. 진짜 클래스 이름 ---
 * MemberServiceImplProxy
 *
 * 해결: 트랜잭션이 필요한 메서드를 다른 빈(클래스)으로 분리해서 외부 호출로 만든다.
 * 참고: private 메서드는 상속으로 재정의할 수 없어서 CGLIB 프록시가 아예 못 가로챔 → public이어야 함
 */
