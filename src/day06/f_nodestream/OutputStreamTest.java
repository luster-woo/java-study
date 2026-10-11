package day06.f_nodestream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/*
 * OutputStream의 주요 메서드 (byte 단위 출력)
 *
 *  void write(int b)                     : 1byte 출력
 *  void write(byte[] b)                  : 배열 전체 출력
 *  void write(byte[] b, int off, int len): off부터 len개만 출력  ← 복사할 때 핵심!
 *  void flush()                          : 버퍼에 남은 걸 내보냄
 *  void close()                          : 자원 반납 (내부적으로 flush 호출)
 *
 *  - ByteArrayOutputStream : 메모리(byte 배열)에 쓰는 노드 스트림 → toByteArray(), toString()으로 꺼냄
 *  - FileOutputStream      : 파일에 쓰는 노드 스트림
 *      new FileOutputStream(파일)        → 덮어쓰기
 *      new FileOutputStream(파일, true)  → 이어쓰기 (append)
 *
 * [실행 결과]
 * --- 1. write 3가지 ---
 * write(int)    : hi
 * write(byte[]) : hi java world
 * write(b,0,3)  : hi java world!!!
 * byte 배열     : [104, 105, 32, 106, 97, 118, 97, 32, 119, 111, 114, 108, 100, 33, 33, 33]
 * --- 2. 파일에 쓰기 / 이어쓰기 ---
 * 자바는 객체지향 언어입니다.
 * Spring도 공부합니다.
 * 파일 크기 : 67byte (한글 18자 × 3byte + 영문·기호 13byte)
 * --- 3. 복사 : InputStream → OutputStream ---
 * 이번에 쓴 개수 : 10
 * 이번에 쓴 개수 : 10
 * 이번에 쓴 개수 : 10
 * 이번에 쓴 개수 : 9
 * 복사 결과 : 자바는 객체지향 언어입니다.
 */
public class OutputStreamTest {
    private static final String FILE_NAME = "day06_output_test.txt";

    public static void main(String[] args) {
        OutputStreamTest test = new OutputStreamTest();
        System.out.println("--- 1. write 3가지 ---");
        test.write1();
        System.out.println("--- 2. 파일에 쓰기 / 이어쓰기 ---");
        test.write2();
        System.out.println("--- 3. 복사 : InputStream → OutputStream ---");
        test.copy();

        new File(FILE_NAME).delete();                     // 연습용 파일 정리 (주석 처리하면 프로젝트 폴더에 파일이 남음)
    }

    // 1. 메모리에 써 보기
    private void write1() {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            output.write(104);                            // 'h'
            output.write('i');                            // char도 int로 바뀌어 들어감
            System.out.println("write(int)    : " + output);

            byte[] data = " java world".getBytes();
            output.write(data);                           // 배열 전체
            System.out.println("write(byte[]) : " + output);

            byte[] more = "!!!???".getBytes();
            output.write(more, 0, 3);                     // 앞에서 3개만 → "!!!"
            System.out.println("write(b,0,3)  : " + output);

            System.out.println("byte 배열     : " + Arrays.toString(output.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 2. 파일에 쓰고, 다시 읽어서 확인
    private void write2() {
        try (OutputStream output = new FileOutputStream(FILE_NAME)) {           // 덮어쓰기
            output.write("자바는 객체지향 언어입니다.\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (OutputStream output = new FileOutputStream(FILE_NAME, true)) {     // 이어쓰기
            output.write("Spring도 공부합니다.\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (InputStream input = new FileInputStream(FILE_NAME)) {
            byte[] all = input.readAllBytes();                                   // Java 9+ : 한 번에 전부
            System.out.print(new String(all));
            System.out.println("파일 크기 : " + all.length + "byte (한글 18자 × 3byte + 영문·기호 13byte)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 3. 읽은 만큼만 쓰기 - 파일 복사의 기본 패턴
    private void copy() {
        String data = "자바는 객체지향 언어입니다.";
        byte[] buffer = new byte[10];

        try (InputStream input = new ByteArrayInputStream(data.getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            int read = -1;
            while ((read = input.read(buffer)) > 0) {
                output.write(buffer, 0, read);            // ✅ 읽은 개수만큼만 (write(buffer) 하면 찌꺼기까지 써짐)
                System.out.println("이번에 쓴 개수 : " + read);
            }
            // 조각조각 썼지만 다 모은 뒤에 한 번에 String으로 바꾸니까 한글이 안 깨진다
            System.out.println("복사 결과 : " + output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
