# DB Transaction

> 교재: 15기_자바전공_DB_Day05_0429오후_Tx (전공) — 1. Transaction / 2. MVCC / 3. Phantom Read와 잠금 메커니즘 · 선택 강의

> [← 멀티스레드](07_멀티스레드.md) · [extra 목록](../README.md) · [DB Index →](09_DB_Index.md)

---

## 1. Transaction

### 트랜잭션(Transaction)

- **원자성**을 가지는 하나의 논리적인 작업 단위 (예: 금융 거래, 재고 관리, 예약 시스템)
- 나눠서 실행할 수 없고 **한 번에 처리해야 하는 작업들의 모음**
- 여러 SQL 문을 하나의 작업 단위로 묶어 처리 → **All or Nothing**
  - 모두 성공 → **COMMIT**, 하나라도 실패 → **ROLLBACK**
- 데이터의 **무결성**을 보장하는 핵심 개념

```
계좌 이체 서비스
1. 출금 계좌 잔액 조회
2. 출금 처리
3. 입금 처리   ← 여기서 장애가 나면? 돈은 빠졌는데 들어가지 않음
→ 1~3 을 하나의 트랜잭션으로 묶어 3이 실패하면 2까지 되돌림
```

### 트랜잭션의 특징 (ACID)

| 특징 | 의미 |
|---|---|
| **Atomicity** (원자성) | 모든 연산이 성공하거나, 하나라도 실패하면 전체 취소 |
| **Consistency** (일관성) | 실행 전후 DB 상태가 일관성 유지 — 무결성(Integrity)과 비즈니스 규칙(Business Rules)이 항상 지켜지는 상태 |
| **Isolation** (고립성) | 한 트랜잭션이 끝날 때까지 다른 트랜잭션이 그 결과를 참조할 수 없음 |
| **Durability** (지속성) | 성공적으로 완료된 결과는 영구적으로 반영 |

- Isolation을 구현하는 수단
  - **잠금(Lock)**: 동시성을 제어하기 위한 기능
  - **격리 수준(Isolation Level)**: 트랜잭션 간 작업 내용을 어떻게 공유·차단할지 정하는 레벨

### 격리 수준

| 격리 수준 | 설명 | 발생할 수 있는 문제 |
|---|---|---|
| READ UNCOMMITTED | 커밋되지 않은 데이터도 읽음 | **Dirty Read** |
| READ COMMITTED | 커밋된 데이터만 읽음 | **Non-Repeatable Read** |
| REPEATABLE READ | 트랜잭션 안에서 같은 데이터를 다시 조회하면 항상 같은 결과 보장 | **Phantom Read** |
| SERIALIZABLE | 가장 엄격 — 동시 실행을 막음 | **성능 저하** |

- 아래로 갈수록 격리 수준이 높아짐 → 안전하지만 동시성은 떨어짐

```sql
-- MySQL 격리 수준 확인
SELECT @@TRANSACTION_ISOLATION;

-- MySQL 격리 수준 명시적 변경 (현재 세션)
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
```

### TCL (Transaction Control Language)

| 명령어 | 의미 |
|---|---|
| `START TRANSACTION` (`BEGIN`) | 명시적 트랜잭션 시작 — 이 트랜잭션 동안만 **자동 커밋 해제** (세션 전체에 적용되는 것이 아님) |
| `COMMIT` | 트랜잭션을 성공적으로 완료하고 변경 사항 저장 |
| `ROLLBACK [TO 세이브포인트명]` | 트랜잭션 취소, 변경 사항 되돌림 |
| `SAVEPOINT 세이브포인트명` | 트랜잭션 안에 특정 지점을 저장 → **부분 롤백** 가능 |

```sql
START TRANSACTION;
-- DML ...
SAVEPOINT P1;
-- DML ...
ROLLBACK TO P1;   -- P1 이후 DML만 취소
-- ROLLBACK;      -- 전체 취소
COMMIT;           -- 남은 변경 확정
```

### 교착 상태 (Dead Lock)

- 두 개 이상의 트랜잭션이 **서로가 가진 락을 무한정 기다리는 순환 대기** 상태

```
T1: A 행 잠금 ──────> B 행 잠금 요청 (T2가 보유 → 대기)
T2: B 행 잠금 ──────> A 행 잠금 요청 (T1이 보유 → 대기)
        → 서로를 기다리며 진행 불가
```

- MySQL InnoDB: 순환 의존성을 **자동 감지** → 즉시 에러 반환 및 롤백
  - 상대적으로 **롤백이 쉬운 트랜잭션**을 골라 롤백

### 교착 상태 방지 기법

| 기법 | 내용 |
|---|---|
| 일관된 자원 획득 순서 (Resource Ordering) | 모든 트랜잭션이 테이블·행에 접근하는 **순서를 동일하게** 규정 → 환형 대기 원천 차단 |
| 잠금 타임아웃 (Lock Timeout) | 무한 대기 방지 — 임계 시간 초과 시 트랜잭션 자동 취소 |
| 트랜잭션 범위 최소화 | 트랜잭션 내부 로직을 간결하게, **잠금 보유 시간** 최소화 → 충돌 확률 감소 |

