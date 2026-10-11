package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class D6P3 {

    enum Level { INFO, WARN, ERROR }

    record LogEntry(String time, String level, String message) { }

    // 잘못된 줄이면 예외를 던진다 → main에서 잡아서 SKIPPED 세기
    static LogEntry parse(String line) {
        String[] p = line.split(" ", 4);                 // 최대 4칸 : 날짜, 시각, 레벨, 메시지(공백 포함)
        if (p.length < 4) throw new IllegalArgumentException("칸 부족");
        Level.valueOf(p[2]);                             // INFO/WARN/ERROR가 아니면 IllegalArgumentException
        return new LogEntry(p[0] + " " + p[1], p[2], p[3]);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine().trim());

        int info = 0, warn = 0, error = 0, skipped = 0;
        LogEntry lastError = null;

        for (int i = 0; i < n; i++) {
            LogEntry e;
            try {
                e = parse(br.readLine());
            } catch (IllegalArgumentException ex) {
                skipped++;
                continue;
            }
            switch (e.level()) {
                case "INFO" -> info++;
                case "WARN" -> warn++;
                case "ERROR" -> {
                    error++;
                    // "yyyy-MM-dd HH:mm:ss"는 문자열 비교만으로 시간 순서가 맞다
                    if (lastError == null || e.time().compareTo(lastError.time()) > 0) {
                        lastError = e;
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("INFO ").append(info).append('\n');
        sb.append("WARN ").append(warn).append('\n');
        sb.append("ERROR ").append(error).append('\n');
        sb.append("SKIPPED ").append(skipped).append('\n');
        sb.append("LAST ERROR: ").append(lastError == null ? "NONE" : lastError.message()).append('\n');
        System.out.print(sb);
    }
}
