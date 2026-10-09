package day02.g_this;

/*
 * this 키워드 = "지금 이 메서드를 호출한 객체 자기 자신"
 *
 * 쓰임새 4가지
 *  1. this.변수   : 멤버 변수와 파라미터(지역 변수) 이름이 같을 때 구분
 *  2. this()      : 같은 클래스의 다른 생성자 호출 (생성자 첫 줄에서만)
 *  3. return this : 자기 자신을 돌려줘서 메서드 체이닝
 *  4. 메서드(this) : 자기 자신을 다른 메서드에 넘기기
 *
 * 주의 : static 메서드 안에서는 this 사용 불가 (어떤 객체인지 정해지지 않았으니까)
 */
public class Student {
    String name;
    int score;

    // ===== 2. this() : 생성자 호출 =====
    public Student() {
        this("이름없음");            // → Student(String) 호출
    }

    public Student(String name) {
        this(name, 0);              // → Student(String, int) 호출
    }

    // ===== 1. this.변수 : 이름이 같을 때 구분 =====
    public Student(String name, int score) {
        this.name = name;           // this.name = 멤버 변수 / name = 파라미터
        this.score = score;
        // name = name;             // ❌ 이렇게 쓰면 파라미터에 자기 자신을 넣는 것 → 멤버 변수는 null 그대로
    }

    // ===== 3. return this : 메서드 체이닝 =====
    public Student addScore(int point) {
        this.score += point;
        return this;                // 자기 자신(주소)을 돌려줌 → 바로 이어서 .addScore() 가능
    }

    // ===== 4. 자기 자신을 넘기기 =====
    public void register(School school) {
        school.enroll(this);        // "나(이 객체)를 등록해줘"
    }

    // this에 실제로 뭐가 들어있는지 확인용
    public void whoAmI() {
        System.out.println("this = " + this.hashCode() + " (" + name + ")");
    }

    public void info() {
        System.out.println(name + " : " + score + "점");
    }

    // static 메서드에서는 this 불가
    static void staticMethod() {
        // System.out.println(this.name);   // ❌ 컴파일 에러
    }
}
