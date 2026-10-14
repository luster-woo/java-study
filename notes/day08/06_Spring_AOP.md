# Spring AOP

> 교재: 15기_자바비전공_Framework(Back)_04_0506_AOP (비전공) — Spring AOP

> [← Proxy](05_Proxy.md) · [day08 목록](../README.md)

## 학습 목표

- Spring AOP를 설정하는 방법을 이해하고 활용할 수 있다.

---

## Pointcut

### Pointcut 지정자

- Join Point 중 **어디에 Advice를 호출할지** 식별하는 필터
- `@Before` 등 advice 선언 애너테이션의 **value**로 설정

| 지정자 | 기준 | 예 |
|---|---|---|
| `execution` | **메서드 시그니처**(반환 타입·패키지·클래스·메서드명·파라미터) — 가장 많이 사용 | `@Before("execution(* com.ssafy..dao.*.*(..))")` |
| `within` | **타입(클래스)·패키지** 안의 모든 메서드 | `@Before("within(com.ssafy..dao.*)")` |
| `bean` | **빈 이름** (`*` 와일드카드 가능, 예: `"bean(*Dao)"`) | `@Before("bean(basicMemberDao)")` |
| `@within` | 특정 **애너테이션이 붙은 타입**의 메서드 | `@Before("@within(org.springframework.stereotype.Service)")` |
| `@annotation` | 특정 **애너테이션이 붙은 메서드** | `@Before("@annotation(org.springframework.transaction.annotation.Transactional)")` |

### execution 표현식 — 구조와 와일드카드

```
execution(  int     my.package.*Bean   .   set*      (..)  )
           ───────  ───────────────────    ──────   ─────
          반환 타입   패키지 + 클래스          메서드     파라미터
```

| 위치 | 와일드카드 | 의미 |
|---|---|---|
| 반환 타입 | `*` | 모든 반환 타입 |
| | `!String` | String이 아닌 반환 타입 |
| 패키지 + 클래스 (생략 가능) | `*` | 모든 패키지·클래스 (`*Bean`: Bean으로 끝나는 클래스) |
| | `..` | 0개 이상의 하위 패키지 |
| 메서드 | `*` | 모든 메서드 (`set*`: set으로 시작하는 메서드) |
| 파라미터 | `..` | 0개 이상의 파라미터 (타입 무관) |
| | `*` | 타입 무관 파라미터 1개 |

```java
execution(* *(..))                      // 모든 메서드
execution(* remove(..))                 // remove 메서드
execution(* set*(..))                   // set으로 시작하는 메서드
execution(* com.ssafy.aop.*.*(..))      // com.ssafy.aop 패키지 모든 클래스의 모든 메서드
execution(* com.ssafy.aop..*.*(..))     // com.ssafy.aop + 하위 패키지 모든 클래스의 모든 메서드
execution(* *(String))                  // String 파라미터 1개인 메서드
execution(* *(String, ..))              // 첫 파라미터가 String + 0개 이상 파라미터인 메서드
```

### Pointcut 특이사항

- 반환 타입·파라미터에 **참조 타입**(원시 타입 제외)을 쓸 때는 **패키지명까지 전부** 명시

```java
// List, Member 같은 참조 타입은 java.util.List, com.example.dto.Member처럼 풀 패키지명
@Before("execution(void com.example.service.*Service.saveMember(com.example.dto.Member))")
```

- 여러 pointcut 연결: `&&`, `||`, `!` 또는 `and`, `or`, `not`

```java
// save로 시작하거나 delete로 시작하는 service 메서드
@Around("execution(* com.example.service.*Service.save*(..))"
      + " || execution(* com.example.service.*Service.delete*(..))")
```

- **named pointcut**: `@Pointcut`으로 이름을 붙여 **재사용**

```java
// 재사용할 pointcut 정의 — 메서드는 pointcut의 ID 역할, 내용 없음
@Pointcut("execution(* com.example.dao.*.*(..))")
public void daoLayerPointcut() {
}

// 같은 Aspect 안에서는 메서드명만 (다른 클래스면 풀 패키지명.클래스.메서드명())
@Before("daoLayerPointcut()")
public void logBeforeDao() {
    log.info("포인트 컷 재사용...!");
}
```

