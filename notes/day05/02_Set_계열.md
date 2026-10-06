# Set 계열

> 교재: 15기_자바_live_07_collection_1_0123_오후 (전공)

> [← List 계열](01_List_계열.md) · [day05 목록](../README.md) · [Map 계열 →](03_Map_계열.md)

### Set interface 특징

- 입력 순서를 관리하지 않음 → 주머니에 구슬(데이터)을 넣는 형태
- 데이터를 구별할 순서(index)가 없어 **중복 불가**
- **효율적인 중복 데이터 제거 수단**

- 관련 클래스 관계도

```text
Set
  ├ HashSet
  └ SortedSet
      └ NavigableSet
          └ TreeSet   (이진 트리로 정렬된 상태로 요소 관리)
```

### Set API 활용

- 같은 데이터는 한 번만 저장됨, 넣은 순서대로 출력되지도 않음
- 기본형은 wrapper로 autoboxing 되어 추가
- index가 없으므로 조회는 순회(for-each, iterator)로
- **update(수정) 메서드가 없음** → 특정 요소를 가리킬 index가 없기 때문, 삭제 후 추가로 처리

```java
import java.util.*;

public class SetTest {
    public static void main(String[] args) {
        Set<Object> hset = new HashSet<>();
        hset.add(Integer.valueOf(1));
        hset.add("Hello");
        hset.add("Hello");   // 같은 데이터 → 추가 안 됨
        hset.add(1);         // 기본형은 wrapper(Integer)로 → 이미 있음
        System.out.println("추가 결과: " + hset);
        System.out.println("개수: " + hset.size());

        for (Object o : hset) {
            System.out.println("조회: " + o);
        }
        hset.remove("Hello");
        System.out.println("삭제 결과: " + hset);
    }
}
/* 출력
추가 결과: [1, Hello]
개수: 2
조회: 1
조회: Hello
삭제 결과: [1]
*/
```

> 참고: 교재 예제의 `new Integer(1)`은 Java 9부터 deprecated. `Integer.valueOf(1)` 또는 autoboxing(`1`)을 쓴다.

### 동일한 데이터의 기준

- 같은 번호의 `SmartPhone`을 두 번 넣어도 **둘 다 들어감** → 기본 `equals()`는 참조(주소) 비교이기 때문
- Set이 같은 데이터로 보는 기준: **`equals()`가 true** 이고 **`hashCode()` 값이 같음**
- 번호가 같으면 같은 전화로 보도록 두 메서드를 재정의

```java
import java.util.*;

public class PhoneSetTest {
    static class SmartPhone {
        String number;
        SmartPhone(String number) { this.number = number; }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SmartPhone) {
                SmartPhone other = (SmartPhone) obj;
                return number.equals(other.number);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return number.hashCode();   // equals 비교 기준과 같은 필드로
        }

        @Override
        public String toString() { return "전화 번호: " + number; }
    }

    public static void main(String[] args) {
        Set<SmartPhone> phones = new HashSet<>();
        phones.add(new SmartPhone("010-111-2222"));
        phones.add(new SmartPhone("010-111-2222"));
        System.out.println(phones.size()); // 1  (재정의 전에는 2)
        System.out.println(phones);        // [전화 번호: 010-111-2222]
    }
}
```

> 참고: 교재 `equals()`의 `obj instanceof SamePhone param`은 `SmartPhone`의 오타. 또 `instanceof 타입 변수` 패턴 매칭은 Java 16부터이므로 그 이전 버전에서는 위처럼 형변환해서 쓴다.

---

## 참고

- `HashSet`·`HashMap`의 출력 순서는 hash 값에 따른 것이며 입력 순서와 무관함 (위 출력은 실행 확인 결과)
