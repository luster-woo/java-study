package day03.e_modifier.b;

import day03.e_modifier.a.Parent;

// 다른 패키지(b), 상속 관계 X
public class Other {
    public void test() {
        Parent p = new Parent();
        System.out.println("[다른 패키지]   " + p.pub);
        // System.out.println(p.pro);   // ❌ protected : 다른 패키지면 "자식"만
        // System.out.println(p.def);   // ❌ default
        // System.out.println(p.pri);   // ❌ private
    }
}
