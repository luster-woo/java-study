package day06.d_custom;

/*
 * 사용자 정의 예외 = 우리 프로그램에 맞는 예외를 직접 만들기
 *
 *  상속 대상          | 종류       | 특징
 *  -------------------|-----------|------------------------------------
 *  Exception          | checked   | 처리 강제 → 누락 위험 ↓, 코드는 길어짐
 *  RuntimeException   | unchecked | 처리 자유 → 코드 간결 (Spring에서는 주로 이쪽)
 *
 *  - 생성자에서 super(메시지) → getMessage()로 꺼낼 수 있음
 *  - 예외도 클래스라서 필드(추가 정보)를 가질 수 있다
 *  - boolean 리턴보다 좋은 점 : 호출할 때마다 if로 결과 확인할 필요 X
 *
 * [실행 결과]
 * --- 1. boolean 리턴 방식 ---
 * 출금 실패... 근데 왜? (잔액 부족? 음수?)
 * 출금 실패... 근데 왜?
 * --- 2. 사용자 정의 예외 (checked) ---
 * 윤우 출금 3000 → 잔액 7000
 * 실패 : 잔액 부족 (잔액 7000원, 요청 50000원)
 * 부족 금액 : 43000원
 * --- 3. 사용자 정의 예외 (unchecked) ---
 * 윤우 입금 5000 → 잔액 12000
 * 실패 : 금액은 0보다 커야 함 : -100
 * --- 4. 한 번에 여러 개 잡기 ---
 * 윤우 출금 2000 → 잔액 10000
 * [InvalidAmountException] 금액은 0보다 커야 함 : -500
 * [InsufficientBalanceException] 잔액 부족 (잔액 10000원, 요청 99999원)
 */

// 잔액 부족 (checked) - 부족한 금액을 필드로 들고 다님
class InsufficientBalanceException extends Exception {
    private final int shortage;

    InsufficientBalanceException(int balance, int amount) {
        super("잔액 부족 (잔액 " + balance + "원, 요청 " + amount + "원)");
        this.shortage = amount - balance;
    }

    int getShortage() { return shortage; }
}

// 잘못된 금액 (unchecked)
class InvalidAmountException extends RuntimeException {
    InvalidAmountException(int amount) {
        super("금액은 0보다 커야 함 : " + amount);
    }
}

class Account {
    private final String owner;
    private int balance;

    Account(String owner, int balance) {
        this.owner = owner;
        this.balance = balance;
    }

    // ===== 예외 없이 : boolean 리턴 → 왜 실패했는지 모름 + 호출자가 매번 확인해야 함 =====
    boolean withdrawOld(int amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        return true;
    }

    // ===== 예외 사용 =====
    void deposit(int amount) {
        if (amount <= 0) throw new InvalidAmountException(amount);      // unchecked → throws 생략 가능
        balance += amount;
        System.out.println(owner + " 입금 " + amount + " → 잔액 " + balance);
    }

    void withdraw(int amount) throws InsufficientBalanceException {    // checked → throws 필수
        if (amount <= 0) throw new InvalidAmountException(amount);
        if (amount > balance) throw new InsufficientBalanceException(balance, amount);
        balance -= amount;
        System.out.println(owner + " 출금 " + amount + " → 잔액 " + balance);
    }
}

public class CustomExceptionTest {
    public static void main(String[] args) {
        Account acc = new Account("윤우", 10000);

        System.out.println("--- 1. boolean 리턴 방식 ---");
        if (!acc.withdrawOld(50000)) System.out.println("출금 실패... 근데 왜? (잔액 부족? 음수?)");
        if (!acc.withdrawOld(-100)) System.out.println("출금 실패... 근데 왜?");

        System.out.println("--- 2. 사용자 정의 예외 (checked) ---");
        try {
            acc.withdraw(3000);
            acc.withdraw(50000);
            acc.withdraw(1000);                          // 실행 X
        } catch (InsufficientBalanceException e) {
            System.out.println("실패 : " + e.getMessage());
            System.out.println("부족 금액 : " + e.getShortage() + "원");   // 예외 객체의 추가 정보 활용
        }

        System.out.println("--- 3. 사용자 정의 예외 (unchecked) ---");
        acc.deposit(5000);
        try {
            acc.deposit(-100);                           // try-catch 없어도 컴파일은 됨 (대신 안 잡으면 종료)
        } catch (InvalidAmountException e) {
            System.out.println("실패 : " + e.getMessage());
        }

        System.out.println("--- 4. 한 번에 여러 개 잡기 ---");
        int[] requests = {2000, -500, 99999};
        for (int req : requests) {
            try {
                acc.withdraw(req);
            } catch (InsufficientBalanceException | InvalidAmountException e) {
                System.out.println("[" + e.getClass().getSimpleName() + "] " + e.getMessage());
            }
        }
    }
}
