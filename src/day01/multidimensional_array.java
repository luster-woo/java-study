package day01;

public class multidimensional_array {
    public static void main(String[] args) {
        int [][] intArray;
        int intArray1 [][];
        int [] intArray2 [];
        int [][] intArray3 = new int[4][];

                int[][] grid = {
                        {2, 3, 1, 4, 7},{8, 13, 3, 33, 1},
                        {7, 4, 5, 80, 12},{17, 9, 11, 5, 4},
                        {4, 5, 91, 27, 7}
                };

                int count = 0;
                int sum = 0;
                for(int [] row: grid) {
                    for(int num:row) {
                        if(num%3==0) {
                            count++;
                            sum+=num;
                        }
                    }
                }
                System.out.printf("개수: %d, 총합: %d%n", count, sum);
            }

}
