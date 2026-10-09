package day01;

public class array {
    public static void main(String[] args) {
        int [] points;
        String names [];
        points = new int [3];
        points[0] = 1;
        points[1] = 'A'; // 묵시적 형 변환
        points[2] = 1; //double 할당 X
        System.out.printf("0번째 요소: %d\n", points[0]);
        System.out.printf("1번째 요소: %d\n", points[1]);
        System.out.printf("2번째 요소: %d\n", points[2]);
        String org = "1234567890";
        char[] chars = new char[org.length()];
        int[] nums = new int[org.length()];
        for (int i = 0; i<chars.length; i++){
            chars[i]=org.charAt(i);
            nums[i] = chars[i]-'0';
        }
        for(int i = 0; i<chars.length;i++){
            System.out.printf("char %c, num %d\n",chars[i],nums[i]);
        }
        int[] intArray = { 3, 27, 13, 8, 235, 7, 22, 9, 435, 31, 54 };
        int min = intArray[0], max = intArray[0], sum = 0;

        for (int num : intArray) {
            sum += num;
            min = min > num ? num : min;
            max = max < num ? num : max;
        }

        double avg = sum / intArray.length;

        int maxDiff = intArray[0], minDiff = intArray[0];
        for (int i = 0; i < intArray.length; i++) {
            minDiff = Math.abs(avg-minDiff) >Math.abs(avg-intArray[i])?intArray[i]:minDiff;
            maxDiff = Math.abs(avg-maxDiff) <Math.abs(avg-intArray[i])?intArray[i]:maxDiff;
        }

        System.out.printf("max: %d, min: %d, avg:%.2f, maxDiff: %d, minDiff:%d%n", min, max, avg,
                maxDiff, minDiff);

    }
}
