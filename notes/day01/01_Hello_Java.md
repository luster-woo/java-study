# Hello Java

> 교재: 15기_자바비전공_Java_01_0119_기본문법_제어문 (비전공)

> [day01 목록](../README.md) · [변수와 자료형 →](02_변수와_자료형.md)

## 학습 목표

- 자바 개발 환경을 이해하고 설명할 수 있다

---

## 미리 알아두기 — 개발 환경

| 구성 | 역할 |
| --- | --- |
| **JVM** (Java Virtual Machine) | 자바 프로그램이 실행되는 환경 |
| **JRE** (Java Runtime Environment) | 자바 프로그램을 실행하기 위한 환경 제공 (JVM 포함) |
| **JDK** (Java Development Kit) | 자바 애플리케이션을 개발하고 실행하기 위한 모든 도구 제공 (JRE + 컴파일러 등) |
| **IDE** (통합 개발 환경) | Eclipse, Spring Tools 4(STS), VS Code 등 |

- 수업 환경: Windows + **JDK 17 이상**

```text
JDK ┌──────────────────────────────┐
    │ 개발 도구 (javac 등)          │
    │  JRE ┌─────────────────────┐ │
    │      │ 라이브러리           │ │
    │      │  JVM ┌───────────┐  │ │
    │      │      │ 실행 엔진  │  │ │
    │      │      └───────────┘  │ │
    │      └─────────────────────┘ │
    └──────────────────────────────┘
```

---

## Hello Java

### 프로그램(Program)

- 컴퓨터의 **명령어들의 집합**
- **프로그래밍** : 컴퓨터에게 명령을 내리는 과정
- **저급 언어**(기계어, 어셈블리어) / **고급 언어**(Java, Python)
- 운영체제(OS) 역시 프로그램

### 컴퓨터의 자료 표현

- **비트(Bit, Binary Digit)** : 0 또는 1의 값만 가짐 → 2진수 사용
- **바이트(Byte)** : 8개의 비트로 구성된 정보 단위, 컴퓨터 메모리의 기본 단위

| 기준 | 단위 |
| --- | --- |
| 10진수 기반 (SI 표준, 1000배) | B → KB → MB → GB → TB → PB → EB → ZB → YB |
| 2진수 기반 (IEC 표준, 1024배) | KiB → MiB → GiB → TiB → PiB → EiB → ZiB → YiB |

### Hello World! (STS 기준 순서)

1. STS 실행 → 워크스페이스 설정
2. 설치한 JDK 설정 확인 : `Window → Preferences → Java → Installed JREs`, `Compiler` 확인
3. Java 프로젝트 생성 — `Create module-info.java file` **체크 해제**
4. Class 파일 생성 및 코드 작성
5. 실행 : `Run → Run` 또는 `Ctrl + F11`

- **module-info.java** : 자바 9부터 도입된 모듈 시스템의 설정 파일
    - 프로젝트가 외부에 노출할 기능, 의존하는 모듈을 정의
    - 작고 단순한 프로젝트는 생략해도 됨, 규모가 커지면 유용

```java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello, World!");  // Hello, World!
    }
}
```

### 주석(Comment)

| 형태 | 의미 |
| --- | --- |
| `// 내용` | 한 줄 주석 — 기호 위치부터 그 줄 끝까지 |
| `/* 내용 */` | 여러 줄 주석 — 범위 내용 전체 |
| `/** 내용 */` | 문서화 주석 — Documentation API(Javadoc)용 |

### 출력 메서드

- `System.out.print(데이터)` : 줄 바꿈 **없이** 출력
- `System.out.println(데이터)` : 출력 후 줄 바꿈(개행) 추가
- `System.out.printf(형식 문자열, 데이터1, 데이터2, ...)` : 문자열을 **형식화(formatting)** 하여 출력

| 형식 지정자 | 의미 |
| --- | --- |
| `%d` | 정수 |
| `%f` | 실수 |
| `%s` | 문자열 |
| `%c` | 문자 |
| `%n` | 줄 바꿈 (플랫폼 독립적) |

- 입력과 함께 알고리즘 과목에서 더 다룰 예정

```java
public class PrintDemo {
    public static void main(String[] args) {
        System.out.print("A");
        System.out.print("B");
        System.out.println();                                  // AB 뒤 줄바꿈
        System.out.println("Java");                            // Java
        System.out.printf("%d년 %s %c반%n", 2026, "SSAFY", 'A'); // 2026년 SSAFY A반
        System.out.printf("%f / %.2f%n", 3.14159, 3.14159);    // 3.141590 / 3.14
    }
}
```

### 무슨 일이 일어나고 있을까? — 컴파일과 실행

```text
Hello.java  ──(javac Hello.java)──▶  Hello.class  ──(java Hello)──▶  JVM 실행
 소스코드        컴파일                  바이트코드
 (고급 언어)                             (저급 언어)
```

- **컴파일(Compile)** : 사람이 읽고 쓰기 편한 고급 언어 소스코드를 컴퓨터가 실행할 수 있는 형태(기계어, 바이트코드 등)로 변환하는 작업
- **자바 컴파일러(javac)** : `.java` 파일(고급 언어)을 JVM이 이해하는 `.class` 파일(바이트코드)로 변환하는 프로그램

### Write Once, Run Anywhere

- 자바 언어의 슬로건
- 플랫폼(OS)에 **독립적** — JVM만 설치되어 있으면 다양한 OS에서 동일한 코드 실행 가능

| 플랫폼 종속적 | 플랫폼 독립적 (Java) |
| --- | --- |
| OS마다 따로 컴파일한 실행 파일 필요 | 같은 `.class`를 각 OS의 JVM이 실행 |
