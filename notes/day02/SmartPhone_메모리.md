# SmartPhone 손컴파일 & 메모리 동작

코드: `src/day02/b_smartphone/SmartPhone.java`, `SmartPhoneTest.java`

## 실행 결과

```
1.0 : 010
띠
띠
띠
1.0 : 010-111-1111
2.0 : 010
```

## 변수 종류와 저장 위치

| 변수 | 종류 | 저장 위치 | 생성 시점 | 소멸 시점 |
|---|---|---|---|---|
| `osVer` | 클래스(static) 변수 | Method Area (Static 영역) | 클래스 로딩 시 | 프로그램 종료 |
| `number` | 멤버(인스턴스) 변수 | Heap (객체 안) | `new` 할 때 | GC가 객체 정리 |
| `to` | 파라미터 변수 | Stack (call 프레임) | `call()` 호출 | `call()` 종료 |
| `msg`, `i` | 로컬 변수 | Stack (call 프레임) | 선언 시 | 블록/메서드 종료 |
| `sphone`, `sphone2` | 로컬 변수 (참조형) | Stack (main 프레임) | 선언 시 | `main()` 종료 |

## 단계별 메모리

### ① 클래스 로딩

```
[Static 영역]            [Stack]                [Heap]
SmartPhone.osVer = 1.0   main: args
```

`main` 실행 전에 클래스가 로딩되면서 `osVer`가 만들어진다. 객체가 없어도 존재한다.

### ② `sphone = new SmartPhone(); sphone.number = "010";`

```
[Static 영역]            [Stack]                [Heap]
osVer = 1.0              main: args             0x100 SmartPhone
                               sphone = 0x100 ──▶  number = "010"
```

- `new` → Heap에 객체 생성, `number`는 처음에 `null`(기본값)
- 출력 `sphone.osVer` → 객체 안이 아니라 **Static 영역**의 값 1.0을 읽음 → `1.0 : 010`

### ③ `sphone.call("011");`

```
[Static 영역]            [Stack]                [Heap]
osVer = 1.0              call: to  = "011"      0x100 number = "010"
                               msg = "띠"
                               i   = 0 → 1 → 2 → 3(종료)
                         main: args, sphone = 0x100
```

- 메서드를 호출하면 Stack 위에 **call 프레임**이 쌓인다.
- `띠` 3번 출력 후 call 프레임이 통째로 사라진다 (`to`, `msg`, `i` 소멸).

### ④ `sphone2 = new SmartPhone(); sphone2.number = "010-111-1111";`

```
[Static 영역]            [Stack]                [Heap]
osVer = 1.0              main: args             0x100 number = "010"
                               sphone  = 0x100
                               sphone2 = 0x200  0x200 number = "010-111-1111"
```

- Heap에 **별도의 객체**가 생긴다. `number`는 객체마다 따로 있다.
- `osVer`는 새로 생기지 않는다 (Static 영역에 1개뿐) → `1.0 : 010-111-1111`

### ⑤ `SmartPhone.osVer = 2.0;`

```
[Static 영역]            [Stack]                [Heap]
osVer = 2.0  ◀── 공유 ── sphone, sphone2 모두   0x100 number = "010"
                                                0x200 number = "010-111-1111"
```

- 하나뿐인 `osVer`를 바꿨으므로 **모든 객체에서 2.0**으로 보인다 → `2.0 : 010`

## 핵심 정리

- **static 변수**는 클래스당 1개, 모든 객체가 공유한다. 그래서 `객체.osVer`보다 `SmartPhone.osVer`(클래스 이름)로 쓰는 게 권장된다.
- **인스턴스 변수**는 객체마다 따로 있다. `sphone.number`와 `sphone2.number`는 서로 영향이 없다.
- **로컬·파라미터 변수**는 메서드가 끝나면 Stack에서 사라진다.
- `sphone`, `sphone2`에는 객체 자체가 아니라 **Heap 주소(참조값)**가 들어 있다.
