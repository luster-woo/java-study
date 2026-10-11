package day04.e_generic;

/*
 * ① 제네릭이 없던 시절 : Object로 받으면 뭐든 담을 수 있지만...
 *    - 꺼낼 때마다 형변환 필요
 *    - 잘못 넣어도 컴파일러가 모름 → 실행 중 ClassCastException
 */
class ObjectBox {
    private Object item;

    void setItem(Object item) { this.item = item; }
    Object getItem()          { return item; }
}

/*
 * ② 제네릭 클래스 : 타입을 "나중에" 정한다
 *    - <T> : 타입 파라미터 (T = Type, 이름은 자유. 관례 : T, E(Element), K/V(Key/Value))
 *    - new Box<String>() 하는 순간 T가 String으로 대체됨
 *    - 컴파일 시점에 타입 체크 → 잘못 넣으면 바로 컴파일 에러, 꺼낼 때 형변환 불필요
 */
public class Box<T> {
    private T item;

    // static T shared;          // ❌ 주의 1 : T는 객체를 만들 때 정해짐 → static에선 사용 불가

    void setItem(T item) { this.item = item; }
    T getItem()          { return item; }

    // T[] arr = new T[10];      // ❌ 주의 2 : 제네릭 타입의 배열은 생성 불가
}
