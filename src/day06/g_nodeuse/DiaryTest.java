package day06.g_nodeuse;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/*
 * FileReader / FileWriter = 문자(char) 단위 파일 입출력  → 한글 다룰 때는 이쪽!
 *
 *  일기장 : 키보드 → Scanner → FileWriter → diary.txt → FileReader → 콘솔
 *
 *  - new FileWriter(파일, true) : 이어 쓰기 → 실행할 때마다 일기가 쌓인다
 *  - write(String) : 문자열을 바로 쓸 수 있음 (getBytes() 필요 X)
 *  - flush() : Writer는 내부 버퍼에 모았다가 쓴다
 *              → flush 안 하면 아직 파일에 없어서 Reader가 못 읽을 수 있음
 *  - char 버퍼로 읽으면 한글이 중간에 잘리지 않는다 (byte와 차이!)
 *
 * [실행 결과]  입력 : 오늘은 입출력 / 스트림 공부 / x   (날짜는 실행 시각)
 * 일기를 작성합니다. 그만두려면 x를 입력하세요.
 * 오늘은 입출력          ← 키보드 입력
 * 스트림 공부            ← 키보드 입력
 * x                      ← 키보드 입력 (종료)
 * ===== diary.txt 내용 =====
 *
 * [2026-10-11 16:37]
 * 오늘은 입출력
 * 스트림 공부
 */
public class DiaryTest {
    public static void main(String[] args) {
        File target = new File("diary.txt");
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        try (Scanner scanner = new Scanner(System.in);
             Writer writer = new FileWriter(target, true);       // true : 이어 쓰기
             Reader reader = new FileReader(target)) {

            System.out.println("일기를 작성합니다. 그만두려면 x를 입력하세요.");
            writer.write("\n[" + now + "]\n");
            while (true) {
                String str = scanner.nextLine();
                if (str.equals("x")) break;
                writer.write(str + "\n");
            }
            writer.flush();                                      // ⭐ 버퍼 → 파일로 내보내기

            System.out.println("===== diary.txt 내용 =====");
            char[] buffer = new char[10];                        // byte가 아니라 char 10개
            int read;
            while ((read = reader.read(buffer)) > 0) {
                System.out.print(String.valueOf(buffer, 0, read)); // 읽은 만큼만 문자열로
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // diary.txt는 지우지 않음 → 다시 실행하면 이전 일기 아래에 계속 쌓이는 걸 확인해 보기
    }
}
