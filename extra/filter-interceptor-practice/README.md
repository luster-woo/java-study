# filter-interceptor-practice

extra 선택 강의 실습 완성본 — [Filter 실습](../../notes/extra/04_Filter_실습.md) (실습 1·2) · [Interceptor 실습](../../notes/extra/06_Interceptor_실습.md) (7-A~D)

## 여는 법

- IntelliJ `File → Open → extra/filter-interceptor-practice` → New Window, JDK 17 이상, Lombok 플러그인 + `Enable annotation processing`
- `FilterInterceptorPracticeApplication` 실행 → http://localhost:8080
- 요청할 때마다 **IntelliJ 콘솔**에 찍히는 로그가 실습의 본체

| 계정 | 비밀번호 | role |
|---|---|---|
| admin | 1234 | ADMIN |
| user | 1234 | USER |

## 파일 위치

| 실습 | 내용 | 파일 |
|---|---|---|
| Filter 1 | 필터 체이닝 A→B→C | `filter/FilterA·B·C.java`, `config/FilterConfig.java` |
| Filter 2 | UTF-8 인코딩(전체), URI·처리시간 로깅(`/api/*`) | `filter/EncodingFilter.java`, `filter/LoggingFilter.java`, `config/WebConfig.java` |
| 7-A | 요청 시간 측정 | `interceptor/LoggingInterceptor.java` |
| 7-B | 세션 로그인 체크 → redirect | `interceptor/LoginInterceptor.java` |
| 7-C | 관리자 체크 → 403 | `interceptor/AdminInterceptor.java` |
| 7-D | 인터셉터 3개 등록·경로 지정 | `config/WebMvcConfig.java` |

## 등록 결과 한눈에

| 순서 | 이름 | 종류 | 경로 |
|---|---|---|---|
| 1 | EncodingFilter | Filter | `/*` |
| 2~4 | FilterA → B → C | Filter | `/*` |
| 5 | LoggingFilter | Filter | `/api/*` |
| ① | LoggingInterceptor | Interceptor | `/**` (단, `/css/**`, `/js/**`, `/resources/**` 제외) |
| ② | LoginInterceptor | Interceptor | `/board/**` |
| ③ | AdminInterceptor | Interceptor | `/board/delete/**` |

## 확인 순서

**1. `/` — 필터 체인과 인터셉터 위치**

```text
A: 필터 전처리
B: 필터 전처리
C: 필터 전처리
[요청 시작] /                         ← Interceptor preHandle (DispatcherServlet 안)
    >> HomeController.index()
[요청 완료] / → 15ms                  ← afterCompletion (JSP 렌더링까지 끝난 뒤)
C: 필터 후처리
B: 필터 후처리
A: 필터 후처리
```

- 필터 전처리는 순서대로, 후처리는 역순 (스택)
- 인터셉터 로그가 필터 사이에 "샌드위치"로 끼어 있음 → Filter가 바깥(서블릿), Interceptor가 안쪽(스프링 MVC)

**2. `/api/users` — LoggingFilter는 `/api/*`에만**

```text
A/B/C 전처리
[Filter 시작] /api/users
[요청 시작] /api/users
    >> ApiController.users()
[요청 완료] /api/users → 3ms
[Filter 종료] /api/users → 5ms
C/B/A 후처리
```

**3. `/css/style.css` — 필터는 걸리고 인터셉터는 안 걸림**

- `A/B/C` 로그만 나오고 `[요청 시작]`은 없음 (`excludePathPatterns("/css/**")`)

**4. 로그인 안 하고 `/board/list`**

- `[LoginInterceptor] ... → /user/login` 후 컨트롤러 로그 없이 로그인 페이지로 redirect(302)
- `/`를 먼저 열었다면 "세션 없음"이 아니라 **"loginUser 없음"** 이 찍힘 → JSP가 화면을 그리면서 세션을 만들어 두기 때문 (`<%@ page session="true" %>`가 기본값)

**5. `user`로 로그인 → 목록 정상 → 삭제 버튼**

```text
[요청 시작] /board/delete/1
  [LoginInterceptor] 로그인 체크 /board/delete/1
  [AdminInterceptor] 관리자 체크 /board/delete/1
  [AdminInterceptor] 관리자 아님 → 403
[요청 완료] /board/delete/1 → 2ms     ← preHandle이 false여도, 이미 통과한 앞 인터셉터의 afterCompletion은 호출됨
C/B/A 후처리
[요청 시작] /error                    ← sendError(403) 후 에러 페이지로 한 번 더 들어온 것
[요청 완료] /error → 10ms
```

- 화면은 `error/403.jsp`
- `/error` 요청에는 필터 A·B·C 로그가 없음 → `FilterRegistrationBean`은 기본으로 REQUEST 디스패치에만 걸림

**6. 로그아웃 → `admin`으로 로그인 → 삭제** → 컨트롤러까지 와서 삭제 후 목록으로

## 교재와 다르게 한 곳

| 어디 | 교재 | 여기 | 이유 |
|---|---|---|---|
| `EncodingFilter` | `setContentType("text/html;charset=UTF-8")` 포함 | 그 줄 주석 처리 | 필터가 Content-Type을 미리 정하면 `@RestController`가 JSON을 못 씀 → `/api/users` 500 (`No converter ... with preset Content-Type`) |
| 필터 order | A=1, B=2, C=3 / Encoding=1, Logging=2 | Encoding 1, A·B·C 2~4, Logging 5 | 실습 1·2를 한 프로젝트에 합치면서 겹치지 않게. 순서는 교재와 같음 |
| `AdminInterceptor` redirect | `"/user/login"` | `getContextPath() + "/user/login"` | 7-B와 맞춤 (노트 참고 항목) |
| `WebMvcConfig` | `@Autowired` 필드 주입, Login에 `.excludePathPatterns("/")` | 생성자 주입, exclude 생략 | 동작 같음 (노트 참고 항목) |
| 삭제 요청 | 경로만 `/board/delete/**` | `POST /board/delete/{id}` (목록의 버튼) | 삭제를 GET 링크로 두지 않으려고 |

## 참고

- 화면이 `/css/style.css`를 불러오므로, 페이지를 열 때마다 바로 뒤에 A·B·C 로그 한 묶음(인터셉터 로그는 없음)이 더 찍힘
- `/favicon.ico`를 브라우저가 알아서 요청해서 A·B·C·`[요청 시작]` 로그가 한 묶음 더 찍힐 수 있음 — 무시
- 콘솔 한글이 깨지면 `Help → Edit Custom VM Options`에 `-Dfile.encoding=UTF-8` 추가 후 재시작
