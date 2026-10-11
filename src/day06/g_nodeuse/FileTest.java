package day06.g_nodeuse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/*
 * File = 파일과 디렉터리 "자체"를 다루는 클래스
 *  - 만들기, 지우기, 이름·경로·크기 확인, 폴더 안 목록 보기
 *  - 파일 "내용"은 못 읽고 못 씀 → 그건 스트림 담당
 *  - new File(...)만으로는 실제 파일이 안 생긴다! → createNewFile(), mkdirs()
 *  - 경로 없이 이름만 쓰면 "실행 위치" 기준 (IntelliJ에서는 프로젝트 폴더)
 *  - File.separator : OS별 경로 구분자 (Windows \ , Mac/Linux /)
 *
 * [실행 결과]  (경로 구분자는 Windows면 \ 로 나옴)
 * --- 1. 객체만 만들면? ---
 * 존재? false
 * --- 2. 폴더 / 파일 만들기 ---
 * mkdirs        : true
 * createNewFile : true
 * 또 만들면     : false
 * --- 3. 정보 보기 ---
 * getName  : memo.txt
 * getPath  : temp_day06/sub/memo.txt
 * isFile   : true / isDirectory : true
 * length   : 0byte
 * 쓴 뒤    : 5byte
 * --- 4. 절대 경로 vs 정식 경로 ---
 * .. 포함? 절대경로 true / 정식경로 false
 * --- 5. 폴더 목록 ---
 *   a.txt (0byte)
 *   b.txt (0byte)
 *   memo.txt (5byte)
 * --- 6. 삭제 ---
 * 내용 있는 폴더 삭제 : false
 * 비운 뒤 삭제        : true
 * 상위 폴더 삭제      : true
 */
public class FileTest {
    public static void main(String[] args) throws IOException {

        System.out.println("--- 1. 객체만 만들면? ---");
        File dir = new File("temp_day06" + File.separator + "sub");
        System.out.println("존재? " + dir.exists());                  // false : 객체만 만든 것

        System.out.println("--- 2. 폴더 / 파일 만들기 ---");
        System.out.println("mkdirs        : " + dir.mkdirs());         // 중간 폴더(temp_day06)까지 전부 생성
        File memo = new File(dir, "memo.txt");                        // dir 안의 memo.txt
        System.out.println("createNewFile : " + memo.createNewFile()); // true
        System.out.println("또 만들면     : " + memo.createNewFile()); // false (이미 있음)

        System.out.println("--- 3. 정보 보기 ---");
        System.out.println("getName  : " + memo.getName());
        System.out.println("getPath  : " + memo.getPath());
        System.out.println("isFile   : " + memo.isFile() + " / isDirectory : " + dir.isDirectory());
        System.out.println("length   : " + memo.length() + "byte");   // 아직 비어 있음

        try (OutputStream out = new FileOutputStream(memo)) {          // 내용은 스트림으로 쓴다
            out.write("hello".getBytes());
        }
        System.out.println("쓴 뒤    : " + memo.length() + "byte");

        System.out.println("--- 4. 절대 경로 vs 정식 경로 ---");
        File dot = new File("temp_day06/sub/../sub/memo.txt");
        System.out.println(".. 포함? 절대경로 " + dot.getAbsolutePath().contains("..")
                + " / 정식경로 " + dot.getCanonicalPath().contains(".."));
        // getAbsolutePath()  : 그냥 앞에 현재 위치를 붙인 것 (.. 그대로)
        // getCanonicalPath() : .. 과 . 을 정리한 "진짜" 경로

        System.out.println("--- 5. 폴더 목록 ---");
        new File(dir, "a.txt").createNewFile();
        new File(dir, "b.txt").createNewFile();
        File[] children = dir.listFiles();
        java.util.Arrays.sort(children);                              // 순서가 OS마다 달라서 정렬
        for (File f : children) {
            System.out.println("  " + f.getName() + " (" + f.length() + "byte)");
        }

        System.out.println("--- 6. 삭제 ---");
        System.out.println("내용 있는 폴더 삭제 : " + dir.delete());   // false : 폴더는 비어 있어야 지워짐
        for (File f : dir.listFiles()) f.delete();                    // 안의 파일부터 지우고
        System.out.println("비운 뒤 삭제        : " + dir.delete());   // true
        System.out.println("상위 폴더 삭제      : " + new File("temp_day06").delete());
    }
}
