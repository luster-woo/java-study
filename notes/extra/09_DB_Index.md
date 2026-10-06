# DB Index

> 교재: 15기_자바전공_DB_Day06_0430오전_Index (전공) — 1. INDEX / 2. INDEX 설계 / 3. Index와 Execution Plan · 선택 강의

> [← DB Transaction](08_DB_Transaction.md) · [extra 목록](../README.md)

---

## 1. INDEX

### Index (색인)

- 데이터의 **검색 효율**을 높이기 위한 데이터베이스 객체
- 하나 이상의 컬럼을 기준으로 **정렬**해 두어 빠른 검색 지원
- 책의 색인과 비슷한 개념 — 본문을 처음부터 넘기지 않고 색인에서 쪽 번호를 찾아 바로 이동

```
색인(Index)          본문(Table)
AI  → p100    ───>   p100 ... AI 내용
DB  → p10     ───>   p10  ... DB 내용
```

- 테이블을 기반으로 **선택적으로** 생성
- MySQL은 다음 제약 조건에 인덱스를 **자동 생성**
  - Primary Key
  - Unique
  - Foreign Key

### 장점과 단점

| 장점 | 단점 |
|---|---|
| 조회 속도 향상 | 잦은 DML은 인덱스 엔트리 변경을 동반 → **성능 저하** |
| 특히 **대량 데이터** 처리 시 효과적 | 인덱스 → 테이블로 이동하는 **Random Access** 발생 |
| | **추가 저장 공간** 필요 |

### B-Tree 인덱스 구조

- **Balanced Tree** — 모든 리프 노드가 **같은 깊이**에 존재
  - 어느 리프까지든 탐색 깊이가 일정 → **일관된 성능** 보장
- 각 노드가 자식 노드 포인터뿐 아니라 **실제 데이터도 함께** 가질 수 있는 탐색 트리

```
                [ Key:data ]                 root      ↓ 수직적 탐색
          /                    \
    [ Key:data ]          [ Key:data ]       branch
     /       \              /       \
[Key:data] [Key:data]  [Key:data] [Key:data] leaf (정렬된 상태)
```

### B+Tree 구조

- **MySQL InnoDB의 인덱스 구조**
- 데이터는 **리프 노드에만** 저장 (root·branch는 Key만)
- 리프 노드끼리 **연결 리스트**로 연결 → **Range Scan에 최적**

```
                   [ Key ]                   root      ↓ 수직적 탐색
            /                 \
       [ Key ]             [ Key ]           branch
       /     \             /     \
[Key:data]<->[Key:data]<->[Key:data]<->[Key:data]   leaf (정렬, → 수평적 탐색)
```

| 구분 | B-Tree | B+Tree |
|---|---|---|
| 데이터 위치 | 모든 노드 | 리프 노드만 |
| 리프 연결 | 없음 | 연결 리스트 |
| 범위 검색 | 트리를 다시 오르내림 | 리프를 옆으로 따라감 |

### Clustered Index

- 리프 노드에 **Key와 record(행 전체)** 가 함께 저장되는 구조
- 일반적으로 Key는 **PK**
- MySQL의 Key 선택 규칙: **PK 컬럼 → NOT NULL UNIQUE 컬럼 → 고유한 값을 갖는 컬럼을 내부적으로 만들어 사용**
- Key가 record의 **저장 위치를 결정** (Key 순서대로 행이 정렬되어 저장)
- 테이블당 **단 하나만** 존재 가능

```
              [ Key ]
         /              \
     [ Key ]          [ Key ]
     /    \           /    \
[Key: record] [Key: record] [Key: record] [Key: record]   ← 리프 = 실제 행
```

### Secondary Index (보조 인덱스)

- Clustered Index 이외에 **추가로 생성**하는 인덱스
- 리프 노드에 **Key와 PK** 가 함께 저장
- **Key lookup** 필요 — 찾은 PK로 Clustered Index를 **다시 탐색**
- 테이블당 **여러 개** 생성 가능

```
① Secondary Index 탐색          ② PK로 Clustered Index 재탐색
   [ Key ]                          [ PK ]
  /      \                         /      \
[Key: PK] [Key: PK]  ──PK──>   [PK: record] [PK: record]
```

---

## 2. INDEX 설계

### Index 논리 유형

