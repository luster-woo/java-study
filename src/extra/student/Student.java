package extra.student;

import java.io.Serializable;

// 교육생 데이터 - 파일에 객체째로 저장하려면 Serializable 필수
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;    // 클래스를 고쳐도 옛 파일을 읽을 수 있게

    private String id;         // 학번 (중복 X, 검색 키)
    private String name;
    private String campus;
    private int classNum;      // 반

    public Student() {}

    public Student(String id, String name, String campus, int classNum) {
        this.id = id;
        this.name = name;
        this.campus = campus;
        this.classNum = classNum;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }
    public int getClassNum() { return classNum; }
    public void setClassNum(int classNum) { this.classNum = classNum; }

    @Override
    public String toString() {
        return "Student [id=" + id + ", name=" + name + ", campus=" + campus + ", class=" + classNum + "]";
    }
}
