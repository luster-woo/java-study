# Service Layer와 TX

> 교재: 15기_자바전공_09_spring_transaction_0519 (전공) — 2. Service Layer와 T.X

> [← RestClient](04_RestClient.md) · [day10 목록](../README.md) · [@Transactional →](06_Transactional.md)

---

## Service Layer의 T.X 관리

### Service Layer가 없을 때 T.X 관리의 문제

- **T.X(트랜잭션)** 는 **Commit(성공)** / **Rollback(실패)** 으로 종료됨
- commit, rollback 등은 **Connection 소속의 메서드** → 각 Connection이 관리
- 요구사항: 회원 정보 삭제 시 먼저 **주소 정보 삭제** 필요

```text
[Service 없이 DAO가 각자 Connection 사용]
AddressDao.deleteByMno()  ── Connection 1 ── commit (결과 반영)
MemberDao.delete()        ── Connection 2 ── 예외 발생!!
=> 주소만 지워지고 회원은 남음 → 이 삭제는 반만 성공한 것?
```

- 결론: **하나의 업무에는 하나의 Connection이 묶여야** 함

### Service Layer를 이용한 T.X 관리

- **Service Layer는 T.X 처리의 시작점** → Connection 관리 책임
- **Repository(DAO)들은 Connection을 주입받아 사용** → 하나의 Connection에서 동작

```text
MemberService.delete()  ← Connection 획득, T.X 시작
   ├─ AddressDao.deleteByMno(con, mno)
   └─ MemberDao.delete(con, mno)
                        → 모두 성공: commit / 하나라도 실패: rollback
```

### 단위테스트 (TODO: 16)

- 서비스의 `delete` 중간에 **일부러 예외**를 섞어 테스트
  - `1 / new Random().nextInt(2)` → 0이 나오면 `ArithmeticException`
- 기대 결과
  - 예외 없음 → 주소, 회원 **모두 null**(둘 다 삭제)
  - 예외 발생 → 주소, 회원 **모두 not null**(둘 다 남아 있어야 함 = rollback)

```java
// 원래 코드
@Override
public void delete(int mno) {
    aDao.deleteByMno(mno);
    mDao.delete(mno);
}

// 테스트용: 두 DAO 호출 사이에 50% 확률로 예외
@Override
public void delete(int mno) {
    aDao.deleteByMno(mno);
    int i = 1 / new Random().nextInt(2);
    mDao.delete(mno);
}
```

```java
@Test
public void deleteTest() {
    try {
        mService.delete(tAddr.getMno());
        log.debug("예외 없음 - 모두 null 기대");
        Assertions.assertNull(aDao.select(tAddr.getAno()));
        Assertions.assertNull(mDao.select(tMember.getEmail()));
    } catch (Exception ignore) {
        log.debug("예외 발생 - 모두 not null 기대: {}", ignore.getMessage());
        Assertions.assertNotNull(aDao.select(tAddr.getAno()));
        Assertions.assertNotNull(mDao.select(tMember.getEmail()));
    }
}
```

---

## 기존 Service Layer의 T.X 처리 문제점

### 사용 기술에 따라 T.X 처리 기술이 다름

- JDBC: `Connection`의 `setAutoCommit(false)` → `commit()` / `rollback()`

```java
@Override
public void delete(int mno) throws SQLException {
    Connection con = util.getConnection();
    try {
        con.setAutoCommit(false);
        aDao.deleteByMno(con, mno);
        mDao.delete(con, mno);
        con.commit();
    } catch (SQLException e) {
        con.rollback();
        throw e;
    } finally {
        util.close(con);
    }
}
```

- MyBatis: `SqlSession`의 `openSession(false)` → `commit()` / `rollback()`

```java
@Override
public void delete(int mno) throws SQLException {
    SqlSession session = sqlSessionFactory.openSession(false);   // auto commit false
    try {
        session.delete("deleteByMno", mno);
        session.delete("delete", mno);
        session.commit();
    } catch (SQLException e) {
        session.rollback();
        throw e;
    } finally {
        session.close();
    }
}
```

- 알겠는데... 너무 귀찮고 뻔함
  - **JDBC API에 완전 종속** → 적용 기술 변경 시(JDBC → MyBatis) 코드 재사용 불가
  - **횡단 관심사(Cross Cutting Concern)** 발생 — 모든 서비스 메서드에 같은 begin/commit/rollback 코드

---

## 스프링의 T.X 처리

### PlatformTransactionManager

- 스프링의 **트랜잭션 관리 추상화** 레벨 — DB 접근 기술에 따라 다양한 구현체 제공
- 기술이 바뀌어도 서비스 코드는 그대로 (스프링의 이런 특성 = **PSA**, Portable Service Abstraction)

