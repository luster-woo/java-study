# SLF4j와 Logging

> 교재: 15기_자바전공_01_framework_0504 (전공) — 2. SLF4j와 Logging

> [← Framework](01_Framework.md) · [day07 목록](../README.md) · [Junit →](03_Junit.md)

---

## 2. SLF4j와 Logging

### logging의 필요성

- `System.out.println()`으로 디버깅하면 생기는 문제

```java
public int add(String a, String b) {
    System.out.println("파라미터 잘 들어오나?: " + a + ", " + b);
    int result = 0;
    try {
        result = Integer.parseInt(a) + Integer.parseInt(b);
    } catch (NumberFormatException e) {
        System.out.println("예외 발생: " + e.getMessage());
    }
    System.out.println("여기까지 오면 결과 생성됨: " + result);
    return result;
}
```

| 문제 | 설명 |
|---|---|
| 구분 불가 | 예외 메시지가 일반 출력과 섞여 구별이 안 됨 |
| 정보 부족 | **언제, 어디서** 발생한 것인지 알 수 없음 |
| 납품 후 | 운영 중 오류가 나도 출력만으로는 원인을 추적하기 어려움 |

- 장기간 동작하는 시스템의 **상태 기록**도 필요

```java
private void doGet(HttpServletRequest req, HttpServletResponse resp) throws Exception {
    String act = req.getParameter("act");
    System.out.println("일시:" + new Date() + ",경로: " + req.getRemoteAddr() + " , 요청명: " + act);
    if ("mvjoin".equals(act)) {
        // do something
    }
}
```

- 콘솔에만 찍으면 콘솔 메모리가 버티지 못함 → 파일 등 다른 곳에 남길 수단 필요

### SLF4J (Simple Logging Facade for Java)

- 자바에서 logging을 위한 **facade**
  - **facade 패턴**: 라이브러리의 복잡한 구조를 단순화해 인터페이스로 제공하는 패턴
- SLF4J의 구현체: **log4j, logback** 등

```
  code ──► SLF4J (인터페이스) ──► log4j
                               └─► logback
```

- 코드에서는 구현체가 숨겨지고 앞에 있는 **slf4j만** 보임 → 구현체를 바꿔도 코드는 그대로

### SLF4J의 특징

- 심각도에 따라 **다섯 단계**로 로그 구분

```
trace < debug < info < warn < error
```

- 사용자 설정에 따라 **확인할 로그 레벨**을 결정 (설정한 레벨 이상만 출력)

| 설정 | 표시되는 로그 |
|---|---|
| 개발 과정 — `trace` | trace, debug, info, warn, error |
| 운영 과정 — `info` | info, warn, error |

- **로그 레이아웃** 설정으로 다양한 정보 제공 (시각, 레벨, 위치 등)

```text
레이아웃: %d{HH:mm:ss} [%-5level] %logger{1.} - %msg%n

log.info("message");
// 결과
12:30:45 [INFO ] c.m.MyService - message
```

- 다양한 **Appender** 제공 → 콘솔뿐 아니라 파일 등으로 출력

### 간단한 프로젝트 생성 — Maven (TODO: 01)

