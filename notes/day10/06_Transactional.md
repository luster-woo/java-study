# @Transactional

> 교재: 15기_자바전공_09_spring_transaction_0519 (전공) — 3. @Transactional

> [← Service Layer와 TX](05_Service_Layer와_TX.md) · [day10 목록](../README.md) · [JPA 소개 →](07_JPA_소개.md)

---

## @Transactional의 속성

### 주요 설정 속성

- 클래스나 메서드에 적용 가능 → **클래스에 선언하고 메서드에서 재정의**

```java
@Target({ElementType.TYPE, ElementType.METHOD})   // 클래스나 메서드에 적용 가능
public @interface Transactional {
    Class<? extends Throwable>[] rollbackFor() default {};      // 지정된 예외 발생 시 롤백
    Class<? extends Throwable>[] noRollbackFor() default {};    // 지정된 예외가 나도 롤백하지 않음
    Propagation propagation() default Propagation.REQUIRED;     // 전파 방식 (있으면 참여, 없으면 생성)
    Isolation isolation() default Isolation.DEFAULT;            // 격리 수준 (DB 기본 설정 사용)
    int timeout() default TransactionDefinition.TIMEOUT_DEFAULT; // 제한 시간(초), 기본 -1 = 제한 없음
    boolean readOnly() default false;                           // 읽기 전용 여부
}
```

| 속성 | 기본값 | 의미 |
|---|---|---|
| `rollbackFor` | `{}` | 롤백할 예외 추가 |
| `noRollbackFor` | `{}` | 롤백하지 않을 예외 지정 |
| `propagation` | `REQUIRED` | 트랜잭션 전파 방식 |
| `isolation` | `DEFAULT` | 트랜잭션 격리(독립성) 수준 |
| `timeout` | `-1` | 제한 시간(초) |
| `readOnly` | `false` | 읽기 전용 트랜잭션 |

---

## Rollback 규칙

- **기본 동작**: **RuntimeException 계열** 예외 발생 시 Rollback
- 기본 동작 재정의
  - **`rollbackFor`**: Checked Exception(business exception) 중 **rollback 대상**이 되는 예외 설정
  - **`noRollbackFor`**: RuntimeException 중 **commit 대상**이 되는 예외 설정

```java
@Transactional(rollbackFor = Exception.class)               // Checked 예외에도 rollback
public void join(Member member) throws Exception { ... }

@Transactional(noRollbackFor = IllegalArgumentException.class)  // 이 예외는 commit
public void update(Member member) { ... }
```

---

## 전파 속성 (propagation)

### 고려해야 할 상황

- `@Transactional` 메서드에서 **다른 `@Transactional` 메서드를 호출**할 때 기존 T.X를 어떻게 이어 갈지
- 기본값: `Propagation.REQUIRED`(이미 있으면 참여, 없으면 생성)

```text
호출 1: Controller → BeanA.methodA()                   (@Transactional, T.X_A)
호출 2: Controller → BeanB.methodB() → BeanA.methodA() (@Transactional, T.X_B → ?)
```

- 호출 1: `BeanA.methodA()` 호출 시 T.X1 시작
- 호출 2: `BeanB.methodB()` 호출 시 T.X2 시작
- 호출 2에서 `BeanB.methodB()` → `BeanA.methodA()` 호출 시 **T.X_B와 T.X_A의 관계는?**
- 하나의 빈 안에서 호출해서 테스트해도 될까? → 안 됨(아래 "Spring AOP의 한계")

### 7가지 전파 속성

| BeanA.methodA()의 전파 속성 | 호출 1: → methodA() | 호출 2: → methodB() → methodA() |
|---|---|---|
| `PROPAGATION_REQUIRED` | 새로운 T.X 시작 | methodB의 T.X에 **참여** |
| `PROPAGATION_REQUIRES_NEW` | 새로운 T.X 시작 | **새로운 T.X 시작** |
| `PROPAGATION_SUPPORTS` | T.X 시작하지 않음 | methodB의 T.X에 참여 |
| `PROPAGATION_MANDATORY` | **예외 발생**(호출할 때 이미 T.X에 포함되어야 함) | methodB의 T.X에 참여 |
| `PROPAGATION_NESTED` | T.X 시작 | **내부에 새로운 T.X 시작** |
| `PROPAGATION_NEVER` | T.X 처리하지 않음 | **예외 발생** |
| `PROPAGATION_NOT_SUPPORTED` | T.X 처리하지 않음 | T.X 처리하지 않음 |

