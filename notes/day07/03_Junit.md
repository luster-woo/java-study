# Junit

> 교재: 15기_자바전공_01_framework_0504 (전공) — 3. Junit, Appendix: Maven

> [← SLF4j와 Logging](02_SLF4j와_Logging.md) · [day07 목록](../README.md) · [의존성과 DI →](04_의존성과_DI.md)

---

## 3. Junit

### JUnit (TODO: 02)

- **Java 코드의 단위 테스트 자동화를 위한 프레임워크** — https://junit.org/junit5/
- 테스트와 자동화가 필요한 이유

```java
public class Calculator {
    public int add(int a, int b) { return a + b; }
    public int divide(int a, int b) { return a / b; }
}
```

```java
public static void main(String[] args) {
    Calculator calculator = new Calculator();
    int result = calculator.add(10, 20);
    System.out.println(result);   // 30
}
```

- main에서 출력해 보는 방식의 한계
  - 성공/실패를 판단하는 근거가 **사람의 눈**("10, 20을 더했으니 30 맞네!")
  - 코드가 길어지고 시간이 지나면 결과를 파악하기 어려움 → 테스트를 **자동화**해야 함

### 의존성 추가

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>6.0.3</version>
    <scope>test</scope>   <!-- test 영역에서만 사용 -->
</dependency>
```

### 테스트 life cycle 관련 기본 annotation

| annotation | 설명 |
|---|---|
| `@Test` | 이 메서드가 단위 테스트를 수행하는 **테스트 메서드**임을 나타냄 |
| `@BeforeEach`, `@AfterEach` | **각각의** `@Test` 전/후에 공통으로 처리할 사전/사후 작업 |
| `@BeforeAll`, `@AfterAll` | 모든 `@Test`에 걸쳐 **한 번만** 필요한 사전/사후 작업, **static 메서드**에 작성 |
| `@DisplayName` | 테스트 내용을 나타내는 문자열 |

```
@BeforeAll (1번)
  ├─ @BeforeEach → @Test A → @AfterEach
  ├─ @BeforeEach → @Test B → @AfterEach
  └─ ...
@AfterAll (1번)
```

### 기본 테스트

- `src/test/java` 영역에 작성

```java
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalculatorUnitTest {
    private static final Logger log = LoggerFactory.getLogger(CalculatorUnitTest.class);
    private Calculator calc;

    @BeforeAll
    public static void setupAll() {
        log.debug("@BeforeAll - 모든 테스트에서 공통으로 사용할 리소스 초기화");
    }

    @BeforeEach
    public void setup() {
        log.debug("@BeforeEach - 개별 테스트에서 필요한 리소스 초기화");
        calc = new Calculator();
    }

    @Test
    @DisplayName("두 수를 더해서 나오는 합을 검증: ex) 1 + 2 = 3")
    public void 두_수를_더해서_나오는_합을_검증() {
        int result = calc.add(10, 20);
        Assertions.assertEquals(30, result);
    }
}
```

- 메서드 이름을 한글로 써도 됨 → 테스트 목적이 바로 보임
- 실행하면 성공/실패가 자동으로 판정됨 → "퇴근이냐, 야근이냐"를 사람이 아니라 테스트가 알려 줌

> 참고: `assertEquals(expected, actual)` 순서가 맞음. 교재는 `assertEquals(result, 30)`처럼 실제값을 앞에 썼는데, 통과 여부는 같지만 실패 메시지의 expected/actual이 뒤바뀌어 나옴. 노트에서는 순서를 바로잡음.

### assertion

- **단정문**: 메서드 호출 결과가 **예상한 값과 같은지** 판단하는 문장
- `org.junit.jupiter.api.Assertions`에 여러 assertion 메서드가 **static**으로 제공됨

#### standard assertion

- 대부분 **긍정/부정이 쌍**으로 존재
  - `assertEquals` : `assertNotEquals`, `assertNull` : `assertNotNull`, ...
- **하나가 실패하면 이후 검증은 실행되지 않음**

```java
@Test
@DisplayName("standard assertion: 하나가 실패하면 이후의 검증은 취소된다.")
public void standardAssertionTest() {
    Assertions.assertNotNull(calc);
    Assertions.assertEquals(2, calc.divide(10, 5));
    Assertions.assertNotEquals(3, calc.divide(10, 5));
    Assertions.assertTrue(calc.divide(10, 5) == 2);
}
```

#### equals vs same

| 메서드 | 검증 내용 |
|---|---|
| `assertEquals` | `equals()` — 내용이 같은가 |
| `assertSame` | `==` — 같은 객체를 참조하는가 |

```java
@Test
@DisplayName("같은 객체를 참조하는가? 같은 내용인가")
public void assertEqualsSameTest() {
    String str1 = new String("Hello");
    String str2 = new String("Hello");
    Assertions.assertEquals(str1, str2);    // 내용 같음 → 통과
    Assertions.assertNotSame(str1, str2);   // 다른 객체 → 통과
}
```

#### 배열의 내용 검증

- 배열은 `equals()`가 재정의되어 있지 않음 → 내용 비교용 메서드를 따로 제공

```java
@Test
@DisplayName("배열의 내용이 같은가?")
public void assertArray() {
    char[] chars1 = "HelloJUnit".toCharArray();
    char[] chars2 = "HelloJUnit".toCharArray();
    Assertions.assertNotEquals(chars1, chars2);    // equals()는 주소 비교 → 다름
    Assertions.assertArrayEquals(chars1, chars2);  // 내용 비교 → 같음
}
```

#### 예외에 대한 검증

- 특정 상황에서 **예상한 예외가 잘 발생하는지** 검증
- `assertThrows(예외클래스, 실행할 람다)` → 발생한 예외 객체를 돌려줌

```java
@Test
@DisplayName("예외 상황에 대한 assertion")
public void assertThrowsTest() {
    ArithmeticException e =
        Assertions.assertThrows(ArithmeticException.class, () -> calc.divide(10, 0));
    Assertions.assertEquals("/ by zero", e.getMessage());
}
```

#### grouped assertion

- 그룹으로 묶지 않으면 중간에 하나만 실패해도 전체가 바로 종료
- `assertAll()`로 묶으면 **하나가 실패해도 모든 검증을 실행**하고 실패를 모아서 보여 줌

```java
@Test
@DisplayName("grouped assertion: 하나가 실패하더라도 모든 검증을 실행한다.")
public void groupedAssertionTest() {
    Assertions.assertAll(
        () -> Assertions.assertEquals(100, calc.divide(10, 5)),  // 결과는 2인데 100? → 실패
        () -> Assertions.assertNotEquals(2, calc.divide(10, 5)), // 2가 맞는데 아니라고? → 실패
        () -> Assertions.assertNotNull(calc),                    // 앞에서 실패했지만 진행
        () -> Assertions.assertTrue(calc.divide(10, 5) == 2)
    );
}
```

### 기본적인 작성 방법 — Given-When-Then 패턴

- **BDD**(Behavior Driven Development)에서 권장하는 패턴

| 단계 | 의미 |
|---|---|
| given | 테스트에 필요한 상황 준비 (이런 상황·객체·데이터가 주어졌을 때) |
| when | 테스트하는 메서드 실행 (어떤 메서드를 실행했다면) |
| then | 결과 검증 (어떤 결과가 나와야 한다) |

```java
import static org.junit.jupiter.api.Assertions.assertEquals;

