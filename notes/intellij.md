# IntelliJ 요약본

기능은 많지만 2주 동안 쓰는 건 여기 있는 것뿐이다. 단축키는 **Windows 키맵** 기준.
기억 안 나면 **`Shift` 두 번** → 기능 이름 검색 (예: `commit`, `reformat`, `database`).

---

## 1. 화면 구성

```
┌──────────────────────────────────────────────────────────────┐
│ ≡ 메뉴 │ 프로젝트명 │ Git 브랜치          ▶ 실행  🐞 디버그    │  ← 위쪽 바
├──────────┬───────────────────────────────────────┬───────────┤
│ 프로젝트  │                                       │ Database  │
│ 창        │            코드 편집기                 │ Gradle    │  ← 오른쪽 도구 창
│ (Alt+1)  │                                       │ Endpoints │
├──────────┴───────────────────────────────────────┴───────────┤
│ Run · Debug · Terminal(Alt+F12) · Git(Alt+9) · Problems(Alt+6) │  ← 아래쪽 도구 창
└──────────────────────────────────────────────────────────────┘
```

- 도구 창은 **가장자리 아이콘**을 눌러 열고 닫는다.
- 화면이 복잡해지면 `Ctrl + Shift + F12`로 편집기만 크게.

---

## 2. 매일 쓰는 것 (이것만 외우기)

| 단축키 | 기능 |
|---|---|
| `Alt + Enter` | **빨간 줄 자동 수정** (import, 예외 처리, 메서드 구현). 막히면 일단 이것 |
| `Shift + F10` | 실행 |
| `Ctrl + Shift + F10` | **지금 열린 파일** 실행 |
| `Ctrl + K` | 커밋 |
| `Shift` 두 번 | 전체 검색 (파일·클래스·기능) |

### 자동 생성 (입력하고 Tab)

| 입력 | 결과 |
|---|---|
| `psvm` | `public static void main(String[] args) { }` |
| `sout` | `System.out.println();` |
| `fori` | `for (int i = 0; i < ; i++) { }` |
| `iter` | `for (String s : list) { }` |
| `ifn` / `inn` | `if (x == null)` / `if (x != null)` |
| `.var` (값 뒤에) | `new ArrayList<>().var` → 변수 선언 |
| `.for` (컬렉션 뒤에) | `list.for` → for-each문 |

---

## 3. 코드 작성

| 단축키 | 기능 |
|---|---|
| `Ctrl + Space` | 자동완성 |
| `Alt + Insert` | 생성자·getter·setter·`toString`·`equals/hashCode` 생성 |
| `Ctrl + O` | 부모 메서드 오버라이드 |
| `Ctrl + I` | 인터페이스 메서드 구현 |
| `Ctrl + /` | 한 줄 주석 |
| `Ctrl + Shift + /` | 블록 주석 |
| `Ctrl + D` | 줄 복제 |
| `Ctrl + Y` | 줄 삭제 |
| `Alt + Shift + ↑/↓` | 줄 위아래 이동 |
| `Ctrl + W` / `Ctrl + Shift + W` | 선택 범위 넓히기 / 좁히기 |
| `Alt + 드래그` | 여러 줄 동시 편집 (세로 선택) |
| `Ctrl + Alt + L` | 코드 정렬 |
| `Ctrl + Alt + O` | 안 쓰는 import 정리 |
| `Ctrl + P` | 메서드 매개변수 힌트 |
| `Ctrl + Q` | 문서(Javadoc) 보기 |

---

## 4. 이동·찾기

| 단축키 | 기능 |
|---|---|
| `Ctrl + B` 또는 `Ctrl + 클릭` | **정의로 이동** (Spring 내부 코드 따라갈 때 필수) |
| `Ctrl + Alt + B` | 구현체로 이동 (인터페이스 → 구현 클래스) |
| `Alt + F7` | 이 코드가 쓰인 곳 전부 찾기 |
| `Ctrl + E` | 최근 파일 |
| `Ctrl + N` | 클래스 찾기 |
| `Ctrl + Shift + N` | 파일 찾기 (`application.yml` 등) |
| `Ctrl + Shift + F` | 프로젝트 전체에서 글자 검색 |
| `Ctrl + F` / `Ctrl + R` | 현재 파일에서 찾기 / 바꾸기 |
| `Ctrl + F12` | 현재 클래스의 메서드 목록 |
| `Ctrl + H` | 상속 구조 보기 |
| `Ctrl + Alt + ←/→` | 이전 / 다음 위치로 |
| `Ctrl + G` | 줄 번호로 이동 |

---

## 5. 리팩터링

| 단축키 | 기능 |
|---|---|
| `Shift + F6` | **이름 바꾸기** (쓰인 곳 전부 같이 바뀜) |
| `Ctrl + Alt + V` | 값을 변수로 뽑기 |
| `Ctrl + Alt + M` | 선택한 코드를 메서드로 뽑기 |
| `Ctrl + Alt + C` | 상수로 뽑기 |
| `Ctrl + Alt + Shift + T` | 리팩터링 메뉴 전체 |

---

## 6. 디버거 — 에러 원인을 찾는 가장 빠른 방법

`System.out.println`을 여기저기 찍는 대신 쓴다. **D2부터 꼭 써 볼 것.**

1. 멈추고 싶은 줄 **번호 옆을 클릭** → 빨간 점(브레이크포인트)
2. **`Shift + F9`** 또는 🐞 버튼으로 디버그 실행
3. 그 줄에서 멈추면 아래 Debug 창에서 변수 값을 볼 수 있다

