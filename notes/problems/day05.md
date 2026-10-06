# D5 연습문제 · 컬렉션·람다·Stream

그날 배운 문법으로 푸는 문제 3개. 직접 만든 문제라 다른 사이트에는 없다.

- **파일 위치**: `src/practice/D5P1.java` 처럼 문제마다 클래스 하나 (`practice` 패키지)
- **실행**: ▶ 실행 → 아래 콘솔에 예제 입력을 붙여넣고 Enter
- **채점**: 예제와 "추가 테스트"의 출력이 **글자 하나까지 같으면** 통과
- **검토**: 다 풀면 "D5P1 검토해줘"라고 말하기


---

## D5P1 · 많이 나온 단어

**써야 할 문법**: `HashMap`, `getOrDefault`, `Comparator`(빈도 내림차순 → 단어 오름차순)

여러 줄의 문장에서 단어 빈도를 세어 **상위 K개**를 출력하라. 대소문자는 구분하지 않고 **소문자로 출력**한다.

**입력**
- 첫째 줄: N K (1 ≤ N ≤ 1,000, 1 ≤ K ≤ 100)
- 다음 N줄: 영문 단어들 (공백 구분, 문장부호 없음)

**출력**: `단어 횟수`, 횟수 내림차순, 같으면 단어 사전순. 서로 다른 단어가 K개보다 적으면 있는 만큼

**예제 입력 1**

```
2 3
Java is fun and Java is fast
spring is FUN
```

**예제 출력 1**

```
is 3
fun 2
java 2
```

<details>
<summary>힌트</summary>