- Maven으로 프로젝트를 만들고 의존성을 추가해서 실습 진행
  - https://maven.apache.org/index.html
  - Maven 자체 설명은 [Junit — APPENDIX: Maven](03_Junit.md#appendix-maven)

### 의존성 추가 및 사용

- **Spring Boot는 logback을 slf4j 기본 구현체로 사용**
- 일반 Maven 프로젝트에서는 logback 의존성을 직접 추가 (교재 슬라이드는 그림이라 아래는 일반적인 형태)

```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.5.x</version>
</dependency>
```

- logger 사용: 클래스마다 `LoggerFactory.getLogger(클래스)`로 logger를 하나 만들어 둠
  - 메시지 안의 `{}`는 뒤에 오는 인자로 치환됨

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerTest {
    private static final Logger log = LoggerFactory.getLogger(LoggerTest.class);

    public static void main(String[] args) {
        log.trace("trace: {}", "trace level");
        log.debug("debug: {}", "debug level");
        log.info("info: {}", "info level");
        log.warn("warn: {}", "warn level");
        log.error("error: {}", "error level");
    }
}
/* 결과 (logback.xml 없이 실행)
21:44:48.342 [main] DEBUG com.ssafy.live.LoggerTest -- debug: debug level
21:44:48.343 [main] INFO  com.ssafy.live.LoggerTest -- info: info level
21:44:48.343 [main] WARN  com.ssafy.live.LoggerTest -- warn: warn level
21:44:48.343 [main] ERROR com.ssafy.live.LoggerTest -- error: error level
*/
```

- **trace는 어디 갔지?** → 설정 파일이 없으면 기본 레벨이 debug라서 trace는 출력되지 않음 → 아래 설정으로 레벨을 바꿈

### logger 설정 1 — logback.xml

- `logback.xml`을 **클래스 패스**(`src/main/resources`)에 두면 자동으로 읽음
  - https://logback.qos.ch/manual/configuration.html#automaticConf

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration>
<configuration>
    <import class="ch.qos.logback.classic.encoder.PatternLayoutEncoder"/>
    <import class="ch.qos.logback.core.ConsoleAppender"/>

    <!-- 출력 대상(appender)과 로그의 형태(pattern) -->
    <appender name="STDOUT" class="ConsoleAppender">
        <encoder class="PatternLayoutEncoder">
            <pattern>%d{HH:mm:ss} [%thread] [%-5level] %C{1}.%M.%L- %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 특정 패키지만 따로 레벨 지정 -->
    <logger name="com.ssafy" level="trace" additivity="false">
        <appender-ref ref="STDOUT"/>
    </logger>

    <!-- root: 전체에서의 출력 레벨과 출력 시 사용할 appender -->
    <root level="trace">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

| 요소 | 의미 |
|---|---|
| `<appender>` | 로그를 **어디로** 보낼지 (ConsoleAppender = 콘솔) |
| `<pattern>` | 로그의 **형태** |
| `<root level>` | 전체의 출력 레벨 + 사용할 appender |
| `<logger name level>` | 특정 패키지(`com.ssafy`)의 레벨을 따로 지정 |

| 패턴 | 출력 |
|---|---|
| `%d{HH:mm:ss}` | 시각 |
| `%thread` | 스레드 이름 |
| `%-5level` | 레벨 (5칸 왼쪽 정렬) |
| `%C{1}` / `%M` / `%L` | 클래스 이름 / 메서드 이름 / 줄 번호 |
| `%msg%n` | 메시지 + 줄바꿈 |

### logger 설정 2 — RollingFileAppender

- 로그를 **파일**로 남기고, 날짜별로 새 파일을 만들어 관리
  - https://logback.qos.ch/manual/appenders.html#RollingFileAppender

```xml
<import class="ch.qos.logback.core.rolling.RollingFileAppender"/>
<import class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy"/>

<appender name="STDOUT" class="ConsoleAppender"> ... </appender>

<appender name="FILE" class="RollingFileAppender">
    <file>logFile.log</file>
    <rollingPolicy class="TimeBasedRollingPolicy">
        <fileNamePattern>logFile.%d{yyyy-MM-dd}.log</fileNamePattern>  <!-- 하루 단위로 새 파일 -->
        <maxHistory>30</maxHistory>                                    <!-- 30개(30일)까지 보관 -->
        <totalSizeCap>3GB</totalSizeCap>                               <!-- 전체 용량 상한 -->
    </rollingPolicy>
    <encoder class="PatternLayoutEncoder">
        <pattern>%d{HH:mm:ss.SSS} %-4relative [%thread] %-5level %logger{35} -%kvp- %msg%n</pattern>
    </encoder>
</appender>

<root level="trace">
    <appender-ref ref="STDOUT"/>
    <appender-ref ref="FILE"/>   <!-- 콘솔과 파일 둘 다 출력 -->
</root>
```

- root에 appender를 여러 개 연결하면 같은 로그가 **콘솔과 파일 모두**에 남음
