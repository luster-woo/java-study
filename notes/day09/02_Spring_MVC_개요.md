# Spring MVC 개요

> 교재: 15기_자바전공_05_springmvc_1_0512 (전공) — 2. Spring@MVC 개요

> [← Spring 내부의 AOP들과 한계](01_Spring_내부의_AOP들과_한계.md) · [day09 목록](../README.md) · [Controller →](03_Controller.md)

---

## Spring MVC 개요

### Spring MVC란?

- **MVC 기반(Model 2)** 웹 애플리케이션을 만들기 위한 Spring Framework의 하위 모듈
- Spring Boot에서는 `spring-boot-starter-web` 하나로 추가

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

- **Front Controller 패턴** → `DispatcherServlet`
  - `/` 경로를 맡으므로 **모든 요청을 받음**
  - `action` 파라미터 방식이 아니라 **URL 기반**으로 요청 분기
- 주요 특징
  - 애너테이션 기반 → **Spring@MVC**
  - 다양한 view 기술 지원(Thymeleaf, Mustache, ..., JSP)
  - RESTful 웹 서비스 지원
  - 테스트가 쉬움

### Spring@MVC 구성 요소

- **DispatcherServlet** (`spring-webmvc`의 `org.springframework.web.servlet.DispatcherServlet`)
  - Spring@MVC에서 가장 중심 역할
  - Front Controller 패턴 → 클라이언트의 모든 request 접수
  - 실제 처리는 주로 다른 객체(Infrastructure Component)에게 맡김
- **Infrastructure Components** (Spring이 제공)

| 컴포넌트 | 하는 일(말로 하면) |
|---|---|
| **HandlerMapping** | "이런 request를 받았는데 어떤 Handler가 처리할 수 있나요?" |
| **HandlerAdapter** | "XXHandler에게 이 request 좀 처리해 달라고 해" |
| **ViewResolver** | "이런 뷰가 필요한데 찾아 주세요" |

- **Handler**: HTTP 요청을 처리할 수 있는 객체
- 프로그래머가 작성할 부분
  - model 영역(Service, Dao)
  - View page
  - Handler

### Spring MVC 이전의 웹 프로그래밍

- Front Controller(Servlet)가 `action` 파라미터를 보고 하위 메서드(Sub Controller 역할)를 **직접 호출**

```java
public String service(HttpServletRequest req, HttpServletResponse resp) {
    String action = req.getParameter("action");
    switch (action) {
        case "member-detail" -> memberDetail(req, resp);
        // ...
    }
    return action;
}

private void memberDetail(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    String email = request.getParameter("email");
    try {
        Member member = mService.selectDetail(email);
        request.setAttribute("member", member);
    } catch (SQLException e) {
        e.printStackTrace();
        request.setAttribute("alertMsg", e.getMessage());
    }
    forward(request, response, "/member/member-detail.jsp");
}
```

- 파라미터 꺼내기, request에 담기, forward 경로 지정까지 전부 직접 → Spring MVC가 이 부분을 대신해 줌

### Spring@MVC 처리 흐름

```text
 Client
   │ 1. request
   ▼
 DispatcherServlet ──2. Handler 문의 및 리턴──► HandlerMapping
   │
   │ 3. request 전달
   ▼
 HandlerAdapter ──4. request 처리 요청──► Handler(@Controller) ──5. 비즈니스 로직 수행──► Service
                                              │
                                              │ 6. View에 전달할 자료 저장 ──► Model (Request Scope)
   ◄──────────── 7. View 이름 전달 ──────────┘
 DispatcherServlet ──8. 이름 기반 View 문의 및 리턴──► ViewResolver
   │
   │ 9. 화면 표시 요청
   ▼
 View ──10. Model 정보 참조, 페이지 구성
   │
   ▼ 11. response
 Client
```

| 단계 | 누가 → 누구 | 내용 |
|---|---|---|
| 1 | Client → DispatcherServlet | 요청 |
| 2 | DispatcherServlet ↔ HandlerMapping | 처리할 Handler 찾기 |
| 3·4 | DispatcherServlet → HandlerAdapter → Handler | 요청 전달·처리 요청 |
| 5 | Handler → Service | 비즈니스 로직 |
| 6 | Handler → Model | 화면에 쓸 데이터 저장(request scope) |
| 7 | Handler → (Adapter) → DispatcherServlet | View 이름 리턴 |
| 8 | DispatcherServlet ↔ ViewResolver | 이름으로 실제 View 찾기 |
| 9·10 | DispatcherServlet → View | Model을 참조해 화면 구성 |
| 11 | → Client | 응답 |

