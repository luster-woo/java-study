package practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class D5P1 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int k = Integer.parseInt(st.nextToken());

        // 1. 단어 → 횟수
        Map<String, Integer> count = new HashMap<>();
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            while (st.hasMoreTokens()) {
                String word = st.nextToken().toLowerCase();       // 대소문자 구분 X → 소문자로 통일
                count.put(word, count.getOrDefault(word, 0) + 1);  // 없으면 0에서 시작
            }
        }

        // 2. 정렬 : 횟수 내림차순 → 같으면 단어 사전순 → 앞에서 k개
        StringBuilder sb = new StringBuilder();
        count.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()   // 횟수 큰 순
                        .thenComparing(Map.Entry.comparingByKey()))                  // 같으면 사전순
                .limit(k)                                                            // 적으면 있는 만큼
                .forEach(e -> sb.append(e.getKey()).append(' ').append(e.getValue()).append('\n'));
        // 같은 정렬을 람다로 쓰면 :
        // .sorted((a, b) -> a.getValue().equals(b.getValue())
        //         ? a.getKey().compareTo(b.getKey())
        //         : b.getValue() - a.getValue())

        System.out.print(sb);
    }
}
