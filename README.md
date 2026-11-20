# java-study

Java와 Spring을 프로젝트에 쓰기 전에 공부한 노트와 실습 코드입니다. 2026년 10월 6일에 시작했고, 처음 계획은 하루 6시간씩 2주였는데 실제로는 약 3주가 걸렸습니다. Spring AI 실습은 11월에 추가했습니다.

학습 기록이라 직접 설계한 코드는 아닙니다. 실습 코드는 SSAFY 15기 교재와 인프런 JPA 강의를 따라 치며 작성했고, 연습문제만 그날 배운 문법을 쓰도록 직접 만들었습니다. 이 공부 뒤에 Java·Spring으로 만든 결과물은 [AudiSay](https://github.com/luster-woo/AudiSay)에 있습니다.

## 구성

| 폴더 | 내용 |
|---|---|
| [`notes/`](notes/README.md) | 강의 하나당 노트 하나. 일자별 목차는 `notes/README.md`에 있습니다 |
| `src/` | 1주차 Java 실습(day01~day06), Spring 기초 실습(day07~day10), 연습문제 풀이(`practice`) |
| [`jpa/`](jpa/README.md) | 인프런 JPA 강의 실습 — `ex1-hello-jpa`(기본편), `jpashop`·`jpashop-practice1`(활용 1편) |
| `security/` | Spring Security 실습 — [`security-practice`](security/security-practice/README.md)(인증·인가), [`jwt-practice`](security/jwt-practice/README.md)(JWT, refresh token) |
| [`ai/spring-ai-practice`](ai/spring-ai-practice/README.md) | Spring AI 실습 — ChatClient, Advisor, VectorStore, RAG, 대화 기억 |
| [`extra/filter-interceptor-practice`](extra/filter-interceptor-practice/README.md) | Filter와 Interceptor 실습 |

## 학습 순서

| 일차 | 주제 | 실습 코드 |
|---|---|---|
| D1 | 기본 문법·배열 | `src/day01` |
| D2 | 클래스와 객체, JVM 메모리, static | `src/day02` |
| D3 | 상속·접근제한자·다형성 | `src/day03` |
| D4 | 추상 클래스·인터페이스·제네릭·enum·annotation | `src/day04` |
| D5 | 컬렉션·람다·Stream, Wrapper·Optional | `src/day05` |
| D6 | 예외·입출력·JSON, String·record | `src/day06` |
| D7 | Spring 기초·DI·Lombok | `src/day07` |
| D8 | Spring Boot·AOP(Proxy) | `src/day08` |
| D9 | Spring MVC | `src/day09` |
| D10 | REST·트랜잭션·JPA 시작 | `src/day10` |
| D11 | JPA 영속성·매핑·연관관계 | `jpa/` |
| D12 | 지연 로딩·N+1·Spring Data JPA | `jpa/` |
| D13 | Spring Security·JWT | `security/` |
| D14 | Spring AI (선택) | `ai/` |

## 노트를 쓴 방식

- 기본은 SSAFY 15기 교재를 교재 순서대로 정리했습니다. 파일마다 맨 위에 출처 교재와 이전·다음 강의 링크가 있습니다
- 교재가 없는 항목은 공개 자료(Oracle·Spring·Hibernate 공식 문서 등)로 정리했고 목차에 따로 표시했습니다
- 교재가 틀렸다고 판단한 곳은 본문에 교재 내용을 두고 `> 참고:`로 바로잡았습니다
- 인프런 JPA 강의 두 편은 `notes/jpa-basic`, `notes/jpa-practice1`에 따로 정리했습니다
- IntelliJ 사용법은 [`notes/intellij.md`](notes/intellij.md)(단축키 요약)와 [`notes/intellij-guide.md`](notes/intellij-guide.md)(상황별 사용법)에 있습니다

## 실행 환경

- JDK 21. Spring 실습은 Spring Boot 3 기준입니다
- 저장소 전체를 IntelliJ로 열면 Maven·Gradle 프로젝트로 인식되지 않습니다. `jpa/`, `security/`, `ai/`, `extra/` 아래 프로젝트는 폴더를 따로 열어야 합니다
- 프로젝트별 여는 법과 필요한 준비(H2, Docker, API 키)는 각 폴더의 README에 있습니다. API 키는 저장소에 올리지 않았습니다
