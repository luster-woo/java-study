package day03.h_polymorphism;

/*
 * 다형성 활용 4가지
 *  1. 다형적 배열     : 부모 타입 배열 하나에 여러 자식을 섞어 담기
 *  2. 매개변수 다형성 : 메서드 파라미터를 부모 타입으로 → 어떤 자식이든 받음
 *  3. 리턴 타입 다형성 : 부모 타입으로 돌려줌 → 상황에 따라 다른 자식 객체
 *  4. instanceof + 형변환 : 자식만의 기능이 필요할 때 꺼내 쓰기
 *
 * [실행 결과]
 * --- 1. 다형적 배열 ---
 * 카드로 10000원 결제 (수수료 300원)
 * 카카오페이로 10000원 결제 (포인트 100원 적립)
 * 현금으로 10000원 결제
 * --- 2. 매개변수 다형성 ---
 * [주문] 커피
 * 카드로 4500원 결제 (수수료 135원)
 * [주문] 케이크
 * 현금으로 6000원 결제
 * --- 3. 리턴 타입 다형성 ---
 * 카카오페이로 3000원 결제 (포인트 30원 적립)
 * --- 4. instanceof ---
 * 카드 할부 3개월 설정
 * 카카오페이는 할부 없음
 * 현금은 할부 없음
 */

// 부모 : 결제 수단의 공통 모양
class Payment {
    void pay(int amount) {
        System.out.println(amount + "원 결제");
    }
}

// 자식들 : 각자 방식대로 재정의
class Card extends Payment {
    @Override
    void pay(int amount) {
        System.out.println("카드로 " + amount + "원 결제 (수수료 " + (amount * 3 / 100) + "원)");
    }

    void installment(int months) {       // 카드에만 있는 기능
        System.out.println("카드 할부 " + months + "개월 설정");
    }
}

class KakaoPay extends Payment {
    @Override
    void pay(int amount) {
        System.out.println("카카오페이로 " + amount + "원 결제 (포인트 " + (amount / 100) + "원 적립)");
    }
}

class Cash extends Payment {
    @Override
    void pay(int amount) {
        System.out.println("현금으로 " + amount + "원 결제");
    }
}

public class PolymorphismTest {

    // 2. 매개변수 다형성 : Payment면 뭐든 받는다
    //    → 결제 수단이 10개로 늘어도 이 메서드는 하나로 끝
    static void order(String menu, int price, Payment payment) {
        System.out.println("[주문] " + menu);
        payment.pay(price);
    }

    // 3. 리턴 타입 다형성 : 입력에 따라 다른 자식을 만들어서 Payment로 돌려줌
    static Payment choosePayment(String type) {
        switch (type) {
            case "card":  return new Card();
            case "kakao": return new KakaoPay();
            default:      return new Cash();
        }
    }

    public static void main(String[] args) {
        System.out.println("--- 1. 다형적 배열 ---");
        Payment[] payments = { new Card(), new KakaoPay(), new Cash() };
        for (Payment p : payments) {
            p.pay(10000);                 // 같은 코드인데 객체마다 다르게 동작
        }

        System.out.println("--- 2. 매개변수 다형성 ---");
        order("커피", 4500, new Card());
        order("케이크", 6000, new Cash());

        System.out.println("--- 3. 리턴 타입 다형성 ---");
        Payment p = choosePayment("kakao");   // 뭐가 나올지는 실행해봐야 앎
        p.pay(3000);

        System.out.println("--- 4. instanceof ---");
        for (Payment pay : payments) {
            // pay.installment(3);        // ❌ Payment 타입엔 installment()가 없음
            if (pay instanceof Card card) {   // Card가 맞으면 card 변수로 형변환까지 (Java 16~)
                card.installment(3);
            } else if (pay instanceof KakaoPay) {
                System.out.println("카카오페이는 할부 없음");
            } else {
                System.out.println("현금은 할부 없음");
            }
        }
    }
}
