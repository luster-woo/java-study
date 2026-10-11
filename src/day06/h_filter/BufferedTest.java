package day06.h_filter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

/*
 * Buffered 계열 = 중간에 버퍼(기본 8192)를 둬서 입출력 횟수를 줄이는 보조 스트림
 *
 *   프로그램 ⇄ [버퍼 8KB] ⇄ 파일
 *   read()를 1byte씩 불러도 실제 파일 접근은 8KB에 한 번 → 훨씬 빠름
 *
 *  - BufferedInputStream / BufferedOutputStream : byte용
 *  - BufferedReader / BufferedWriter            : char용
 *      readLine() : 한 줄 통째로 읽기 (줄바꿈 문자는 빠짐), 끝이면 null
 *      newLine()  : 줄바꿈 쓰기 (OS에 맞는 줄바꿈)
 *  - 출력 쪽은 버퍼가 찰 때까지 안 나가므로 flush() 또는 close() 필수
 *
 * [실행 결과]  (시간은 컴퓨터마다 다름)
 * --- 1. 노드 vs 보조 : 1byte씩 복사 ---
 * 노드 스트림 : 852 ms
 * 보조 스트림 : 63 ms
 * --- 2. BufferedWriter / BufferedReader ---
 * 1 → 첫째 줄 : 보조 스트림
 * 2 → 둘째 줄 : 스트림 체이닝
 * 3 → 셋째 줄 : readLine
 * --- 3. Scanner vs BufferedReader (10만 줄) ---
 * BufferedReader : 32 ms
 * Scanner        : 198 ms   ← 편하지만 느림 (그래서 코테는 BufferedReader)
 */
public class BufferedTest {
    public static void main(String[] args) throws IOException {

        System.out.println("--- 1. 노드 vs 보조 : 1byte씩 복사 ---");
        File src = new File("buf_day06.bin");
        File target = new File("buf_day06_copy.bin");
        try (OutputStream out = new FileOutputStream(src)) {
            out.write(new byte[1024 * 1024]);                    // 1MB
        }

        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(target)) {
            copyOneByOne("노드 스트림", in, out);                // 파일에 100만 번 접근
        }
        try (InputStream in = new BufferedInputStream(new FileInputStream(src));        // 체이닝!
             OutputStream out = new BufferedOutputStream(new FileOutputStream(target))) {
            copyOneByOne("보조 스트림", in, out);                // 실제 접근은 8KB마다 → 약 128번
        }
        src.delete();
        target.delete();

        System.out.println("--- 2. BufferedWriter / BufferedReader ---");
        File lines = new File("lines_day06.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(lines))) {
            bw.write("첫째 줄 : 보조 스트림");
            bw.newLine();
            bw.write("둘째 줄 : 스트림 체이닝");
            bw.newLine();
            bw.write("셋째 줄 : readLine");
        }
        try (BufferedReader br = new BufferedReader(new FileReader(lines))) {
            String line;
            int no = 1;
            while ((line = br.readLine()) != null) {             // 끝이면 -1이 아니라 null!
                System.out.println(no++ + " → " + line);
            }
        }

        System.out.println("--- 3. Scanner vs BufferedReader (10만 줄) ---");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(lines))) {
            for (int i = 0; i < 100_000; i++) {
                bw.write("line " + i);
                bw.newLine();
            }
        }
        long t1 = System.currentTimeMillis();
        try (BufferedReader br = new BufferedReader(new FileReader(lines))) {
            while (br.readLine() != null) { }
        }
        long t2 = System.currentTimeMillis();
        try (Scanner sc = new Scanner(lines)) {
            while (sc.hasNextLine()) sc.nextLine();
        }
        long t3 = System.currentTimeMillis();
        System.out.println("BufferedReader : " + (t2 - t1) + " ms");
        System.out.println("Scanner        : " + (t3 - t2) + " ms   ← 편하지만 느림 (그래서 코테는 BufferedReader)");
        lines.delete();
    }

    // InputStream / OutputStream 타입으로 받으니 노드든 보조든 다 들어감 (다형성)
    static void copyOneByOne(String type, InputStream in, OutputStream out) throws IOException {
        long start = System.currentTimeMillis();
        int b;
        while ((b = in.read()) != -1) {                         // 일부러 1byte씩
            out.write(b);
        }
        System.out.println(type + " : " + (System.currentTimeMillis() - start) + " ms");
    }
}
