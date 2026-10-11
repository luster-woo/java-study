package extra.student;

// 없는 학번으로 조회·수정·삭제할 때
public class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String id) {
        super(id + " 학번의 교육생이 없습니다.");
    }
}
