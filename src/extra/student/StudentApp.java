package extra.student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/*
 * SSAFY 교육생 관리 프로그램 (종합실습)
 *
 *  StudentApp ──uses──> StudentManager ──has-a──> List<Student>
 *                            └─ save()/load() ──> students.dat (객체 스트림)
 *
 *  사용한 것 : 캡슐화, 싱글턴, List, 사용자 정의 예외 + throws, multi-catch,
 *             BufferedReader(키보드), ObjectInput/OutputStream(파일), Serializable
 *
 *  ※ students.dat 은 프로젝트 폴더에 생김 → 다시 실행하면 이전 데이터가 그대로!
 *
 * [실행 결과]  (메뉴 안내 줄은 생략, 메뉴 > / 학번 > 뒤는 키보드 입력)
 * --- 1회차 실행 (students.dat 없음) ---
 * 메뉴 > 1
 * 학번,이름,캠퍼스,반 > 1500001,김싸피,서울,1
 * 등록 완료
 * 메뉴 > 1
 * 학번,이름,캠퍼스,반 > 1500002,이자바,대전,3
 * 등록 완료
 * 메뉴 > 1
 * 학번,이름,캠퍼스,반 > 1500001,홍길동,부울경,2
 * [오류] 1500001 학번은 이미 등록되어 있습니다.
 * 메뉴 > 1
 * 학번,이름,캠퍼스,반 > 1500003,박오류,구미,반
 * [오류] 입력 형식이 잘못되었습니다.
 * 메뉴 > 2
 * Student [id=1500001, name=김싸피, campus=서울, class=1]
 * Student [id=1500002, name=이자바, campus=대전, class=3]
 * 메뉴 > 4
 * 학번,캠퍼스,반 > 1500002,구미,5
 * 수정 완료
 * 메뉴 > 3
 * 학번 > 1500002
 * Student [id=1500002, name=이자바, campus=구미, class=5]
 * 메뉴 > 5
 * 학번 > 1500009
 * [오류] 1500009 학번의 교육생이 없습니다.
 * 메뉴 > 0
 * 종료합니다. (데이터는 students.dat에 저장되어 있음)
 *
 * --- 2회차 실행 (다시 켜도 데이터가 남아 있음!) ---
 * 메뉴 > 2
 * Student [id=1500001, name=김싸피, campus=서울, class=1]
 * Student [id=1500002, name=이자바, campus=구미, class=5]
 * 메뉴 > 5
 * 학번 > 1500001
 * 삭제 완료
 * 메뉴 > 2
 * Student [id=1500002, name=이자바, campus=구미, class=5]
 */
public class StudentApp {
    public static void main(String[] args) throws IOException {
        StudentManager sm = StudentManager.getInstance();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("\n1.등록 2.전체조회 3.학번조회 4.수정 5.삭제 0.종료");
            System.out.print("메뉴 > ");
            String line = br.readLine();
            if (line == null) break;                      // 입력 끝 (Ctrl+D)

            try {
                switch (line.trim()) {
                    case "1" -> {
                        System.out.print("학번,이름,캠퍼스,반 > ");
                        String[] in = br.readLine().split(",");
                        sm.add(new Student(in[0].trim(), in[1].trim(), in[2].trim(), Integer.parseInt(in[3].trim())));
                        System.out.println("등록 완료");
                    }
                    case "2" -> {
                        List<Student> list = sm.getList();
                        if (list.isEmpty()) System.out.println("등록된 교육생이 없습니다.");
                        list.forEach(System.out::println);
                    }
                    case "3" -> {
                        System.out.print("학번 > ");
                        System.out.println(sm.get(br.readLine().trim()));
                    }
                    case "4" -> {
                        System.out.print("학번,캠퍼스,반 > ");
                        String[] up = br.readLine().split(",");
                        sm.update(up[0].trim(), up[1].trim(), Integer.parseInt(up[2].trim()));
                        System.out.println("수정 완료");
                    }
                    case "5" -> {
                        System.out.print("학번 > ");
                        sm.remove(br.readLine().trim());
                        System.out.println("삭제 완료");
                    }
                    case "0" -> {
                        System.out.println("종료합니다. (데이터는 students.dat에 저장되어 있음)");
                        return;
                    }
                    default -> System.out.println("메뉴 번호를 다시 입력하세요.");
                }
            } catch (StudentNotFoundException | DuplicateStudentException e) {     // 우리가 만든 예외
                System.out.println("[오류] " + e.getMessage());
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {  // 입력 형식 실수
                System.out.println("[오류] 입력 형식이 잘못되었습니다.");
            }
        }
    }
}
