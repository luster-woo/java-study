package day03.e_modifier.a;

/*
 * 접근 제한자 4가지 (넓은 순)
 *
 *  제한자      | 같은 클래스 | 같은 패키지 | 다른 패키지의 자식 | 다른 패키지(전체)
 *  ------------|------------|------------|-------------------|-----------------
 *  public      |     O      |     O      |        O          |       O
 *  protected   |     O      |     O      |        O          |       X
 *  (default)   |     O      |     O      |        X          |       X
 *  private     |     O      |     X      |        X          |       X
 *
 *  (default) = 아무것도 안 붙인 것 = package-private
 */
public class Parent {
    public    String pub  = "public";
    protected String pro  = "protected";
              String def  = "default";
    private   String pri  = "private";

    // 같은 클래스 안 : 4개 전부 접근 가능
    public void showAll() {
        System.out.println("[같은 클래스]   " + pub + ", " + pro + ", " + def + ", " + pri);
    }

    // private 필드는 밖에서 못 보니까, 보여주고 싶으면 public 메서드로 열어준다 (getter)
    public String getPri() {
        return pri;
    }
}
