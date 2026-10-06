# annotation

> 교재: 15기_자바_live_06_Generic_0123 (전공)

> [← enum](06_enum.md) · [day04 목록](../README.md)

## annotation이란?

- 컴파일러, JVM, 프레임워크 등이 보는 **주석** — 소스코드에 **메타 데이터**를 삽입하는 형태
  - 코드에 정보를 추가 → 소스 코드 구조 변경, 환경 설정 정보 추가 등의 작업 진행
- JDK 기본 annotation 예

| annotation | 컴파일러에게 알려 주는 것 |
| --- | --- |
| `@Deprecated` | 해당 메서드가 deprecated 되었음 |
| `@Override` | override한 메서드임 → override가 잘 되었는지 컴파일러가 확인 |
| `@SuppressWarnings` | 사소한 warning은 신경 쓰지 말 것 |

- Framework들은 annotation 천국 (예: Spring 테스트)

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/mvc-config.xml"})
@WebAppConfiguration
public class RequestMappingTest {}
```

## annotation 분석

- 선언: interface 선언과 비슷하게 **`@interface`** 사용
- 구성
  - `@Target`, `@Retention` 등 **메타 애너테이션**
  - 애너테이션을 위한 **속성**

```java
@Target({TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE})  // 메타 애너테이션
@Retention(RetentionPolicy.SOURCE)                                      // 메타 애너테이션
public @interface SuppressWarnings {                                    // 애너테이션 선언
    String[] value();                                                   // 애너테이션 속성
}
```

### 메타 애너테이션

- 애너테이션 설정을 위한 애너테이션

| 메타 애너테이션 | 의미 | 교재 예 |
| --- | --- | --- |
| `@Documented` | JavaDoc을 만들 때 이 애너테이션이 문서에 표시됨 | `@Component`가 붙은 `@Controller`의 JavaDoc |
| `@Inherited` | 애너테이션이 하위 클래스에 상속됨 | `AbstractBoardTest`의 `@ActiveProfiles({"h2","dev"})`가 자식 `CascadeTest`에도 적용 |
| `@Repeatable` | 같은 애너테이션을 반복 적용할 수 있음 | `@PropertySource("source1")`, `@PropertySource("source2")` |
| `@Retention` | 어느 단계까지 정보를 유지할지 | 아래 표 |
| `@Target` | 어디에 사용할 수 있는지 | 아래 표 |

> 참고: 교재에 `@Document`로 적힌 곳이 있으나 실제 이름은 `@Documented`(java.lang.annotation.Documented).

### @Retention — 어느 단계까지 정보를 유지할 것인가?

- `RetentionPolicy` enum 항목 중 용도에 맞게 **하나만** 선택

| 이름 | 범위 | 주 사용처 | 예 |
| --- | --- | --- | --- |
| `SOURCE` | 컴파일러가 사용, 클래스 파일에 포함되지 않음 | 컴파일러, IDE, 소스코드 분석 도구 | `@Override`, `@SuppressWarnings` |
| `CLASS` (생략 시 기본) | 클래스 파일에 포함되나 리플렉션으로 정보를 얻을 수 없음 | SOURCE의 사용처 + 바이트코드 레벨 분석 도구 | |
| `RUNTIME` | 클래스 파일에 포함되고 리플렉션으로 정보를 얻을 수 있음 | CLASS의 사용처 + 응용 애플리케이션, 프레임워크, 라이브러리 | `@Deprecated` |

### @Target — 어디에서 사용할 수 있는가?

| `ElementType` | 범위 |
| --- | --- |
| `TYPE` | 클래스, 인터페이스, enum 타입 |
| `FIELD` | 필드, enum 타입의 값 |
| `METHOD` | 메서드 |
| `PARAMETER` | 메서드·생성자의 파라미터 |
| `CONSTRUCTOR` | 생성자 |
| `LOCAL_VARIABLE` | 로컬 변수 |
| `ANNOTATION_TYPE` | 애너테이션을 만들 때 `@interface`에 선언 |
| `PACKAGE` | 패키지 |

### 속성

- 추상 메서드처럼 선언
  - 메서드 이름 → 속성 이름, 리턴 타입 → 속성 타입
- 사용할 때는 일반 속성처럼 **`키=값`**
  - 배열은 `{ }` 사용, 길이가 1이면 `{ }` 생략 가능
  - 속성은 **default** 값을 가질 수 있음 → 이 경우 설정 생략 가능
  - 설정하는 속성이 `value` 하나면 키 `value=` 생략 가능 (주로 자주 쓰는 속성을 value로 둠)

```java
@SuppressWarnings(value = {"unused", "rawtypes"})   // 키=값 형태로 배열 할당
@SuppressWarnings(value = "unused")                 // 배열이지만 값이 하나면 중괄호 생략
@SuppressWarnings({"unused", "rawtypes"})           // 속성이 value 하나면 키 생략
```

- 정리 예제: `RUNTIME` 애너테이션은 리플렉션으로 읽히고, `SOURCE` 애너테이션은 읽히지 않음

```java
import java.lang.annotation.*;
import java.lang.reflect.Method;

public class AnnotationTest {
    public static void main(String[] args) throws Exception {
        // RUNTIME 유지(@Deprecated) → 리플렉션으로 확인 가능
        Method method = SomeClass.class.getMethod("deprecatedMethod");
        if (method.isAnnotationPresent(Deprecated.class)) {
            System.out.println("이 메서드는 안 쓰는게 좋아요!");
        }

        // SOURCE 유지(@Override) → 클래스 파일에 없으므로 false
        Method ts = SomeClass.class.getMethod("toString");
        System.out.println(ts.isAnnotationPresent(Override.class));  // false

        // 직접 만든 애너테이션의 속성 읽기
        Info info = SomeClass.class.getAnnotation(Info.class);
        System.out.println(info.value() + ", " + info.version());     // 게시판, 1
    }
}

@Target(ElementType.TYPE)                 // 클래스·인터페이스·enum 에만
@Retention(RetentionPolicy.RUNTIME)       // 실행 중에도 유지
@interface Info {
    String value();                       // 속성 이름 = 메서드 이름, 타입 = 리턴 타입
    int version() default 1;              // default가 있으면 생략 가능
}

@Info("게시판")                            // 속성이 value 하나만 설정 → 키 생략
class SomeClass {
    @Deprecated
    public void deprecatedMethod() {}

    @Override
    public String toString() { return "SomeClass"; }
}

/* 출력
이 메서드는 안 쓰는게 좋아요!
false
게시판, 1
*/
```

---

## 참고

- `@Target`의 `ElementType`에는 교재 표 외에 `TYPE_PARAMETER`, `TYPE_USE`(Java 8), `MODULE`(Java 9), `RECORD_COMPONENT`(Java 16)도 있음