---

## Advice Type

### 5가지 Advice 타입

- Join Point에서 Advice가 **실행되는 시점**에 따라 5가지

| 타입 | 실행 시점 | 특징 |
|---|---|---|
| `@Before` | Target 메서드 **실행 전** | Target 실행 전에 할 일 |
| `@AfterReturning` | Target이 **정상 반환한 후** | `returning` 속성으로 반환값을 받을 수 있음 |
| `@AfterThrowing` | Target에서 **예외 발생 시** | `throwing` 속성으로 예외 객체를 받을 수 있음 |
| `@After` | 성공·예외 **관계없이 실행 후** | try-finally의 finally와 같음 |
| `@Around` | Target 실행 **전/후 모두** | `proceed()`로 Target을 **직접 실행**, 반환값을 직접 return해야 함, try-catch로 예외까지 처리 가능 — 가장 강력 |

### 실행 순서

```
Client
  │
  ▼ ─────────── AOP Proxy Boundary ───────────
  1. @Around (proceed 전)
  2. @Before
  3. Target Method (핵심 로직)
       ├─ 성공 → 4-1. @AfterReturning
       └─ 예외 → 4-2. @AfterThrowing
  5. @After (finally)
  6. @Around (proceed 후)
  ▲ ───────────────────────────────────────────
  │ 결과(Return)
Client
```

- Proxy의 `PersonProxy.coding()`과 대응

| PersonProxy 코드 위치 | Advice |
|---|---|
| try 전 "컴퓨터를 부팅한다" | `@Before` |
| `target.coding()` 정상 후 "Git에 Push 한다" | `@AfterReturning` |
| catch "조퇴를 한다" | `@AfterThrowing` |
| finally "보람찬 하루를 마무리한다" | `@After` |
| 메서드 전체 | `@Around` |

### 5가지 메서드 정의 (예시 코드)

- 실습 코드: `src/day08/start` — `aspect/LogAspect.java`, `service/CalcService.java`, 테스트 `aspect/LogAspectTest.java`

```java
@Slf4j
@Component      // ① 빈 등록 (필수)
@Aspect         // ② Aspect 선언 (필수)
public class LogAspect {

    // 공통 Pointcut — service 패키지의 모든 클래스의 모든 메서드
    @Pointcut("execution(* com.ssafy.start.service.*.*(..))")
    public void serviceLayer() {}

    // @Before — 대상 메서드 실행 직전
    @Before("serviceLayer()")
    public void logBefore(JoinPoint jp) {
        log.info(">>> {}", jp.getSignature());
    }

    // @After — 종료 후 (성공·실패 무관, finally)
    @After("serviceLayer()")
    public void logAfter(JoinPoint jp) {
        log.info("<<< {}", jp.getSignature());
    }

    // @AfterReturning — 정상 종료 후 (반환값 접근)
    @AfterReturning(pointcut = "serviceLayer()", returning = "ret")
    public void logReturn(JoinPoint jp, Object ret) {
        log.info("return = {}", ret);
    }

    // @AfterThrowing — 예외가 던져졌을 때만
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void logThrow(JoinPoint jp, Exception ex) {
        log.error("error = {}", ex.getMessage());
    }

    // @Around — 실행 전후 모두 + 흐름 가로채기 (가장 강력)
    @Around("serviceLayer()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        long t = System.currentTimeMillis();
        Object ret = pjp.proceed();                  // 원본 메서드 호출
        log.info("took {}ms", System.currentTimeMillis() - t);
        return ret;
    }
}
```

```java
@Service
public class CalcService {                       // Target — 핵심 로직만, 로그 코드 없음
    public int add(int a, int b) { return a + b; }
    public int divide(int a, int b) { return a / b; }   // b == 0 → @AfterThrowing 확인용
}
```

### 각 Advice별 필수 작성 사항

