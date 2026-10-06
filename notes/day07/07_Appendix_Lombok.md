# Appendix_Lombok

> 교재: 15기_자바비전공_Framework(Back)_01_0504_Framework (비전공) — Appendix: Lombok

> [← 묵시적 DI](06_묵시적_DI.md) · [day07 목록](../README.md)

## 학습 목표

- **Lombok**을 사용해 반복 코드(Boilerplate code)를 효율적으로 줄일 수 있음

---

## Appendix: Lombok

### Lombok의 등장 — 보일러플레이트 코드

- **보일러플레이트(Boilerplate) 코드**: 거의 바뀌지 않으면서 여러 곳에 반복해서 써야 하는 코드
- 자바에서 DTO·VO 같은 객체를 만들 때마다 캡슐화를 위해 **Getter, Setter, 생성자, toString(), equals(), hashCode()** 를 기계적으로 작성해야 했음
- Lombok은 이 코드를 **애너테이션 하나로 대신 만들어 줌**

```java
// Lombok 적용 전 — 필드 4개인데 코드가 계속 길어짐
public class Member {
    private String name;
    private int age;
    private String email;
    private String phoneNumber;

    public Member(String name, int age, String email, String phoneNumber) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // ... getter, setter

    @Override
    public String toString() { ... }
    @Override
    public int hashCode() { ... }
    @Override
    public boolean equals(Object obj) { ... }
}
```

```java
// Lombok 적용 후
import lombok.Data;

@Data
public class Member {
    private String name;
    private int age;
    private String email;
    private String phoneNumber;
}
```

### Lombok 설치

- 공식 사이트: https://projectlombok.org/
- **둘 다 해야 함**
  - IDE 설치 → 에디터의 **빨간 줄**이 사라짐(IDE가 생성될 메서드를 인식)
  - Maven 의존성 → **빌드·실행** 시 실제로 코드가 생성됨

| 대상 | 방법 |
|---|---|
| IDE (STS5, 수동 설치) | ① 받은 `lombok.jar`를 STS5 설치 폴더에 둠 ② 메모장으로 `SpringToolsForEclipse.ini` 열기 ③ 맨 아래에 `-javaagent:lombok.jar` 한 줄 추가 |
| 빌드 (Maven) | `pom.xml`에 lombok 의존성 추가 (https://projectlombok.org/setup/maven) |

```xml
<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.44</version>
    <scope>compile</scope>
</dependency>
```

### 주요 애너테이션

- 만들어지는 코드는 소스에 보이지 않고, **컴파일할 때 `.class` 파일에 포함**됨
- 무분별하게 쓰면 의도치 않은 **접근(access) 문제**, **순환 참조 문제**(`@ToString`이 서로를 참조)가 생길 수 있음

| 애너테이션 | 하는 일 | 생성 결과 예 |
|---|---|---|
| `@Getter` / `@Setter` | getter/setter 생성 | `getName()`, `setName(String)` |
| `@ToString` | `toString()` 재정의 | `Member(mno=1, name=kim, ...)` |
| `@EqualsAndHashCode` | `equals()`, `hashCode()` 재정의 | 객체 비교 로직 |
| `@NonNull` | null 불가 필드 표시 + null 체크 코드 삽입 | null이 들어오면 `NullPointerException` |
| `@NoArgsConstructor` | 기본 생성자 | `public Member() {}` |
| `@AllArgsConstructor` | 모든 필드를 받는 생성자 | `public Member(int, String, ...)` |
| `@RequiredArgsConstructor` | `final`·`@NonNull` 필드만 받는 생성자 | 필수 필드만 받는 생성자 |
| `@Data` | 아래 5개를 한 번에 | `@Getter` + `@Setter` + `@ToString` + `@EqualsAndHashCode` + `@RequiredArgsConstructor` |
| `@Builder` | 빌더 패턴으로 객체 생성 | `Member.builder().name("kim").build()` |
| `@Slf4j` | Logger 변수 `log` 생성 | `private static final Logger log = ...` |

### @Slf4j — Logger 선언 줄이기

```java
// Lombok 없이
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleLogTest {
    private static final Logger log = LoggerFactory.getLogger(SimpleLogTest.class);

    public static void main(String[] args) {
        log.info("샘플 로그입니다.");
    }
}
```

```java
// Lombok 사용 — 위의 log 필드가 자동으로 생김
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleLogTest {
    public static void main(String[] args) {
        log.info("샘플 로그입니다.");
    }
}
```

---

## 참고

- `@Data`는 `@RequiredArgsConstructor`까지만 포함 → **기본 생성자·전체 생성자는 만들지 않음**. 필요하면 `@NoArgsConstructor`, `@AllArgsConstructor`를 따로 붙임
- `@ToString` 순환 참조: 서로를 필드로 가진 두 클래스가 모두 모든 필드를 출력하면 `toString()`이 끝없이 서로를 부름 → `StackOverflowError`. 한쪽을 `@ToString.Exclude`로 빼서 해결

```java
// @ToString이 만드는 것과 같은 모양을 직접 작성해 본 예
import java.util.ArrayList;
import java.util.List;

public class ToStringLoop {
    public static void main(String[] args) {
        Team team = new Team("A팀");
        Member kim = new Member("kim", team);
        team.members.add(kim);
        try {
            System.out.println(kim);
        } catch (StackOverflowError e) {
            System.out.println("StackOverflowError 발생");   // 결과: StackOverflowError 발생
        }
    }
}

class Team {
    String name;
    List<Member> members = new ArrayList<>();
    Team(String name) { this.name = name; }
    @Override
    public String toString() { return "Team(name=" + name + ", members=" + members + ")"; }
}

class Member {
    String name;
    Team team;
    Member(String name, Team team) { this.name = name; this.team = team; }
    @Override
    public String toString() { return "Member(name=" + name + ", team=" + team + ")"; }
}
```

- 교재는 `<scope>compile</scope>`이지만, Lombok은 컴파일할 때만 필요해서 공식 안내는 `provided` scope를 권장함(실행 jar에 포함되지 않음). Spring Boot 프로젝트는 보통 `<optional>true</optional>`로 넣음
- IntelliJ는 Lombok 플러그인이 기본 포함 → 위의 STS `.ini` 수정은 필요 없음. 대신 **Settings → Build → Compiler → Annotation Processors**에서 annotation processing이 켜져 있는지 확인
