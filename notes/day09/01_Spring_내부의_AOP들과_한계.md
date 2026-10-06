# Spring 내부의 AOP들과 한계

> 교재: 15기_자바전공_05_springmvc_1_0512 (전공) — 1. Spring 내부의 AOP들과 한계

> [day09 목록](../README.md) · [Spring MVC 개요 →](02_Spring_MVC_개요.md)

---

## Spring 내부의 AOP들과 한계

### AOP 복습

- **AOP(Aspect Oriented Programming, 관점 지향 프로그래밍)**: 기능을 비즈니스 로직 단위가 아니라 **관점 단위**로 바라봄
- 메서드마다 반복되는 인증·트랜잭션·암호화·예외 처리·성능 모니터링·로깅 코드 → 복사·붙여넣기 대신 관점별로 떼어냄
- **횡단 관심의 모듈화 → 재사용 → 개발자는 비즈니스 로직에만 집중**

```text
[AOP 전]                         [AOP 후]
methodA() {                      methodA() { 비즈니스 로직 }
  인증 및 권한 검사               methodB() { 비즈니스 로직 }
  트랜잭션 처리                        ↑ 공통 관점이 감싸서 적용
  암호화                         인증·권한 관점 / 트랜잭션 관점 / 암호화 관점
  비즈니스 로직                   예외 처리 관점 / 성능 모니터링 관점 / 로깅 관점
  예외 처리 · 성능 모니터링 · 로깅
}
methodB() { ...같은 코드 반복... }
```

### 기본 용어

| 용어 | 의미 |
|---|---|
| **target** | 핵심 비즈니스 로직을 가진 빈 — AOP 적용 대상 |
| **aspect** | 모듈화된 횡단 관심사(관점)를 부르는 이름 — advice 하나 이상으로 구성 |
| **advice** | 횡단 관심 코드를 모듈화한 메서드. 동작 시점에 따라 `@Before`·`@After`·`@AfterReturning`·`@AfterThrowing`·`@Around`. pointcut + 횡단 관심 코드(메서드 body)로 구성 |
| **join point** | advice를 적용할 수 있는 지점. 원래 AOP는 필드 변경·메서드 실행·생성자 호출 등 다양하지만 **Spring은 프록시 기반이라 메서드 실행 시점만** |
| **pointcut** | advice를 적용할 대상을 고르는 표현식(join point 필터) |
| **weaving** | aspect를 비즈니스 로직 코드에 적용하는 행위 |

- 예: "`setXXX` 메서드가 호출되면 로그를 남겨라"

| 항목 | 내용 |
|---|---|
| pointcut (어디서) | `setXXX` 형태의 메서드 |
| advice type (언제) | `@Before` |
| advice code (무엇을) | `log.trace("설정 중")` |

```java
private String name;

public void setName(String name) {
    // 비즈니스 로직
    this.name = name;
}
```

### singleton의 비밀 — @Configuration

- 예제 구성: `HeavyWorkBean`이 `PointcutTestBean`을 생성자로 주입받음

```java
@Slf4j
@RequiredArgsConstructor
public class HeavyWorkBean {
    private final PointcutTestBean ptb;

    @Async
    public void heavyWork(int i) throws InterruptedException {
        Thread.sleep(1000);
        log.trace("처리중 {}", ptb.factorial(i));
    }

    @Cacheable("guguCache")
    public List<String> cacheableGugu(int n) {
        List<String> result = new ArrayList<>();
        // ...
        return result;
    }

    public void methodA() {
        log.debug("methodA called");
    }

    public void methodB() {
        log.debug("methodB called");
        methodA();
    }
}
```

```java
@Configuration
@EnableAsync
@EnableCaching
public class CustomConfiguration {
    @Bean
    PointcutTestBean pointcutTestBean() {
        return new PointcutTestBean();
    }

    @Bean
    HeavyWorkBean heavyWorkBean() {
        return new HeavyWorkBean(pointcutTestBean());   // 메서드를 직접 호출하는데?
    }
}
```

- 질문: `pointcutTestBean()`을 그냥 호출하면 매번 `new` 될 텐데, 어떻게 singleton이 유지될까?
- 답: **`@Configuration` 클래스 자체가 CGLIB 프록시**로 만들어져 빈으로 등록됨

```java
@Autowired
CustomConfiguration config;

@Test
public void isProxy() {
    log.debug(config.getClass().getName());
    // com.ssafy.live.aop.config.CustomConfiguration$$SpringCGLIB$$0
    log.debug("isproxy: {}", AopUtils.isAopProxy(config));
    // isproxy: false
}
```

