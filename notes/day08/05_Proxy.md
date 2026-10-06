# Proxy

> 교재: 15기_자바비전공_Framework(Back)_04_0506_AOP (비전공) — Proxy

> [← 관점 지향 프로그래밍](04_관점_지향_프로그래밍.md) · [day08 목록](../README.md) · [Spring AOP →](06_Spring_AOP.md)

## 학습 목표

- 프록시의 개념을 이해하고 설명할 수 있다.

---

## Proxy

### 프록시란

- 사전적 의미: **대리인**
- 프록시 서버: 클라이언트가 자신을 거쳐 다른 네트워크 서비스에 **간접적으로 접속**하게 해 주는 시스템·프로그램
- SW에서: 실제 핵심 로직이 실행되기 전 **요청을 가로채서** 부가 기능을 추가하거나 접근을 제어하는 **대리 객체**

```
Client ──요청──▶ Proxy ──위임──▶ Target(실제 객체)
                  │
                  └─ 앞뒤로 부가 기능 / 접근 제어
```

### 프로그래머와 SSAFY 교육생의 일과 — 공통점을 뽑아내자

- 두 사람의 하루: 핵심인 **코딩**만 다르고 앞뒤는 똑같음

| 순서 | 프로그래머 | SSAFY 교육생 | 구분 |
|---|---|---|---|
| 1 | 컴퓨터를 부팅한다 | 컴퓨터를 부팅한다 | 공통(부가) |
| 2 | 코딩 | 코딩 | **핵심 — 각자 다름** |
| 3 | Git에 Push 한다 | Git에 Push 한다 | 공통(부가) |
| 3' | (문제가 생기면) 조퇴를 한다 | (문제가 생기면) 조퇴를 한다 | 공통(부가) |
| 4 | 보람찬 하루를 마무리한다 | 보람찬 하루를 마무리한다 | 공통(부가) |

- 공통 부분을 **프록시**로 뽑고, 핵심(`coding()`)만 Target에 남김

### 프록시 패턴

- 프록시는 Target과 **같은 인터페이스**를 구현 → 클라이언트는 프록시인지 모르고 사용
- 프록시가 공통 부가 기능을 수행하고, 핵심은 `target.coding()`으로 **위임**

```java
import java.util.Random;

public class ProxyTest {
    public static void main(String[] args) {
        Person p1 = new PersonProxy(new Programmer());
        p1.coding();
        System.out.println("-----");
        Person p2 = new PersonProxy(new Ssafy());
        p2.coding();
    }
}

interface Person {
    void coding();
}

class Programmer implements Person {
    @Override
    public void coding() { System.out.println("[Programmer] 서비스 코드를 작성한다."); }
}

class Ssafy implements Person {
    @Override
    public void coding() { System.out.println("[Ssafy] 실습 코드를 작성한다."); }
}

class OuchException extends RuntimeException {
}

class PersonProxy implements Person {
    private Person target;

    PersonProxy(Person target) { this.target = target; }

    @Override
    public void coding() {
        System.out.println("컴퓨터를 부팅한다.");
        try {
            target.coding();                      // 핵심 로직은 Target에 위임
            if (new Random().nextBoolean())
                throw new OuchException();
            System.out.println("Git에 Push 한다.");
        } catch (OuchException e) {
            System.out.println("조퇴를 한다.");
        } finally {
            System.out.println("보람찬 하루를 마무리한다.");
        }
    }
}
// 결과 (Random이라 실행마다 Push/조퇴가 갈림, 예시)
// 컴퓨터를 부팅한다.
// [Programmer] 서비스 코드를 작성한다.
// 조퇴를 한다.
// 보람찬 하루를 마무리한다.
// -----
// 컴퓨터를 부팅한다.
// [Ssafy] 실습 코드를 작성한다.
// Git에 Push 한다.
// 보람찬 하루를 마무리한다.
```

- 이 try-catch-finally 위치가 그대로 다음 강의의 Advice 타입(`@Before`, `@AfterReturning`, `@AfterThrowing`, `@After`)에 대응

---

## Spring AOP의 동작 원리: Proxy

### Proxy 기반 Bean

- Spring은 **Proxy 기반의 Bean**으로 AOP를 구현
- 기본 동작: **CGLIB**(Code Generator Library)로 **클래스 기반** Proxy 생성 (Spring Boot 2.x+)
  - **Target 클래스를 상속**해서 Proxy를 만듦 → 인터페이스 불필요
- 인터페이스 기반의 **JDK Dynamic Proxy** 방식도 있음

```
  «interface» MemberService
          ▲ implements
  MemberServiceImpl                     : Target
          ▲ extends
  MemberServiceImpl$$SpringCGLIB$$0     : Proxy  ── target.method() 로 위임
```

| 방식 | 만드는 법 | 조건 |
|---|---|---|
| **CGLIB** (Boot 기본) | Target 클래스를 **상속** | 인터페이스 없어도 됨 |
| JDK Dynamic Proxy | Target의 **인터페이스를 구현** | 인터페이스 필요 |

### AOP가 관리 대상 Bean을 바꾸는 과정

```
빈 객체 생성
   │
   ▼
Pointcut 검사 (AOP 적용 대상인가?)  →  Proxy 생성 유무 결정
   │
   ├─ 대상 아님 → target 빈을 그대로 등록   → ctx.getBean() → target 빈
   └─ 대상임   → proxy 빈을 만들어 등록     → ctx.getBean() → proxy 빈
                (target → proxy 로 교체)
```

