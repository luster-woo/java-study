# Java·Spring 2주 학습 노트

하루 6시간, 2주. 영상은 SSAFY 다시보기(전공·비전공) 위주, JPA는 인프런 기본편.
일자별 영상 링크와 체크리스트는 **Java·Spring 2주 플랜** 페이지에 있다.

**IntelliJ**
- [요약본](intellij.md) — 단축키 표, 한 장으로
- [실전 가이드](intellij-guide.md) — 상황별 사용법 18가지 (실행 설정·디버깅·Git 브랜치·충돌·JUnit·Spring 프로젝트 생성·Gradle·DB 연결·HTTP Client·Claude Code)

## 노트 구성

- `dayNN/` 폴더 안에 **플랜의 강의(영상) 하나당 파일 하나**. 파일 번호는 플랜 순서다.
- 기본은 **SSAFY 15기 교재**를 교재 순서대로 정리한 노트(TIL 형식). 각 파일 맨 위에 출처 교재와 이전·다음 강의 링크가 있다.
- ᴮ = SSAFY 교재가 없는 항목을 **공개 자료**(TCP School·점프 투 자바·Oracle·Spring·Hibernate 공식 문서)로 정리한 보충 노트. 인프런 JPA 주제(D10~D12)도 강의 내용이 아니라 공개 자료로 같은 주제를 정리한 것이다.
- ˢ = 플랜에서 **선택** 강의.
- 교재가 틀렸다고 판단한 곳은 본문에 교재 내용을 두고 `> 참고:`로 바로잡았다.
- `instanceof X x` 패턴 매칭(16+), `.formatted()`(15+), switch 식(14+), sealed(17+), record(16+) 예제는 JDK 21에서 돌아간다. Spring 코드는 Spring Boot 3 기준.

## 1주차 · Java

| 일 | 주제 | 강의별 노트 |
|---|---|---|
| D1 | 기본 문법·배열 | [01 Hello Java](day01/01_Hello_Java.md)<br>[02 변수와 자료형](day01/02_변수와_자료형.md)<br>[03 연산자](day01/03_연산자.md)<br>[04 제어문](day01/04_제어문.md)<br>[05 배열](day01/05_배열.md)<br>[06 다차원 배열](day01/06_다차원_배열.md) |
| D2 | 클래스와 객체, JVM 메모리, static | [01 객체지향 프로그래밍](day02/01_객체지향_프로그래밍.md)<br>[02 변수](day02/02_변수.md)<br>[03 메서드](day02/03_메서드.md)<br>[04 생성자](day02/04_생성자.md)<br>[05 JVM 메모리구조](day02/05_JVM_메모리구조.md)<br>[06 static 키워드](day02/06_static_키워드.md) |
| D3 | 상속·접근제한자·다형성 | [01 상속](day03/01_상속.md)<br>[02 메서드 재정의](day03/02_메서드_재정의.md)<br>[03 package_import](day03/03_package_import.md)<br>[04 제한자](day03/04_제한자.md)<br>[05 encapsulation](day03/05_encapsulation.md)<br>[06 polymorphism](day03/06_polymorphism.md)<br>[07 객체의 형변환](day03/07_객체의_형변환.md)<br>[08 Object 메서드 재정의](day03/08_Object_메서드_재정의.md) |
| D4 | 추상 클래스·인터페이스·제네릭·enum·annotation | [01 abstract class](day04/01_abstract_class.md)<br>[02 Interface](day04/02_Interface.md)<br>[03 Interface의 필요성](day04/03_Interface의_필요성.md)<br>[04 default_static_private](day04/04_default_static_private.md)<br>[05 generic](day04/05_generic.md)<br>[06 enum](day04/06_enum.md)<br>[07 annotation](day04/07_annotation.md) |
| D5 | 컬렉션·람다·Stream + Wrapper·Optional | [01 List 계열](day05/01_List_계열.md)<br>[02 Set 계열](day05/02_Set_계열.md)<br>[03 Map 계열](day05/03_Map_계열.md)<br>[04 정렬](day05/04_정렬.md)<br>[05 Lambda 표현식](day05/05_Lambda_표현식.md)<br>[06 표준함수형 인터페이스](day05/06_표준함수형_인터페이스.md)<br>[07 Stream API](day05/07_Stream_API.md)<br>[08 Wrapper 클래스](day05/08_Wrapper_클래스.md) ᴮ<br>[09 Optional](day05/09_Optional.md) ᴮ |
| D6 | 예외·입출력·JSON + String·record | [01 예외의 처리](day06/01_예외의_처리.md)<br>[02 try_catch_finally](day06/02_try_catch_finally.md)<br>[03 throws 활용](day06/03_throws_활용.md)<br>[04 사용자 정의 예외](day06/04_사용자_정의_예외.md)<br>[05 APPENDIX_부적절한 예외 처리](day06/05_APPENDIX_부적절한_예외_처리.md)<br>[06 노드스트림](day06/06_노드스트림.md)<br>[07 노드스트림 활용](day06/07_노드스트림_활용.md)<br>[08 보조스트림](day06/08_보조스트림.md)<br>[09 보조스트림 활용](day06/09_보조스트림_활용.md)<br>[10 JSON](day06/10_JSON.md)<br>[11 String](day06/11_String.md) ᴮ<br>[12 StringBuffer_StringBuilder](day06/12_StringBuffer_StringBuilder.md) ᴮ<br>[13 record](day06/13_record.md) ᴮ |

