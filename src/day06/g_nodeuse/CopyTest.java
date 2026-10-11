package day06.g_nodeuse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * FileInputStream + FileOutputStream = 파일 복사
 *
 *   while ((read = in.read(buffer)) > 0) {
 *       out.write(buffer, 0, read);           // 읽은 만큼만 쓰기
 *   }
 *
 *  - 버퍼가 클수록 read/write 호출 횟수가 줄어서 빨라진다
 *    (5MB를 10byte씩 → 약 52만 번 / 8192byte씩 → 약 640번)
 *  - 이미지, 영상, zip 등 "모든 파일"은 byte라서 이 코드로 다 복사된다
 *
 * [실행 결과]  (시간은 컴퓨터마다 다름)
 * 원본 크기 : 5MB
 * buffer    10 byte : read 약 524,288번, 540 ms
 * buffer  1024 byte : read 약 5,120번, 8 ms
 * buffer  8192 byte : read 약 640번, 9 ms
 * 크기 같음? true
 */
public class CopyTest {

    static long fileCopy(File src, File target, int bufferSize) {
        long start = System.currentTimeMillis();
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(target)) {
            byte[] buffer = new byte[bufferSize];
            int read;
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis() - start;
    }

    public static void main(String[] args) throws IOException {
        File src = new File("big_day06.bin");
        File target = new File("big_day06_copy.bin");

        // 1. 테스트용 5MB 파일 만들기 (내용은 0으로 채워짐)
        try (OutputStream out = new FileOutputStream(src)) {
            out.write(new byte[5 * 1024 * 1024]);
        }
        System.out.println("원본 크기 : " + src.length() / 1024 / 1024 + "MB");

        // 2. 버퍼 크기를 바꿔 가며 복사 시간 비교
        for (int size : new int[]{10, 1024, 8192}) {
            long ms = fileCopy(src, target, size);
            long calls = (src.length() + size - 1) / size;            // 올림 나눗셈
            System.out.printf("buffer %5d byte : read 약 %,d번, %d ms%n", size, calls, ms);
        }

        // 3. 제대로 복사됐는지 크기 비교
        System.out.println("크기 같음? " + (src.length() == target.length()));

        // 정리 (주석 처리하면 프로젝트 폴더에 파일이 남음)
        src.delete();
        target.delete();
    }
}
