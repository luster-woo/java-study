package day03.f_getset;

/*
 * [실행 결과]
 * 홍길동, 20살, 관리자 : false
 * 잘못된 나이 : -5 → 저장 안 함
 * 홍길동, 20살, 관리자 : true
 */
public class MemberTest {
    public static void main(String[] args) {
        Member m = new Member();

        // m.name = "홍길동";     // ❌ private이라 직접 접근 불가
        // m.age = -5;            // ❌ 이게 막히는 게 핵심

        m.setName("홍길동");      // setter로 쓰기
        m.setAge(20);
        System.out.println(m.getName() + ", " + m.getAge() + "살, 관리자 : " + m.isAdmin());

        m.setAge(-5);             // 잘못된 값 → setter가 걸러냄
        m.setAdmin(true);
        System.out.println(m.getName() + ", " + m.getAge() + "살, 관리자 : " + m.isAdmin());
    }
}
