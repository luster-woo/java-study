# Filter 실습

> 교재: 15기_자바비전공_Framework(Back)_07_0512_Interceptor (비전공) — Filter 실습 · 선택 강의

> [← Filter](03_Filter.md) · [extra 목록](../README.md) · [Interceptor →](05_Interceptor.md)

## 학습 목표

- Filter 개념을 이해하고 활용할 수 있음

---

## Filter 실습

### 실습 1 — 필터 체이닝(FilterChain)

- 여러 필터를 체인 형태로 연결해서 등록할 수 있음
- `chain.doFilter()`를 기준으로 **앞은 전처리(Before), 뒤는 후처리(After)**
- 필터 3개(FilterA, FilterB, FilterC)를 등록해 호출 순서를 확인

```java
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<FilterA> filterARegistration() {
        FilterRegistrationBean<FilterA> registration = new FilterRegistrationBean<>();
        registration.setFilter(new FilterA());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<FilterB> filterBRegistration() { ... }   // setOrder(2)

    @Bean
    public FilterRegistrationBean<FilterC> filterCRegistration() { ... }   // setOrder(3)
}
```

```java
public class FilterA implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("A: 필터 전처리");
        chain.doFilter(request, response);
        System.out.println("A: 필터 후처리");
    }
}
// FilterB, FilterC도 같은 모양 (출력 글자만 B, C)
```

- 요청 하나가 들어왔을 때 콘솔

```text
A: 필터 전처리
B: 필터 전처리
C: 필터 전처리
(DispatcherServlet → Controller 실행)
C: 필터 후처리
B: 필터 후처리
A: 필터 후처리
```

- 흐름 그림

```text
request  ──▶ Filter A ──▶ Filter B ──▶ Filter C ──▶ DispatcherServlet
             (전처리)     (전처리)     (전처리)            │
response ◀── Filter A ◀── Filter B ◀── Filter C ◀──────────┘
             (후처리)     (후처리)     (후처리)
```

- 전처리는 **등록 순서대로**, 후처리는 **역순으로** 실행됨(스택처럼 쌓였다가 풀림)

### 실습 2 — EncodingFilter + LoggingFilter 등록

| 필터 | 하는 일 | 적용 경로 |
|---|---|---|
| `EncodingFilter` | 모든 요청을 UTF-8로 강제 | 전체 URL |
| `LoggingFilter` | URI와 처리 시간을 콘솔에 기록 | `/api/*`만 |

- 등록은 `WebConfig.java`에서 `FilterRegistrationBean`으로

```java
// EncodingFilter.java — UTF-8 인코딩 처리
public class EncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 전처리 — 인코딩 설정
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 다음 필터 / 서블릿 호출
        chain.doFilter(request, response);
    }
}
```

```java
// LoggingFilter.java — 요청/응답 로깅 출력
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        long start = System.currentTimeMillis();
        String uri = ((HttpServletRequest) req).getRequestURI();
        System.out.println("[Filter 시작] " + uri);                    // 전처리

        chain.doFilter(req, res);                                      // ← 이 시점 전후

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("[Filter 종료] " + uri + " → " + elapsed + "ms");   // 후처리
    }
}
```

- `ServletRequest`에는 URI 정보가 없어서 `HttpServletRequest`로 **형변환** 후 `getRequestURI()` 호출
- 처리 시간 = `chain.doFilter()` 앞뒤 시각의 차이 → 뒤쪽 필터·컨트롤러·뷰까지 걸린 시간 전체

- 교재 설명대로 `WebConfig.java`에 등록하면 이런 모양

```java
@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<EncodingFilter> encodingFilter() {
        FilterRegistrationBean<EncodingFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new EncodingFilter());
        bean.addUrlPatterns("/*");          // 전체 URL
        bean.setOrder(1);                   // 인코딩을 가장 먼저
        return bean;
    }

    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LoggingFilter());
        bean.addUrlPatterns("/api/*");      // /api/ 아래만
        bean.setOrder(2);
        return bean;
    }
}
```

```text
// GET /api/users 요청 시 콘솔 (시간은 실행마다 다름)
[Filter 시작] /api/users
[Filter 종료] /api/users → 12ms
```

---

## 참고

- 교재 실습 2는 등록 위치(`WebConfig.java`, `FilterRegistrationBean`)만 말하고 등록 코드는 없음 → 위 `WebConfig`는 실습 1의 등록 방식을 그대로 옮겨 적은 것
- `FilterRegistrationBean.addUrlPatterns()`는 서블릿 URL 패턴 규칙을 따름: `/*`는 **모든 하위 경로까지** 포함(`/api/*` → `/api/users`, `/api/users/1` 모두 해당). Interceptor 경로 패턴(`/*`는 한 단계, `/**`는 전체)과 규칙이 다름
- Spring Boot는 기본으로 `CharacterEncodingFilter`(UTF-8)를 등록해 둠(`server.servlet.encoding.*` 설정) → 실무에서는 인코딩 필터를 직접 만들 일이 드묾. 실습은 필터 동작 원리 확인용
