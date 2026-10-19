# JPA 실습

인프런 김영한 JPA 강의를 따라 치는 폴더. 강의 노트는 [notes/jpa-basic](../notes/jpa-basic/README.md), [notes/jpa-practice1](../notes/jpa-practice1/README.md).

| 폴더 | 강의 | 빌드 | 시작 상태 |
|---|---|---|---|
| `ex1-hello-jpa/` | 자바 ORM 표준 JPA 프로그래밍 - 기본편 | Maven | 강의 제공 2024 시작 프로젝트 그대로 (`JpaMain`의 `//code` 자리부터 따라 치기) |
| `jpashop/` | 실전! 스프링 부트와 JPA 활용1 | Gradle | start.spring.io로 막 만든 빈 프로젝트 (web, thymeleaf, jpa, h2, lombok, validation) |

## IntelliJ에서 여는 법

`java-study` 전체를 열면 Maven/Gradle로 인식이 안 된다. **각 프로젝트 폴더를 따로** 연다.

- `File → Open → jpa/ex1-hello-jpa` (pom.xml 인식) / `File → Open → jpa/jpashop` (build.gradle 인식) → New Window
- 공통: `Project Structure`에서 JDK 17 이상, Lombok 플러그인 + `Enable annotation processing`
- jpashop: `Settings → Build Tools → Gradle` → Build and run using / Run tests using = **Gradle**

## H2 DB

| 프로젝트 | 최초 1번 (파일 생성) | 이후 접속 URL |
|---|---|---|
| ex1-hello-jpa | `jdbc:h2:~/test` | `jdbc:h2:tcp://localhost/~/test` |
| jpashop | `jdbc:h2:~/jpashop` | `jdbc:h2:tcp://localhost/~/jpashop` |

- 실행 전에 H2 서버(`h2.bat`)를 켜 둘 것
- ex1-hello-jpa는 H2 2.2.224, jpashop은 스프링 부트가 관리하는 H2 버전 → 설치한 H2 서버 버전과 너무 차이 나면 접속 오류

## 완성본 참고

- 강의 완성 소스는 `Downloads/jpashop-v20250325`에 그대로 있음 — 막힐 때 비교용
- 7장 웹 계층의 부트스트랩 파일(`static/css`, `static/js`)과 타임리프 템플릿은 완성본에서 복사해 와도 된다

## 커밋 루틴

`Ctrl + K` → `jpa: 오늘 한 것` → Commit and Push (빌드 결과물 `target/`, `build/`, `.gradle/`은 .gitignore 처리됨)