- **Handler**: 클라이언트의 HTTP 요청을 실제로 처리하는 객체
  - 종류: `Controller`, `WebSocketHandler`, `HttpRequestHandler`, `ResourceHttpRequestHandler`
  - `Controller`는 `@Controller`로 작성
- 왜 이렇게 나눴나 → 각 단계(핸들러 찾기·실행·뷰 찾기)를 갈아끼울 수 있게 역할 분리

### 프로젝트 생성 및 구조

- Spring Boot 프로젝트를 만들 때 Web(Spring Web) 의존성 선택
- **Spring Boot는 JSP를 기본 지원하지 않음** → 번거롭지만 JSP 관련 설정은 직접 처리

| 위치 | 용도 |
|---|---|
| `src/main/resources/static` | 정적 웹 리소스(img, css, js, html) |
| `src/main/resources/templates` | Spring Boot가 지원하는 동적 뷰(Mustache, Thymeleaf 등) |
| `src/main/webapp/WEB-INF/views` | JSP를 위한 폴더 — 직접 만들고 `index.jsp` 작성 |

- 이전(서블릿 시절)에는 JSP를 `WEB-INF` 밖에 두기도 했지만, 이제는 `WEB-INF` 안에 둠 → 브라우저가 직접 접근 불가, 컨트롤러를 거쳐서만 보임

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>Insert title here</title>
</head>
<body>
    <h1>Hello Spring MVC</h1>
</body>
</html>
```

### JSP를 위한 의존성 추가

- `spring-boot-starter-web`에 들어 있는 `tomcat-embed-core`에는 **JSP 엔진이 없음** → `tomcat-embed-jasper` 추가
- JSTL은 spec(API)과 구현체를 둘 다 추가

```xml
<!-- JSP 처리를 위한 tomcat-embed-jasper -->
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
</dependency>
<!-- JSTL (spec) -->
<dependency>
    <groupId>jakarta.servlet.jsp.jstl</groupId>
    <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
</dependency>
<!-- JSTL (impl) -->
<dependency>
    <groupId>org.glassfish.web</groupId>
    <artifactId>jakarta.servlet.jsp.jstl</artifactId>
</dependency>
```

- 실습: `static`에 `common.css`를 만들어 `h1`을 파란색으로 → 정적 리소스 연결 확인

### View Resolver 설정

- Handler는 **View 이름**(예: `index`)만 리턴 → 특정 view 기술(JSP)에 종속된 부분은 **ViewResolver**가 맡음
- `index` → `/WEB-INF/views/` + `index` + `.jsp`

```text
DispatcherServlet ── request dispatching ──► HandlerAdapter ──► @Controller(Handler)
        ▲                                                            │
        └──────────────────── View 이름: index ◄────────────────────┘
        │
        ▼
   ViewResolver:  /WEB-INF/views/ + index + .jsp
```

- 방법 1: 명시적 빈 등록

```java
@Bean
public ViewResolver internalResourceViewResolver() {
    InternalResourceViewResolver resolver = new InternalResourceViewResolver();
    resolver.setPrefix("/WEB-INF/views/");
    resolver.setSuffix(".jsp");
    return resolver;
}
```

- 방법 2: `application.properties`

```properties
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp
```

### 본격적인 Spring MVC

- Spring MVC = 백엔드에서 지금까지 배운 것(서블릿, request/session, forward/redirect, JSP 등)을 **더 편하게 쓰는 것**

---

## 참고

> 참고: 교재의 `org.springframework.web.servlet.DispatchServlet`은 `DispatcherServlet`의 오타.

- Spring Boot 3.x는 Jakarta EE 기반 → JSTL도 `javax`가 아니라 `jakarta.servlet.jsp.jstl` 패키지를 씀
- Spring Boot 공식 문서의 JSP 제약: 실행 가능한 **jar**로 패키징하면 JSP가 동작하지 않음 → JSP를 쓰면 **war** 패키징 사용