| 구현체 | 사용 기술 |
|---|---|
| `DataSourceTransactionManager` | 일반적인 JDBC 기반 기술(MyBatis 등) |
| `JpaTransactionManager` | JPA를 사용하는 경우 |
| `JtaTransactionManager` | 분산 트랜잭션 처리 |
| … | |

```text
           PlatformTransactionManager
      ┌──────────────┼──────────────┐
DataSourceTM     JpaTM    …    JtaTM
```

### AOP에 의한 선언적 Transaction 처리

- T.X 처리 코드가 없어지고 **`@Transactional`로 T.X 처리를 선언**
- `@Transactional`의 역할: **Target 객체에 대한 proxy 생성** → **Around Advice** 적용
- proxy 객체의 동작
  - target 메서드 호출 **이전에 트랜잭션 시작**
  - target 메서드 **정상 종료 후 commit**
  - **RuntimeException 발생 시 rollback**

```text
호출자 ──delete(…)──> Proxy.delete(..)
                        ├ T.X begin            ┐
                        ├ target.delete(…)     │ PlatformTransactionManager
                        └ Commit or Rollback   ┘
                                 │
                        XXXServiceImpl  @Transactional delete(…)
```

- proxy 동작을 순수 자바(JDK 동적 프록시)로 흉내 낸 코드

```java
import java.lang.reflect.*;

public class TxProxyDemo {
    interface MemberService { void delete(int mno); }

    static class MemberServiceImpl implements MemberService {
        boolean fail;
        public void delete(int mno) {
            System.out.println("  주소 삭제: " + mno);
            if (fail) { int i = 1 / 0; }          // 중간에 예외
            System.out.println("  회원 삭제: " + mno);
        }
    }

    // @Transactional 이 만들어 주는 proxy 의 역할 (Around)
    static MemberService txProxy(MemberService target) {
        return (MemberService) Proxy.newProxyInstance(
            MemberService.class.getClassLoader(),
            new Class<?>[]{MemberService.class},
            (proxy, method, args) -> {
                System.out.println("T.X begin");
                try {
                    Object r = method.invoke(target, args);
                    System.out.println("commit");
                    return r;
                } catch (InvocationTargetException e) {
                    if (e.getCause() instanceof RuntimeException) System.out.println("rollback");
                    throw e.getCause();
                }
            });
    }

    public static void main(String[] args) {
        MemberServiceImpl target = new MemberServiceImpl();
        MemberService service = txProxy(target);

        service.delete(1);
        target.fail = true;
        try {
            service.delete(2);
        } catch (ArithmeticException e) {
            System.out.println("호출자가 받은 예외: " + e.getClass().getSimpleName());
        }
    }
}
// 결과
// T.X begin
//   주소 삭제: 1
//   회원 삭제: 1
// commit
// T.X begin
//   주소 삭제: 2
// rollback
// 호출자가 받은 예외: ArithmeticException
```

### Service 구현체

- Service에서 DAO 호출 시 **Connection을 전달하지 않음**
- `@Transactional`을 활용한 **선언적 트랜잭션 처리** → 트랜잭션 관련 코드 삭제
- 비즈니스 로직만 남음

```java
// before - 직접 T.X 처리
@Override
public void registAddress(Address address) {
    Connection con = util.getConnection();
    try {
        con.setAutoCommit(false);
        dao.insert(con, address);
        con.commit();
    } catch (SQLException e) {
        con.rollback();
        throw e;
    } finally {
        util.close(con);
    }
}

// after - 선언적 T.X 처리
@Override
@Transactional
public void registAddress(Address address) {
    dao.insert(address);
}
```

---

## 서비스의 예외 처리

### @Controller

- Repository에서 **`SQLException`이 전파되지 않음** → 스프링의 **`DataAccessException`** 으로 대체
- `DataAccessException`은 RuntimeException 계열 → `throws` 없이도 되고, 필요한 곳(Controller)에서 잡아 처리

```java
@PostMapping("/regist-member")
private String registMember(@ModelAttribute Member member, Model model, HttpSession session) {
    try {
        // ...
    } catch (DataAccessException e) {
        e.printStackTrace();
        model.addAttribute("error", e.getMessage());
        return "member/member-regist-form";
    }
}
```

---

## 참고

> 참고: 교재 Service 구현체 슬라이드의 `@Transaction`은 `@Transactional`의 오타.

> 참고: 비교용 before 코드는 개념 설명용이라 그대로는 컴파일되지 않음. MyBatis `SqlSession` 메서드는 `SQLException`을 던지지 않으므로(unchecked `PersistenceException`) `catch (SQLException e)`는 컴파일 오류이고, before `registAddress`는 `con.rollback()` 등이 던지는 `SQLException`에 대한 `throws` 선언이 빠져 있음.

> 참고: 기본 rollback 대상은 `RuntimeException`과 함께 **`Error`** 도 포함(Checked Exception은 commit). 위 데모의 proxy는 RuntimeException만 흉내 냄.
