# List 계열

> 교재: 15기_자바_live_07_collection_1_0123_오후 (전공)

> [day05 목록](../README.md) · [Set 계열 →](02_Set_계열.md)

### 자료 구조와 Collection Framework

- **자료 구조**: 데이터를 효율적으로 접근·수정하도록 조직·관리·저장하는 방법 (데이터 값의 모임 + 데이터 간 관계 + 적용 가능한 연산)
- 가장 기본 자료 구조는 **배열**
  - homogeneous collection: 같은 타입만 관리 가능 → 타입이 다르면 배열을 따로 만들어야 함
- **다형성(Object)** 으로 모든 객체를 담을 수 있음
  - 담을 때는 편하지만 꺼낼 때 Object로만 나옴 → 런타임에 실제 타입 확인 후 형변환해야 하는 번거로움
- **Generic** 으로 타입 한정
  - 컴파일 시점에 저장 타입 제한 → 형변환 번거로움 제거

### Collection Framework 핵심 interface

- `java.util` 패키지, 다수의 데이터를 쉽게 처리하는 방법 제공 → DB처럼 **CRUD** 기능이 중요

```text
Iterable
  └ Collection
      ├ List
      └ Set
Map   (Collection을 상속하지 않음)
```

| interface | 특징 | 예 | 대표 구현체 |
|---|---|---|---|
| **List** | 입력 순서가 있는 데이터 집합. 순서가 있으므로 **중복 허용** | 일렬로 줄 서기 | ArrayList, LinkedList |
| **Set** | 입력 순서를 유지하지 않는 집합. 같은 데이터를 구별할 수 없으므로 **중복 불가** | 알파벳이 한 종류씩 든 주머니 | HashSet, TreeSet |
| **Map** | key-value 쌍으로 관리. 순서 없음, **key 중복 불가 / value 중복 가능** | 지역번호-지역 | HashMap, TreeMap |

### Collection interface 주요 메서드

- Collection을 쓸 줄 안다 = 특성에 맞게 자료를 추가·수정·삭제·조회할 수 있다

| 분류 | 메서드 |
|---|---|
| 추가 | `add(E e)`, `addAll(Collection<? extends E> c)` |
| 조회 | `contains(Object o)`, `containsAll(Collection<?> c)`, `equals()`, `isEmpty()`, `iterator()`, `size()` |
| 삭제 | `clear()`, `remove(Object o)`, `removeAll(Collection<?> c)` |
| 기타 | `toArray()` |

### List 특징

- 입력 순서가 있는 데이터 집합, 순서가 있으므로 중복 허용
- 내부적으로 index(0 ~ n-1)마다 객체 참조를 저장, 같은 객체를 여러 칸이 참조할 수 있고 `null`도 저장 가능

```text
index :  0       1       2       ...  n-1
참조  :  0x100   0x200   0x200   ...  null
          "Hi"   "Hello" (같은 객체)
```

- 관련 클래스 관계도

```text
Collection
  ├ List
  │   ├ Vector (과거 버전)
  │   │   └ Stack
  │   ├ ArrayList
  │   └ LinkedList
  └ Queue
      └ Deque
          ├ LinkedList
          └ ArrayDeque
```

### List 주요 메서드

- Collection 메서드 + **index 기반** 메서드가 추가됨

| 분류 | Collection | List에서 추가 |
|---|---|---|
| 추가 | `add(E e)`, `addAll(Collection c)` | `add(int index, E element)`, `addAll(int index, Collection c)` |
| 조회 | `contains`, `containsAll`, `equals`, `isEmpty`, `iterator`, `size` | `get(int index)`, `indexOf(Object o)`, `lastIndexOf(Object o)`, `listIterator()` |
| 삭제 | `clear`, `remove(Object o)`, `removeAll` | `remove(int index)` |
| 수정 | - | `set(int index, E element)` |
| 기타 | `toArray()` | `subList(int fromIndex, int toIndex)` |