| 구분 | 유형 | 의미 |
|---|---|---|
| 컬럼 수 | **Single Index** | 하나의 컬럼으로 구성 |
| 컬럼 수 | **Concatenated Index** | 여러 컬럼으로 구성 (복합 컬럼 인덱스, 멀티 컬럼 인덱스) |
| 중복 값 여부 | **Unique Index** | 컬럼 값이 고유 |
| 중복 값 여부 | **Non-unique Index** | 컬럼 값에 중복 허용 |
| | **Covering Index** | SQL 실행에 필요한 **모든 컬럼을 가진** 인덱스 (테이블 접근 불필요) |
| | **Functional Based Index** | 함수를 적용한 결과를 기반으로 만든 인덱스 |

### Concatenated Index (결합 인덱스)

- 여러 컬럼의 조합으로 하나의 인덱스 생성 — 예: `(column1, column2)`
  - 조건절에 `column1` 조건만 있어도 인덱스 이용 가능
  - 조건절에 `column1 + column2` 조건이 있어도 인덱스 이용 가능
- 단일 인덱스 여러 개를 merge하는 것보다 **결합 인덱스가 유리**
- 자주 쓰이는 **조건 형태를 분석**해 구성 컬럼의 **순서**를 정함
  - **선행 컬럼**의 조건 형태가 인덱스 검색 범위를 줄일 수 있는지를 결정 (매우 중요)

| 조건 유형 | 연산자 |
|---|---|
| **점 조건** | `=`, `IN` |
| **선분 조건** | `=`, `IN` 이 아닌 연산자 (`LIKE`, `BETWEEN`, `<`, `>` ...) |

### 결합 인덱스 순서 비교

```sql
SELECT *
FROM 주문
WHERE 주문일자 BETWEEN '2026-01-01' AND '2026-01-02'
  AND 상품코드 = 'A';
```

| | CASE 1: `(주문일자, 상품코드)` | CASE 2: `(상품코드, 주문일자)` |
|---|---|---|
| 선행 컬럼 조건 | 선분 조건(`BETWEEN`) | 점 조건(`=`) |
| 탐색 범위 | 두 날짜 사이의 **모든 상품**을 읽으며 `상품코드 = 'A'` 를 걸러냄 | `상품코드 = 'A'` 안에서 두 날짜 구간만 읽음 |
| 결과 | 불필요한 인덱스 엔트리까지 스캔 | **필요한 범위만** 스캔 → 유리 |

```
CASE 1 (주문일자, 상품코드)          CASE 2 (상품코드, 주문일자)
01-01 A  ← 읽음                      A 01-01  ← 읽음
01-01 B  ← 읽고 버림                 A 01-02  ← 읽음
01-01 C  ← 읽고 버림                 B 01-01
01-02 A  ← 읽음                      B 01-02
01-02 B  ← 읽고 버림                 C 01-01
```

### 인덱스 설계 시 주의 사항

- `WHERE`, `JOIN`, `ORDER BY`, `GROUP BY` 절에 **자주 사용되는 컬럼** 고려
- **자주 검색되는** 컬럼 고려
- 컬럼의 **분포도가 10~15% 이내**일 때 적용하면 좋음
  - 중복 값이 적을수록 분포도가 좋음
  - **Cardinality가 높은** 컬럼 우선 (High Cardinality → 최적 효율)
  - 읽어야 할 레코드가 전체의 **20~25%를 넘으면** 인덱스 스캔보다 **Full Table Scan이 유리**할 수 있음
- **복합 컬럼 인덱스** 고려 — **점 조건은 앞에, 선분 조건은 마지막**에
- **커버링 인덱스** 고려
- 정기적인 **모니터링**과 그에 따른 최적화 필요

### 인덱스 관리 SQL

```sql
-- 인덱스 조회
SHOW INDEX FROM 테이블명;

-- 인덱스 생성
CREATE [UNIQUE] INDEX 인덱스명 ON 테이블명(컬럼명[, 컬럼명, ...]);

-- 인덱스 삭제
DROP INDEX 인덱스명 ON 테이블명;

-- 인덱스 비활성화 / 활성화 (옵티마이저가 사용하지 않게 / 다시 사용하게)
ALTER TABLE 테이블명 ALTER INDEX 인덱스명 INVISIBLE;
ALTER TABLE 테이블명 ALTER INDEX 인덱스명 VISIBLE;
```

```sql
-- 예: 위 CASE 2 결합 인덱스
CREATE INDEX idx_order_product_date ON 주문(상품코드, 주문일자);
```

---

## 3. Index와 Execution Plan

### 실행 계획

- **옵티마이저**가 작성한 SQL 실행 절차의 상세 명세서
- 쿼리 성능 저하 원인 파악, **인덱스 사용 여부** 확인, 조인 방식 분석 가능
- 확인 방법
  - MySQL Workbench의 **Visual Explain** 활용
  - SQL문 앞에 명령 붙이기

