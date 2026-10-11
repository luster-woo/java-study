package day06.i_json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/*
 * JSON = 객체를 "사람이 읽을 수 있는 문자열"로 표현하는 방법   (직렬화의 실무 버전)
 *
 *   { "key": 값, "key": 값 }   → 객체 (자바의 Map / DTO)
 *   [ 값, 값, 값 ]             → 배열 (자바의 List)
 *   값 : "문자열", 숫자, true/false, null, 객체, 배열
 *
 * Jackson의 ObjectMapper 하나로 변환
 *   writeValueAsString(객체)       : 객체 → JSON 문자열  (직렬화)
 *   readValue(JSON, 클래스.class)  : JSON 문자열 → 객체  (역직렬화)
 *   writeValue(파일, 객체) / readValue(파일, 클래스.class) : 파일로 바로
 *
 *  DTO 규칙 : 기본 생성자 + getter/setter (Jackson이 getter로 읽고, 기본 생성자로 만들고, setter로 채움)
 *
 *  ※ 라이브러리 필요 : 프로젝트의 lib 폴더 jar 3개 → 우클릭 → Add as Library
 *
 * [실행 결과]
 * --- 1. 객체 → JSON (직렬화) ---
 * {"id":"yunwoo","age":25,"skills":["Java","Spring"],"addr":{"city":"서울","zipCode":"06234"}}
 * {
 *   "id" : "yunwoo",
 *   "age" : 25,
 *   "skills" : [ "Java", "Spring" ],
 *   "addr" : {
 *     "city" : "서울",
 *     "zipCode" : "06234"
 *   }
 * }
 * --- 2. JSON → 객체 (역직렬화) ---
 * Member[id=kim, age=30, skills=[Python, SQL], addr=부산(48000), pass=null]
 * 도시만 : 부산
 * --- 3. List ↔ JSON 배열 ---
 * [{"id":"a","age":20,"skills":["C"],"addr":{"city":"대전","zipCode":"34000"}},{"id":"b","age":21,"skills":[],"addr":null}]
 * 복원 개수 : 2, 첫 번째 id : a
 * --- 4. DTO 없이 Map으로 ---
 * key 목록 : [id, age, skills, addr, nickname]
 * addr 값의 타입 : LinkedHashMap
 * skills 값의 타입 : ArrayList
 * --- 5. 파일로 저장 / 읽기 ---
 * 파일 크기 : 135byte   (Windows는 줄바꿈이 \r\n이라 조금 더 큼)
 * 읽은 객체 : Member[id=yunwoo, age=25, skills=[Java, Spring], addr=서울(06234), pass=null]
 */

@JsonIgnoreProperties(ignoreUnknown = true)       // JSON에 DTO에 없는 key가 있어도 에러 안 나고 무시
class Member {
    private String id;
    private int age;
    private List<String> skills;
    private Address addr;                          // 객체 안의 객체 → JSON 안의 { }
    @JsonIgnore                                    // JSON에서 제외 (직렬화의 transient 역할)
    private String pass;

    public Member() {}                             // ⭐ Jackson이 객체 만들 때 사용 (없으면 readValue 에러)

    public Member(String id, int age, List<String> skills, Address addr, String pass) {
        this.id = id;
        this.age = age;
        this.skills = skills;
        this.addr = addr;
        this.pass = pass;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public Address getAddr() { return addr; }
    public void setAddr(Address addr) { this.addr = addr; }
    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }

    @Override
    public String toString() {
        return "Member[id=" + id + ", age=" + age + ", skills=" + skills + ", addr=" + addr + ", pass=" + pass + "]";
    }
}

class Address {
    private String city;
    private String zipCode;

    public Address() {}

    public Address(String city, String zipCode) {
        this.city = city;
        this.zipCode = zipCode;
    }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    @Override
    public String toString() { return city + "(" + zipCode + ")"; }
}

public class JsonTest {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();     // 보통 하나 만들어서 계속 재사용
        Member m = new Member("yunwoo", 25, List.of("Java", "Spring"), new Address("서울", "06234"), "pass1234");

        System.out.println("--- 1. 객체 → JSON (직렬화) ---");
        String json = mapper.writeValueAsString(m);
        System.out.println(json);                                       // 한 줄로 (네트워크 전송용)
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(m));   // 보기 좋게
        // pass는 @JsonIgnore라서 빠짐

        System.out.println("--- 2. JSON → 객체 (역직렬화) ---");
        String input = """
                {
                  "id": "kim",
                  "age": 30,
                  "skills": ["Python", "SQL"],
                  "addr": { "city": "부산", "zipCode": "48000" },
                  "nickname": "DTO에 없는 key"
                }
                """;                                                    // 텍스트 블록 (Java 15+)
        Member loaded = mapper.readValue(input, Member.class);
        System.out.println(loaded);
        System.out.println("도시만 : " + loaded.getAddr().getCity());   // 이제 평범한 자바 객체
        // nickname은 Member에 없지만 @JsonIgnoreProperties 덕분에 무시됨

        System.out.println("--- 3. List ↔ JSON 배열 ---");
        List<Member> list = List.of(
                new Member("a", 20, List.of("C"), new Address("대전", "34000"), null),
                new Member("b", 21, List.of(), null, null));
        String arrJson = mapper.writeValueAsString(list);
        System.out.println(arrJson);                                    // [ {...}, {...} ]
        List<Member> back = mapper.readValue(arrJson, new TypeReference<List<Member>>() {});
        // List<Member>.class 는 문법상 불가 → TypeReference로 제네릭 타입을 알려줌
        System.out.println("복원 개수 : " + back.size() + ", 첫 번째 id : " + back.get(0).getId());

        System.out.println("--- 4. DTO 없이 Map으로 ---");
        Map<String, Object> map = mapper.readValue(input, new TypeReference<Map<String, Object>>() {});
        System.out.println("key 목록 : " + map.keySet());
        System.out.println("addr 값의 타입 : " + map.get("addr").getClass().getSimpleName());   // 안쪽 { } 도 Map
        System.out.println("skills 값의 타입 : " + map.get("skills").getClass().getSimpleName()); // [ ] 는 List

        System.out.println("--- 5. 파일로 저장 / 읽기 ---");
        File file = new File("member_day06.json");
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, m);    // 메모장으로 열어도 읽힘
        Member fromFile = mapper.readValue(file, Member.class);
        System.out.println("파일 크기 : " + file.length() + "byte");
        System.out.println("읽은 객체 : " + fromFile);
        file.delete();                                                  // 주석 처리하면 파일을 직접 열어볼 수 있음
    }
}