- 친구 이름 관리 예

```java
import java.util.*;

public class ListBasic {
    public static void main(String[] args) {
        List<String> friends = new ArrayList<>();
        friends.add("홍길동");
        friends.add("홍길동");          // 중복 허용
        friends.add("장길산");
        friends.add("임꺽정");
        friends.add(0, "이몽룡");       // 0번에 끼워 넣기
        System.out.println(friends);                     // [이몽룡, 홍길동, 홍길동, 장길산, 임꺽정]

        System.out.println(friends.get(1));              // 홍길동
        System.out.println(friends.indexOf("홍길동"));    // 1
        System.out.println(friends.lastIndexOf("홍길동")); // 2
        System.out.println(friends.contains("임꺽정"));   // true
        System.out.println(friends.size());              // 5

        friends.set(1, "성춘향");                         // 수정
        friends.remove(2);                               // index로 삭제
        friends.remove("임꺽정");                         // 객체로 삭제
        System.out.println(friends);                     // [이몽룡, 성춘향, 장길산]
        System.out.println(friends.subList(0, 2));       // [이몽룡, 성춘향]
    }
}
```

### ArrayList 소스 분석 — 내부는 배열

- 생성자
  - `ArrayList(int initialCapacity)`: 내부적으로 `Object[]`를 그 크기로 생성 (0이면 빈 배열, 음수면 `IllegalArgumentException`)
  - `ArrayList()`: 빈 배열(`{}`)로 시작
  - 크기를 미리 안다면 초기 용량을 지정하는 것도 좋음
- 추가 흐름: `add` → `ensureCapacityInternal` → `ensureExplicitCapacity` → `grow`
  - 칸이 부족하면 **기존 크기의 1.5배**(`old + (old >> 1)`)로 새 배열을 만들고 `Arrays.copyOf`로 복사

```java
// grow()의 핵심 (JDK 소스 요약)
int oldCapacity = elementData.length;
int newCapacity = oldCapacity + (oldCapacity >> 1);   // 1.5배
if (newCapacity - minCapacity < 0)
    newCapacity = minCapacity;
elementData = Arrays.copyOf(elementData, newCapacity);
```

### 배열과 ArrayList

| | 내용 |
|---|---|
| 배열의 장점 | 가장 기본 자료 구조라 간단·사용 쉬움, **접근 속도 빠름** |
| 배열의 단점 | 크기 변경 불가 → 추가하려면 새 배열 생성 후 복사 / **비순차적 추가·삭제에 시간이 많이 걸림** (뒤 요소를 밀고 당겨야 함) |

- ArrayList도 배열을 쓰므로 배열의 장단점을 그대로 가짐

### LinkedList

- 각 요소를 **Node**로 정의, Node = 데이터 + 다음 요소의 참조
- 요소들이 메모리에 연속적일 필요 없음

```text
0x100        0x200        0x500        0x600
[0 | 0x200]→ [1 | 0x500]→ [2 | 0x600]→ [3 | null]
 값  다음노드
```

- 삭제·추가는 **링크(참조)만 바꾸면 됨**

```text
1번 노드 삭제 : [0 | 0x200] → [0 | 0x500]       (0번이 2번을 바로 가리킴)
4번 노드 삽입 : [2 | 0x600] → [2 | 0x700],  0x700 = [4 | 0x600]
```

### LinkedList와 ArrayList의 용도

| 구분 (단위: 나노초) | ArrayList | LinkedList |
|---|---|---|
| 순차 추가/수정/삭제 (100만 건 추가) | **빠름** 22,108,711 | 느림 84,540,898 |
| 비순차 추가/수정/삭제 (10만 건 추가) | 느림 31,444,190,835 | **빠름** 5,900,334 |
| 조회 (10만 건 조회) | **빠름** 6,428,554 | 느림 20,895,705,164 |

