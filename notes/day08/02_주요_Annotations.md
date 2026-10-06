# 주요 Annotations

> 교재: 15기_자바전공_03_springboot_0507 (전공) — 2. 주요 Annotations

> [← Spring Boot](01_Spring_Boot.md) · [day08 목록](../README.md) · [Servlet Web To Spring Boot →](03_Servlet_Web_To_Spring_Boot.md)

---

## @SpringBootApplication

### @SpringBootApplication

- Spring Boot 애플리케이션의 **시작점 클래스**에 선언
- 내부적으로 세 애너테이션을 합친 것

```
@SpringBootApplication
 ├─ @SpringBootConfiguration   → @Configuration (설정 클래스, 명시적 빈 등록)
 ├─ @EnableAutoConfiguration   → 의존성 기반 자동 설정
 └─ @ComponentScan             → @Component 계열 빈 스캔
```

```java
@Target(ElementType.TYPE)
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = {
        @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
        @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {

    @AliasFor(annotation = EnableAutoConfiguration.class)
    Class<?>[] exclude() default {};          // 자동 설정에서 제외할 클래스
                                              // 예) exclude = {DataSourceAutoConfiguration.class}

    @AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
    String[] scanBasePackages() default {};

    @AliasFor(annotation = ComponentScan.class, attribute = "basePackageClasses")
    Class<?>[] scanBasePackageClasses() default {};

    @AliasFor(annotation = ComponentScan.class, attribute = "nameGenerator")
    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    @AliasFor(annotation = Configuration.class)
    boolean proxyBeanMethods() default true;
}
```

- **`@AliasFor`**: `@SpringBootApplication`의 속성을 내부 애너테이션 속성으로 전달
  - `@SpringBootApplication(exclude = ...)` → `@EnableAutoConfiguration(exclude = ...)`
  - `@SpringBootApplication(scanBasePackages = ...)` → `@ComponentScan(basePackages = ...)`

### @SpringBootConfiguration

- 내부적으로 **`@Configuration`**을 가짐 → 시작 클래스에서도 `@Bean`으로 명시적 빈 등록 가능
- 하지만 일반적으로 **전용 설정 클래스**를 따로 만드는 것을 권장

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@Indexed
public @interface SpringBootConfiguration {
    @AliasFor(annotation = Configuration.class)
    boolean proxyBeanMethods() default true;
}
```

- **`proxyBeanMethods`**: `@Configuration` 클래스 안에서 `@Bean` 메서드를 호출할 때 **프록시를 쓸지** 여부
  - `false`로 하면 속도는 조금 빨라질 수 있지만, `@Bean` 메서드끼리 참조할 때 싱글톤이 깨질 위험

### @EnableAutoConfiguration

- **의존성에 근거해 자동 설정** 지원 (예: starter-web이 있으면 내장 Tomcat·Spring MVC 설정)

```java
@Target(ElementType.TYPE)
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
    Class<?>[] exclude() default {};   // 자동 설정에서 제외할 클래스
                                       // 예) exclude = {DataSourceAutoConfiguration.class}
    String[] excludeName() default {};
}
```

- 제공되는 자동 구성 목록: Spring Boot 문서 Appendix "Auto-configuration Classes"

#### 예: Tomcat → Jetty

- `spring-boot-starter-web`은 기본으로 `spring-boot-starter-tomcat`을 포함
- Tomcat을 빼고(`exclusion`) Jetty starter를 넣으면 자동 설정이 Jetty로 바뀜

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

### @ComponentScan

- **묵시적 빈 등록**(`@Component` 계열)으로 선언된 빈을 scan
- 언제나 **`@SpringBootApplication`이 선언된 클래스의 패키지와 하위 패키지**를 스캔 중
  - 그 밖의 별도 패키지에 빈을 두려면 따로 scan 지정 필요 (`scanBasePackages` 등)

```java
@ComponentScan(excludeFilters = {
        // @SpringBootTest에서 등록한 빈 등 불필요한 빈은 제외
        @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
        // 자동 환경 구성 빈들은 이미 처리되었으므로 제외
        @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class)
})
```

---

## @SpringBootTest

### @SpringBootTest

- 스프링 애플리케이션의 **통합 테스트**를 위한 애너테이션

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@BootstrapWith(SpringBootTestContextBootstrapper.class)
@ExtendWith(SpringExtension.class)    // JUnit 5에 스프링 테스트 기능 연결
public @interface SpringBootTest { }
```

| 구분 | 통합 테스트 (`@SpringBootTest`) | slice test |
|---|---|---|
| 로딩 범위 | 애플리케이션 **전체** 구동 | **필요한 모듈만** 로딩 |
| 특징 | 간단하지만 무거움 | 번거롭고 복잡하지만 가벼움 |
| 용도 | 전체 흐름 확인 | 특정 모듈 단위 테스트 (교재 권장) |

- slice test 목록: Spring Boot 문서 Appendix "Test Auto-configuration Slices"

---

## 참고

- slice test 예: `@WebMvcTest`(웹 계층만), `@DataJpaTest`(JPA 계층만) 등 — 교재는 문서 링크만 제시
- `proxyBeanMethods`가 실제로 어떻게 동작하는지(`@Configuration` 프록시)는 [Proxy](05_Proxy.md)에서 다룸
