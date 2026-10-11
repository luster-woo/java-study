package day03.e_modifier.b;

import day03.e_modifier.a.Parent;   // 다른 패키지라서 import 필요

// 다른 패키지(b) + Parent의 자식
public class Child extends Parent {
    public void test() {
        // 상속받은 내 것으로 접근 (this.pro)
        System.out.println("[다른패키지 자식] " + pub + ", " + pro);
        // System.out.println(def);     // ❌ default : 같은 패키지에서만
        // System.out.println(pri);     // ❌ private : 같은 클래스에서만
        System.out.println("[다른패키지 자식] getter로 private 읽기 : " + getPri());
    }
}
