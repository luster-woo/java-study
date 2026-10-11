package extra.student;

// 이미 있는 학번으로 등록할 때
public class DuplicateStudentException extends Exception {
    public DuplicateStudentException(String id) {
        super(id + " 학번은 이미 등록되어 있습니다.");
    }
}
