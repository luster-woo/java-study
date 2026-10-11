package day04.e_generic;

import java.util.ArrayList;
import java.util.List;

/*
 * [실행 결과]
 * --- 1. Object 박스의 문제 ---
 * 형변환 필요 : 사과
 * 에러 : ClassCastException
 * --- 2. 제네릭 박스 ---
 * 사과 (길이 2)
 * 100 + 1 = 101
 * --- 3. 한정된 타입 ---
 * 합계 : 6.0
 * 합계 : 4.0
 * --- 4. 제네릭 메서드 ---
 * 첫 번째 : 가
 * 첫 번째 : 10
 * --- 5. 와일드카드 ---
 * [Person, SpiderMan]
 * [SpiderMan, VenomSpider]
 * [SpiderMan, VenomSpider]
 * 소비 후 : [Person, SpiderMan]
 * 소비 후 : [SpiderMan]
 */
public class GenericTest {

    // ===== 4. 제네릭 메서드 : 메서드 단위로 타입 파라미터 선언 (리턴 타입 앞에 <T>) =====
    static <T> T first(List<T> list) {
        return list.get(0);
    }

    // ===== 5. 와일드카드 ? : "어떤 타입인지 몰라도 / 범위만 정해서" 받기 =====

    // <?> : 아무 타입이나 → 읽기만 (Object로)
    static void printAll(List<?> list) {
        System.out.println(list);
    }

    // <? extends SpiderMan> : SpiderMan과 그 "자식"만  → 꺼내서 SpiderMan으로 읽기 OK (Producer)
    static void readSpiders(List<? extends SpiderMan> list) {
        for (SpiderMan s : list) {        // 무엇이 와도 최소 SpiderMan이니까 안전
            s.name();
        }
        System.out.println(list);
        // list.add(new SpiderMan());     // ❌ 실제로 List<VenomSpider>일 수도 있어서 추가 불가
    }

    // <? super SpiderMan> : SpiderMan과 그 "조상"만 → SpiderMan(자식 포함) 넣기 OK (Consumer)
    static void addSpider(List<? super SpiderMan> list) {
        list.add(new SpiderMan());        // 리스트가 Person이든 Object든 SpiderMan은 들어감
        System.out.println("소비 후 : " + list);
        // SpiderMan s = list.get(0);     // ❌ 꺼내면 뭔지 모름 (Object로만 받을 수 있음)
    }

    public static void main(String[] args) {
        System.out.println("--- 1. Object 박스의 문제 ---");
        ObjectBox ob = new ObjectBox();
        ob.setItem("사과");
        String s1 = (String) ob.getItem();           // 꺼낼 때마다 형변환
        System.out.println("형변환 필요 : " + s1);
        ob.setItem(123);                             // 숫자를 넣어도 컴파일러는 모름
        try {
            String s2 = (String) ob.getItem();       // 실행 중에야 터짐
        } catch (ClassCastException e) {
            System.out.println("에러 : ClassCastException");
        }

        System.out.println("--- 2. 제네릭 박스 ---");
        Box<String> strBox = new Box<>();            // 오른쪽 <> 는 생략 가능 (다이아몬드)
        strBox.setItem("사과");
        // strBox.setItem(123);                      // ❌ 컴파일 에러 → 실행 전에 잡아줌
        String s3 = strBox.getItem();                // 형변환 불필요
        System.out.println(s3 + " (길이 " + s3.length() + ")");

        Box<Integer> intBox = new Box<>();
        // Box<int> b;                               // ❌ 주의 3 : 기본형 불가 → 래퍼 클래스(Integer) 사용
        intBox.setItem(100);
        System.out.println("100 + 1 = " + (intBox.getItem() + 1));

        System.out.println("--- 3. 한정된 타입 ---");
        NumberBox<Integer> nb1 = new NumberBox<>(List.of(1, 2, 3));
        NumberBox<Double> nb2 = new NumberBox<>(List.of(1.5, 2.5));
        // NumberBox<String> nb3;                    // ❌ String은 Number의 자식이 아님
        System.out.println("합계 : " + nb1.sum());
        System.out.println("합계 : " + nb2.sum());

        System.out.println("--- 4. 제네릭 메서드 ---");
        String f1 = first(List.of("가", "나"));      // T = String 으로 자동 추론
        Integer f2 = first(List.of(10, 20));        // T = Integer
        System.out.println("첫 번째 : " + f1);
        System.out.println("첫 번째 : " + f2);

        System.out.println("--- 5. 와일드카드 ---");
        List<Person> persons = new ArrayList<>(List.of(new Person()));
        List<SpiderMan> spiders = new ArrayList<>(List.of(new SpiderMan(), new VenomSpider()));
        List<VenomSpider> venoms = new ArrayList<>(List.of(new VenomSpider()));

        printAll(List.of(new Person(), new SpiderMan()));   // ? : 아무거나

        readSpiders(spiders);                       // ⭕ SpiderMan
        readSpiders(new ArrayList<>(List.of(new SpiderMan(), new VenomSpider())));
        // readSpiders(persons);                    // ❌ Person은 SpiderMan의 조상 → extends 범위 밖

        addSpider(persons);                         // ⭕ Person은 SpiderMan의 조상
        addSpider(new ArrayList<SpiderMan>());      // ⭕ SpiderMan 자기 자신
        // addSpider(venoms);                       // ❌ VenomSpider는 자식 → super 범위 밖

        // 참고 : List<SpiderMan>은 List<Person>의 자식이 아니다!
        // List<Person> p = spiders;                // ❌ 그래서 와일드카드가 필요
    }
}
