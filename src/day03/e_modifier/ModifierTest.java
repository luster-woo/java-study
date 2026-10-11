package day03.e_modifier;

import day03.e_modifier.a.Parent;
import day03.e_modifier.a.SamePackage;
import day03.e_modifier.b.Child;
import day03.e_modifier.b.Other;

/*
 * [실행 결과]
 * [같은 클래스]   public, protected, default, private
 * [같은 패키지]   public, protected, default
 * [다른패키지 자식] public, protected
 * [다른패키지 자식] getter로 private 읽기 : private
 * [다른 패키지]   public
 * MAX = 100, PI = 3.14, local = 10
 * 브레이크는 바꿀 수 없음
 *
 * 위로 갈수록 볼 수 있는 게 많다 = 가까운 사이일수록 많이 공개
 */
public class ModifierTest {
    public static void main(String[] args) {
        new Parent().showAll();
        new SamePackage().test();
        new Child().test();
        new Other().test();

        new FinalTest().change();
        new SportsCar().brake();     // 오버라이딩은 못 해도 물려받아 쓰는 건 가능
    }
}
