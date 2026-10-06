# APPENDIX_부적절한 예외 처리

> 교재: 15기_자바_live_09_exception_0126_오후 (전공)

> [← 사용자 정의 예외](04_사용자_정의_예외.md) · [day06 목록](../README.md) · [노드스트림 →](06_노드스트림.md)

### 예외를 잡고 모르쇠

```java
public void method(String[] args, String name, int divisor) {
    try {
        System.out.println(args[0]);
        System.out.println(name.length());
        System.out.println(1 / divisor);
    } catch (Exception e) {
        // 쉿!!!
    }
}
```

- 예외를 잡은 뒤 아무것도 안 하면 정확한 진단을 내릴 수 없음
- 많은 경우 **RuntimeException 은 처리 대상이기보다 디버깅 대상**
- 무조건 예외로 처리하는 것도 무책임 → 상태 확인 코드로 막기

```java
public void method2(String[] args, String name, int divisor) {
    if (args != null && args.length > 0) {
        System.out.println(args[0]);
    }
    if (name != null) {
        System.out.println(name.length());
    }
    if (divisor != 0) {
        System.out.println(1 / divisor);
    }
}
```

> 참고: 교재 코드는 `args.length > 1` 인데, `args[0]` 만 쓰므로 `args.length > 0` 이어야 인자가 1개일 때도 출력된다.

### 예외로 흐름 제어

- 예외는 **예외적인 상황에서만** 사용, 흐름 제어 용도로 쓰면 안 됨
- 상태 확인 코드로 흐름을 제어하는 것이 상식

```java
// 나쁜 예: 배열 끝을 예외로 감지
int sum = 0;
try {
    for (int i = 0; ; i++) {
        sum += nums[i];
    }
} catch (ArrayIndexOutOfBoundsException e) {
    e.printStackTrace();
}

// 좋은 예: 길이로 감지
int sum2 = 0;
for (int i = 0; i < nums.length; i++) {
    sum2 += nums[i];
}
```

### 상태 종속적 코드

- `scanner.nextInt()` 같은 상태 종속적 코드는 상태 확인 메서드 `scanner.hasNextInt()` 와 함께 사용

```java
// 나쁜 예: 숫자가 아닌 입력이 오면 InputMismatchException 으로 끝남
while (true) {
    int input = scanner.nextInt();
    if (input == 0) break;
    sum += input;
}

// 좋은 예: 먼저 확인
Scanner scanner = new Scanner("10 20 abc 30");
int sum = 0;
while (scanner.hasNextInt()) {
    int input = scanner.nextInt();
    if (input == 0) break;
    sum += input;
}
System.out.printf("good case sum: %d%n", sum);   // good case sum: 30
```
