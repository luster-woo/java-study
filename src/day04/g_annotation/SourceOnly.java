package day04.g_annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// 비교용 : SOURCE라서 컴파일되면 사라짐 → 실행 중에 읽을 수 없다
@Retention(RetentionPolicy.SOURCE)
public @interface SourceOnly {
}
