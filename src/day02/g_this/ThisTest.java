package day02.g_this;

/*
 * [실행 결과]
 * --- 1, 2. this.변수 / this() ---
 * 이름없음 : 0점
 * 홍길동 : 0점
 * 임꺽정 : 90점
 * --- 3. return this (체이닝) ---
 * 홍길동 : 30점
 * --- 4. this 넘기기 ---
 * 임꺽정 학생 등록 완료
 * --- this의 정체 ---
 * (s3의 hashCode 와 this의 hashCode 가 같음 → this == 호출한 객체)
 */
public class ThisTest {
    public static void main(String[] args) {
        System.out.println("--- 1, 2. this.변수 / this() ---");
        Student s1 = new Student();               // 기본 → 1개짜리 → 2개짜리 순서로 호출됨
        Student s2 = new Student("홍길동");
        Student s3 = new Student("임꺽정", 90);
        s1.info();
        s2.info();
        s3.info();

        System.out.println("--- 3. return this (체이닝) ---");
        s2.addScore(10).addScore(20).info();      // addScore가 s2를 돌려주니까 계속 이어 쓸 수 있음

        System.out.println("--- 4. this 넘기기 ---");
        s3.register(new School());

        System.out.println("--- this의 정체 ---");
        System.out.println("s3   = " + s3.hashCode());
        s3.whoAmI();                              // 같은 값 출력 → this는 s3 자신
    }
}