## 2주차 · Spring + JPA

| 일 | 주제 | 강의별 노트 |
|---|---|---|
| D7 | Spring 기초·DI·Lombok | [01 Framework](day07/01_Framework.md)<br>[02 SLF4j와 Logging](day07/02_SLF4j와_Logging.md)<br>[03 Junit](day07/03_Junit.md)<br>[04 의존성과 DI](day07/04_의존성과_DI.md)<br>[05 명시적 DI](day07/05_명시적_DI.md)<br>[06 묵시적 DI](day07/06_묵시적_DI.md)<br>[07 Appendix_Lombok](day07/07_Appendix_Lombok.md) |
| D8 | Spring Boot·AOP(Proxy) | [01 Spring Boot](day08/01_Spring_Boot.md)<br>[02 주요 Annotations](day08/02_주요_Annotations.md)<br>[03 Servlet Web To Spring Boot](day08/03_Servlet_Web_To_Spring_Boot.md)<br>[04 관점 지향 프로그래밍](day08/04_관점_지향_프로그래밍.md)<br>[05 Proxy](day08/05_Proxy.md)<br>[06 Spring AOP](day08/06_Spring_AOP.md) |
| D9 | Spring MVC | [01 Spring 내부의 AOP들과 한계](day09/01_Spring_내부의_AOP들과_한계.md)<br>[02 Spring MVC 개요](day09/02_Spring_MVC_개요.md)<br>[03 Controller](day09/03_Controller.md)<br>[04 Controller 단위테스트](day09/04_Controller_단위테스트.md)<br>[05 요청 처리 메서드의 파라미터](day09/05_요청_처리_메서드의_파라미터.md)<br>[06 예외 처리](day09/06_예외_처리.md)<br>[07 file upload](day09/07_file_upload.md) |
| D10 | REST·트랜잭션·JPA 시작 | [01 REST API](day10/01_REST_API.md)<br>[02 REST 서비스 작성](day10/02_REST_서비스_작성.md)<br>[03 Swagger](day10/03_Swagger.md)<br>[04 RestClient](day10/04_RestClient.md)<br>[05 Service Layer와 TX](day10/05_Service_Layer와_TX.md)<br>[06 @Transactional](day10/06_Transactional.md)<br>[07 JPA 소개](day10/07_JPA_소개.md) ᴮ<br>[08 JPA 시작하기](day10/08_JPA_시작하기.md) ᴮ |
| D11 | JPA 영속성·매핑·연관관계 | [01 영속성 관리](day11/01_영속성_관리.md) ᴮ<br>[02 엔티티 매핑](day11/02_엔티티_매핑.md) ᴮ<br>[03 연관관계 매핑 기초](day11/03_연관관계_매핑_기초.md) ᴮ |
| D12 | 지연 로딩·N+1·Spring Data JPA | [01 프록시와 연관관계 관리](day12/01_프록시와_연관관계_관리.md) ᴮ<br>[02 객체지향 쿼리 언어 중급](day12/02_객체지향_쿼리_언어_중급.md) ᴮ<br>[03 다양한 연관관계 매핑](day12/03_다양한_연관관계_매핑.md) ᴮ ˢ<br>[04 Spring Data JPA](day12/04_Spring_Data_JPA.md) ᴮ |
| D13 | Spring Security·JWT | [01 Spring Security 개요](day13/01_Spring_Security_개요.md)<br>[02 SecurityFilterChain](day13/02_SecurityFilterChain.md)<br>[03 Authentication](day13/03_Authentication.md)<br>[04 Authorization](day13/04_Authorization.md)<br>[05 Authentication 실습](day13/05_Authentication_실습.md)<br>[06 Authorization 실습](day13/06_Authorization_실습.md)<br>[07 JWT 개요](day13/07_JWT_개요.md)<br>[08 JWT 생성](day13/08_JWT_생성.md)<br>[09 Spring Security와의 연동](day13/09_Spring_Security와의_연동.md)<br>[10 refresh token](day13/10_refresh_token.md)<br>[11 SPA의 인증 처리](day13/11_SPA의_인증_처리.md) |
| D14 | Spring AI (선택) | [01 Spring AI 기본 컨셉](day14/01_Spring_AI_기본_컨셉.md) ˢ<br>[02 Chat Client API](day14/02_Chat_Client_API.md) ˢ<br>[03 Multimodal](day14/03_Multimodal.md) ˢ<br>[04 Multimodal (2)](day14/04_Multimodal_2.md) ˢ<br>[05 Advisor API](day14/05_Advisor_API.md) ˢ<br>[06 Embedding과 VectorStore](day14/06_Embedding과_VectorStore.md) ˢ<br>[07 ConversationMemory](day14/07_ConversationMemory.md) ˢ<br>[08 RAG ETL Pipeline](day14/08_RAG_ETL_Pipeline.md) ˢ<br>[09 RAG Advisor](day14/09_RAG_Advisor.md) ˢ<br>[10 ChatBot](day14/10_ChatBot.md) ˢ |

