# record

> 출처: [Record Classes - Java SE 17 Language Updates](https://docs.oracle.com/en/java/javase/17/language/records.html) · [Jakarta Persistence 3.1 Specification](https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1.html)

> [← StringBuffer_StringBuilder](12_StringBuffer_StringBuilder.md) · [day06 목록](../README.md)

## Record 클래스 (Java 16+)

### 무엇인가
- **단순한 데이터 묶음**을 일반 클래스보다 훨씬 적은 코드로 만드는 특별한 클래스
- 헤더에 "무엇을 담는지"만 적으면 생성자·접근자·`equals`·`hashCode`·`toString`이 **자동 생성**
- 필드는 모두 **final** — 값을 나르는 **데이터 운반체(data carrier)**가 목적
- Java 16에서 정식 기능(JEP 395) → Java 17에서 그대로 사용

### 한 줄 선언 = 이만큼의 클래스

```java
record Rectangle(double length, double width) { }
```

```java
// 위 record 와 같은 의미의 일반 클래스
public final class Rectangle {
    private final double length;
    private final double width;

    public Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    public double length() { return this.length; }   // getLength() 가 아님
    public double width()  { return this.width; }

    // equals / hashCode : 같은 타입이고 모든 필드 값이 같으면 같은 객체
    // toString          : 필드 이름과 값을 모두 포함 -> Rectangle[length=4.0, width=5.0]
}
```

### 자동으로 생기는 멤버

| 헤더의 구성요소(component)마다 | 클래스 전체 |
|---|---|
| 같은 이름·타입의 `private final` 필드 | 헤더와 시그니처가 같은 **정식(canonical) 생성자** |
| 같은 이름의 `public` 접근자 메서드 (`length()`) | 모든 값을 비교하는 `equals()` / `hashCode()` |
| | 이름과 값을 모두 보여 주는 `toString()` |

- 생성은 보통 클래스처럼 `new Rectangle(4, 5)`
- 접근자 이름이 `getXxx()`가 아니라 **필드 이름 그대로** (`r.length()`)

## 생성자 — 검증 넣기

### 정식 생성자를 직접 쓰기
- 헤더와 같은 시그니처로 선언하고 필드 대입까지 직접 작성

### 컴팩트 생성자(compact constructor)
- 매개변수 목록을 생략한 형태 — **record에서만** 가능
- 검증 코드만 쓰면 되고, 끝에서 **필드 대입이 자동**으로 이뤄짐 (`this.length = length;` 안 씀)

```java
record Rectangle(double length, double width) {
    public Rectangle {                                   // 컴팩트 생성자
        if (length <= 0 || width <= 0) {
            throw new IllegalArgumentException(
                String.format("Invalid dimensions: %f, %f", length, width));
        }
    }
}
```

## record에 넣을 수 있는 것 / 없는 것

| 가능 | 불가능 |
|---|---|
| static 필드, static 초기화 블록, static 메서드 | **인스턴스 필드**(헤더 밖의 non-static 필드) |
| 인스턴스 메서드 | 인스턴스 초기화 블록 |
| 접근자·`equals`·`hashCode`·`toString` 직접 재정의 | `native` 메서드 |
| 중첩 클래스·인터페이스·중첩 record(암묵적 static) | **상속**(`extends`) — record는 암묵적 final |
| 제네릭 `record Pair<A, B>(A a, B b)`, `implements` 인터페이스, 어노테이션 | |

- 접근자를 직접 쓰면 자동 생성본과 **같은 특성**(public, 같은 반환 타입)이어야 함
- 모든 record의 공통 부모는 `java.lang.Record` — 그래서 따로 `extends` 할 수 없음
- **로컬 record**: 메서드 안에 선언 가능(스트림 중간 결과를 이름 붙여 묶을 때 유용), 암묵적 static이라 바깥 메서드의 지역 변수에 접근 불가

```java
import java.util.List;

public class RecordTest {
    record Point(int x, int y) {
        static Point origin() { return new Point(0, 0); }   // static 메서드 OK
        double distance() { return Math.sqrt(x * x + y * y); } // 인스턴스 메서드 OK
    }

    public static void main(String[] args) {
        Point p = new Point(3, 4);
        System.out.println(p);                          // Point[x=3, y=4]
        System.out.println(p.x() + ", " + p.y());       // 3, 4
        System.out.println(p.distance());               // 5.0
        System.out.println(p.equals(new Point(3, 4)));  // true  (값이 같으면 같음)
        System.out.println(p == new Point(3, 4));       // false (다른 인스턴스)
        System.out.println(Point.origin());             // Point[x=0, y=0]
        // p.x = 10;                                    // 컴파일 에러: final 필드
    }
}
```

## DTO를 record로 만드는 이유

### DTO와 record의 궁합
- DTO(Data Transfer Object) = 계층 사이·요청/응답으로 **값만 나르는 객체** → record의 목적(데이터 운반체)과 그대로 겹침
- 이유 정리

| DTO에 필요한 것 | record가 주는 것 |
|---|---|
| 필드 + 생성자 + getter 반복 코드 제거 | 헤더 한 줄로 자동 생성 |
| 만든 뒤 값이 바뀌지 않아야 안전 | 모든 필드 `final`, setter 없음 |
| 테스트에서 값 비교, 로그 출력 | 값 기반 `equals`/`hashCode`, 필드가 다 나오는 `toString` |
| 들어온 값 검증 | 컴팩트 생성자에서 한 번에 |

```java
// 요청 DTO
public record MemberCreateRequest(String name, int age) {
    public MemberCreateRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수");
        }
    }
}

// 응답 DTO - 엔티티에서 필요한 값만 꺼내 담음
public record MemberResponse(Long id, String name) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getName());
    }
}
```

```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public MemberResponse create(@RequestBody MemberCreateRequest request) {
        return MemberResponse.from(memberService.join(request.name(), request.age()));
    }
}
```

### 엔티티는 record로 만들지 않음
- Jakarta Persistence 명세의 엔티티 조건과 record의 성질이 충돌
  - 엔티티 클래스는 **final이면 안 됨** ↔ record는 암묵적 final
  - 엔티티의 메서드·영속 인스턴스 변수도 **final이면 안 됨** ↔ record의 필드는 모두 final
  - (그 밖에 엔티티는 public/protected 기본 생성자도 요구함)
- 그래서 **엔티티는 일반 클래스, 요청/응답 DTO는 record**로 나누는 구성이 자연스러움

---

## 참고

- 이 환경(JDK 11)에서는 record를 컴파일할 수 없어 위 record 예제는 **실행하지 못함**(Java 16+ 문법만 확인). `toString` 형식 `Point[x=3, y=4]`는 공식 문서 설명("이름과 값을 모두 포함")에 맞춘 것
- Spring 코드(`@RestController`, `@RequestBody`)는 실행하지 않고 어노테이션·import만 확인함. `Member`, `MemberService.join(...)`은 설명용 가정
- 다른 패키지에 `Record`라는 클래스가 있고 `import 패키지.*`로 가져오면 `java.lang.Record`와 이름이 겹쳐 컴파일 에러 → 그 클래스를 정확한 이름으로 import

## 면접 포인트

- **record란?** 값만 담는 클래스를 헤더 한 줄로 선언하는 Java 16+ 기능. final 필드, 정식 생성자, 접근자, `equals`/`hashCode`/`toString`이 자동 생성됨.
- **DTO를 record로 만드는 이유?** DTO는 값을 나르기만 하므로 불변·값 비교·보일러플레이트 제거라는 record의 성질이 그대로 맞음. 검증은 컴팩트 생성자에 둠.
- **JPA 엔티티를 record로 만들 수 있나?** 안 됨. 명세상 엔티티 클래스와 영속 필드는 final이면 안 되는데 record는 클래스도 필드도 final임.
- **record의 제약은?** 상속(`extends`) 불가, 헤더 밖에 인스턴스 필드 추가 불가. static 멤버와 인스턴스 메서드는 넣을 수 있음.