@Test
public void successOld() {
    Calculator calc = new Calculator();
    assertEquals(10 / 5, calc.divide(10, 5));
}

@Test
public void successNew() {
    // given
    Calculator calc = new Calculator();
    int a = 10;
    int b = 5;
    // when
    int result = calc.divide(a, b);
    // then
    assertEquals(10 / 5, result);
}
```

- 강제되는 패턴은 아니지만 **가급적** 따르는 것이 좋음

### F.I.R.S.T 원칙

| 원칙 | 의미 | 예 |
|---|---|---|
| **F**ast | 단위 테스트는 빠르게 동작해야 함 — 목적을 단순하게, 외부 환경과 무관하게 | `@SpringBootTest`는 편하지만 통합 테스트 → 지양, `@DataJpaTest` 같은 **슬라이스 테스트** 권장 |
| **I**ndependent | 이전 테스트가 남긴 상태에 의존하면 안 됨 — 어떤 순서로 돌려도 성공해야 함 | insert 테스트 뒤에 select 테스트가 와야 하는 구조는 안 됨 → select 테스트의 setup에서 insert, 테스트 후 delete |
| **R**epeatable | 여러 번 반복해도 똑같이 동작해야 함 | DB 관련 테스트는 **rollback** 과정이 반드시 필요 |
| **S**elf-Validating | 테스트 자체만으로 검증이 끝나야 함 | 결과를 개발자가 눈으로 확인하면 자동화가 아님 → **Assert 문** 작성 |
| **T**imely | TDD라면 테스트는 production code보다 먼저 작성 | 모두가 TDD를 하는 것은 아님 |

---

## APPENDIX: Maven

### Maven

- 아파치 재단에서 관리하는 **SW 프로젝트 관리 도구** — https://maven.apache.org/index.html
- **POM**(Project Object Model) 기반
  - 프로젝트 구성요소·빌드 설정·의존성 관리 등을 XML(**pom.xml**)에서 관리
  - 컴파일·테스트 실행 시 필요한 라이브러리와 플러그인 관리

| 주요 특징 | 효과 |
|---|---|
| 프로젝트 구조를 강제 | 다양한 개발 환경(OS, IDE)에서 같은 개발 환경 구축 |
| 중앙 집중형 라이브러리 저장소(Maven Central Repository)로 의존성 관리 | 프로젝트 설정 시간 감소, 의존성 관리 간소화 |
| 빌드 생명 주기 관리 | 표준화된 빌드 생명 주기 제공 |

### 프로젝트 구조 강제

| 폴더 | 설명 |
|---|---|
| `src/main/java` | 애플리케이션/라이브러리 소스 |
| `src/main/resources` | 애플리케이션/라이브러리 리소스 (예: logback.xml) |
| `src/main/filters` | 리소스 필터 파일 |
| `src/main/webapp` | 웹 애플리케이션 소스 |
| `src/test/java` | 테스트 소스 |
| `src/test/resources` | 테스트 리소스 |
| `src/test/filters` | 테스트 리소스 필터 파일 |
| `src/it` | 통합 테스트 (주로 플러그인용) |
| `src/assembly` | assembly 기술자 |
| `src/site` | 사이트 |
| `LICENSE.txt` | 프로젝트 라이선스 |
| `NOTICE.txt` | 의존 라이브러리가 요구하는 고지·저작자 표시 |
| `README.txt` | 프로젝트 readme |

### pom.xml의 주요 속성

| 속성 | 의미 |
|---|---|
| `modelVersion` | POM 모델 버전, 현재 **4.0.0** 고정 |
| `parent` | 상위 프로젝트 정보 — 상위 프로젝트의 내용을 상속해서 사용 |
| `groupId` | 프로젝트를 만드는 회사(조직)의 ID, 보통 **도메인을 거꾸로** (`com.ssafy`) |
| `artifactId` | 프로젝트 이름 성격, groupId 안에서 유일해야 함 |
| `version` | 현재 버전, 개발 중이면 `SNAPSHOT` 접미사 |
| `packaging` | 배포 파일의 패키징 유형 (`jar`, `war`) |
| `name` | 프로젝트 이름 |
| `properties` | pom.xml에서 쓸 속성 선언 — 주로 라이브러리 **버전을 한 곳에서 관리** |
| `dependencies` | 의존 관계에 있는 라이브러리 관리 |

### dependency와 scope

- **scope**: 해당 의존성을 **언제, 어떻게 클래스 패스에 포함**할 것인가

| scope | 컴파일 | 테스트 | 런타임 | 설명 |
|---|---|---|---|---|
| `compile` | O | O | O | **기본값**, 생략하면 적용 |
| `provided` | O | O | X (외부 제공) | 런타임에는 외부 환경이 제공 — Servlet API는 WAS(tomcat)가 제공하므로 패키징에서 제외 |
| `runtime` | X | O | O | 실행 시에만 필요 — JDBC 드라이버 |
| `test` | X | O | X | 테스트 시에만 사용, 배포 시 제외 → **main 영역에서 사용 불가** |
| `system` | | | | 로컬 시스템에 설치된 JAR처럼 외부에서 제공되는 의존성 참조 |

- provided 비유: 운전 연습은 연습용 차로 하지만, 시험은 시험장 차로 봄

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### life cycle 관리

- 3가지 life cycle

| life cycle | 하는 일 |
|---|---|
| `clean` | 이전 빌드에서 생성된 파일 제거 |
| `default` (build) | 프로젝트 빌드·배포 |
| `site` | 프로젝트 문서와 사이트 생성 |

- default(build) life cycle의 주요 phase — 뒤 단계를 실행하면 앞 단계가 차례로 실행됨

```
compile → test → package → install → deploy
```

| phase | 하는 일 |
|---|---|
| compile | 소스 코드 컴파일 |
| test | 단위 테스트 수행 |
| package | 컴파일 결과물과 리소스를 jar, war로 묶음 |
| install | 패키지를 **로컬 저장소**에 설치 |
| deploy | 패키지를 **원격 저장소**에 배포 |

- 각 phase는 **논리적인 단계**일 뿐
  - 실제로는 phase에 연결된 **plugin**이 있고, plugin이 수행할 수 있는 명령(**goal**)이 실행됨

### plugin 설정

- `maven-compiler-plugin`: JDK 버전 설정 + Maven으로 컴파일할 때 lombok 설정

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <source>${jdk.version}</source>   <!-- jdk version 설정 -->
                <target>${jdk.version}</target>
                <annotationProcessorPaths>        <!-- 컴파일 과정에서 lombok 설정 -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.36</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### goal의 실행

- IDE의 Maven 실행 설정에서 **사용할 goal을 명시**해 실행하고, 필요하면 **추가 goal을 등록**해 둘 수 있음 (교재 슬라이드는 화면 캡처)
- 명령줄로는 phase나 goal 이름을 그대로 넘김

```bash
mvn clean package     # clean 후 package까지 실행
mvn test              # compile → test
```