- **공통 필수**
  - `build.gradle`: `implementation 'org.springframework.boot:spring-boot-starter-aspectj'`
  - Aspect 클래스에 **`@Component` + `@Aspect` 둘 다** (빈이 아니면 스프링이 Aspect를 모름)
  - 어노테이션 값으로 **Pointcut 표현식** (`"execution(...)"` 또는 `"serviceLayer()"`)
  - `JoinPoint jp` 파라미터는 **선택** — 메서드 이름(`getSignature()`)·인자(`getArgs()`)가 필요할 때만

| Advice | 필수로 맞춰야 할 것 |
|---|---|
| `@Before` | 없음 (Pointcut만) |
| `@After` | 없음 (Pointcut만) |
| `@AfterReturning` | 반환값을 받으려면 `returning = "ret"` 이름 == 파라미터 이름 `ret` |
| `@AfterThrowing` | 예외를 받으려면 `throwing = "ex"` 이름 == 파라미터 이름 `ex` |
| `@Around` | 아래 4가지 **모두** |

- **`@Around` 필수 4가지** — 하나라도 빠지면

| 필수 | 빠뜨리면 |
|---|---|
| 파라미터 **`ProceedingJoinPoint`** | `JoinPoint`엔 `proceed()`가 없음 |
| **`pjp.proceed()`** 호출 | 원본 메서드가 **아예 실행 안 됨** |
| 반환타입 `Object` + **`return ret;`** | 호출한 쪽이 `null`(기본형이면 0)을 받음 |
| **`throws Throwable`** | `proceed()`가 Throwable을 던져서 컴파일 에러 |

- 속성이 2개 이상(`pointcut` + `returning`/`throwing`)이면 **`pointcut = "..."`으로 이름을 붙여서** 작성
- `@AfterThrowing`의 예외 타입(`Exception ex`)과 맞는 예외일 때만 실행됨 (`ArithmeticException`이면 `Exception`으로 받을 수 있음)

---

## Spring AOP 시작하기

### 의존성

- **AspectJ**: 자바 언어에 관점 지향 프로그래밍을 적용하도록 돕는 표준화된 AOP 프레임워크 — Spring AOP가 그 애너테이션을 사용

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aspectj</artifactId>
</dependency>
```

- Spring Boot는 이 starter만 있으면 AOP를 자동 설정 → `@EnableAspectJAutoProxy`를 직접 붙이지 않아도 됨

### Annotation으로 Aspect 작성

1. **`@Component`**: Aspect도 Spring 빈으로 등록해야 AOP가 적용됨
2. **`@Aspect`**: 이 빈이 Aspect임을 선언
3. **`@Pointcut` + Advice 타입**: 어디에(Pointcut) 언제·무엇을(Advice)
4. Advice 메서드는 **`JoinPoint`** 파라미터로 대상 메서드 정보를 받을 수 있음 (`getSignature()`, `getArgs()` 등)

```java
@Component      // ① Spring 빈으로 등록
@Aspect         // ② AOP Aspect 선언
public class MyAspect {

    // ③ Pointcut + Advice 타입
    @Pointcut("execution(* com.ssafy.aop.*.coding())")
    public void mypt() {
    }

    @Before("mypt()")
    public void before() {
        System.out.println("컴퓨터를 부팅한다.");
    }

    @AfterReturning(value = "mypt()", returning = "num")
    public void afterReturning(int num) {               // coding()의 반환값을 num으로 받음
        System.out.println("Git Push 한다. : " + num + "개의 코드를");
    }

    @AfterThrowing(value = "mypt()", throwing = "th")
    public void afterThrowing(Throwable th) {           // 발생한 예외를 th로 받음
        System.out.println("조퇴를 한다.");
        if (th instanceof OuchException) {
            ((OuchException) th).handleException();
        }
    }