### 트랜잭션 성능 최적화

- 트랜잭션 범위 최소화 → 잠금 유지 시간 감소
- 적절한 격리 수준 선택 → 동시성 개선
- 인덱스 활용 → 성능 향상

---

## 2. MVCC

### 동시성 제어의 딜레마

- 다중 사용자 환경에서 동시 접근의 위험
  - **갱신 손실(Lost Updates)**
  - **더티 리드(Dirty Reads)**
- 데이터 보호를 위한 **직렬화**: 한 트랜잭션이 읽거나 쓸 때 다른 트랜잭션 차단
  - 대기 시간 증가
  - **읽기 작업까지 병목** 발생
- 해결책 → **MVCC**

### MVCC (Multi Version Concurrency Control)

- **읽기(Read)와 쓰기(Write)의 동시성**을 보장하는 다중 버전 관리
- 여러 트랜잭션을 동시에 처리할 때 각 트랜잭션이 **자신만의 일관된 데이터 뷰**를 가지게 하는 기술

| 핵심 구성 요소 | 내용 |
|---|---|
| **Hidden Columns** | 각 레코드에 숨겨진 컬럼 — 수정 트랜잭션 ID(`DB_TRX_ID`), 롤백 포인터(`DB_ROLL_PTR`) |
| **Undo Log** | 수정 전 원본 데이터를 별도 저장 |
| **Read View** | 조회 시작 시점의 **활성 트랜잭션 목록 스냅샷** |

### 데이터가 수정될 때

- 이전 값 → **Undo 로그**에 보관
- 새로운 값 → 테이블에 즉시 반영
- 다른 트랜잭션은 **자신의 시작 시점**에 맞는 버전을 읽음

```
테이블(최신)          Undo Log
[TRX 9 | 수정 중] ──DB_ROLL_PTR──> [TRX 4 | 5,000] ──> ...
```

- 결과: **읽기와 쓰기가 서로를 방해하지 않음** → 동시성 크게 향상
  - 쓰기가 활발해도 읽기가 차단되지 않고, 스냅샷으로 일관된 데이터를 즉시 조회

### Read View

- 생성되는 순간 세 가지 정보를 저장해 데이터의 **가시성** 판단

| 정보 | 의미 |
|---|---|
| `up_limit_id` | 현재 활성 트랜잭션 중 **가장 작은 ID** (최소 활성 ID) |
| `low_limit_id` | 지금까지 발급된 ID 중 가장 큰 ID + 1 (**발급 예정 ID**) |
| active transaction list | 현재 실행 중인 트랜잭션 목록 |

- 레코드의 `DB_TRX_ID` 로 판단하는 규칙 (아래 시나리오 정리)

| `DB_TRX_ID` 위치 | 판단 |
|---|---|
| `< up_limit_id` | 이미 커밋된 과거 데이터 → **보임** |
| `≥ low_limit_id` | Read View 이후에 생긴 미래 데이터 → 안 보임, Undo로 이전 버전 |
| 그 사이 + 활성 목록에 있음 | 아직 작업 중 → 안 보임, Undo로 이전 버전 |

### 시나리오 1 — 과거 데이터 즉시 읽기

- Read View: `up_limit 8`, `low_limit 11`, 활성 `[8, 10]`

| DB_TRX_ID | DB_ROLL_PTR | VALUE | 판단 |
|---|---|---|---|
| 5 | PTR_0 | 10,000 KRW | 5 < 8 → 커밋 완료 데이터, **즉시 조회** |

### 시나리오 2 — 조작 중인 데이터

- Read View: `up_limit 8`, `low_limit 10`, 활성 `[8, 9]`

| 위치 | DB_TRX_ID | VALUE | 판단 |
|---|---|---|---|
| 현재 레코드 | 9 | 수정 중... | 8 ≤ 9 < 10 이고 활성 목록에 있음 → **SKIP** |
| Undo Log (U1) | 4 | 5,000 KRW | 4 < 8 → **보임** → 5,000 KRW 조회 |

### 시나리오 3 — 미래의 데이터

- Read View: `up_limit 8`, `low_limit 12`, 활성 `[8]`

| 위치 | DB_TRX_ID | VALUE | 판단 |
|---|---|---|---|
| 현재 레코드 | 15 | 50,000원 | 15 ≥ 12 → 미래 데이터 → **SKIP** |
| Undo Log (U2) | 8 | 20,000원 | 활성 목록에 있는 TRX → **SKIP** |
| Undo Log (U1) | 7 | 1,000원 | 7 < 8 → **보임** → 1,000원 조회 |

> 참고: 교재 시나리오 3 설명의 "레코드 TRX_ID(9) 확인"은 그림의 값(15)과 맞지 않음 — "TRX_ID(15) 확인"이 맞음.

---

## 3. Phantom Read와 잠금 메커니즘

### Phantom Read

- 한 트랜잭션 안에서 같은 조건으로 여러 번 조회할 때 **없던 새 데이터가 함께 조회**되는 상황

