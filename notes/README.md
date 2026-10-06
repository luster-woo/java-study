# Java·Spring 2주 학습 노트

하루 6시간, 2주. 영상은 SSAFY 다시보기(전공·비전공) 위주, JPA는 인프런 기본편.
일자별 영상 링크와 체크리스트는 **Java·Spring 2주 플랜** 페이지에 있다.

**IntelliJ**
- [요약본](intellij.md) — 단축키 표, 한 장으로
- [실전 가이드](intellij-guide.md) — 상황별 사용법 18가지 (실행 설정·디버깅·Git 브랜치·충돌·JUnit·Spring 프로젝트 생성·Gradle·DB 연결·HTTP Client·Claude Code)

## 노트 구성

각 노트는 같은 순서로 되어 있다.

1. **오늘 볼 강의** — 순서대로
2. **핵심 개념** — Python과 다른 점 위주
3. **예제 코드** — 그대로 쳐서 실행해 볼 수 있는 코드
4. **자주 하는 실수**
5. **면접 질문** — 한두 문장으로 답할 수 있으면 통과
6. **오늘의 실습**

## 1주차 · Java

| 일 | 주제 | 노트 | 연습문제 |
|---|---|---|---|
| D1 | 기본 문법·배열 | [day01.md](day01.md) | [합계·다이아몬드·이웃 합](problems/day01.md) |
| D2 | 클래스와 객체, JVM 메모리, static | [day02.md](day02.md) | [은행 창구·가까운 두 점·시계](problems/day02.md) |
| D3 | 상속·접근제한자·다형성 | [day03.md](day03.md) | [도형·급여·중복 좌표](problems/day03.md) |
| D4 | 추상 클래스·인터페이스·제네릭·enum·annotation | [day04.md](day04.md) | [결제·제네릭 스택·요일](problems/day04.md) |
| D5 | 컬렉션·람다·Stream·Wrapper·Optional | [day05.md](day05.md) | [단어 빈도·괄호·성적표](problems/day05.md) |
| D6 | 예외·입출력·JSON·String·record | [day06.md](day06.md) | [계산기·압축·로그 분석](problems/day06.md) |

연습문제는 그날 문법을 쓰도록 직접 만든 문제다.

- **코드 작성 3문제**: 예제와 추가 테스트의 출력이 글자 하나까지 같으면 통과. 다 풀면 "D1P1 검토해줘"
- **코드 읽기 문제**: 출력 예측 3~4개 · 버그 찾기 2개 · 빈칸 채우기 2개. 실행하지 말고 먼저 손으로 답하기 (면접·필기 대비)

## 2주차 · Spring + JPA

| 일 | 주제 | 노트 |
|---|---|---|
| D7 | Spring 기초·DI·Lombok | 작성 예정 |
| D8 | Spring Boot·AOP(Proxy) | 작성 예정 |
| D9 | Spring MVC | 작성 예정 |
| D10 | REST·트랜잭션·JPA 시작 | 작성 예정 |
| D11 | JPA 영속성·매핑·연관관계 | 작성 예정 |
| D12 | 지연 로딩·N+1·Spring Data JPA | 작성 예정 |
| D13 | Spring Security·JWT | 작성 예정 |
| D14 | 마무리 실습 | 작성 예정 |

## 매일 루틴

1. 오늘 노트의 "핵심 개념"을 5분 훑고 강의 시작
2. 강의 코드는 `src/dayNN/` 패키지에 직접 따라 치기
3. 실습 → 연습문제 3개 (`src/practice/`), 여유 있으면 SWEA·프로그래머스
4. `Ctrl + K` → `dayNN: 오늘 한 것` → Commit and Push