- 결론
  - 어느 클래스가 좋고 나쁜 게 아니라 **용도에 맞게** 사용
  - 소량 데이터는 큰 차이 없음
  - 정적인 데이터 활용, 단순 조회용 → **ArrayList**
  - 동적인 데이터 추가·삭제가 많은 작업 → **LinkedList**

### 자료 삭제 시 주의 사항 — index for 문

- 요소를 삭제하면 **size가 줄고 뒤 요소가 앞으로 당겨짐** → i를 그대로 증가시키면 바로 다음 요소를 건너뜀
- 해결: 삭제 후 `i--` 또는 **뒤에서부터 거꾸로** 접근

```java
import java.util.*;

public class RemoveTest {
    public static void main(String[] args) {
        List<Integer> nums = new ArrayList<>(List.of(14, 2, 12, 19, 15, 6, 15, 15, 4, 16));

        // 잘못된 삭제: 짝수를 지우는데 짝수가 남음
        List<Integer> a = new ArrayList<>(nums);
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i) % 2 == 0) a.remove(i);
        }
        System.out.println(a); // [2, 19, 15, 15, 15, 16]

        // 해결 1: 삭제했으면 index 차감
        List<Integer> b = new ArrayList<>(nums);
        for (int i = 0; i < b.size(); i++) {
            if (b.get(i) % 2 == 0) {
                b.remove(i);
                i--;
            }
        }
        System.out.println(b); // [19, 15, 15, 15]

        // 해결 2: 거꾸로 접근 (역발상)
        List<Integer> c = new ArrayList<>(nums);
        for (int i = c.size() - 1; i >= 0; i--) {
            if (c.get(i) % 2 == 0) c.remove(i);
        }
        System.out.println(c); // [19, 15, 15, 15]
    }
}
```

> 참고: 교재의 거꾸로 접근 코드는 조건이 `% 2 == 1`(홀수 삭제)로 되어 있음. 앞 예제와 같이 짝수를 지우려면 `% 2 == 0`.

### 자료 삭제 시 주의 사항 — forEach 문

- forEach(향상된 for)로 순회하는 동안에는 **Collection 크기가 변하면 안 됨**
- 순회 중 `add`·`remove` → `ConcurrentModificationException`

```java
import java.util.*;

public class ForEachTest {
    public static void main(String[] args) {
        List<Integer> nums = new ArrayList<>();
        for (int i = 0; i < 10; i++) nums.add(i);
        System.out.println("시작: " + nums);
        try {
            for (Integer num : nums) {
                if (num % 2 == 0) nums.remove(num);   // 순회 중 크기 변경
            }
        } catch (ConcurrentModificationException e) {
            System.out.println(e.getClass().getSimpleName());
        }
    }
}
/* 출력
시작: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
ConcurrentModificationException
*/
```

### Collection과 Generic — 공변과 불변

- **배열은 공변(covariant)**: `Object[] arr = new Person[3];` 컴파일 OK → 다른 타입을 넣으면 **런타임**에 `ArrayStoreException`
- **제네릭은 불변(invariant)**: `List<Object> list = new ArrayList<Person>();` → **컴파일 에러**(type mismatch)
- 제네릭은 잘못된 대입을 컴파일 시점에 막아 줌

```java
public class CovariantTest {
    static class Person {}
    public static void main(String[] args) {
        Object[] arr2 = new Person[3];   // 배열은 공변: 컴파일 OK
        try {
            arr2[1] = 1;                 // 실제 배열 타입은 Person[]
        } catch (ArrayStoreException e) {
            System.out.println(e);       // java.lang.ArrayStoreException: java.lang.Integer
        }
        // List<Object> list = new ArrayList<Person>(); // 제네릭은 불변: 컴파일 에러
    }
}
```

---

## 참고

- `Arrays.asList(...)`로 만든 리스트는 크기 고정 → `sort`·`set`은 되지만 `add`·`remove`는 `UnsupportedOperationException`