- 클래스 이름에 `$$SpringCGLIB$$`가 붙음 → 하위 클래스 프록시
- 그런데 `AopUtils.isAopProxy()`는 **false** → `SpringProxy`를 구현하지 않기 때문(일반적인 AOP 목적의 프록시가 아님)

| | 개발자가 작성한 `@Configuration` 클래스 | Spring이 만든 프록시 |
|---|---|---|
| `@Bean` 메서드 | `return new HeavyWorkBean();` | 1. 기존 빈이 있으면 그 빈 리턴 → 2. 없으면 원래 메서드(타겟) 호출 → 3. 빈 등록 후 리턴 |

- 같은 구조를 순수 자바로 흉내낸 코드 — 프록시 = 원래 클래스를 상속해 `@Bean` 메서드를 재정의

```java
public class ConfigProxyDemo {
    public static void main(String[] args) {
        CustomConfiguration plain = new CustomConfiguration();
        System.out.println(plain.heavyWorkBean().ptb == plain.heavyWorkBean().ptb);   // false

        CustomConfiguration proxy = new CustomConfigurationProxy();
        HeavyWorkBean h1 = proxy.heavyWorkBean();
        HeavyWorkBean h2 = proxy.heavyWorkBean();
        System.out.println(h1 == h2);                           // true
        System.out.println(h1.ptb == proxy.pointcutTestBean()); // true
    }
}

class PointcutTestBean { }

class HeavyWorkBean {
    final PointcutTestBean ptb;
    HeavyWorkBean(PointcutTestBean ptb) { this.ptb = ptb; }
}

// 개발자가 작성한 @Configuration 클래스 역할
class CustomConfiguration {
    PointcutTestBean pointcutTestBean() { return new PointcutTestBean(); }
    HeavyWorkBean heavyWorkBean() { return new HeavyWorkBean(pointcutTestBean()); }
}

// Spring이 CGLIB로 만드는 하위 클래스(프록시) 역할
class CustomConfigurationProxy extends CustomConfiguration {
    private final Map<String, Object> container = new HashMap<>();

    @Override
    PointcutTestBean pointcutTestBean() {
        return (PointcutTestBean) container.computeIfAbsent("pointcutTestBean", k -> super.pointcutTestBean());
    }

    @Override
    HeavyWorkBean heavyWorkBean() {
        Object bean = container.get("heavyWorkBean");   // 1. 기존 빈 확인
        if (bean == null) {
            bean = super.heavyWorkBean();               // 2. 없으면 원래 메서드 호출
            container.put("heavyWorkBean", bean);       // 3. 등록 후 리턴
        }
        return (HeavyWorkBean) bean;
    }
}
// 결과
// false
// true
// true
```

### 간단한 비동기 처리 — @Async

- 비동기 작업을 위해 Thread·ThreadPool을 **직접 만들 필요 없음**
- 내부적으로 Spring의 **`TaskExecutor`**로 비동기 처리
- `@Configuration` 클래스에 **`@EnableAsync`** 필요

```java
@Async
public void heavyWork(int i) throws InterruptedException {
    Thread.sleep(1000);
    log.trace("{}에 의해 처리중 {}! = {}", Thread.currentThread(), i, ptb.factorial(i));
}
```

```java
@Test
public void asyncTest() throws InterruptedException {
    Assertions.assertTrue(AopUtils.isAopProxy(hbean));   // @Async가 붙은 빈은 AOP 프록시
    for (int i = 0; i < 10; i++) {
        hbean.heavyWork(i);
    }
}
```

```text
[TRACE] c.ssafy.live.aop.bean.HeavyWorkBean Thread[#40,task-4,5,main]에 의해 처리 중 3! = 6
[TRACE] c.ssafy.live.aop.bean.HeavyWorkBean Thread[#42,task-6,5,main]에 의해 처리 중 5! = 120
...
[TRACE] c.ssafy.live.aop.bean.HeavyWorkBean Thread[#43,task-7,5,main]에 의해 처리 중 6! = 720
```

- 스레드 관련 코드가 한 줄도 없는데 `task-N` 스레드들이 나눠서 처리 → 순서도 섞여서 출력

### @Async와 PSA — TaskExecutor

| 구분 | 내용 |
|---|---|
| Java 표준 | `Executor` — `void execute(Runnable)`, 사용법이 복잡·다양 |
| Spring 추상화 | `TaskExecutor` — `void execute(Runnable)` |
| Spring 구현체 | `ThreadPoolTaskExecutor`(ThreadPoolExecutor 활용) · `SimpleAsyncTaskExecutor`(매번 `new Thread()`) · `SyncTaskExecutor`(동기 실행, 테스트용) |

