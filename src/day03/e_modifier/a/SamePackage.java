package day03.e_modifier.a;

// 같은 패키지(a), 상속 관계 X
public class SamePackage {
    public void test() {
        Parent p = new Parent();
        System.out.println("[같은 패키지]   " + p.pub + ", " + p.pro + ", " + p.def);
        // System.out.println(p.pri);   // ❌ private : 같은 클래스에서만
    }
}
