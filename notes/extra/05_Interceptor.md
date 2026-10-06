# Interceptor

> 교재: 15기_자바비전공_Framework(Back)_07_0512_Interceptor (비전공) — Interceptor · 선택 강의

> [← Filter 실습](04_Filter_실습.md) · [extra 목록](../README.md) · [Interceptor 실습 →](06_Interceptor_실습.md)

## 학습 목표

- Interceptor의 개념을 이해하고 활용할 수 있음

---

## Interceptor

### Interceptor란?

- **`HandlerInterceptor` 인터페이스**를 구현한 객체
- 요청을 처리하는 과정에서 요청을 **가로채서** 처리
- **DispatcherServlet과 Controller 사이**에서 동작
- 접근 제어(Auth), 로그(Log)처럼 비즈니스 로직과 구분되는 **반복적·부수적인 로직**을 처리

| 메서드 | 호출 시점 | 특징 |
|---|---|---|
| `preHandle()` | 컨트롤러 실행 **전** | `true` → 계속 진행, `false` → 요청 중단 |
| `postHandle()` | 컨트롤러 실행 **후** | `ModelAndView` 수정 가능 |
| `afterCompletion()` | 뷰 렌더링 **완료 후** | 예외가 나도 호출 → 예외 처리 + 리소스 정리 |

### Interceptor 흐름

```text
DispatcherServlet ──preHandle──▶ Controller(Handler)
        │                              │
        │◀──────── postHandle ─────────┘
        │
        ▼
      View ──▶ afterCompletion
```

### preHandle

```java
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
```

- Controller(핸들러) 실행 **이전**에 호출
- `true` 반환 → 계속 진행 / `false` 반환 → 요청 종료(컨트롤러까지 가지 않음)

### postHandle

```java
@Override
public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        @Nullable ModelAndView modelAndView) throws Exception {
```

- Controller 실행 **후** 호출
- 정상 실행 뒤 추가 기능을 넣을 때 사용
- Controller에서 **예외가 나면 실행되지 않음**

### afterCompletion

```java
@Override
public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
        @Nullable Exception ex) throws Exception {
```

- 뷰 렌더링이 끝난 뒤 **항상** 실행
- Controller에서 예외가 나면 **네 번째 파라미터 `ex`** 로 전달됨(없으면 `null`)
- 그래서 발생한 예외, 실행 시간 같은 것을 기록하는 **후처리**에 주로 사용

### HandlerInterceptor 구현 예시

- 세 메서드 모두 **default 메서드** → 필요한 것만 골라 오버라이드

```java
@Component
public class MyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        System.out.println("[preHandle]");
        return true;    // true = 진행, false = 중단
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("[postHandle]");
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("[afterCompletion]");
    }
}
// 정상 요청 1번의 결과
// [preHandle]
// [postHandle]
// [afterCompletion]
```

### Interceptor 등록 (설정 파일)

- `WebMvcConfigurer`의 **`addInterceptors()`** 메서드로 등록
- 매핑할 URL을 지정할 수 있고, **제외할 URL**도 지정할 수 있음

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 인터셉터 등록
    }
}
```

### 경로 패턴 와일드카드

| 패턴 | 의미 | 매칭 예시 |
|---|---|---|
| `?` | 임의의 글자 **1개** | `/a`, `/b` (O) / `/ab` (X) |
| `*` | 0개 이상의 임의 글자 (**한 경로 단계 안**) | `/board`, `/user` (O) / `/board/list` (X) |
| `**` | 0개 이상의 경로 단계 (**하위 경로 모두**) | `/board`, `/board/list`, `/board/1/detail` (O) |

- `/*` → 한 단계 경로만(`/board`, `/user` 등)
- `/**` → 모든 하위 경로(`/board`, `/board/list`, `/board/1/edit` 등) → Interceptor에서는 보통 **`/**`를 기본**으로 씀

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    // 모든 요청 (리소스 제외)
    registry.addInterceptor(loggingInterceptor)
            .addPathPatterns("/**")                 // 매핑 지정
            .excludePathPatterns(                   // 매핑 제외
                    "/resources/**",
                    "/css/**",
                    "/js/**");

    // 게시판 전체 (로그인 필요)
    registry.addInterceptor(loginInterceptor)
            .addPathPatterns("/board/**")
            .excludePathPatterns("/");

    // 관리자 전용 (삭제만)
    registry.addInterceptor(adminInterceptor)
            .addPathPatterns(
                    "/board/delete/**",
                    "/admin/**");
}
```

- 패턴 문법 공식 문서: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/util/pattern/PathPattern.html

### Interceptor 체이닝

- 여러 인터셉터를 동시에 등록할 수 있음
- **작성(등록) 순서대로** 동작

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    // 순서대로 등록
    registry.addInterceptor(aInterceptor).addPathPatterns("/**");
    registry.addInterceptor(bInterceptor).addPathPatterns("/**");
    registry.addInterceptor(cInterceptor).addPathPatterns("/**");
}
// 결과
// A : preHandle
// B : preHandle
// C : preHandle
// C : postHandle
// B : postHandle
// A : postHandle
// C : afterCompletion
// B : afterCompletion
// A : afterCompletion
```

```text
request  ──▶ a.preHandle ──▶ b.preHandle ──▶ c.preHandle ──▶ Handler
                                                               │
response ◀── a.post / a.after ◀── b.post / b.after ◀── c.post / c.after
```

- `preHandle`은 등록 순서대로, `postHandle`·`afterCompletion`은 **역순**

### Filter vs Interceptor

- **Filter**: 모든 HTTP 요청에 공통으로 적용할 **"낮은 층"** 처리에 적합
- **Interceptor**: Controller 단위의 판단이 필요한 **"높은 층"** 처리에 적합

| 구분 | Filter | Interceptor |
|---|---|---|
| 관리 주체 | Servlet Container | Spring Container |
| 동작 위치 | DispatcherServlet **이전** | Controller **전·후** |
| Spring Bean 접근 | 제한적 | 쉽게 가능 |
| 예외 처리 | Servlet 예외 처리 | `@ControllerAdvice`와 연계 |
| 주요 사용 | 인코딩, 보안 필터 | 인증·인가, 로깅, 권한 체크 |

- 한 줄 정리: **Filter는 Servlet의 영역, Interceptor는 Spring MVC의 영역**에서 동작

---

## 참고

- `afterCompletion`이 "항상" 호출되는 것은 **`preHandle`이 `true`를 반환한 인터셉터에 한해서**임. 체인 중간에서 `false`가 나오면 컨트롤러·`postHandle`은 건너뛰고, 그 앞에서 이미 `true`를 반환한 인터셉터들의 `afterCompletion`만 역순으로 호출됨
- 컨트롤러 예외를 `@ExceptionHandler`/`@ControllerAdvice`가 처리하면 `afterCompletion`의 `ex`는 `null`로 들어옴
- `@RestController`처럼 뷰 없이 응답 본문을 바로 쓰는 경우 `postHandle`의 `modelAndView`는 `null`이고, 응답이 이미 쓰인 뒤라 여기서 응답을 바꾸기 어려움
- 순서를 등록 순서와 다르게 하고 싶으면 `registry.addInterceptor(...).order(n)`으로 지정 가능(값이 낮을수록 먼저)
- 교재의 "DispacherServlet", "HandlerIntercepter"는 오타 → `DispatcherServlet`, `HandlerInterceptor`
