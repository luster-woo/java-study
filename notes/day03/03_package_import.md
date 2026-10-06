# package_import

> 교재: 15기_자바_live_03_inheritance_0121 (전공)

> [← 메서드 재정의](02_메서드_재정의.md) · [day03 목록](../README.md) · [제한자 →](04_제한자.md)

### Package

- PC의 많은 파일은 **폴더**로 관리
  - 비슷한 목적의 파일끼리 묶음, 의미 있는 이름으로 계층적 접근 (`c:\Program Files\Internet Explorer\images\bing.ico`)
- 프로그램의 많은 클래스는 **패키지**로 관리
  - 의미 있는 이름으로 만들고 `.`으로 계층적 접근
  - 물리적으로 패키지는 **클래스 파일을 담고 있는 디렉터리**
  - **package name + class name**으로 클래스를 구분 → **fully qualified name**
- 선언: `package package_name;`
  - 주석·공백을 제외한 **첫 번째 문장**에, **하나의 패키지만** 선언
  - 모든 클래스는 반드시 하나의 패키지에 속함
  - 생략하면 default package에 속하는데, default package는 가급적 쓰지 않음

```java
package org.springframework.cache;

public class Cache {
    // ...
}
```

### package naming 룰

- **소속.프로젝트.용도**

| 소속(회사 identity) | 프로젝트 | 용도 |
|---|---|---|
| `com.ssafy` | `hrm` | `common`, `service`, ... |

- 예: `com.ssafy.hrm.common`, `com.ssafy.hrm.service`

### import

- **다른 패키지**에 선언된 클래스를 사용하기 위한 키워드
  - 패키지 선언과 클래스 선언 **사이**에 위치
  - 패키지와 달리 **여러 번** 선언 가능
- 선언 방법
  - `import 패키지명.클래스명;`
  - `import 패키지명.*;` — **하위 패키지까지 import하지는 않음**
- import한 패키지들에 같은 이름의 클래스가 있어 구분해야 하면 → 클래스 이름 앞에 **전체 패키지명**을 씀
- **default import package**: `java.lang.*` (String, System, Math 등은 import 없이 사용)
- 이클립스 자동 import 단축키: `Ctrl + Shift + O`

```java
import java.util.*;

public class ImportTest {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();           // java.util.List
        java.awt.List list2 = null;                      // 이름이 같으면 전체 이름으로 구분
        String s = "java.lang은 import 없이 사용";        // java.lang.String
        list.add(s);
        System.out.println(list.size() + ", " + (list2 == null));  // 1, true
    }
}
```

### 일반적인 클래스 레이아웃

| 순서 | 구성 요소 | 예 |
|---|---|---|
| 1 | 패키지 선언부 | `package structure;` |
| 2 | 외부 패키지 import | `import java.io.*;` |
| 3 | class 선언부 | `public class ClassStructure {` |
| 4 | 멤버 변수 | `String name; int age;` |
| 5 | 초기화 블록 | `{ name = "andy"; }` |
| 6 | 생성자 | `public ClassStructure(String name, int age) {...}` |
| 7 | 멤버 메서드 | `public void setName(String name) {...}`, `main` |

---

## 참고

- 교재 코드의 클래스 이름은 노트에서 일부 바꿨고, 단일 파일 실행(`java Xxx.java`)을 위해 `main`이 있는 클래스를 파일 맨 위에 둠.
