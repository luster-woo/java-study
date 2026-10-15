Spring Boot

> 교재: 15기_자바전공_03_springboot_0507 (전공) — 1. Spring Boot

> [day08 목록](../README.md) · [주요 Annotations →](02_주요_Annotations.md)

---

## Spring Boot

### Spring Boot란

- 또 다른 스프링이 아니라 **스프링 애플리케이션을 개발하는 템플릿**
- **설정 자동화**로 스프링 개발 속도 향상
- **단위 테스트 강화**로 프로젝트 안정성 강화
- 개발자는 설정보다 **비즈니스 로직에 집중**

| Spring Boot가 해 주는 것 | 예전엔 직접 하던 일 |
|---|---|
| 의존성 버전 관리 | 라이브러리마다 버전 맞추기 |
| 설정 자동화 | "DB 써야지" → DataSource 연결 설정 |
| 내장 WAS | "웹 서비스 해야지" → WAS(Tomcat) 설치·설정 |
| 프로덕션 레디, 단위 테스트 강화 ... | |

### 프로젝트 생성 (TODO: 01)

- `File > New > New Spring Starter Project`
- 패키징은 **jar** → standalone 애플리케이션. 웹도 jar로 됨(내장 Tomcat)
- 교재 버전: **Boot 3.5.11** = **Spring Core 6.2.16** 기반 (3.x.x 정식 버전 선택)
- 생성 화면에서 필요한 의존성(starter)을 체크해 등록

### 프로젝트 구조

```
프로젝트
├─ src/main/java          일반적인 자바 코드
├─ src/main/resources
│   ├─ static/            정적 웹 리소스 (index.html, css, js, 이미지)
│   ├─ templates/         동적 웹 리소스 (템플릿 엔진용, JSP는 src/main/webapp)
│   └─ application.properties   애플리케이션 설정 정보
├─ src/test/java          테스트 관련 소스 및 리소스
└─ pom.xml                프로젝트 구성 정보
```

- 정적인 `index.html`은 `src/main/resources/static/`에 만들면 됨 → welcome page로 등록

### 프로젝트 실행

- `Run As > Spring Boot Application`
- 또는 `Window > Show View > Other > Boot Dashboard` → 애플리케이션 start → Open Web Browser
- Tomcat을 따로 설치·설정하지 않았는데 웹 서버가 뜸 → **내장 Tomcat**

#### 로그 분석

| 로그 | 의미 |
|---|---|
| `TomcatWebServer : Tomcat initialized with port 8080 (http)` | 내장 Tomcat이 8080 포트로 초기화 |
| `StandardEngine : Starting Servlet engine: [Apache Tomcat/10.1.36]` | 서블릿 엔진 시작(Tomcat 10.1) |
| `Initializing Spring embedded WebApplicationContext` | 스프링 웹 컨테이너 초기화 |
| `WelcomePageHandlerMapping : Adding welcome page: class path resource [static/index.html]` | `static/index.html`을 welcome page로 등록 |
| `OptionalLiveReloadServer : LiveReload server is running on port 35729` | devtools의 LiveReload 서버 |
| `Tomcat started on port 8080 (http) with context path '/'` | 서버 시작 완료, context path는 `/` |

---

## application.properties

### Application의 property (TODO: 02)

- 애플리케이션 동작에 필요한 property를 **application.properties** 또는 **application.yml**로 작성
- 작성 방법만 다르고 프로그램 내부 동작은 같음
- `.properties` 작성법: `속성=값`, 키와 값은 `=`로 구분
- Eclipse(STS)에서는 `Text > Spring Properties File` 에디터로 열면 편함 (한글 깨짐이 신경 쓰이면 에디터·인코딩 확인)

```properties
# 기본적으로 속성=값 형태로 저장
manager.id=hong
manager.pass=1234
manager.age=30
# list 표현 (collection으로 매핑)
manager.roles=admin, manager, guest
```

### 코드에서 속성 활용 — `@Value`

- `@Value("${property_name}")`로 속성값을 필드에 주입
- 필요한 타입(`int`, `List<String>` 등)으로 **자동 형 변환**

```java
@SpringBootTest
class Fw03BootApplicationTests {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${manager.id}")
    private String id;

    @Value("${manager.age}")
    private int age;                 // "30" -> 30

    @Value("${manager.roles}")
    private List<String> roles;      // "admin, manager, guest" -> List

    @Test
    void valueTest() {
        Assertions.assertEquals(30, age);
        Assertions.assertEquals(List.of("admin", "manager", "guest"), roles);
        log.debug("id: {}", id);
    }
}
```

- `log.debug(...)`가 안 보이면 로그 레벨이 DEBUG보다 높은 것 → 아래 `logging.level.*`로 조절

### built-in property

- Spring Boot가 미리 정의한 속성들 — 필요할 때 재정의
- 목록: Spring Boot 공식 문서 Appendix "Common Application Properties"

| 구분 | Name | 설명 | 기본값 |
|---|---|---|---|
| Core | `logging.level.*` | 패키지별 로그 레벨 지정 | INFO |
| Server | `server.port` | 서버의 HTTP 포트 | 8080 |
| Server | `server.servlet.context-path` | 서버의 context root path | `/` |

```properties
logging.level.org.springframework=INFO
logging.level.com.ssafy=TRACE
```

---

## pom.xml

### pom.xml 분석 (부분)

```xml
<project>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>${SPRING_BOOT_VERSION}</version>
    </parent>
    <properties>
        <java.version>${JDK_VERSION}</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!-- spring-boot-devtools, spring-boot-starter-web, lombok, spring-boot-starter-test -->
    </dependencies>
</project>
```

- 일반 pom.xml과의 차이: **`spring-boot-starter-parent`를 parent로 상속** → 의존성 버전을 부모가 관리
- 의존성 추가 시 `<version>` 판단

```
필요한 라이브러리
  └─ Spring(Boot)이 버전을 관리하는가?
       ├─ YES → <version> 생략
       └─ NO  → <version> 명시
```

- **starter**: 관련 라이브러리 묶음. `spring-boot-starter-web` 하나로 Spring MVC + 내장 Tomcat 등이 함께 들어옴

---

## 참고

> 참고: 교재 9쪽 테스트 코드는 `@Value("${service.manager.age}")`, `@Value("${service.roles}")`로 되어 있지만 8쪽 properties의 키는 `manager.age`, `manager.roles`임. 키가 다르면 값을 못 찾아 컨텍스트 로딩이 실패하므로 둘을 맞춰야 함(위 코드는 `manager.*`로 맞춤).

> 참고: 교재 10쪽 표의 `server.servlet.contextpath`는 실제 속성명 `server.servlet.context-path`.

- 교재 버전: Spring Boot 3.5.11 / Spring Framework 6.2.16 / 내장 Tomcat 10.1 (Jakarta EE 패키지 `jakarta.*` 사용)
