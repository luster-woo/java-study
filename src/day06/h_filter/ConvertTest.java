package day06.h_filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/*
 * 보조 스트림 = 노드 스트림에 "기능을 덧붙이는" 스트림 (혼자서는 못 씀, 항상 노드 스트림이 필요)
 *
 *   new 보조스트림( new 노드스트림(...) )     ← 생성자에 이전 스트림을 넣어 연결 (스트림 체이닝)
 *   보조스트림.close() 하면 연결된 노드 스트림까지 같이 닫힘
 *
 * InputStreamReader / OutputStreamWriter = byte 스트림 ↔ char 스트림 변환기
 *  - InputStreamReader  : byte 들어옴 → char로 바꿔서 읽기
 *  - OutputStreamWriter : char 쓰면 → byte로 바꿔서 내보내기
 *  - 이때 어떤 인코딩(UTF-8, EUC-KR ...)으로 바꿀지 지정할 수 있다
 *  - 대표 사용처 : System.in(키보드, byte)을 문자로 읽을 때
 *
 * [실행 결과]
 * byte 개수 : 16 (한글 5자 × 3 + 공백 1)
 * --- 1. byte → char (InputStreamReader) ---
 * 자|바| |스|트|림|
 * --- 2. char → byte (OutputStreamWriter) ---
 * byte 개수 : 6
 * byte 값   : [-22, -80, -128, -21, -126, -104]
 * --- 3. 인코딩이 다르면? ---
 * EUC-KR byte 개수 : 4
 * EUC-KR로 읽기 : 가나
 * UTF-8로 읽기  : ����   (콘솔에 따라 ? 로도 보임)
 */
public class ConvertTest {
    public static void main(String[] args) throws IOException {
        byte[] bytes = "자바 스트림".getBytes(StandardCharsets.UTF_8);
        System.out.println("byte 개수 : " + bytes.length + " (한글 5자 × 3 + 공백 1)");

        System.out.println("--- 1. byte → char (InputStreamReader) ---");
        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8)) {
            int c;
            while ((c = reader.read()) != -1) {                 // read() 한 번에 "한 글자"
                System.out.print((char) c + "|");
            }
            System.out.println();
        }

        System.out.println("--- 2. char → byte (OutputStreamWriter) ---");
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (Writer writer = new OutputStreamWriter(bout, StandardCharsets.UTF_8)) {
            writer.write("가나");                               // 문자열을 그대로 씀
        }                                                       // close 해야 버퍼에 남은 게 bout으로 나감
        System.out.println("byte 개수 : " + bout.size());
        System.out.println("byte 값   : " + Arrays.toString(bout.toByteArray()));

        System.out.println("--- 3. 인코딩이 다르면? ---");
        byte[] eucKr = "가나".getBytes(Charset.forName("EUC-KR"));   // 옛날 한글 인코딩 (한글 2byte)
        System.out.println("EUC-KR byte 개수 : " + eucKr.length);
        System.out.println("EUC-KR로 읽기 : " + readAll(eucKr, Charset.forName("EUC-KR")));
        System.out.println("UTF-8로 읽기  : " + readAll(eucKr, StandardCharsets.UTF_8));  // 깨짐!
        // → 쓸 때와 읽을 때 인코딩이 같아야 한다 (윈도우 메모장 옛 파일이 깨지는 이유)
    }

    static String readAll(byte[] data, Charset cs) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(data), cs)) {
            int c;
            while ((c = reader.read()) != -1) sb.append((char) c);
        }
        return sb.toString();
    }
}
