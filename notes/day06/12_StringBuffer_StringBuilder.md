# StringBuffer_StringBuilder

> 출처: [03-05 StringBuffer - 점프 투 자바](https://wikidocs.net/276) · [StringBuilder (Java SE 17)](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/StringBuilder.html)

> [← String](11_String.md) · [day06 목록](../README.md) · [record →](13_record.md)

## StringBuffer

### 문자열을 추가·변경할 때 쓰는 자료형
- **가변(mutable)** 문자열 — 만들어 둔 객체 하나의 내용을 계속 고침
- `append()`로 이어 붙이고, 다 만들면 `toString()`으로 String으로 바꿈

### String으로 같은 일을 하면
- 결과는 같아도 내부 동작이 다름
  - StringBuffer: 객체 **1개**를 만들어 계속 수정
  - String: `+=` 할 때마다 **새 String 객체** 생성 → 아래 예는 총 4개

```
String result = "";          // 1. ""
result += "hello";           // 2. "hello"
result += " ";               // 3. "hello "
result += "jump to java";    // 4. "hello jump to java"
```

| 용어 | 뜻 | 예 |
|---|---|---|
| 이뮤터블(immutable) | 값을 바꿀 수 없음 | String |
| 뮤터블(mutable) | 값을 바꿀 수 있음 | StringBuffer, StringBuilder |

### 언제나 StringBuffer가 좋은가 — 아님
- StringBuffer는 String보다 **무거움** — 생성 시 메모리를 더 쓰고 느림
- 추가·변경이 **많으면** StringBuffer(또는 StringBuilder), **적으면** String
- 대략의 기준: 반복문 안에서 문자열을 조작하거나 결합이 여러 번(10회 이상) 반복될 때

## 주요 메서드

| 메서드 | 하는 일 |
|---|---|
| `append(x)` | 끝에 추가 (자기 자신을 반환 → 연쇄 호출 가능) |
| `insert(index, str)` | 지정 위치에 삽입 |
| `delete(start, end)` | `start` ~ `end-1` 삭제 (**끝 위치 미포함**) |
| `substring(start, end)` | String의 substring과 같음 |
| `toString()` | String으로 변환 |
| `reverse()`, `setLength(0)` | 뒤집기, 비우기(재사용) |

```java
public class SbTest {
    public static void main(String[] args) throws InterruptedException {
        StringBuffer sb = new StringBuffer();
        sb.append("hello").append(" ").append("jump to java");
        System.out.println(sb.toString());            // hello jump to java

        StringBuffer ins = new StringBuffer("jump to java");
        ins.insert(0, "hello ");
        System.out.println(ins);                      // hello jump to java

        StringBuffer del = new StringBuffer("Hello jump to java");
        del.delete(6, 11);                            // 6~10 삭제, 11은 포함 안 함
        System.out.println(del);                      // Hello to java

        StringBuffer sub = new StringBuffer("Hello jump to java");
        System.out.println(sub.substring(0, 4));      // Hell

        StringBuilder b = new StringBuilder("abc");
        b.reverse();
        System.out.println(b);                        // cba
        b.setLength(0);                               // 비우고 재사용
        for (int i = 0; i < 5; i++) b.append(i);
        System.out.println(b);                        // 01234

        // 같은 객체를 수정 (String 과 달리 새 객체 아님)
        StringBuilder same = b.append("!");
        System.out.println(same == b);                // true

        // 여러 스레드가 같은 StringBuffer 에 append -> 길이 보장
        StringBuffer shared = new StringBuffer();
        Runnable job = () -> { for (int i = 0; i < 10000; i++) shared.append('x'); };
        Thread t1 = new Thread(job), t2 = new Thread(job);
        t1.start(); t2.start(); t1.join(); t2.join();
        System.out.println(shared.length());          // 20000
    }
}
```

## StringBuilder와의 차이

### 사용법은 같음
- StringBuffer와 **API가 호환** — 클래스 이름만 바꾸면 그대로 동작
- 차이는 **동기화(synchronization)** 여부

| | String | StringBuffer | StringBuilder |
|---|---|---|---|
| 가변성 | 불변 | 가변 | 가변 |
| 멀티 스레드 안전 | 안전(불변이라) | **안전**(동기화) | **안전하지 않음** |
| 속도 | 변경마다 새 객체 | 동기화 비용만큼 느림 | 대부분 구현에서 더 빠름 |
| 언제 | 변경이 적을 때 | 여러 스레드가 같은 객체를 수정 | 한 스레드 안에서 조립(대부분의 경우) |

### 공식 문서의 권장
- StringBuilder는 **한 스레드에서 쓰던 StringBuffer 자리를 대체**하려고 만든 클래스
  - 보통은 한 스레드에서 쓰므로 **가능하면 StringBuilder를 우선** 사용
- StringBuilder 인스턴스는 여러 스레드가 함께 쓰기에 **안전하지 않음** → 동기화가 필요하면 StringBuffer

---

## 참고

- 메서드 안에서 만들어 쓰고 버리는 빌더(지역 변수)는 다른 스레드와 공유되지 않으므로 StringBuilder로 충분함
- 위 예제의 마지막 부분처럼 StringBuffer는 두 스레드가 동시에 `append`해도 길이가 20000으로 맞음. 같은 코드를 StringBuilder로 바꾸면 결과가 보장되지 않음(실행마다 다를 수 있어 출력은 싣지 않음)

## 면접 포인트

- **String, StringBuffer, StringBuilder 차이?** String은 불변이라 변경마다 새 객체가 생기고, 둘은 가변이라 한 객체를 고침. StringBuffer는 동기화되어 멀티 스레드에 안전하고 StringBuilder는 동기화가 없어 더 빠름.
- **보통 어느 것을 쓰나?** 한 스레드 안에서 문자열을 조립하는 경우가 대부분이라 StringBuilder. 공식 문서도 가능하면 StringBuilder를 권장함.
- **반복문에서 `+=`를 피하는 이유?** 매번 새 String 객체가 만들어지기 때문. StringBuilder 하나에 `append`하고 마지막에 `toString()`.