- **간단한 설정만으로 구현체가 바뀜** → 우리 코드(`@Async` 메서드)는 그대로

```java
@Bean   // 커스텀 TaskExecutor 등록
public TaskExecutor simple() {
    return new SimpleAsyncTaskExecutor();
}
```

### 간단한 Cache 처리 — @Cacheable

- 데이터 캐시를 위해 복잡한 코드를 쓰지 않아도 됨
- 기본은 **메모리 기반(`ConcurrentHashMap`)** 캐시
- `@Configuration` 클래스에 **`@EnableCaching`** 필요

```java
@Cacheable("guguCache")
public List<String> cacheableGugu(int n) {
    log.info("캐시 미적용: 실제 계산 수행 - n={}", n);
    List<String> result = new ArrayList<>();
    for (int i = 1; i <= 9; i++) {
        result.add(n + " x " + i + " = " + (n * i));
    }
    return result;
}
```

```java
@Test
public void cacheableTest() {
    List<String> first = hbean.cacheableGugu(5);
    List<String> second = hbean.cacheableGugu(5);   // 같은 인자 → 캐시에서 꺼냄
    Assertions.assertEquals(first, second);
}
// "캐시 미적용: 실제 계산 수행 - n=5" 로그는 한 번만 찍힘
```

- 생각해볼 것: 캐시 저장소(메모리·Redis 등)가 바뀌어도 `@Cacheable` 코드는 그대로 → 이것도 **PSA**(`CacheManager` 추상화)

### Spring AOP의 한계

- Spring AOP는 **Spring Bean에 대한 Proxy**를 기반으로 동작
- **Proxy를 거치지 않은 호출에는 AOP가 적용되지 않음**
- 특히 **타겟 내부에서 호출하는 메서드**에는 적용 불가
  - 같은 클래스 안에서 `methodB()`가 `methodA()`를 호출하면?
  - 프록시 입장에서는 `methodB()`만 호출된 것 → `methodA()`의 advice는 동작하지 않음

```text
pointcut: execution(* method*())

  호출자 ──► [proxy]                         [target]
            methodA() ──(advice 후)──►       methodA() { do something }
            methodB() ──(advice 후)──►       methodB() { methodA(); }   ← this.methodA() — proxy를 안 거침
```

- 순수 자바 동적 프록시(JDK Proxy)로 확인

```java
public class SelfInvocationDemo {
    public static void main(String[] args) {
        MyService target = new MyServiceImpl();
        MyService proxy = (MyService) Proxy.newProxyInstance(
                MyService.class.getClassLoader(),
                new Class<?>[] { MyService.class },
                (p, method, params) -> {
                    System.out.println("[advice] " + method.getName() + " 호출 전");
                    return method.invoke(target, params);
                });

        proxy.methodA();
        System.out.println("---");
        proxy.methodB();
    }
}

interface MyService {
    void methodA();
    void methodB();
}

class MyServiceImpl implements MyService {
    public void methodA() { System.out.println("methodA 실행"); }
    public void methodB() {
        System.out.println("methodB 실행");
        methodA();   // this.methodA() — 프록시를 거치지 않음
    }
}
// 결과
// [advice] methodA 호출 전
// methodA 실행
// ---
// [advice] methodB 호출 전
// methodB 실행
// methodA 실행        ← methodA의 advice가 없음
```

- 같은 이유로 `@Async`·`@Cacheable`·`@Transactional`도 **같은 클래스 내부 호출에서는 동작하지 않음**

---

## 참고

> 참고: 교재 advice 목록의 `@AfterRetuning`은 `@AfterReturning`의 오타.

> 참고: 교재는 기본 캐시를 `SimpleCacheManager`라고 적었지만, Spring Boot에서 캐시 라이브러리 없이 `@EnableCaching`만 켜면 `ConcurrentMapCacheManager`가 등록됨(둘 다 내부 저장소는 `ConcurrentHashMap` 계열).

- `$$SpringCGLIB$$0` 이름은 Spring 6(Boot 3) 기준. Spring 5 이전은 `$$EnhancerBySpringCGLIB$$...` 형태
- 프록시 예제 2개는 순수 자바(JDK 11)로 실행해 결과를 확인함. 교재의 실제 코드는 Spring Boot 테스트 환경(`@SpringBootTest`)에서 동작