```sql
EXPLAIN SQL문;
EXPLAIN ANALYZE SQL문;
```

### EXPLAIN vs EXPLAIN ANALYZE

| 명령 | 차이 |
|---|---|
| `EXPLAIN` | 쿼리를 **실행하지 않고** 옵티마이저의 예상 계획만 보여줌 |
| `EXPLAIN ANALYZE` | 쿼리를 **실제로 실행**하고 계획과 함께 실제 처리 결과를 보여줌 |

### 대표적인 Scan 방식

| 방식 | 설명 |
|---|---|
| **Index Unique Scan** | Unique한 인덱스 값 **1개만** 탐색 |
| **Index Range Scan** | 범위의 시작점을 찾은 뒤 범위에 해당하는 만큼 탐색 |
| **Index Full Scan** | 인덱스의 처음부터 끝까지 모두 탐색 |
| **Index Skip Scan** | 인덱스를 **점프하며** 읽는 최적화 탐색 |
| **Full Table Scan** | 인덱스 없이 테이블 전체를 처음부터 끝까지 탐색 — 읽을 레코드가 전체의 20~25% 이상이면 옵티마이저가 선택 |

```
Unique scan   leaf: [ ][ ][■][ ][ ][ ][ ][ ]   한 건
Range scan    leaf: [ ][■][■][■][■][ ][ ][ ]   시작점부터 범위 끝까지
Full scan     leaf: [■][■][■][■][■][■][■][■]   처음부터 끝까지
```

---

## 면접 포인트

### 1. 인덱스를 쓰면 왜 조회가 빨라지고, 단점은 무엇인가요?

- 인덱스는 컬럼 값을 정렬해 둔 B+Tree 구조라 루트에서 리프까지 일정한 깊이로 내려가 원하는 값을 찾고, 리프가 연결 리스트로 이어져 범위 검색도 빠름
- 단점: INSERT·UPDATE·DELETE마다 인덱스 엔트리도 바뀌어 DML 성능이 떨어지고, 추가 저장 공간이 들고, 인덱스에서 테이블로 가는 Random Access가 생김
- 그래서 읽을 레코드가 전체의 20~25%를 넘으면 오히려 Full Table Scan이 유리할 수 있음

### 2. Clustered Index와 Secondary Index의 차이는?

- Clustered Index: 리프에 Key와 행 전체가 함께 저장되고, Key(보통 PK)가 행의 저장 위치를 정함 → 테이블당 하나
- Secondary Index: 리프에 Key와 PK만 저장 → 찾은 PK로 Clustered Index를 다시 탐색(key lookup)해야 함, 테이블당 여러 개 가능
- 쿼리에 필요한 컬럼을 모두 가진 커버링 인덱스면 이 재탐색 없이 인덱스만으로 처리 가능

### 3. 결합 인덱스의 컬럼 순서는 어떻게 정하나요?

- 선행 컬럼의 조건 형태가 검색 범위 축소 여부를 결정하므로, 자주 쓰이는 조건을 분석해 **점 조건(`=`, `IN`)은 앞에, 선분 조건(`BETWEEN`, `<`, `LIKE` 등)은 뒤에** 둠
- 예: `주문일자 BETWEEN ... AND 상품코드 = 'A'` 이면 `(상품코드, 주문일자)` 가 `(주문일자, 상품코드)` 보다 읽는 범위가 좁음
- 그 밖에 Cardinality가 높고 WHERE·JOIN·ORDER BY·GROUP BY에 자주 쓰이는 컬럼을 우선 고려

---

## 참고

- 교재의 `EXPLAIN ANYALIZE` 는 **`EXPLAIN ANALYZE`** 의 오타 (MySQL 8.0.18 이상 지원)
- 결합 인덱스 `(column1, column2)` 에서 **`column2` 조건만** 있으면 일반적으로 인덱스 범위 탐색을 못 함 → 이때 옵티마이저가 선행 컬럼 값마다 건너뛰며 읽는 것이 Index Skip Scan (MySQL 8.0.13 이상)
- InnoDB가 PK·NOT NULL UNIQUE가 모두 없을 때 내부적으로 만드는 키는 6바이트 row ID(`GEN_CLUST_INDEX`)
- `INVISIBLE` 인덱스는 MySQL 8.0부터 지원 — 삭제 전에 인덱스를 꺼서 성능 영향을 먼저 확인할 때 사용
