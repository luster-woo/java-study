package day06.d_custom;

import java.util.Arrays;

/*
 * UserExceptionTest를 사용자 정의 예외로 바꾼 버전
 *
 *  - getFruit2 : 과일이 없으면 FruitNotFoundException을 던진다
 *  - addFruit  : 빈 칸이 없으면 NoSpaceException을 던진다 (교재 과제)
 *  - 호출할 때마다 if로 확인할 필요 X → 실패하면 바로 catch로 점프
 *
 * [실행 결과]
 * 오렌지 꺼냄
 * 오렌지에 해당하는 과일은 없습니다.
 * 수박을(를) 넣을 빈 칸이 없습니다.
 * [사과, 포도, 토마토]
 */
class FruitNotFoundException extends Exception {
    public FruitNotFoundException(String name) {
        super(name + "에 해당하는 과일은 없습니다.");
    }
}

class NoSpaceException extends Exception {
    public NoSpaceException(String name) {
        super(name + "을(를) 넣을 빈 칸이 없습니다.");
    }
}

public class FruitExceptionTest {
    private static String[] fruits = {"사과", "오렌지", "토마토"};

    public static void main(String[] args) {
        try {
            getFruit2("오렌지");
            System.out.println("오렌지 꺼냄");
            getFruit2("오렌지");                         // 없음 → 예외
            System.out.println("여기는 실행되지 않음");
        } catch (FruitNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            addFruit("포도");                            // 오렌지가 빠진 칸(null)에 들어감
            addFruit("수박");                            // 빈 칸 없음 → 예외
        } catch (NoSpaceException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(Arrays.toString(fruits));
    }

    private static void getFruit2(String name) throws FruitNotFoundException {
        for (int i = 0; i < fruits.length; i++) {
            if (fruits[i] != null && fruits[i].equals(name)) {
                fruits[i] = null;
                return;
            }
        }
        throw new FruitNotFoundException(name);
    }

    private static void addFruit(String name) throws NoSpaceException {
        for (int i = 0; i < fruits.length; i++) {
            if (fruits[i] == null) {
                fruits[i] = name;
                return;
            }
        }
        throw new NoSpaceException(name);
    }
}
