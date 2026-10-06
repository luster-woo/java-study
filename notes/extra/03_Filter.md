# Filter

> 교재: 15기_자바비전공_Framework(Back)_07_0512_Interceptor (비전공) — Filter · 선택 강의

> [← 종합실습](02_종합실습.md) · [extra 목록](../README.md) · [Filter 실습 →](04_Filter_실습.md)

## 학습 목표

- Filter 개념을 이해하고 활용할 수 있음

---

## Filter

### Filter란?

- 클라이언트 요청이 **DispatcherServlet에 도달하기 전에 가로채서** 전·후 처리를 하는 객체(서블릿 영역)
- 여러 필터를 **체인(chain)** 으로 연결해 차례로 동작시킬 수 있음 → **Filter Chain**
  - 각 필터는 `chain.doFilter()`로 다음 필터(마지막이면 DispatcherServlet)에 요청을 넘김

```text
Client Request
    │
    ▼
 aFilter ──chain.doFilter()──▶ bFilter ──chain.doFilter()──▶ cFilter ──chain.doFilter()──▶ DispatcherServlet
                                                                                                  │
Client Response ◀──────────────── (응답은 역순으로 필터를 거쳐 돌아옴) ◀──────────────────────────────┘
```

### Filter 활용

| 용도 | 하는 일 |
|---|---|
| **인코딩 처리** | 요청·응답의 문자 인코딩 통일(웹은 주로 UTF-8) |
| **인증/인가 확인** | 인증받은 사용자인지, 이 요청을 할 권한이 있는지 확인 |
| **로깅** | 요청·응답의 시간과 정보를 기록 → 추적 가능 |
| **보안 확인** | 요청을 검사해 악의적인 코드나 공격 차단 |

### Legacy Servlet 환경 vs Spring Boot — 같은 인터페이스, 다른 등록

| 구분 | 전통 Servlet 환경 | Spring Boot 환경 |
|---|---|---|
| Filter 인스턴스를 만드는 쪽 | **서블릿 컨테이너**가 직접 생성·관리 | 내장 서블릿 컨테이너(Embedded Tomcat)를 Spring이 관리 → **Spring이 Filter를 생성** |
| 등록 방법 | `web.xml`의 `<filter>` 태그, `@WebFilter` | `@Component`, JavaConfig의 `FilterRegistrationBean` |
| Spring Bean 주입 | 어려움(Spring 컨텍스트 바깥에 있음) | 가능(Filter 자체가 Bean) |

- 정리: Filter의 **관리**는 Spring(IoC Container)이 하지만, **실행**은 Servlet 시절과 똑같이 Spring 바깥 영역(DispatcherServlet 앞)에서 일어남

```text
클라이언트 ──▶ [ SERVLET 영역: Filter ] ──▶ [ SPRING 영역: DispatcherServlet(Front Controller) ... ]
```

### Filter 등록 방법 ① @Component

- 매우 단순하고 직관적인 방법
- `@Order(n)`: 필터 적용 순서(Integer 범위, **값이 낮을수록 먼저**)
  - 순서를 안 주면 `Integer.MAX_VALUE`(가장 낮은 순위)로 배정
- 단점: **URL 패턴을 지정할 수 없음** → 무조건 모든 요청(`/*`)에 적용

```java
package com.ssafy.mvc.filter;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@Component
@Order(1)                        // 필터 적용 순서
public class MyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("서블릿 전1");         // 전처리
        chain.doFilter(request, response);        // 다음 필터 / DispatcherServlet
        System.out.println("서블릿 후1");         // 후처리
    }
}
```

### Filter 등록 방법 ② FilterRegistrationBean

- `FilterRegistrationBean`: 서블릿 필터를 **프로그래밍 방식으로 등록·설정**해 주는 Bean
- **세밀한 라우팅(URL 패턴)과 우선순위 제어**가 가능

```java
package com.ssafy.mvc.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ssafy.mvc.filter.MyFilter;

// 설정 파일들의 모음집
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<MyFilter> myfilter() {
        FilterRegistrationBean<MyFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new MyFilter());     // 어떤 필터를 등록할지
        registrationBean.addUrlPatterns("/hello");      // 적용할 경로
        registrationBean.setOrder(1);                   // 순서

        return registrationBean;
    }
}
```

| 비교 | `@Component` (+ `@Order`) | `FilterRegistrationBean` |
|---|---|---|
| 작성량 | 적음 | 설정 클래스가 따로 필요 |
| URL 패턴 | 지정 불가(모든 요청) | `addUrlPatterns()`로 지정 |
| 순서 | `@Order` | `setOrder()` |

---

## 참고

- 교재 ② `FilterRegistrationBean` 쪽에도 "단점: URL 패턴 지정이 불가능해 모든 요청에 적용됨"이 적혀 있는데, ① 쪽 문장이 그대로 남은 것으로 보임 — 같은 쪽 코드처럼 `addUrlPatterns("/hello")`로 **경로를 지정할 수 있는 것이 ②의 장점**
- 교재는 Filter를 "서블릿 객체"라고 부르지만, 정확히는 Servlet 스펙의 컴포넌트(`jakarta.servlet.Filter`)이고 서블릿(`Servlet`) 자체는 아님
- ①과 ②에 같은 `MyFilter`를 동시에 쓰면 안 됨: `@Component`로 Bean 등록된 필터는 Spring Boot가 자동으로 모든 요청에 등록하므로, ②에서 또 등록하면 두 번 실행될 수 있음 → ②를 쓸 필터에는 `@Component`를 붙이지 않음
- Spring Boot 3.x는 `jakarta.servlet.*` 패키지를 사용(2.x까지는 `javax.servlet.*`)