### Propagation.REQUIRED

- 호출 2에서 methodB와 methodA가 **하나의 T.X**를 구성

```java
// BeanA
@Transactional
void methodA() {
    // DB 작업 A
    int i = 1 / 0;          // ArithmeticException → Rollback 표시 후 전파
}

// BeanB
@Transactional
void methodB() {
    // DB 작업 B
    try {
        beanA.methodA();
    } catch (Exception e) {}   // 예외를 잡아도... Rollback or Commit?
}
```

- methodA에서 rollback 발생 시 **전체 T.X rollback**
- methodB에서 예외를 잡고 정상 종료해도 commit되지 않고 **`UnexpectedRollbackException`** 이 전파됨

### Propagation.REQUIRES_NEW

```text
호출 2: methodB (T.X_B) ── pending(일시 중지) ──────────── 재개 → commit/rollback
                └→ methodA (새로운 T.X_A) → commit/rollback
```

- **별개의 T.X 구성**: T.X_A에서 rollback 발생해도 **T.X_B에 영향을 주지 않음**

### Propagation.NESTED

```text
호출 2: methodB (T.X_B) ─ SavePoint ─ methodA (T.X_B') ─ ...
```

- 기존 T.X 안에 **SavePoint**로 T.X_B' 구성
- T.X_B'가 롤백되면 **SavePoint까지만 롤백**되고 부모는 커밋
- 반대로 **부모가 롤백되면 T.X_B'도 롤백**
- **JPA에서는 지원하지 않음**

| 비교 | REQUIRED | REQUIRES_NEW | NESTED |
|---|---|---|---|
| 내부 T.X | 바깥 T.X에 참여(하나) | 완전히 별개 | 바깥 T.X 안의 SavePoint |
| 내부 rollback | 전체 rollback | 바깥에 영향 없음 | SavePoint까지만 |
| 바깥 rollback | 전체 rollback | 내부는 이미 commit됨 | 내부도 rollback |

---

## 독립성 수준 (isolation)

- 하나의 데이터에 **T.X가 병행 처리**될 경우 각 T.X의 독립성 수준 결정

| 독립성 수준 | 의미 |
|---|---|
| `ISOLATION_READ_UNCOMMITTED` | 다른 T.X가 변경하고 **아직 commit 하지 않은 데이터도 읽음** |
| `ISOLATION_READ_COMMITTED` | 다른 T.X가 변경했지만 **commit 하지 않은 자료는 읽지 않음** |
| `ISOLATION_REPEATABLE_READ` | T.X 안에서 여러 번 읽을 때 다른 T.X가 도중에 값을 갱신해도 **같은 값 반환** |
| `ISOLATION_SERIALIZABLE` | T.X를 **하나씩 순서대로** 처리 |
| `ISOLATION_DEFAULT` | 데이터베이스가 제공하는 기본 독립성 수준 이용 |

```text
READ_UNCOMMITTED → READ_COMMITTED → REPEATABLE_READ → SERIALIZABLE
성능 높음  ◀──────────────────────────────────────────▶  안정성 높음
```

- 일반적으로 **`ISOLATION_DEFAULT`** 사용 — 개별 프로그래머 영역이 아니라 **DBA의 결정 사항**
  - Oracle 기본: `READ_COMMITTED`
  - MySQL 기본: `REPEATABLE_READ`

---

## 기타 속성

### timeout

- T.X가 취소되는 **만료 시간**, **초 단위** 지정

### readOnly

- 읽기 전용 속성이지만 DML 사용 시 **반드시 오류가 나는 것은 아님**
  - DB, 사용 기술마다 다름: MyBatis에서는 오류 없음, JPA에서는 예외 발생
- JPA 등 일부 프레임워크에서 **snapshot을 만들지 않는 등 최적화**에 사용 가능
  - MySQL은 snapshot을 사용하지 않으므로 아예 관리 안 하는 것이 유리할 수도
