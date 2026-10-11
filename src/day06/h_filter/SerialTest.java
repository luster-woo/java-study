package day06.h_filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * 객체 직렬화 = 객체를 byte로 바꿔서 파일 저장 / 네트워크 전송   (역직렬화 = 그 반대)
 *
 *   ObjectOutputStream.writeObject(obj)  → 직렬화
 *   ObjectInputStream.readObject()       → 역직렬화 (Object로 나오니까 형변환 필요)
 *
 *  조건
 *   - Serializable 인터페이스 구현 (메서드 없음, "직렬화 해도 됨" 표시만)
 *   - 안에 들어 있는 객체(has-a)도 전부 Serializable이어야 함 → 아니면 NotSerializableException
 *   - transient 필드 : 저장 제외 → 복원하면 기본값(null, 0)
 *   - static 필드    : 객체가 아니라 클래스 것이라 저장 X
 *   - serialVersionUID 직접 지정 권장 → 클래스를 고쳐도 옛 파일을 읽을 수 있게
 *
 * [실행 결과]
 * --- 1. 직렬화 (저장) ---
 * 저장 전 : Person[id=yunwoo, pass=pass1234, age=25, addr=서울(06234)]
 * 저장 완료 (204byte)
 * --- 2. 역직렬화 (복원) ---
 * 복원    : Person[id=yunwoo, pass=null, age=25, addr=서울(06234)]
 * 같은 객체? false → 내용만 같은 새 객체
 * pass가 null → transient라 저장 안 됨
 * --- 3. 여러 개 저장 (List째로) ---
 * Person[id=kim, pass=null, age=20, addr=부산(01000)]
 * Person[id=lee, pass=null, age=30, addr=대전(02000)]
 * --- 4. Serializable 아닌 멤버가 있으면? ---
 * NotSerializableException : day06.h_filter.Car
 */
class Address implements Serializable {
    private static final long serialVersionUID = 1L;
    private String zipCode;
    private String city;

    Address(String zipCode, String city) {
        this.zipCode = zipCode;
        this.city = city;
    }

    @Override
    public String toString() {
        return city + "(" + zipCode + ")";
    }
}

class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    static int count = 0;                                    // static : 직렬화 X
    private String id;
    private transient String pass;                           // transient : 직렬화 X (비밀번호 같은 민감 정보)
    private int age;
    private Address addr;                                    // has-a : Address도 Serializable이라 OK

    Person(String id, String pass, int age, Address addr) {
        this.id = id;
        this.pass = pass;
        this.age = age;
        this.addr = addr;
        count++;
    }

    @Override
    public String toString() {
        return "Person[id=" + id + ", pass=" + pass + ", age=" + age + ", addr=" + addr + "]";
    }
}

class Car {                                                  // Serializable 구현 X
    String name = "아반떼";
}

class Owner implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name = "윤우";
    private Car car = new Car();                             // ❌ 직렬화 안 되는 멤버
}

public class SerialTest {
    private static final File TARGET = new File("person_day06.dat");

    public static void main(String[] args) {

        System.out.println("--- 1. 직렬화 (저장) ---");
        Person p = new Person("yunwoo", "pass1234", 25, new Address("06234", "서울"));
        System.out.println("저장 전 : " + p);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TARGET))) {
            oos.writeObject(p);
            System.out.println("저장 완료 (" + TARGET.length() + "byte)");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("--- 2. 역직렬화 (복원) ---");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TARGET))) {
            Object obj = ois.readObject();
            if (obj instanceof Person loaded) {              // 패턴 매칭 instanceof (Java 16+) : 검사 + 형변환
                System.out.println("복원    : " + loaded);
                System.out.println("같은 객체? " + (p == loaded) + " → 내용만 같은 새 객체");
                System.out.println("pass가 null → transient라 저장 안 됨");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("--- 3. 여러 개 저장 (List째로) ---");
        List<Person> list = new ArrayList<>();
        list.add(new Person("kim", "1111", 20, new Address("01000", "부산")));
        list.add(new Person("lee", "2222", 30, new Address("02000", "대전")));
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TARGET))) {
            oos.writeObject(list);                           // ArrayList도 Serializable
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TARGET))) {
            @SuppressWarnings("unchecked")
            List<Person> loadedList = (List<Person>) ois.readObject();
            loadedList.forEach(System.out::println);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("--- 4. Serializable 아닌 멤버가 있으면? ---");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TARGET))) {
            oos.writeObject(new Owner());
        } catch (NotSerializableException e) {
            System.out.println("NotSerializableException : " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        TARGET.delete();                                     // 연습용 파일 정리
    }
}