    @After("mypt()")
    public void after() {
        System.out.println("보람찬 하루를 마무리 한다.");
    }
}
```

### @Around 하나로 표현하기

- `ProceedingJoinPoint.proceed()`가 Target 실행 → 앞뒤·catch·finally에 나머지 4가지를 직접 배치

```java
@Around("mypt()")
public int around(ProceedingJoinPoint pjt) {
    int num = 0;
    this.before();                       // @Before 자리
    try {
        num = (int) pjt.proceed();       // Target 실행
        this.afterReturning(num);        // @AfterReturning 자리
    } catch (Throwable e) {
        this.afterThrowing(e);           // @AfterThrowing 자리
    } finally {
        this.after();                    // @After 자리
    }
    return num;                          // 반환값을 직접 돌려줘야 함
}
```

- `@Around`와 위 4개 Advice를 **같이 켜 두면** 같은 문구가 두 번씩 출력됨 → 둘 중 하나만 사용

### 4개 Advice → @Around 하나로 변환 (동작 동일 버전)

- 실습 코드: `src/day08/start` — `aop/MyAspect.java`(4개 Advice), `aop/AroundAspect.java`(변환), `aop/AroundSwallowAspect.java`(위 노트 버전), 테스트 `aop/AopTest.java`
- 위 노트 코드는 예외를 **삼킴**. 4개 Advice 버전과 **완전히 똑같이** 동작하게 하려면 catch에서 `throw th;`

```java
@Around("mypt()")
public Object around(ProceedingJoinPoint pjp) throws Throwable {
    System.out.println("컴퓨터를 부팅한다.");                        // @Before 자리
    try {
        Object result = pjp.proceed();                              // coding() 실행
        int num = (int) result;
        System.out.println("Git Push 한다. : " + num + "개의 코드를"); // @AfterReturning 자리
        return result;                                              // 반환값 그대로 돌려주기
    } catch (Throwable th) {
        System.out.println("조퇴를 한다.");                           // @AfterThrowing 자리
        if (th instanceof OuchException) {
            ((OuchException) th).handleException();
        }
        throw th;                                                   // 예외를 다시 던짐 (원래 동작 유지)
    } finally {
        System.out.println("보람찬 하루를 마무리 한다.");               // @After 자리
    }
}
```

| 4개 Advice 버전 | `@Around` 버전 |
|---|---|
| `@Before` 메서드 | `proceed()` **전** |
| `@AfterReturning`의 `returning = "num"` | `proceed()`의 **반환값**을 직접 받음 |
| `@AfterThrowing`의 `throwing = "th"` | **`catch`**로 직접 받음 |
| `@After` 메서드 | **`finally`** |

- 예외 처리 방식에 따른 차이

| 버전 | 예외 발생 시 호출한 쪽은 |
|---|---|
| 4개 Advice / 위 코드 (`throw th;`) | **예외를 받음** (원래 동작 그대로) |
| 노트 버전 (`throw` 없음) | 예외 대신 **0을 받음** (예외를 삼킴) |

- 예외를 삼키거나 반환값을 바꾸는 등 **흐름 자체를 바꿀 수 있는 게 `@Around`만의 힘** → "가장 강력"
- 반대로 `throw`·`return`을 빼먹으면 원래 동작이 **조용히 바뀜** → 주의
- `MyAspect`(4개 Advice)는 **하나의 Aspect** = Pointcut 1개 + Advice 4개가 합쳐져 `coding()` 하나를 감쌈. `@Around` 하나로도 같은 일을 할 수 있음
- `@Pointcut` 메서드(`mypt()`)는 직접 호출해도 아무 일 없음 — 본문이 비어 있고, `"mypt()"`는 호출이 아니라 **Pointcut 이름 참조**. 다른 클래스에서 쓸 땐 `"com.ssafy.start.aop.MyAspect.mypt()"`처럼 풀 경로

---

## 참고

> 참고: 교재 21쪽 `within(com.ssafy..dao)`는 타입 패턴이라 `dao`라는 **이름의 클래스**를 찾음. dao 패키지 안의 클래스 전체를 뜻하려면 `within(com.ssafy..dao.*)`(위 표는 고쳐 씀).

> 참고: starter 이름은 버전에 따라 다름 — Spring Boot 3.x(교재 springboot 0507의 3.5.11 포함)는 `spring-boot-starter-aop`, Spring Boot 4.x부터 `spring-boot-starter-aspectj`. 실습 프로젝트의 Boot 버전에 맞춰 쓸 것.

- Spring AOP는 프록시 기반이라 Join Point가 **메서드 실행**뿐이고, 내부 호출에는 적용되지 않음([Proxy](05_Proxy.md))