```sql
-- T1: 범위 조회 (일반 SELECT → MVCC 스냅샷 읽기)
SELECT col, col2 FROM t_table WHERE id > 10;              -- [12, 15]

-- T2: 범위 안에 새 데이터 삽입 후 커밋
INSERT INTO t_table (id, col, col2) VALUES (13, ..., ...);
COMMIT;

-- T1: 같은 범위를 잠금 읽기로 재조회 → Current Read (최신 커밋 데이터를 읽음)
SELECT col, col2 FROM t_table WHERE id > 10 FOR SHARE;    -- [12, 13, 15]  ← 없던 13 등장
```

- 일반 SELECT는 스냅샷을 읽지만, `FOR SHARE`·`FOR UPDATE` 같은 **잠금 읽기는 최신 데이터(Current read)** 를 읽음 → 새 행이 보임

### 잠금 읽기

| 구분 | 문법 | 의미 | 허용/차단 |
|---|---|---|---|
| **공유 잠금(S)** | `SELECT ~ FOR SHARE` | 내가 읽는 동안 아무도 고치지 마 | 읽기 허용, **수정 차단** |
| **배타 잠금(X)** | `SELECT ~ FOR UPDATE` | 내가 수정할 거니 아무도 건드리지 마 | **읽기/쓰기 모두 차단** |

### MySQL InnoDB 엔진의 해결 방법

- InnoDB는 **Next-Key Lock** 을 기본으로 사용해 Phantom Read를 효과적으로 차단

| 잠금 | 대상 |
|---|---|
| **Record Lock** | 인덱스 레코드 자체 |
| **Gap Lock** | 레코드와 레코드 **사이의 빈 공간** |
| **Next-Key Lock** | Gap Lock + Record Lock — 자신과 **앞쪽 간격**을 동시에 보호 |

```
id 인덱스:   ... 10 ][ (10,12) ][ 12 ][ (12,15) ][ 15 ][ (15,+∞) ...
WHERE id > 10 FOR SHARE → 12, 15 레코드 + 그 사이 간격까지 잠금
                        → T2가 13을 INSERT 하려 하면 대기 → 새 행이 끼어들 수 없음
```

---

## 면접 포인트

### 1. 트랜잭션의 ACID와 격리 수준을 설명해 주세요.

- 트랜잭션은 나눠서 실행할 수 없는 논리적 작업 단위로, 모두 성공하면 COMMIT, 하나라도 실패하면 ROLLBACK 함(All or Nothing)
- ACID: 원자성(전부 아니면 전무), 일관성(전후 무결성·규칙 유지), 고립성(끝나기 전 결과를 다른 트랜잭션이 참조 불가), 지속성(커밋 결과는 영구 반영)
- 격리 수준은 READ UNCOMMITTED(Dirty Read) → READ COMMITTED(Non-Repeatable Read) → REPEATABLE READ(Phantom Read) → SERIALIZABLE(성능 저하) 순으로 높아지고, 높을수록 안전하지만 동시성이 떨어짐

### 2. MVCC가 무엇이고, 어떻게 읽기와 쓰기가 서로를 막지 않나요?

- 데이터를 수정하면 이전 값은 Undo Log에 남기고 새 값은 테이블에 바로 반영하는 다중 버전 관리 방식
- 레코드마다 숨은 컬럼(`DB_TRX_ID`, `DB_ROLL_PTR`)이 있고, 조회 시점의 Read View(최소 활성 ID, 발급 예정 ID, 활성 목록)와 비교해 보이는 버전을 고름
- 보이지 않는 버전(작업 중이거나 미래의 데이터)이면 롤백 포인터를 따라 Undo Log의 이전 버전을 읽음 → 쓰기 중에도 읽기가 차단되지 않음

### 3. Phantom Read는 무엇이고 InnoDB는 어떻게 막나요?

- 같은 조건으로 다시 조회했을 때 다른 트랜잭션이 삽입·커밋한 새 행이 나타나는 현상 — 특히 `FOR SHARE`/`FOR UPDATE` 같은 잠금 읽기(Current read)는 최신 데이터를 읽어 발생할 수 있음
- InnoDB는 레코드 사이 빈 공간을 잠그는 Gap Lock과 Record Lock을 합친 **Next-Key Lock**을 기본으로 사용 → 조회 범위에 새 행이 삽입되지 못하게 막음

---

## 참고

- MySQL InnoDB의 기본 격리 수준은 **REPEATABLE READ** — `SELECT @@TRANSACTION_ISOLATION;` 결과 `REPEATABLE-READ`
- `@@transaction_isolation` 은 MySQL 8.0 이름 (5.7 이전은 `@@tx_isolation`)
- 교재의 잠금 타임아웃 `LOCK_TIMEOUT` 은 SQL Server의 설정(`SET LOCK_TIMEOUT`) 이름. MySQL InnoDB에서 같은 역할은 `innodb_lock_wait_timeout`(기본 50초)
- 교재 "Next Key Kock" 은 **Next-Key Lock** 의 오타
