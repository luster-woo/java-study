package day06.f_nodestream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 * InputStream의 주요 메서드 (byte 단위 입력)
 *
 *  int read()            : 1byte 읽어서 0~255 반환, 끝이면 -1
 *  int read(byte[] buf)  : buf 크기만큼 읽어서 buf에 채우고 "읽은 개수" 반환, 끝이면 -1
 *
 *  - ByteArrayInputStream : byte 배열을 데이터 소스로 쓰는 노드 스트림 (연습용으로 편함)
 *  - try-with-resources로 열면 끝나고 자동 close
 *
 *  ⚠ UTF-8 한글은 한 글자가 3byte
 *     → 10byte씩 자르면 글자 중간에서 잘려서 깨진다 (?? 출력)
 *     → 또 new String(buffer)는 버퍼 "전체"를 쓰므로 마지막에 이전 데이터가 남는다
 *     → new String(buffer, 0, read) 로 읽은 만큼만 써야 함
 *     → 문자는 Reader(char 단위)로 읽는 게 정답 (다음 노트)
 *
 * [실행 결과]
 * --- 1. 바이트 한 개씩 읽음 ---
 * 읽은 값: 104, 문자로: h
 * 읽은 값: 105, 문자로: i
 * 읽은 값: 32, 문자로:
 * 읽은 값: 106, 문자로: j
 * 읽은 값: 97, 문자로: a
 * 읽은 값: 118, 문자로: v
 * 읽은 값: 97, 문자로: a
 * 읽은 값: 32, 문자로:
 * 읽은 값: 119, 문자로: w
 * 읽은 값: 111, 문자로: o
 * 읽은 값: 114, 문자로: r
 * 읽은 값: 108, 문자로: l
 * 읽은 값: 100, 문자로: d
 * --- 2. Buffer 만큼씩 읽음 ---
 * 읽은 개수: 10, 문자열: 자바는
 * 읽은 개수: 10, 문자열: 객체지?
 * 읽은 개수: 10, 문자열: ?? 언어?
 * 읽은 개수: 9, 문자열: ??니다.?
 */
public class InputStreamTest {
    private String data1 = "hi java world";
    private String data2 = "자바는 객체지향 언어입니다.";

    public static void main(String[] args) {
        InputStreamTest test = new InputStreamTest();
        System.out.println("--- 1. 바이트 한 개씩 읽음 ---");
        test.read1();
        System.out.println("--- 2. Buffer 만큼씩 읽음 ---");
        test.read2();
    }

    private void read1() {
        try (InputStream input = new ByteArrayInputStream(data1.getBytes())) {
            int read = -1;
            while ((read = input.read()) != -1) {          // 읽고 → read에 넣고 → -1인지 비교 (한 줄에)
                System.out.printf("읽은 값: %d, 문자로: %c%n", read, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read2() {
        byte[] buffer = new byte[10];
        try (InputStream input = new ByteArrayInputStream(data2.getBytes())) {
            int read = -1;
            while ((read = input.read(buffer)) > 0) {
                System.out.printf("읽은 개수: %d, 문자열: %s%n", read, new String(buffer));
                // 올바른 방법 : new String(buffer, 0, read)  ← 읽은 개수만큼만
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
