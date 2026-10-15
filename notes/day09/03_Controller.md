  # Controller

> 교재: 15기_자바전공_05_springmvc_1_0512 (전공) — 3. @Controller

> [← Spring MVC 개요](02_Spring_MVC_개요.md) · [day09 목록](../README.md) · [Controller 단위테스트 →](04_Controller_단위테스트.md)

---

## @Controller

### Controller

- **Handler의 한 종류** — MVC에서 클라이언트의 요청을 받아들이는 클래스
- Stereotype annotation인 **`@Controller`**로 표시 → 내부에 `@Component`가 있어 컴포넌트 스캔 대상

```java
@Target(ElementType.TYPE)
@Component
public @interface Controller { }
```

- 한 클래스에 **요청 처리 메서드를 여러 개** 포함

### @RequestMapping

- 요청 처리 메서드를 만들기 위한 애너테이션
- `value`(= `path`) 속성으로 처리할 경로 지정

```java
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequestMapping {
    @AliasFor("path")
    String[] value() default {};
    RequestMethod[] method() default {};
    String[] params() default {};
    // ...
}
```

| 붙이는 위치 | 의미 |
|---|---|
| **Type 레벨**(클래스) | 클래스의 모든 요청 처리 메서드 경로 앞에 붙는 **prefix** |
| **Method 레벨** | 그 메서드에만 적용되는 경로 |

```java
@Controller
@RequestMapping("/simple")
@Slf4j
public class SimpleController {

    @RequestMapping              // → /simple
    public String home1() {
        log.debug("home 1 called");
        return "index";
    }

    @RequestMapping("/hello")    // → /simple/hello
    public String home2() {
        log.debug("home 2 called");
        return "index";
    }
}
```

### 요청 방식(method) 지정

- `method` 속성으로 GET / POST 등을 구분해 매핑

```java
@RequestMapping(value = "/onlyget", method = RequestMethod.GET)
public String onlyGet() {
    return "index";
}
// 이 경로를 POST로 요청하면 → 405 Method Not Allowed
```

- **축약형**을 사용하자

| 축약형 | 같은 의미 |
|---|---|
| `@GetMapping` | `@RequestMapping(method = RequestMethod.GET)` |
| `@PostMapping` | `@RequestMapping(method = RequestMethod.POST)` |

```java
@Target(ElementType.METHOD)
@RequestMapping(method = RequestMethod.GET)
public @interface GetMapping { /* ... */ }
```

### 요청 처리 메서드의 동작

- **요청 처리 메서드(request handling method)**: URL을 기준으로 사용자 요청을 처리하는 메서드
  - `@RequestMapping`으로 요청(URL)과 메서드를 연결
- 동작 절차와 역할

| 순서 | 할 일 | 방법 |
|---|---|---|
| 1 | 요청 분석 | 파라미터 확인 |
| 2 | 비즈니스 로직 수행 | Service 연결 |
| 3 | 자료 저장 | `Model` 객체 활용 |
| 4 | View 이름 리턴 | 문자열 → ViewResolver가 실제 View로 |

```text
DispatcherServlet ── request dispatching ──► HandlerAdapter ──► @Controller(Handler)
       ▲                                                          1. 요청 분석
       │                                                          2. 비즈니스 로직(Service)
       │                                                          3. Model에 자료 저장
       └──────────────── 4. View 이름 리턴 ◄──────────────────────┘
       │
       ▼
  ViewResolver
```

### @RequestParam으로 파라미터 처리

- `HttpServletRequest`의 parameter 조회(`getParameter`)를 대신함
- 요청 파라미터 name과 **같은 이름의 변수**에 값을 매핑
  - `name` 속성으로 파라미터 이름 지정
  - name과 변수명이 같으면 `name` 생략 가능
- 타입은 primitive 또는 wrapper — 문자열이 자동 형 변환됨
- 값이 여러 개면 배열 또는 `List`/`Set`으로 받음

```java
@Target(ElementType.PARAMETER)
public @interface RequestParam {
    @AliasFor("value")
    String name() default "";                               // 요청 파라미터 이름
    boolean required() default true;                        // 필수 여부
    String defaultValue() default ValueConstants.DEFAULT_NONE;
}
```

- **`Model`**: View에서 쓸 데이터를 담는 공간 → **request scope**에 저장됨

### 간단한 MVC 처리

```java
@Service
public class SimpleService {
    public String echo(String data) {
        return "echo: " + data;
    }

    public int add(int a, int b) {
        return a + b;
    }
}
```

```java
// SimpleController — @RequiredArgsConstructor로 생성자 주입
private final SimpleService service;

@GetMapping("/echo")
public String echo(@RequestParam String msg, Model model) {
    String echoData = service.echo(msg);     // 2. 비즈니스 로직
    model.addAttribute("msg", echoData);     // 3. 자료 저장
    return "index";                          // 4. View 이름
}
```

```jsp
<!-- index.jsp -->
<form action="${root}/simple/echo">
    <input type="text" name="msg" />
    <output>${requestScope.msg}</output> <br>
    <button>echo</button>
</form>
<!-- 결과: Hello 입력 후 전송 → echo: Hello 표시 -->
```

### 다양한 응답의 형태 — forward / redirect

- **forward**: logical view name을 그대로 리턴

```java
@RequestMapping("/")
public String welcome(Model model) {
    model.addAttribute("message", service.sayHello());
    return "index";
}
```

- **redirect**: view name 앞에 `redirect:` 추가
  - 상대경로, 절대경로, 외부 사이트 모두 가능
  - `HttpServletResponse.sendRedirect()`와 달리 **`/`는 context-root** 기준

```java
return "redirect:target";                    // 상대경로
return "redirect:/target";                   // context-root 기준 절대경로
return "redirect:http://www.google.com";     // 외부 사이트

@RequestMapping("/redir")
public String redir(Model model) {
    return "redirect:/";
}
```

| | forward | redirect |
|---|---|---|
| 흐름 | 1. request `/` → 2. `welcome()`이 `index.jsp`로 forward → 3. response | 1. request `/redir` → 2. `redir()`이 `redirect:/` 응답 → 3. 브라우저가 `/` 재요청 → `welcome()` → `index.jsp` → 4. response |
| 요청 횟수 | 1번 | 2번 |
| 주소창 | 처음 URL 유지 | 새 URL로 바뀜 |
| Model(request scope) 데이터 | 유지 | 사라짐 |

### 다양한 응답의 형태 — JSON (@ResponseBody)

- 메서드 리턴 값을 View 이름이 아니라 **응답 본문(body)** 그대로 보냄
- 객체·Map은 내부적으로 **Jackson(jackson-databind)** 등이 JSON으로 변환

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBody { }
```

```java
@GetMapping("/json")
public @ResponseBody Map<String, Object> json() {
    return Map.of("name", "hong gil dong", "age", 10);
}
// 결과(응답 본문, 키 순서는 보장되지 않음)
// {"name":"hong gil dong","age":10}
```

---

## 참고

- `@RequestParam`의 `required` 기본값은 `true` → 파라미터가 없으면 400 Bad Request. 선택값이면 `required = false` 또는 `defaultValue` 지정
- `@RestController` = `@Controller` + `@ResponseBody` (클래스의 모든 메서드에 `@ResponseBody` 적용)
- `${root}`는 실습 프로젝트에서 context path를 담아 둔 변수(교재 예제 기준)
