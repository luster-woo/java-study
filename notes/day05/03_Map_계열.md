# Map 계열

> 교재: 15기_자바_live_07_collection_1_0123_오후 (전공)

> [← Set 계열](02_Set_계열.md) · [day05 목록](../README.md) · [정렬 →](04_정렬.md)

### Map interface 특징

- **Key와 Value를 하나의 Entry(`Map.Entry`)로 묶어** 데이터 관리
  - Key: 중복 불가
  - Value: 중복 가능

```text
Key(중복 불가)   Value(중복 가능)
휴대폰      →   010-XX
사무실      →   02-12X
집전화      →   02-12X   (사무실과 같은 값 참조 가능)
```

- 관련 클래스 관계도

```text
Map
  ├ Hashtable (과거 버전)
  │   └ Properties
  ├ HashMap
  └ SortedMap
      └ NavigableMap
          └ TreeMap
```

### Map 주요 메서드

| 분류 | 메서드 |
|---|---|
| 추가 | `put(K key, V value)`, `putAll(Map<? extends K, ? extends V> m)` |
| 조회 | `containsKey(Object key)`, `containsValue(Object value)`, `entrySet()`, `keySet()`, `get(Object key)`, `values()`, `size()`, `isEmpty()` |
| 삭제 | `clear()`, `remove(Object key)` |
| 수정 | `put(K key, V value)`, `putAll(...)` |

- **key와 entry는 Set으로 관리됨** (`keySet()`, `entrySet()`의 반환 타입이 Set)
- 수정 메서드가 따로 없음 → 같은 key로 `put`하면 **값을 덮어씀** (편하지만 조심)
  - `put`의 반환값: 그 key의 **이전 값** (없었으면 `null`)
  - `putIfAbsent`: key에 값이 없을 때만 추가

```java
import java.util.*;

public class MapTest {
    public static void main(String[] args) {
        Map<String, String> hMap = new HashMap<>();
        System.out.println("추가 성공?: " + hMap.put("andy", "1234")); // 추가 성공?: null
        System.out.println("추가 성공?: " + hMap.put("andy", "4567")); // 추가 성공?: 1234
        hMap.put("kate", "9999");
        hMap.putIfAbsent("kate", "1234");   // key가 이미 있으므로 무시
        hMap.put("henry", "4567");          // value는 중복 가능
        System.out.println("추가 결과: " + hMap); // 추가 결과: {henry=4567, kate=9999, andy=4567}

        // 조회
        System.out.println(hMap.get("kate"));              // 9999
        System.out.println(hMap.containsKey("andy"));      // true
        System.out.println(hMap.containsValue("1234"));    // false
        for (Map.Entry<String, String> e : hMap.entrySet()) {
            System.out.println(e.getKey() + " = " + e.getValue());
        }
        // 삭제
        hMap.remove("andy");
        System.out.println(hMap);                          // {henry=4567, kate=9999}
    }
}
/* entrySet 순회 출력
henry = 4567
kate = 9999
andy = 4567
*/
```

---

## 참고

- `HashSet`·`HashMap`의 출력 순서는 hash 값에 따른 것이며 입력 순서와 무관함 (위 출력은 실행 확인 결과)
