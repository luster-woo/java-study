package day03.f_getset;

/*
 * getter / setter = private 필드를 밖에서 "안전하게" 읽고 쓰는 public 메서드
 *  - getter : 값 읽기  → get + 필드명(첫 글자 대문자)   (boolean은 is + 필드명)
 *  - setter : 값 쓰기  → set + 필드명
 *  - setter 안에서 잘못된 값을 걸러낼 수 있다 → 이게 핵심 (캡슐화)
 */
public class Member {
    private String name;      // private → 밖에서 m.name 으로 직접 접근 불가
    private int age;
    private boolean admin;

    // getter
    public String getName() {
        return name;
    }

    // setter
    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    // setter에서 검사 : 잘못된 값은 저장 안 함
    public void setAge(int age) {
        if (age < 0 || age > 150) {
            System.out.println("잘못된 나이 : " + age + " → 저장 안 함");
            return;
        }
        this.age = age;
    }

    // boolean 필드의 getter는 get 대신 is
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
