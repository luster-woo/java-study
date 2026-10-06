# IntelliJ 실전 가이드

[요약본](intellij.md)은 단축키 표, 이 문서는 **"이걸 하려면 어디를 어떻게 누르나"**다. 상황별로 순서대로 따라 하면 된다.
메뉴 위치가 버전마다 조금 다르니, 못 찾으면 **`Shift` 두 번 → 굵게 쓴 기능 이름**으로 검색한다.

| # | 상황 | 언제 |
|---|---|---|
| 1 | [파일·패키지 다루기](#1-파일패키지-다루기) | 매일 |
| 2 | [실행 설정 (입력 파일·인자)](#2-실행-설정) | D1~ |
| 3 | [콘솔과 에러 메시지 읽기](#3-콘솔과-에러-메시지-읽기) | D1~ |
| 4 | [디버깅 실전](#4-디버깅-실전) | D2~ |
| 5 | [Scratch 파일로 코드 바로 실험하기](#5-scratch-파일) | D1~ |
| 6 | [노란 줄·빨간 줄과 Alt+Enter](#6-노란-줄빨간-줄과-altenter) | 매일 |
| 7 | [나만의 단축 입력 만들기 (Live Template)](#7-live-template-만들기) | D1~ |
| 8 | [Git 실전](#8-git-실전) | 매일 · 팀 프로젝트 |
| 9 | [단위 테스트 (JUnit 5)](#9-단위-테스트-junit-5) | D7~ |
| 10 | [Spring Boot 프로젝트 만들기](#10-spring-boot-프로젝트-만들기) | D7 |
| 11 | [Gradle 다루기](#11-gradle-다루기) | D7~ |
| 12 | [application.yml과 비밀번호 관리](#12-applicationyml과-비밀번호-관리) | D10~ |
| 13 | [MySQL 띄우고 Database 창 연결](#13-mysql-띄우고-database-창-연결) | D10~ |
| 14 | [HTTP Client로 API 테스트](#14-http-client로-api-테스트) | D10~ |
| 15 | [Spring 실행·로그·SQL 확인](#15-spring-실행로그sql-확인) | D7~ |
| 16 | [코드 탐색 실전 (Spring 내부 따라가기)](#16-코드-탐색-실전) | D7~ |
| 17 | [화면·작업 효율](#17-화면작업-효율) | 언제든 |
| 18 | [Claude Code 플러그인](#18-claude-code-플러그인) | D7~ |

---

## 1. 파일·패키지 다루기

| 하고 싶은 것 | 방법 |
|---|---|
| 패키지 만들기 | `src` 우클릭 → New → **Package** → `day02` |
| 클래스 만들기 | 패키지 우클릭 → New → **Java Class** → 이름 → 목록에서 Class / Interface / Enum / Record 선택 |
| 빠르게 새 파일 | 프로젝트 창에서 폴더 선택 후 `Alt + Insert` |
| 이름 바꾸기 | 파일 선택 → `Shift + F6`. 클래스 이름·import가 전부 같이 바뀐다 |
| 다른 패키지로 옮기기 | 파일을 드래그하거나 `F6`. `package` 선언과 import가 자동 수정된다 |
| 안전하게 삭제 | 파일 선택 → `Alt + Delete` (**Safe Delete**). 다른 곳에서 쓰고 있으면 알려 준다 |
| 복사해서 새 파일 | 파일 선택 → `F5` → 새 이름. 연습문제 틀 복사할 때 |
| 지금 파일이 프로젝트 어디 있는지 | 프로젝트 창 위 **◎(Select Opened File)** 버튼, 또는 `Alt + F1` → Project View |
| 파일 탐색기에서 열기 | 파일 우클릭 → Open In → **Explorer** |

**주의**: 파일을 Windows 탐색기에서 직접 옮기거나 이름을 바꾸지 말 것. `package` 선언이 안 맞아서 빨간 줄이 생긴다. 항상 IntelliJ 안에서.

---

## 2. 실행 설정

IntelliJ는 실행할 때마다 **Run Configuration**(실행 설정)을 만든다. 위쪽 바의 ▶ 왼쪽 드롭다운이 그 목록이다.

### 연습문제 입력을 매번 붙여넣기 귀찮을 때

1. 연습문제 클래스 옆에 `input.txt` 만들기 (예: `src/practice/D1P1.txt`)에 예제 입력 붙여넣기
2. 위쪽 실행 드롭다운 → **Edit Configurations...**
3. 왼쪽에서 `D1P1` 선택 (없으면 한 번 실행하면 생긴다)
4. **Modify options** → **Redirect input from** 체크
5. 새로 생긴 칸에 `input.txt` 경로 지정 → OK
6. 이제 ▶만 누르면 파일 내용이 입력으로 들어간다

### 그 밖의 설정

| 칸 | 용도 |
|---|---|
| **Program arguments** | `main(String[] args)`의 args에 들어갈 값 |
| **Environment variables** | 환경 변수. D10부터 DB 비밀번호를 여기 넣는다 (12절) |
| **Working directory** | 상대 경로 파일(`new FileReader("memo.txt")`)의 기준 폴더. 기본은 프로젝트 폴더 |

### 실행 관리

| 단축키 | 기능 |
|---|---|
| `Shift + F10` | 드롭다운에 선택된 설정 실행 |
| `Ctrl + Shift + F10` | **지금 커서가 있는 파일** 실행 (설정이 자동으로 생김) |
| `Alt + Shift + F10` | 실행 설정 목록에서 골라 실행 |
| `Ctrl + F5` | 다시 실행 |
| `Ctrl + F2` | 중지 |

**설정이 너무 많아졌을 때**: Edit Configurations에서 안 쓰는 것을 선택하고 `−`로 지운다. 실행 설정은 `.idea`에 저장돼서 Git에는 안 올라간다.

---

## 3. 콘솔과 에러 메시지 읽기

### 콘솔

- 프로그램이 입력을 기다리면 콘솔에 커서가 깜빡인다. 거기에 입력하고 Enter.
- 입력을 끝낸다는 신호(EOF)는 **`Ctrl + D`**. "입력이 끝날 때까지 읽기" 문제에서 필요하다.
- 콘솔 왼쪽 **🗑**: 출력 지우기, **⬇**: 자동 스크롤 켜고 끄기, **↩(Soft-Wrap)**: 긴 줄 접기.

### 에러(스택 트레이스) 읽는 법

```
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index 3 out of bounds for length 3
	at practice.D1P3.sum(D1P3.java:24)
	at practice.D1P3.main(D1P3.java:12)
```

1. **첫 줄**: 무슨 예외인지 + 원인. 여기선 "길이 3인 배열에서 인덱스 3을 읽음"
2. **`at` 줄들**: 위에서 아래로 "어디서 터졌나 → 누가 불렀나". **맨 위 `at` 중 내 코드**가 범인
3. **파란 링크 `D1P3.java:24`를 클릭**하면 그 줄로 바로 이동한다

| 자주 보는 예외 | 대부분의 원인 |
|---|---|
| `NullPointerException` | `new` 안 한 객체, `null`인 값으로 메서드 호출. 메시지에 어떤 변수가 null인지 나온다 |
| `ArrayIndexOutOfBoundsException` | `i <= length`, 음수 인덱스 |
| `NumberFormatException` | `Integer.parseInt`에 숫자 아닌 문자열 (공백 포함) |
| `ClassCastException` | 잘못된 다운캐스팅 |
| `ConcurrentModificationException` | for-each 도중 리스트 수정 |
| `InputMismatchException` | Scanner에 타입이 안 맞는 입력 |

### Spring 에러는 아래에서 위로

Spring 에러는 수십 줄이다. **`Caused by:`로 시작하는 줄 중 가장 아래 것**이 진짜 원인인 경우가 많다. `Ctrl + F`로 `Caused by` 검색.

---

## 4. 디버깅 실전

`System.out.println`을 찍고 지우는 대신 디버거를 쓴다. 예: D1P3(이웃 합)에서 값이 이상하게 나올 때.

### 기본 흐름

1. 의심 가는 줄(예: 이중 for문 안 첫 줄) **줄 번호 옆을 클릭** → 빨간 점
2. **`Shift + F9`** (또는 위쪽 🐞) → 입력을 넣으면 그 줄에서 멈춘다
3. 아래 **Debug 창**:
   - **Variables**: 지금 모든 변수 값. 배열·객체는 ▶를 펼쳐서 안을 본다
   - **Frames**: 지금 어느 메서드 안인지, 누가 불렀는지
   - 코드 옆에도 회색 글씨로 변수 값이 표시된다
4. `F8`로 한 줄씩 내려가며 값이 언제 이상해지는지 본다
5. 원인을 찾으면 `Ctrl + F2`로 중지

| 단축키 | 기능 | 언제 |
|---|---|---|
| `F8` | Step Over: 다음 줄 | 기본 |
| `F7` | Step Into: 메서드 안으로 | 내가 만든 메서드가 의심될 때 |
| `Shift + F8` | Step Out: 메서드 밖으로 | 들어왔는데 볼 게 없을 때 |
| `Alt + F9` | Run to Cursor: 커서 줄까지 실행 | 브레이크포인트 없이 건너뛰기 |
| `F9` | Resume: 다음 브레이크포인트까지 | 반복문 다음 회차로 |
| `Alt + F8` | Evaluate: 식 계산 | `grid[nr][nc]`, `list.size()` 등을 즉석에서 확인 |

### 더 쓸모 있는 기능

| 기능 | 방법 | 쓰는 상황 |
|---|---|---|
| **조건부 브레이크포인트** | 빨간 점 우클릭 → Condition: `i == 2 && j == 3` | 반복문 1000번 중 특정 회차만 |
| **Watch** | Variables 창의 `+` → `sum / cnt` | 계속 지켜볼 식 |
| **값 바꾸기** | 변수 우클릭 → Set Value | "이 값이면 어떻게 되나" 실험 |
| **예외 브레이크포인트** | `Ctrl + Shift + F8` → `+` → Java Exception Breakpoints → `NullPointerException` | 예외가 **터지는 순간** 멈춤. 원인 찾기 최강 |
| **로그만 찍기** | 빨간 점 우클릭 → Suspend 체크 해제 → Evaluate and log에 식 | 멈추지 않고 값만 콘솔에 |
| **브레이크포인트 전체 보기** | `Ctrl + Shift + F8` | 여기저기 찍은 것 정리 |

**Spring에서**: 서버를 🐞로 실행하고 Controller 메서드에 점을 찍은 뒤 HTTP Client로 요청을 보내면, 요청이 들어오는 순간 멈춘다. 요청 값이 제대로 들어왔는지 확인할 때 최고.

---

## 5. Scratch 파일

**프로젝트에 파일을 안 만들고** 코드 몇 줄을 바로 실행해 보는 메모장. [코드 읽기 문제](problems/) 답 확인할 때 딱이다.

1. `Ctrl + Alt + Shift + Insert` → **Java** 선택
2. `class Scratch { public static void main(...) }`가 열린다. 코드를 쓰고 ▶
3. 프로젝트 창 맨 아래 **Scratches and Consoles**에 모인다. 프로젝트 폴더 밖에 저장돼서 Git에도 안 올라간다

---

## 6. 노란 줄·빨간 줄과 Alt+Enter

| 표시 | 뜻 |
|---|---|
| **빨간 밑줄** | 컴파일 에러. 실행 불가 |
| **노란 배경·밑줄** | 경고. 실행은 되지만 문제 소지 (안 쓰는 변수, null 가능성) |
| **회색 글씨** | 안 쓰는 코드 (import, 변수) |
| 오른쪽 스크롤바의 빨강·노랑 줄 | 파일 안 문제 위치. 클릭하면 이동 |
| 오른쪽 위 ✓ / ⚠ 숫자 | 파일 전체 상태. 클릭하면 목록 |

`F2` / `Shift + F2`: 다음 / 이전 문제 위치로 점프.
`Alt + 6`: **Problems 창** — 프로젝트 전체 에러 목록.

### Alt + Enter가 해 주는 것 (자주 쓰는 순)

- **Import class**: 빨간 클래스 이름 → import 추가
- **Create method / field / class**: 아직 없는 걸 먼저 쓰고 → 자동으로 만들기 (코드를 위에서부터 쓸 때 편함)
- **Add exception to method signature / Surround with try/catch**: Checked 예외 처리
- **Implement methods**: 인터페이스·추상 클래스 메서드 자동 생성
- **Introduce local variable**: 반환값을 변수에 담기
- **Replace with lambda / method reference**: 익명 클래스·람다를 더 짧게
- **Convert to record**: 값만 담는 클래스를 record로 (D6 이후)

**주의**: Alt+Enter 제안을 **이해하지 않고 누르지 말 것**. 특히 "Cast to...", "Make ... static"은 원인을 가리는 경우가 많다. 왜 에러인지 먼저 읽기.

---

## 7. Live Template 만들기

자주 쓰는 코드를 단축 입력으로 등록한다. 연습문제 입력 틀을 `bj` + Tab으로 만들어 보자.

1. `Ctrl + Alt + S` → Editor → **Live Templates**
2. 오른쪽 `+` → **Template Group** → 이름 `my`
3. `my` 선택 → `+` → **Live Template**
4. Abbreviation: `bj`, Description: `빠른 입력 틀`
5. Template text:
   ```java
   BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
   StringTokenizer st = new StringTokenizer(br.readLine());
   $END$
   ```
6. 아래 **Define** (또는 Change) → **Java** 체크
7. 오른쪽 **Options** → **Shorten FQ names** 체크 (import 자동 추가)
8. OK → 이제 `main` 안에서 `bj` + Tab

`$END$`는 입력 후 커서가 갈 자리, `$NAME$`처럼 쓰면 Tab으로 이동하며 채울 칸이 된다.

---

## 8. Git 실전

### 하루 작업 (혼자)

| 순서 | 동작 | 방법 |
|---|---|---|
| 시작 | 최신 받기 | `Ctrl + T` → Merge 선택 → OK |
| 끝 | 커밋 | `Ctrl + K` → 파일 체크 → 메시지 → **Commit and Push** |

### 커밋 창(`Ctrl + K`)에서 할 수 있는 것

- **파일 더블클릭**: 무엇이 바뀌었는지 비교(Diff) 화면. 커밋 전에 한 번씩 보는 습관
- **파일 체크 해제**: 이번 커밋에서 빼기
- **Diff 화면에서 변경 블록 옆 체크박스**: 파일의 **일부만** 커밋 (부분 커밋)
- **Amend 체크**: 방금 한 커밋에 덧붙이기 (아직 Push 안 했을 때만)
- 메시지 칸 오른쪽 시계 아이콘: 지난 커밋 메시지 다시 쓰기

### 되돌리기 — 상황별

| 상황 | 방법 |
|---|---|
| 파일을 마지막 커밋 상태로 | 파일 우클릭 → Git → **Rollback** (`Ctrl + Alt + Z`) |
| 한 블록만 되돌리기 | 왼쪽 여백 색 막대 클릭 → **↶ Rollback** |
| 커밋했는데 아직 Push 안 함 | `Alt + 9` → Log 탭 → 그 커밋 우클릭 → **Undo Commit** (변경 내용은 남음) |
| Push까지 한 커밋 취소 | Log 탭 → 커밋 우클릭 → **Revert Commit** (취소하는 새 커밋을 만든다. 팀 프로젝트에서는 이것만) |
| 커밋 안 했는데 지워버림 | 파일(또는 폴더) 우클릭 → **Local History** → Show History → 원하는 시점 → Revert |

### 하던 작업 잠깐 치워 두기

다른 걸 급히 해야 하는데 지금 코드는 커밋하기 애매할 때:
≡ → Git → Uncommitted Changes → **Stash Changes** → 나중에 **Unstash Changes**.

### 브랜치 (팀 프로젝트에서 필수)

**왼쪽 위 브랜치 이름**(예: `main`)을 클릭하면 브랜치 메뉴가 뜬다.

| 하고 싶은 것 | 방법 |
|---|---|
| 새 기능 브랜치 만들기 | 브랜치 메뉴 → **New Branch** → `feature/login` → Checkout 체크 |
| 다른 브랜치로 이동 | 브랜치 메뉴 → 브랜치 선택 → **Checkout** |
| 원격 브랜치 가져오기 | 브랜치 메뉴 → Remote Branches → `origin/feature/x` → Checkout |
| 내 브랜치에 develop 최신 반영 | develop 받은 뒤, 내 브랜치에서 브랜치 메뉴 → `develop` → **Merge into Current** |
| 첫 Push | `Ctrl + Shift + K` → 원격에 브랜치가 생긴다 |

Merge Request(Pull Request)는 GitLab·GitHub 웹에서 만든다.

### 충돌(Conflict) 해결

Pull이나 Merge 중 같은 줄을 둘 다 고쳤으면 **Conflicts** 창이 뜬다.

1. 충돌 파일 선택 → **Merge...** 버튼
2. 화면이 3칸: **왼쪽 = 내 것**, **가운데 = 결과**, **오른쪽 = 상대 것**
3. 블록마다 `>>` / `<<` 화살표로 가운데에 넣거나 `X`로 버린다. 가운데에서 직접 고쳐도 된다
4. 모두 처리하면 **Apply** → 커밋

충돌이 무서우면: **자주 Pull 하고, 작게 자주 커밋**하면 충돌 자체가 작아진다.

### 팀 저장소(SSAFY GitLab) 클론

1. 시작 화면 또는 ≡ → File → New → **Project from Version Control**
2. URL에 GitLab 저장소 주소 붙여넣기 → Clone
3. 인증 창이 뜨면 GitLab의 **Personal Access Token**을 비밀번호 칸에 (GitLab 웹 → Preferences → Access Tokens에서 발급)

### 커밋 메시지 규칙 (팀 프로젝트에서 흔히 쓰는 형식)

```
feat: 회원 가입 API 추가
fix: 로그인 시 토큰 만료 처리 오류 수정
refactor: UserService 조회 로직 분리
docs: README 실행 방법 추가
test: UserService 단위 테스트 추가
```

---

## 9. 단위 테스트 (JUnit 5)

`main`에서 println으로 확인하던 걸 **자동으로 검사하는 코드**로 바꾸는 것.

### 테스트 만들기

1. 테스트할 클래스(예: `Account`) 안에서 **`Ctrl + Shift + T`** → **Create New Test...**
2. Testing library: **JUnit 5** (경고가 뜨면 **Fix** → 라이브러리 자동 추가)
3. 테스트할 메서드 체크 → OK → `AccountTest`가 생긴다
4. Spring 프로젝트에서는 `src/test/java` 아래 같은 패키지에 생긴다

```java
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;   // Spring Boot에 기본 포함

class AccountTest {

    @Test
    void 입금하면_잔액이_늘어난다() {
        Account acc = new Account("규", 1000);
        acc.deposit(500);
        assertThat(acc.getBalance()).isEqualTo(1500);
    }

    @Test
    void 잔액보다_많이_출금하면_예외() {
        Account acc = new Account("규", 1000);
        assertThatThrownBy(() -> acc.withdraw(5000))
                .isInstanceOf(InsufficientBalanceException.class);
    }
}
```
(순수 Java 프로젝트에서 AssertJ가 없으면 `assertEquals(1500, acc.getBalance())` — `org.junit.jupiter.api.Assertions`)

### 실행과 결과

| 동작 | 방법 |
|---|---|
| 테스트 하나 실행 | 메서드 옆 ▶ |
| 클래스 전체 | 클래스 이름 옆 ▶ |
| 패키지·프로젝트 전체 | 프로젝트 창에서 `test` 폴더 우클릭 → Run 'All Tests' |
| 테스트 ↔ 원본 오가기 | `Ctrl + Shift + T` |
| 커버리지 | ▶ 우클릭 → **Run with Coverage**. 코드 옆 초록 = 실행됨, 빨강 = 안 됨 |

- 결과 창에서 초록 ✓ / 빨강 ✗. 실패한 테스트를 누르면 **기대값 vs 실제값**이 나오고, **Click to see difference**로 비교 화면을 볼 수 있다.
- 메서드 이름을 한글로 쓰면 결과 창에서 읽기 좋다(실무에서도 흔함). `@DisplayName("...")`도 된다.

---

## 10. Spring Boot 프로젝트 만들기

**D7에서 할 일.** ≡ → File → New → **Project** → 왼쪽 **Spring Boot**.

### 첫 화면

| 칸 | 값 | 설명 |
|---|---|---|
| Name | `spring-study` | 프로젝트 이름 |
| Location | `~\Desktop\java` | `java-study` 옆에 |
| Create Git repository | 체크 | |
| Language | **Java** | |
| Type | **Gradle - Groovy** | 팀이 정한 게 있으면 그걸로 (Kotlin DSL·Maven) |
| Group | `com.ssafy` | 회사·조직 도메인 거꾸로 |
| Artifact | `spring-study` | |
| Package name | `com.ssafy.springstudy` | 자동으로 채워짐. 하이픈은 못 씀 |
| JDK | Temurin 21 | |
| Java | **21** | |
| Packaging | **Jar** | |

### 두 번째 화면 (Dependencies)

위쪽 **Spring Boot 버전**은 숫자만 있는 최신 버전 (`SNAPSHOT`, `M1`, `RC` 붙은 건 피하기).

| 의존성 | 검색어 | 언제부터 |
|---|---|---|
| **Spring Web** | web | D7 (REST API) |
| **Lombok** | lombok | D7 |
| **Spring Boot DevTools** | devtools | D7 (코드 바꾸면 자동 재시작, 선택) |
| **Validation** | validation | D10 (`@Valid`) |
| **Spring Data JPA** | jpa | D10 |
| **MySQL Driver** | mysql | D10 |
| **H2 Database** | h2 | D10 (MySQL 없이 메모리 DB로 연습, 선택) |
| **Spring Security** | security | D13 — **처음부터 넣으면 모든 API가 로그인 화면으로 막히니 D13에 추가** |

**Create** → 오른쪽 아래 Gradle 진행 막대가 끝날 때까지 기다린다 (처음엔 몇 분).

### 생긴 구조

```
spring-study/
├─ src/main/java/com/ssafy/springstudy/
│   └─ SpringStudyApplication.java     ← main. ▶로 서버 실행
├─ src/main/resources/
│   ├─ application.properties          ← 설정 (yml로 바꿀 것, 12절)
│   ├─ static/                          ← 정적 파일 (안 씀)
│   └─ templates/                       ← 화면 템플릿 (REST API면 안 씀)
├─ src/test/java/...                    ← 테스트
├─ build.gradle                         ← 라이브러리 목록
└─ settings.gradle
```

`SpringStudyApplication`의 ▶ → 콘솔에 `Started SpringStudyApplication in ... seconds`가 나오면 성공. 브라우저에서 `http://localhost:8080` → Whitelabel Error Page가 뜨면 정상(아직 API가 없을 뿐).

**패키지 구조 관례**: `SpringStudyApplication`과 **같은 패키지 또는 그 아래**에 만들어야 Spring이 찾는다.

```
com.ssafy.springstudy
├─ SpringStudyApplication.java
├─ controller/
├─ service/
├─ repository/
├─ domain/        (엔티티)
└─ dto/
```

---

## 11. Gradle 다루기

### 라이브러리 추가

1. `build.gradle`의 `dependencies { }` 안에 한 줄 추가
   ```groovy
   implementation 'org.springframework.boot:spring-boot-starter-security'
   ```
2. 편집기 오른쪽 위에 뜨는 **코끼리 🔄 아이콘** 클릭 (또는 `Ctrl + Shift + O`, **Load Gradle Changes**)
3. 오른쪽 아래 진행이 끝나면 import 빨간 줄이 사라진다

| 키워드 | 뜻 |
|---|---|
| `implementation` | 실행에 필요 |
| `compileOnly` + `annotationProcessor` | 컴파일 때만 (Lombok) |
| `runtimeOnly` | 실행 때만 (DB 드라이버) |
| `testImplementation` | 테스트에만 |

### Gradle 창 (오른쪽 코끼리 아이콘)

| 동작 | 위치 |
|---|---|
| 전체 새로고침 | 🔄 (Reload All Gradle Projects) |
| 실행 가능한 jar 만들기 | Tasks → build → **bootJar** → `build/libs/`에 생김 (배포용) |
| 깨끗이 다시 빌드 | Tasks → build → **clean** 후 build |
| 의존성 트리 | Dependencies 펼치기 (어떤 라이브러리가 딸려 왔나) |

---

## 12. application.yml과 비밀번호 관리

### properties → yml로 바꾸기

`application.properties` 우클릭 → Refactor → Rename → `application.yml`. 내용은 들여쓰기(공백 2칸) 형식으로.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/study
    username: root
    password: ${DB_PASSWORD}            # 환경 변수에서 읽기
  jpa:
    hibernate:
      ddl-auto: update                  # 엔티티 보고 테이블 자동 생성 (연습용)
    properties:
      hibernate:
        format_sql: true
    show-sql: true                      # 실행되는 SQL을 콘솔에 출력 (N+1 확인용)

server:
  port: 8080
```
- Ultimate라서 yml에서 **자동완성**이 된다. `spring.jpa.`까지 치면 후보가 나온다.
- 들여쓰기는 **공백만**. 탭이 섞이면 실행 에러.

### 비밀번호를 Git에 올리지 않기

1. 실행 드롭다운 → Edit Configurations → `SpringStudyApplication`
2. **Modify options** → **Environment variables** → `DB_PASSWORD=내비밀번호`
3. yml에는 `${DB_PASSWORD}`만 남는다 → 그대로 커밋해도 안전

(FinFit에서 `.env.prod`로 분리했던 것과 같은 목적이다.)

---

## 13. MySQL 띄우고 Database 창 연결

### MySQL 실행 (Docker가 있으면 가장 간단)

Terminal(`Alt + F12`)에서:
```
docker run --name mysql-study -e MYSQL_ROOT_PASSWORD=비밀번호 -e MYSQL_DATABASE=study -p 3306:3306 -d mysql:8.4
```
- 다음부터는 `docker start mysql-study`
- Docker가 없으면 처음엔 **H2**(메모리 DB)로 연습해도 된다

### Database 창 연결

1. 오른쪽 **Database** 아이콘 → `+` → Data Source → **MySQL**
2. Host `localhost`, Port `3306`, User `root`, Password 입력, Database `study`
3. 아래에 "Download missing driver files"가 뜨면 클릭
4. **Test Connection** → Succeeded → OK

### 쓰는 법

| 하고 싶은 것 | 방법 |
|---|---|
| 테이블 보기 | `study` → `tables` 펼치기 → 테이블 더블클릭 → 데이터 표 |
| 데이터 직접 수정 | 표에서 셀 더블클릭 → 수정 → 위쪽 **↑ Submit** (`Ctrl + Enter`) |
| SQL 실행 | 연결 우클릭 → New → **Query Console** → SQL 작성 → 커서 줄에서 `Ctrl + Enter` |
| 테이블 구조 | 테이블 우클릭 → Modify Table, 또는 `Ctrl + Q` |
| ERD | 스키마 우클릭 → Diagrams → **Show Diagram** (`Ctrl + Alt + Shift + U`) |

**JPA 공부할 때**: 엔티티를 만들고 서버를 실행한 뒤 Database 창에서 🔄 → JPA가 테이블을 어떻게 만들었는지(컬럼 타입, 외래키) 바로 확인한다.

---

## 14. HTTP Client로 API 테스트

### 요청 파일 만들기

프로젝트 루트에 `http/` 폴더 → 우클릭 → New → **HTTP Request** → `user.http`

```http
### 회원 가입
POST {{host}}/api/users
Content-Type: application/json

{
  "name": "규",
  "email": "gyu@example.com",
  "password": "test1234"
}

### 회원 조회
GET {{host}}/api/users/1

### 로그인 → 토큰 저장
POST {{host}}/api/auth/login
Content-Type: application/json

{
  "email": "gyu@example.com",
  "password": "test1234"
}

> {%
    client.global.set("token", response.body.accessToken);
%}

### 토큰이 필요한 요청
GET {{host}}/api/users/me
Authorization: Bearer {{token}}
```

- 요청은 `###`으로 구분한다. 각 요청 왼쪽 **▶**로 실행.
- 응답은 아래 창에 뜨고, `.idea/httpRequests`에 기록이 남는다.
- `> {% ... %}`: 응답에서 값을 꺼내 다음 요청에 쓰기. **D13 JWT 테스트에 필수**.

### 환경 변수 파일

같은 폴더에 `http-client.env.json`:
```json
{
  "local": { "host": "http://localhost:8080" },
  "prod":  { "host": "http://내서버주소" }
}
```
`.http` 파일 위쪽 드롭다운에서 `local` / `prod`를 골라 같은 요청을 다른 서버로 보낸다. 비밀번호 같은 값은 `http-client.private.env.json`에 넣고 `.gitignore`에 추가.

**더 빠른 방법**: Controller의 `@GetMapping` 옆 **🌐 아이콘** 클릭 → 그 API의 요청이 자동으로 만들어진다.

---

## 15. Spring 실행·로그·SQL 확인

| 하고 싶은 것 | 방법 |
|---|---|
| 서버 실행 / 디버그 실행 | `Application` 클래스 ▶ / 🐞 |
| 실행 중인 서버 모아 보기 | **Services 창** (`Alt + 8`). Spring 서버·Docker 컨테이너가 한곳에 |
| 재시작 | Run 창의 🔄 (`Ctrl + F5`) |
| 코드 고친 걸 바로 반영 | DevTools를 넣었다면 `Ctrl + F9`(Build) → 자동 재시작 |
| 로그에서 찾기 | Run 창 클릭 후 `Ctrl + F` |
| 실행된 SQL 보기 | yml에 `show-sql: true` → 콘솔에 `Hibernate: select ...` |
| N+1 확인 | 목록 API 한 번 호출 → 콘솔에 select가 몇 번 찍혔나 세기 |

**"Port 8080 was already in use"**: 서버가 이미 하나 떠 있다. Services 창에서 중지하거나 Run 창의 ■.

---

## 16. 코드 탐색 실전

Spring을 배울 때 "이게 내부에서 뭘 하는지" 따라가는 법.

| 하고 싶은 것 | 방법 |
|---|---|
| 애너테이션·클래스 내부 보기 | `Ctrl + 클릭` (처음엔 Download Sources를 눌러 원본 코드 받기) |
| 인터페이스의 실제 구현체 | `Ctrl + Alt + B` (예: `JpaRepository` → `SimpleJpaRepository`) |
| 이 메서드를 누가 부르나 | 메서드 이름에서 `Ctrl + Alt + H` (**Call Hierarchy**) |
| 이 Bean이 어디서 주입되나 | 필드 옆 **초록 Bean 아이콘** 클릭 |
| 클래스 상속 구조 | `Ctrl + H` |
| 클래스 다이어그램 | 패키지 우클릭 → Diagrams → **Show Diagram** |
| 왔던 곳으로 돌아가기 | `Ctrl + Alt + ←` |
| 최근 본 코드 위치 | `Ctrl + Shift + E` (**Recent Locations**) |

---

## 17. 화면·작업 효율

| 기능 | 방법 |
|---|---|
| 화면 분할 | 편집기 탭 우클릭 → **Split Right** (강의 코드와 내 코드 나란히) |
| 편집기만 크게 | `Ctrl + Shift + F12` (도구 창 전부 숨기기) |
| 집중 모드 | View → Appearance → **Distraction Free Mode** |
| 북마크 | 줄에서 `F11` → `Shift + F11`로 목록 |
| TODO 모아 보기 | 코드에 `// TODO: 예외 처리` → 아래 **TODO 창** |
| 두 파일 비교 | 프로젝트 창에서 두 파일 선택 → `Ctrl + D` |
| 클립보드와 비교 | 파일 우클릭 → Compare with Clipboard (강의 코드와 내 코드 비교) |
| 글자 크기 | Settings → Editor → Font. 또는 `Ctrl + 마우스 휠` (Settings → Editor → General → Change font size with Ctrl+Mouse Wheel 체크) |
| 탭 너무 많을 때 | 탭 우클릭 → **Close Other Tabs** / Close All |
| 마우스로 쓴 기능의 단축키 알기 | Key Promoter X 플러그인 |

---

## 18. Claude Code 플러그인

Pro 계정으로 쓰는 방법. 설치는 D7 이후 추천 (그 전에는 직접 짜는 게 공부).

1. Terminal(`Alt + F12`)에서 Claude Code 설치: `irm https://claude.ai/install.ps1 | iex`
2. Settings → Plugins → Marketplace → **Claude Code [Beta]** 설치 → 재시작
3. Terminal에서 `claude` → **Claude 계정(Pro)**으로 로그인 (API 키·Console 로그인은 사용량 과금이니 고르지 않기)
4. Terminal 탭을 오른쪽으로 옮기면 VS Code처럼 옆에 띄워 쓸 수 있다

| 쓰는 법 | 설명 |
|---|---|
| 코드 선택 후 질문 | 선택한 코드가 자동으로 Claude에게 전달된다 |
| 파일 언급 | `Ctrl + Alt + K`로 현재 파일 참조 넣기 |
| 수정 제안 | IntelliJ Diff 화면으로 보여 주고, 받을지 고른다 |
| 에러 | 빨간 줄 정보도 Claude가 함께 본다 |

**공부할 때 좋은 질문**: "이 코드 왜 NullPointerException 나?" 대신 **"이 스택 트레이스에서 내가 어디부터 봐야 해?"**처럼 **찾는 방법**을 물어보면 실력이 는다.
