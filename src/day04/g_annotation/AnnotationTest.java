package day04.g_annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/*
 * 애너테이션 (annotation) = 컴파일러, JVM, 프레임워크가 읽는 "주석" (메타데이터)
 *  - 코드의 동작을 직접 바꾸진 않고, "이 코드는 이런 거야"라는 정보를 붙인다
 *  - 그 정보를 읽은 누군가(컴파일러, Spring 등)가 그에 맞게 처리한다
 *
 * [실행 결과]
 * --- 1. JDK 기본 애너테이션 ---
 * 멍멍
 * 옛날 방식 (쓰지 마세요)
 * --- 2. 직접 만든 애너테이션 읽기 (리플렉션) ---
 * 클래스 작성자 : 이윤우, 버전 : 2
 * hello() 작성자 : 김싸피, 버전 : 1
 * bye() 에 @MyInfo 있음? false
 * @SourceOnly 실행 중에 보임? false
 */

class Animal {
    void sound() { System.out.println("..."); }
}

class Dog extends Animal {
    // @Override : "부모 메서드를 재정의한 거야" → 오타나 시그니처가 틀리면 컴파일 에러
    @Override
    void sound() { System.out.println("멍멍"); }

    // @Deprecated : "더 이상 쓰지 마" → 호출하는 곳에 취소선 + 경고
    @Deprecated
    void oldSound() { System.out.println("옛날 방식 (쓰지 마세요)"); }
}

// 직접 만든 애너테이션 사용 : 속성=값
@MyInfo(author = "이윤우", version = 2)
@SourceOnly
class Greeter {

    @MyInfo(author = "김싸피")           // version 생략 → default 1
    void hello() {}

    void bye() {}                         // 아무것도 안 붙임
}

public class AnnotationTest {

    // @SuppressWarnings : "이 경고는 알고 있으니 무시해" → 남발 금지
    @SuppressWarnings({"rawtypes", "unchecked", "deprecation"})
    public static void main(String[] args) throws Exception {
        System.out.println("--- 1. JDK 기본 애너테이션 ---");
        Dog d = new Dog();
        d.sound();
        d.oldSound();                     // deprecation 경고 (위에서 무시함)
        List raw = new ArrayList();       // rawtypes 경고 (위에서 무시함)
        raw.add("x");

        System.out.println("--- 2. 직접 만든 애너테이션 읽기 (리플렉션) ---");
        // 리플렉션 : 실행 중에 클래스 정보를 들여다보는 기능 (Spring이 이렇게 애너테이션을 읽는다)
        Class<Greeter> clazz = Greeter.class;

        MyInfo classInfo = clazz.getAnnotation(MyInfo.class);
        System.out.println("클래스 작성자 : " + classInfo.author() + ", 버전 : " + classInfo.version());

        Method hello = clazz.getDeclaredMethod("hello");
        MyInfo methodInfo = hello.getAnnotation(MyInfo.class);
        System.out.println("hello() 작성자 : " + methodInfo.author() + ", 버전 : " + methodInfo.version());

        Method bye = clazz.getDeclaredMethod("bye");
        System.out.println("bye() 에 @MyInfo 있음? " + bye.isAnnotationPresent(MyInfo.class));

        // SOURCE 애너테이션은 컴파일하면서 사라져서 실행 중엔 안 보인다
        System.out.println("@SourceOnly 실행 중에 보임? " + clazz.isAnnotationPresent(SourceOnly.class));
    }
}
