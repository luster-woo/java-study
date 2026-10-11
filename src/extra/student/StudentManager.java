package extra.student;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/*
 * 교육생 목록 관리 (CRUD) + 파일 저장/로드
 *
 *  1단계 : List에 넣고 빼기 (메모리) → 프로그램 끄면 사라짐
 *  3단계 : save() / load() 추가 → students.dat 파일에 List를 통째로 직렬화
 *          - 생성자에서 load()  : 시작할 때 파일 → List
 *          - 변경할 때마다 save() : List → 파일 (강제 종료해도 안 날아감)
 *
 *  싱글턴 : 관리자는 프로그램 전체에 하나만 (private 생성자 + getInstance)
 */
public class StudentManager {
    private static final String FILE = "students.dat";
    private static final StudentManager instance = new StudentManager();

    private List<Student> list = new ArrayList<>();      // 배열 대신 크기가 자동으로 늘어나는 List

    private StudentManager() {
        load();
    }

    public static StudentManager getInstance() {
        return instance;
    }

    // ===== Create =====
    public void add(Student s) throws DuplicateStudentException {
        if (find(s.getId()) != null) throw new DuplicateStudentException(s.getId());
        list.add(s);
        save();
    }

    // ===== Read =====
    public List<Student> getList() {
        return new ArrayList<>(list);                     // 복사본 → 밖에서 원본 목록을 못 건드림
    }

    public Student get(String id) throws StudentNotFoundException {
        Student s = find(id);
        if (s == null) throw new StudentNotFoundException(id);
        return s;
    }

    // ===== Update : 캠퍼스와 반만 바꾼다 (학번·이름은 그대로) =====
    public void update(String id, String campus, int classNum) throws StudentNotFoundException {
        Student s = get(id);                              // 없으면 여기서 예외가 위로 전파됨
        s.setCampus(campus);
        s.setClassNum(classNum);
        save();
    }

    // ===== Delete =====
    public void remove(String id) throws StudentNotFoundException {
        list.remove(get(id));
        save();
    }

    private Student find(String id) {
        for (Student s : list) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    // ===== 메모리(List) → 파일 =====
    private void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(list);                        // ArrayList, String, int 모두 직렬화 가능
        } catch (IOException e) {
            System.out.println("[저장 실패] " + e.getMessage());
        }
    }

    // ===== 파일 → 메모리(List) =====
    @SuppressWarnings("unchecked")
    private void load() {
        File f = new File(FILE);
        if (!f.exists()) return;                          // 첫 실행 : 파일 없음 → 빈 목록으로 시작
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            list = (List<Student>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[불러오기 실패] " + e.getMessage());
        }
    }
}
