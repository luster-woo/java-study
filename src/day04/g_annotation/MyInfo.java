package day04.g_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 직접 만드는 애너테이션 : @interface
 *
 *  메타 애너테이션 (애너테이션을 설정하는 애너테이션)
 *   - @Retention : 언제까지 정보를 유지할지
 *       SOURCE  : 컴파일 후 사라짐 (@Override 처럼 컴파일러만 봄)
 *       CLASS   : .class 파일엔 있지만 실행 중엔 못 읽음 (기본값)
 *       RUNTIME : 실행 중에도 읽을 수 있음 (리플렉션) → 프레임워크가 쓰는 방식
 *   - @Target : 어디에 붙일 수 있는지 (TYPE=클래스, METHOD, FIELD ...)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MyInfo {
    // 속성 : 추상 메서드처럼 선언, 사용할 땐 이름=값
    String author();                     // 필수 속성 (default 없음)
    int version() default 1;             // 선택 속성 (안 쓰면 1)
}