```java
// Proxy 내부 동작 개념 (CGLIB 기반)
public class MemberServiceImpl$$SpringCGLIB$$0 extends MemberServiceImpl {
    @Override
    public Member selectByEmail(String email) {
        // (이 사이사이에 AOP Advice가 삽입된 형태)
        // 1. 실제 Target 메서드에 위임(delegation)
        Member result = target.selectByEmail(email);
        // 2. 에러가 없다면 정상 결과 반환
        return result;
    }
}
```

### 내부 호출(Self-Invocation)은 지양

- 프록시와 target은 **별개의 객체** → target 안에서 자기 메서드를 부르면(`this.xxx()`) **프록시를 거치지 않음** → Advice 미적용

```java
@Service
public class MemberServiceImpl implements MemberService {
    public void registerMember() {
        saveMemberData();       // 실제로는 this.saveMemberData() — this는 target
    }

    @Transactional              // 트랜잭션 Pointcut을 적용하는 애너테이션
    private void saveMemberData() {
        // saveMemberData 핵심 로직
    }
}
```

- 순수 자바로 CGLIB 프록시 구조(상속 + target 위임)를 흉내 내 확인

```java
public class SelfInvocationTest {
    public static void main(String[] args) {
        MemberServiceImpl target = new MemberServiceImpl();
        MemberServiceImpl proxy = new MemberServiceImplProxy(target);   // 컨테이너가 주입해 주는 것은 proxy

        proxy.saveMemberData();      // 외부에서 직접 호출
        System.out.println("-----");
        proxy.registerMember();      // 내부 호출(self-invocation)
    }
}

class MemberServiceImpl {
    public void registerMember() {
        System.out.println("registerMember 핵심 로직");
        saveMemberData();            // this.saveMemberData() -> this는 target
    }

    public void saveMemberData() {
        System.out.println("saveMemberData 핵심 로직");
    }
}

// CGLIB가 만드는 프록시 개념: Target을 상속 + 실제 일은 target에 위임
class MemberServiceImplProxy extends MemberServiceImpl {
    private final MemberServiceImpl target;

    MemberServiceImplProxy(MemberServiceImpl target) { this.target = target; }

    @Override
    public void registerMember() {
        target.registerMember();     // 부가 기능 없는 메서드: 위임만
    }

    @Override
    public void saveMemberData() {   // 부가 기능(트랜잭션)이 걸린 메서드
        System.out.println("[TX] 시작");
        target.saveMemberData();
        System.out.println("[TX] 커밋");
    }
}
// 결과
// [TX] 시작
// saveMemberData 핵심 로직
// [TX] 커밋
// -----
// registerMember 핵심 로직
// saveMemberData 핵심 로직      ← 내부 호출이라 [TX] 없음
```

---

## @Configuration 프록시: 진짜 상속과 가로채기

- Spring은 `@Configuration` 클래스를 **그대로 쓰지 않음**
- CGLIB로 **서브클래스**를 만들고, Target 객체 없이 **프록시 자신(= 빈 객체)**이 `@Bean` 메서드 호출을 가로채 컨테이너를 제어
  - AOP 프록시: 프록시가 별도 target 객체에 위임 / `@Configuration` 프록시: `super.method()`로 **부모(원래 클래스) 메서드를 직접 호출**

```java
@Configuration   // ← CGLIB 프록시 적용
public class AppConfig {
    @Bean
    public KeyBoard keyboard() {
        return new KeyBoard();
    }

    @Bean
    public Developer developerA() {
        return new Developer(keyboard());   // keyboard() 재호출 → 프록시가 가로채 등록된 Bean 반환
    }
}
```

```
AppConfig$$...SpringCGLIB...  (AppConfig를 상속한 프록시)

keyboard() 호출
  ├─ 컨테이너에 이미 KeyBoard 빈이 있으면 → 등록된 빈 반환 (getBean)
  └─ 없으면 → super.keyboard() 호출(return new KeyBoard()) → 빈으로 등록 후 반환
```

- 그래서 `developerA()` 안에서 `keyboard()`를 다시 불러도 `KeyBoard`는 **하나(싱글톤)**로 유지
- `@SpringBootConfiguration`의 `proxyBeanMethods = true`가 바로 이 프록시를 쓰겠다는 설정 ([주요 Annotations](02_주요_Annotations.md))

---

## 참고

> 참고: 교재 18쪽 예시의 `@Transactional private void saveMemberData()`는 내부 호출이 아니어도 적용되지 않음. CGLIB 프록시는 상속으로 메서드를 재정의하는데 `private` 메서드는 재정의할 수 없음 → 프록시 대상 메서드는 `public`이어야 함(교재가 말하려는 핵심은 내부 호출 문제).

- `@Configuration` 프록시 클래스 이름은 Spring 5까지 `AppConfig$$EnhancerBySpringCGLIB$$...`, Spring 6(Boot 3)부터 `AppConfig$$SpringCGLIB$$0` 형태
- `@Transactional`과 JPA 지연 로딩(가짜 엔티티 객체)도 이 프록시 원리로 동작
- `ProxyTest`, `SelfInvocationTest` 두 예제는 이 sandbox(JDK 11)에서 실행해 결과 확인 (`Programmer`/`Ssafy`의 출력 문장은 교재 그림 대신 임의로 넣은 것)
