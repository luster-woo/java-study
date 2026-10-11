package day06.h_filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 * 교재 과제 : 일기장(DiaryTest)을 Buffered 계열로 바꾸기
 *
 *  키보드 입력 : System.in(byte) → InputStreamReader(char로 변환) → BufferedReader(줄 단위)
 *  파일 쓰기   : FileWriter → BufferedWriter (write + newLine)
 *  파일 읽기   : FileReader → BufferedReader (readLine)
 *
 *  바뀐 점 (DiaryTest와 비교)
 *   - Scanner 대신 BufferedReader로 키보드 입력 (코테에서 쓰는 그 방식)
 *   - "\n" 대신 newLine()
 *   - char[] 버퍼 + String.valueOf 대신 readLine() 한 줄로 끝
 *
 * [실행 결과]  입력 : 보조 스트림 공부 / 체이닝 이해함 / x   (날짜는 실행 시각)
 * 일기를 작성합니다. 그만두려면 x를 입력하세요.
 * 보조 스트림 공부       ← 키보드 입력
 * 체이닝 이해함          ← 키보드 입력
 * x                      ← 키보드 입력 (종료)
 * ===== diary_buffered.txt 내용 =====
 * [2026-10-11 17:05]
 * 보조 스트림 공부
 * 체이닝 이해함
 */
public class BufferedDiaryTest {
    public static void main(String[] args) {
        File target = new File("diary_buffered.txt");
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // ⚠ System.in은 닫으면 프로그램에서 다시 못 쓰므로 try 괄호 밖에 둠
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(target, true))) {
            System.out.println("일기를 작성합니다. 그만두려면 x를 입력하세요.");
            writer.write("[" + now + "]");
            writer.newLine();
            String line;
            while ((line = keyboard.readLine()) != null && !line.equals("x")) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {                            // try 블록 끝 → close → 버퍼 내용이 파일로
            e.printStackTrace();
        }

        System.out.println("===== " + target.getName() + " 내용 =====");
        try (BufferedReader reader = new BufferedReader(new FileReader(target))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 다시 실행하면 이전 일기 아래에 계속 쌓인다
    }
}
