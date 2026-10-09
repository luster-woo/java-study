package day03.d_sealed;

/*
 * sealed 클래스 (Java 17~) : "누가 나를 상속할 수 있는지" 직접 정한다
 *
 *  - sealed class 부모 permits 자식1, 자식2 ... → 적힌 자식만 상속 가능
 *  - 허락받은 자식은 반드시 셋 중 하나를 붙여야 한다 (안 붙이면 컴파일 에러)
 *      final      : 여기서 끝. 더 이상 상속 불가
 *      sealed     : 나도 다시 permits로 자식을 제한
 *      non-sealed : 제한 해제. 아무나 나를 상속 가능
 *
 *  상속 구조
 *      SealedStudyGroup (sealed)
 *       ├─ Algo    (final)       → 자식 X
 *       ├─ Java    (sealed)      → Spring만 허락
 *       │    └─ Spring (final)
 *       └─ CS      (non-sealed)  → 누구나 상속 가능
 *            └─ Network (일반 class)
 */

// TODO: SealedStudyGroup을 sealed로 선언하고 Algo, Java, CS에게 상속을 허락해보세요.
sealed class SealedStudyGroup permits Algo, Java, CS {
    public void study() {
        System.out.println("공부 공부 공부!!");
    }
}
// END

// TODO: Algo, Java, CS 클래스를 각각 final, sealed, non-sealed 형태로 작성해보세요.
final class Algo extends SealedStudyGroup {}
sealed class Java extends SealedStudyGroup permits Spring {}
non-sealed class CS extends SealedStudyGroup {}
// END

// TODO: Java와 CS를 각각 상속받는 Spring과 Network를 구성해보세요.
final class Spring extends Java {}     // Java가 sealed → 허락받은 Spring도 final/sealed/non-sealed 중 하나 필수
class Network extends CS {}            // CS가 non-sealed → 일반 class로 자유롭게 상속 가능
// END

// ❌ 컴파일 에러 예시
// class Math extends SealedStudyGroup {}   // permits에 없음
// class Kotlin extends Java {}             // Java는 Spring만 허락
// class Graph extends Algo {}              // Algo는 final
// class Python extends SealedStudyGroup {} // (permits에 넣어도) final/sealed/non-sealed 안 붙이면 에러

public class SealedTest {

    public static void main(String[] args) {
        SealedStudyGroup group = new SealedStudyGroup();
        group.study();

        // 자식들도 부모의 study()를 물려받는다
        new Algo().study();
        new Spring().study();
        new Network().study();
    }
}