- **케이스 바이 케이스** — 사용하는 기술에 따라 적절히 사용

```java
@Transactional(readOnly = true, timeout = 10)
public List<Member> search(SearchCondition condition) { ... }
```

---

## AOP 명시적 T.X 처리

### Spring AOP의 한계

- **Spring Bean에 대해서만** 적용 가능
- **Proxy 기반**으로 동작
  - 같은 클래스 안에서 methodA가 methodB를 호출한다면?
  - proxy 밖에서 보면 **methodA만 호출**된 것
  - 내부에서 호출되는 methodB에는 **AOP가 적용될 수 없음**

```text
           proxy                          target
methodA(){ aop 적용 → target.methodA() } → methodA(){ methodB(); }  ← this.methodB(): proxy 안 거침
methodB(){ aop 적용 → target.methodB() }   methodB(){ // do something }
pointcut: execution(* meth*(..))  → methodB도 대상이지만 내부 호출이라 적용 안 됨
```

- 순수 자바(JDK 동적 프록시)로 확인

```java
import java.lang.reflect.*;

public class SelfInvocationDemo {
    interface Service { void methodA(); void methodB(); }

    static class ServiceImpl implements Service {
        public void methodA() {
            System.out.println("  target.methodA()");
            methodB();                 // this.methodB() - proxy 를 거치지 않음
        }
        public void methodB() { System.out.println("  target.methodB()"); }
    }

    public static void main(String[] args) {
        Service target = new ServiceImpl();
        Service proxy = (Service) Proxy.newProxyInstance(
            Service.class.getClassLoader(), new Class<?>[]{Service.class},
            (p, m, a) -> {
                System.out.println("[aop 적용] " + m.getName());
                return m.invoke(target, a);
            });

        System.out.println("1) proxy.methodA()");
        proxy.methodA();
        System.out.println("2) proxy.methodB()");
        proxy.methodB();
    }
}
// 결과
// 1) proxy.methodA()
// [aop 적용] methodA
//   target.methodA()
//   target.methodB()        <- 내부 호출: aop 적용 안 됨
// 2) proxy.methodB()
// [aop 적용] methodB
//   target.methodB()
```

### 명시적 T.X 처리 필요

- AOP 기반 T.X는 **proxy 밖에서 메서드를 호출하는 경우에만** 동작(around 적용)

```text
Client ──update──> Proxy ── T.X 시작 (AOP around) ──> Service.update ── T.X 종료
Client ──somemethod──> Proxy ──> Service.somemethod ──> (내부에서) update   : TX 적용 안 됨
```

- Proxy를 통해 호출된 `update` → `@Transactional` 처리 대상
- 내부에서 호출된 `update` → Proxy를 거치지 않으므로 `@Transactional`과 무관
- → **소스 코드에서 명시적으로 T.X 처리** 필요

### 명시적 T.X 처리 — PlatformTransactionManager 직접 사용

1. `PlatformTransactionManager` 주입
2. `DefaultTransactionDefinition`으로 트랜잭션 속성(전파 등) 설정
3. `txManager.getTransaction(def)` → `TransactionStatus` 획득(T.X 시작)
4. 성공하면 `commit(status)`, 실패하면 `rollback(status)`

```java
@Autowired
private PlatformTransactionManager txManager;      // TransactionManager 획득

@Override
public int joinProgrammingTX(Member member) {
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();   // transaction 속성 설정
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

    TransactionStatus status = txManager.getTransaction(def);   // TransactionStatus 획득
    int result = -1;
    try {
        // insert 작업
        // update 작업
        txManager.commit(status);       // 성공 - commit
    } catch (RuntimeException e) {
        txManager.rollback(status);     // 실패 - rollback
    }
    return result;
}
```

---

## 참고

> 참고: 교재 REQUIRED 슬라이드의 `UnExpectedRollbackException` → 정확한 이름은 `UnexpectedRollbackException`. REQUIRES_NEW 슬라이드 제목의 `REQUIRED_NEW` → 정확한 상수는 `Propagation.REQUIRES_NEW`.

> 참고: 기본 rollback 대상은 `RuntimeException`과 **`Error`**. Checked Exception은 `rollbackFor`로 지정하지 않으면 commit됨.