`map.entrySet().stream().sorted(...).limit(k)` 또는 `List`로 옮긴 뒤 `sort`.

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
1 5
a b c
```

출력

```
a 1
b 1
c 1
```

**테스트 2** 입력

```
3 2
b a
A B
c c c
```

출력

```
c 3
a 2
```

</details>


---

## D5P2 · 괄호 짝

**써야 할 문법**: `Deque<Character> stack = new ArrayDeque<>()`, `push`·`pop`·`peek`

`()`, `[]`, `{}`로만 이루어진 문자열이 올바르게 짝지어졌는지 판단하라.

**입력**
- 첫째 줄: T (1 ≤ T ≤ 100)
- 다음 T줄: 문자열 (길이 1 ~ 10,000)

**출력**: 줄마다 `YES` 또는 `NO`

**예제 입력 1**

```
4
([]{})
([)]
((
}
```

**예제 출력 1**

```
YES
NO
NO
NO
```

<details>
<summary>힌트</summary>

여는 괄호는 push, 닫는 괄호면 맨 위가 짝인지 확인. 끝났을 때 스택이 비어 있어야 한다.

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
3
{}[]()
{[()()]}
())(
```

출력

```
YES
YES
NO
```

**테스트 2** 입력

```
2
[
]
```

출력

```
NO
NO
```

</details>


---

## D5P3 · 성적표

**써야 할 문법**: `record Student`, `List.sort` + `Comparator` 체이닝, `stream().filter().count()`

학생 N명의 국어·영어·수학 점수가 주어진다. **총점 내림차순, 같으면 국어 내림차순, 그것도 같으면 이름 사전순**으로 정렬해 출력하고, 마지막 줄에 총점 240점 이상인 학생 수를 출력하라. 합격자 수는 **Stream으로** 셀 것.

**입력**
- 첫째 줄: N (1 ≤ N ≤ 1,000)
- 다음 N줄: `이름 국어 영어 수학` (이름은 영문 10자 이하, 점수 0 ~ 100)

**출력**: `이름 총점` N줄, 마지막 줄 `PASS 인원`

**예제 입력 1**

```
4
gyu 90 80 70
kim 80 90 70
lee 100 100 100
park 70 90 80
```

**예제 출력 1**

```
lee 300
gyu 240
kim 240
park 240
PASS 4
```

<details>
<summary>힌트</summary>

`Comparator.comparingInt(Student::total).reversed().thenComparing(...)` — 람다로 `(a, b) -> ...` 써도 된다.

</details>

<details>
<summary>추가 테스트 (예제를 통과한 뒤에 열어 보기)</summary>

**테스트 1** 입력

```
3
b 80 80 80
a 80 80 80
c 79 81 80
```

출력

```
a 240
b 240
c 240
PASS 3
```

**테스트 2** 입력

```
1
zero 0 0 0
```

출력

```
zero 0
PASS 0
```

</details>

---

# 코드 읽기 문제

면접 질문과 필기(IT 직무상식)는 코드를 **읽고** 답하는 형식이 많다. IntelliJ로 실행하지 말고 먼저 손으로 답을 적은 뒤, 정답을 열거나 직접 실행해서 확인한다.

- **출력 예측**: 실행 결과를 정확히 적기 (공백·줄바꿈까지)
- **버그 찾기**: 무엇이 잘못됐는지, 어떻게 고치는지
- **빈칸 채우기**: `____`에 들어갈 코드

### 출력 예측

#### 출력 1

```java
List<Integer> list = new ArrayList<>(List.of(10, 20, 30, 40));
list.remove(1);
list.remove(Integer.valueOf(40));
list.add(0, 5);
System.out.println(list + " " + list.size());
```

<details>
<summary>정답·해설</summary>

```
[5, 10, 30] 3
```

`remove(1)`은 **인덱스 1**(값 20)을 지운다. 값 40을 지우려면 `Integer.valueOf(40)`처럼 객체로 넘겨야 한다.

</details>

#### 출력 2

```java
List<String> words = List.of("spring", "java", "jpa", "security", "jwt");
System.out.println(words.stream()
        .filter(w -> w.startsWith("j"))
        .map(String::toUpperCase)
        .sorted()
        .collect(Collectors.joining(",")));
System.out.println(words.stream().mapToInt(String::length).max().getAsInt());
```

<details>
<summary>정답·해설</summary>

```
JAVA,JPA,JWT
8
```

j로 시작하는 java·jpa·jwt만 남고, 대문자로 바꾼 뒤 사전순. 두 번째 글자 A < P < W. 가장 긴 단어는 security(8).

</details>

#### 출력 3

```java
Map<String, Integer> m = new HashMap<>();
m.put("a", 1);
m.put("a", m.get("a") + 1);
m.putIfAbsent("a", 100);
m.merge("b", 5, Integer::sum);
m.merge("b", 5, Integer::sum);
System.out.println(m.get("a") + " " + m.get("b") + " " + m.getOrDefault("c", 0));
```

<details>
<summary>정답·해설</summary>

```
2 10 0
```

같은 키로 put하면 덮어쓴다. `putIfAbsent`는 키가 이미 있어서 무시. `merge`는 없으면 5를 넣고, 있으면 기존 값과 합친다.

</details>

#### 출력 4

```java
Optional<String> o = Optional.of("jpa");
System.out.println(o.map(String::length).orElse(-1));
System.out.println(o.filter(s -> s.length() > 5).orElse("short"));
System.out.println(Optional.empty().isPresent());
```

<details>
<summary>정답·해설</summary>

```
3
short
false
```

filter 조건을 통과하지 못하면 빈 Optional이 되어 orElse 값이 나온다.

</details>

### 버그 찾기

#### 버그 1 · 짝수 지우기

```java
List<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4));
for (Integer n : nums) {
    if (n % 2 == 0) nums.remove(n);
}
```

<details>
<summary>정답·해설</summary>

향상된 for문으로 도는 도중에 리스트를 수정하면 `ConcurrentModificationException`. **`nums.removeIf(n -> n % 2 == 0);`**

</details>

#### 버그 2 · 같은데 다르다고 나옴

```java
Integer a = 1000;
Integer b = 1000;
if (a == b) {
    System.out.println("같다");
}
```

<details>
<summary>정답·해설</summary>

Integer는 객체라 `==`는 같은 객체인지 비교한다. -128~127만 캐시돼서 같은 객체이고, 1000은 서로 다른 객체라 false. **`a.equals(b)`** 또는 `a.intValue() == b.intValue()`.

</details>

### 빈칸 채우기

#### 빈칸 1 · 단어 개수 세기

```java
Map<String, Integer> map = new HashMap<>();
for (String w : words) {
    map.put(w, ____);
}
```

<details>
<summary>정답·해설</summary>

`map.getOrDefault(w, 0) + 1`

또는 한 줄로 `map.merge(w, 1, Integer::sum);`

</details>

#### 빈칸 2 · 정렬 기준 두 개

```java
record Student(String name, int score) { }

// 점수 내림차순, 같으면 이름 오름차순
list.sort(Comparator.comparingInt(Student::score).____()
        .thenComparing(____));
```

<details>
<summary>정답·해설</summary>

`reversed`, `Student::name`

</details>