| 단축키 | 기능 |
|---|---|
| `F8` | 다음 줄 (Step Over) |
| `F7` | 메서드 안으로 들어가기 (Step Into) |
| `Shift + F8` | 메서드에서 빠져나오기 (Step Out) |
| `F9` | 다음 브레이크포인트까지 계속 (Resume) |
| `Alt + F8` | 식 계산 (멈춘 상태에서 아무 코드나 실행해 보기) |
| `Ctrl + F2` | 중지 |

**팁**:
- 브레이크포인트 우클릭 → Condition에 `i == 5`처럼 쓰면 그 조건일 때만 멈춘다. 반복문 디버깅에 유용.
- 변수 값을 우클릭 → Set Value로 값을 바꿔서 계속 실행할 수 있다.

---

## 7. 실행 입력 (연습문제 풀 때)

- ▶ 실행 → 아래 **Run 창**이 뜨면 거기에 입력을 붙여넣고 Enter.
- 매번 붙여넣기 귀찮으면: 위쪽 실행 설정 드롭다운 → **Edit Configurations** → **Modify options** → **Redirect input from** → 입력 파일(`input.txt`) 지정.
- 입력을 다 넣었는데 프로그램이 안 끝나면 **`Ctrl + D`**(입력 끝 신호). 그래도 안 끝나면 `Ctrl + F2`로 중지.

---

## 8. Git

| 단축키 | 기능 |
|---|---|
| `Ctrl + K` | 커밋 (Commit and Push까지 한 번에 가능) |
| `Ctrl + Shift + K` | 푸시 |
| `Ctrl + T` | 받아오기 (Pull) — **다른 노트북에서 시작할 때 먼저** |
| `Alt + 9` | Git 창 (커밋 기록) |
| `Ctrl + Alt + Z` | 수정한 내용 되돌리기 (Rollback) |

- 코드 왼쪽 여백 색: **초록** = 새 줄, **파랑** = 바뀐 줄, **회색 삼각형** = 지운 줄. 색을 클릭하면 원래 코드가 보인다.
- 파일 우클릭 → **Git → Show History**: 그 파일이 어떻게 바뀌어 왔는지.
- 파일 우클릭 → **Local History**: 커밋 안 했어도 IntelliJ가 저장해 둔 기록. 실수로 지운 코드 살리기.

---

## 9. 2주차(Spring)에 쓸 기능

| 기능 | 여는 법 | 용도 |
|---|---|---|
| **Spring Boot 프로젝트 만들기** | New Project → Spring Boot | D7. 의존성 고르기(Spring Web, JPA, Lombok, MySQL, Security) |
| **Gradle 창** | 오른쪽 코끼리 아이콘 | 라이브러리 추가 후 🔄 **Reload** (빨간 import가 안 사라질 때) |
| **Database** | 오른쪽 Database 아이콘 → `+` → MySQL | D11~. JPA가 만든 테이블·데이터 확인 |
| **HTTP Client** | `.http` 파일 만들기 | D10~. Postman 없이 API 테스트 |
| **Endpoints** | View → Tool Windows → Endpoints | 내가 만든 API 주소 목록 |
| **Bean 표시** | 코드 왼쪽 초록 아이콘 | D7. 어떤 Bean이 주입되는지 클릭해서 이동 |
| **Terminal** | `Alt + F12` | Docker, Claude Code |

### HTTP Client 예시 (`api-test.http`)

```http
### 회원 목록
GET http://localhost:8080/api/users

### 회원 가입
POST http://localhost:8080/api/users
Content-Type: application/json

{
  "name": "규",
  "email": "gyu@example.com"
}
```
요청 왼쪽 ▶를 누르면 실행되고 응답이 아래에 뜬다.

---

## 10. 문제가 생겼을 때

| 증상 | 해결 |
|---|---|
| import가 전부 빨갛다 (Spring 프로젝트) | Gradle 창 🔄 Reload |
| "Project JDK is not defined" | 파일 위 노란 바의 **Setup SDK** → Temurin 21 |
| 코드는 맞는데 계속 빨간 줄 | ≡ → File → **Invalidate Caches** → Invalidate and Restart |
| Lombok `@Getter`가 안 먹힌다 | Settings → Compiler → Annotation Processors → **Enable** |
| 한글이 깨진다 | Settings → Editor → File Encodings → 전부 **UTF-8** |
| 실행 버튼이 회색 | `main` 메서드가 있는 파일을 열고 `Ctrl + Shift + F10` |
| "Port 8080 was already in use" | 이미 실행 중인 서버가 있다. Run 창의 빨간 ■로 중지 후 다시 실행 |
| 단축키가 다르게 동작 | Settings → Keymap이 **Windows**인지 확인 |

---

## 11. 지금 설정 상태 (확인용)

- [x] JDK: Eclipse Temurin 21
- [x] Ultimate 학생 라이선스 (2027-07-12까지)
- [x] GitHub 로그인, `java-study` 저장소 연결
- [ ] File Encodings UTF-8
- [ ] Auto Import 두 개 체크
- [ ] Keymap = Windows
- [ ] (D7) Annotation Processing 켜기
- [ ] (D7) Gradle → Build and run using: IntelliJ IDEA
- [ ] (선택) Key Promoter X 플러그인 — 마우스로 누른 기능의 단축키를 알려 준다