## 선택 · 시간 남을 때

| 폴더 | 강의별 노트 |
|---|---|
| extra | [01 객체배열관리실습](extra/01_객체배열관리실습.md) ˢ<br>[02 종합실습](extra/02_종합실습.md) ˢ<br>[03 Filter](extra/03_Filter.md) ˢ<br>[04 Filter 실습](extra/04_Filter_실습.md) ˢ<br>[05 Interceptor](extra/05_Interceptor.md) ˢ<br>[06 Interceptor 실습](extra/06_Interceptor_실습.md) ˢ<br>[07 멀티스레드](extra/07_멀티스레드.md) ᴮ ˢ<br>[08 DB Transaction](extra/08_DB_Transaction.md) ˢ<br>[09 DB Index](extra/09_DB_Index.md) ˢ |

## 연습문제

| 일 | 연습문제 |
|---|---|
| D1 | [합계·다이아몬드·이웃 합](problems/day01.md) |
| D2 | [은행 창구·가까운 두 점·시계](problems/day02.md) |
| D3 | [도형·급여·중복 좌표](problems/day03.md) |
| D4 | [결제·제네릭 스택·요일](problems/day04.md) |
| D5 | [단어 빈도·괄호·성적표](problems/day05.md) |
| D6 | [계산기·압축·로그 분석](problems/day06.md) |

연습문제는 그날 문법을 쓰도록 직접 만든 문제다.

- **코드 작성 3문제**: 예제와 추가 테스트의 출력이 글자 하나까지 같으면 통과. 다 풀면 "D1P1 검토해줘"
- **코드 읽기 문제**: 출력 예측 3~4개 · 버그 찾기 2개 · 빈칸 채우기 2개. 실행하지 말고 먼저 손으로 답하기 (면접·필기 대비)

## 매일 루틴

1. 오늘 강의 노트를 5분 훑고 강의 시작
2. 강의 코드는 `src/dayNN/` 패키지에 직접 따라 치기
3. 실습 → 연습문제 3개 (`src/practice/`), 여유 있으면 SWEA·프로그래머스
4. `Ctrl + K` → `dayNN: 오늘 한 것` → Commit and Push
