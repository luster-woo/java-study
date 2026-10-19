# 실전! 스프링 부트와 JPA 활용1 - 웹 애플리케이션 개발 (인프런 · 김영한)

> 강의 노트 PDF(`springjpa1-v20260118`) 7개 장 + 강의 소스코드(`jpashop-v20250325`)를 함께 정리한 노트.
> [JPA 기본편](../jpa-basic/README.md)에서 배운 걸 **스프링 부트 + 실제 쇼핑몰 예제(jpashop)** 로 적용하는 강의다.

> 실습 폴더: [`jpa/jpashop`](../../jpa/README.md) (빈 프로젝트에서 따라 치기, 완성본은 Downloads에 그대로)

## 목차

| # | 장 | 핵심 키워드 |
|---|---|---|
| 01 | [프로젝트 환경설정](01_프로젝트_환경설정.md) | start.spring.io, build.gradle, H2, application.yml, 쿼리 파라미터 로그(p6spy) |
| 02 | [도메인 분석 설계](02_도메인_분석_설계.md) | 엔티티 8개 설계·매핑, **엔티티 설계 시 주의점**(Setter·LAZY·컬렉션 초기화·네이밍) |
| 03 | [애플리케이션 구현 준비](03_애플리케이션_구현_준비.md) | 요구사항, 계층형 아키텍처, 패키지 구조 |
| 04 | [회원 도메인 개발](04_회원_도메인_개발.md) | Repository(EntityManager), Service(`@Transactional(readOnly)`), 생성자 주입, 테스트 |
| 05 | [상품 도메인 개발](05_상품_도메인_개발.md) | 엔티티에 비즈니스 로직(재고), persist/merge |
| 06 | [주문 도메인 개발](06_주문_도메인_개발.md) | 생성 메서드, 도메인 모델 패턴, 주문 테스트, **동적 쿼리**(JPQL 문자열·Criteria) |
| 07 | [웹 계층 개발](07_웹_계층_개발.md) | 타임리프, 폼 객체, **변경 감지 vs 병합(merge)** ★ |

## 강의 소스코드 지도 (`jpashop-v20250325`)

```
jpabook.jpashop
├─ JpashopApplication            @SpringBootApplication
├─ Hello, HelloController        1장 동작 확인용
├─ domain/                       엔티티 (모든 계층에서 사용)
│  ├─ Member, Order, OrderItem, Delivery, Category, Address(@Embeddable)
│  ├─ OrderStatus(ORDER, CANCEL), DeliveryStatus(READY, COMP), OrderSearch(검색 조건)
│  └─ item/ Item(abstract, SINGLE_TABLE) ← Book(B), Album(A), Movie(M)
├─ exception/NotEnoughStockException
├─ repository/                   EntityManager 직접 사용
│  ├─ MemberRepository           findOne, findAll, findByName
│  ├─ ItemRepository             save(persist/merge), findOne, findAll
│  └─ OrderRepository            save, findOne, findAllByString, findAllByCriteria
├─ service/                      비즈니스 로직 위임, 트랜잭션
│  ├─ MemberService              join(중복 검증), findMembers, findOne
│  ├─ ItemService                saveItem, updateItem(변경 감지), findItems, findOne
│  └─ OrderService               order, cancelOrder, findOrders
└─ web/                          컨트롤러 + 폼 객체
   ├─ HomeController, MemberController(+MemberForm), ItemController(+BookForm), OrderController
resources/
├─ application.yml
├─ static/  (bootstrap 4.3.1, jumbotron-narrow.css, index.html)
└─ templates/ home, fragments/{header,bodyHeader,footer}, members/, items/, order/
test/ service/MemberServiceTest, OrderServiceTest
```

| URL | 컨트롤러 메서드 | 화면 |
|---|---|---|
| `GET /` | `HomeController.home` | home |
| `GET/POST /members/new` | `MemberController.createForm / create` | members/createMemberForm |
| `GET /members` | `MemberController.list` | members/memberList |
| `GET/POST /items/new` | `ItemController.createForm / create` | items/createItemForm |
| `GET /items` | `ItemController.list` | items/itemList |
| `GET/POST /items/{id}/edit` | `ItemController.updateItemForm / updateItem` | items/updateItemForm |
| `GET/POST /order` | `OrderController.createForm / order` | order/orderForm |
| `GET /orders` | `OrderController.orderList` | order/orderList |
| `POST /orders/{id}/cancel` | `OrderController.cancelOrder` | redirect:/orders |

## 영상과 다른 점 (강의 영상은 Spring Boot 2.x, 메뉴얼·소스는 3.x)

| 영상 | 지금 (메뉴얼·소스) |
|---|---|
| Spring Boot 2.1.x, Java 8 | **Spring Boot 3.4.4, Java 17** (21도 가능) |
| `javax.persistence`, `javax.validation` | **`jakarta.persistence`, `jakarta.validation`** |
| JUnit4 `@RunWith(SpringRunner.class)` | **JUnit5, `@RunWith` 제거** |
| `org.junit.Test` | `org.junit.jupiter.api.Test` |
| `@Test(expected = X.class)` | **`assertThrows(X.class, () -> ...)`** |
| `assertEquals(메시지, 기대, 실제)` | **`assertEquals(기대, 실제, 메시지)`** |
| H2 1.4.199, URL `;MVCC=TRUE` | **H2 2.1.214 이상, `;MVCC=TRUE` 제거** |
| `org.hibernate.type: trace` | **`org.hibernate.orm.jdbc.bind: trace`** |
| p6spy 1.5.6 | 메뉴얼: 1.12.1 (Boot 3.x) / 2.0.0 (Boot 4.x), 소스: 1.10.0 |
| `th:replace="fragments/header :: header"` | **`th:replace="~{fragments/header :: header}"`** |
| validation 모듈 없음 | **`spring-boot-starter-validation` 직접 추가** |
| IntelliJ 빌드 | **Build and run using: Gradle** |

## 이 강의의 핵심 규칙

1. 엔티티는 **Getter만 열고 Setter는 최소화**, 변경은 의미 있는 비즈니스 메서드로
2. **모든 연관관계 LAZY**, 컬렉션은 필드에서 초기화
3. 비즈니스 로직은 엔티티에 (**도메인 모델 패턴**), 서비스는 위임 + 트랜잭션
4. 화면 데이터는 **폼 객체/DTO**, 엔티티는 순수하게
5. 수정은 **merge 말고 변경 감지** — 서비스에 id + 바꿀 값만 넘겨서 영속 엔티티를 조회해 변경
