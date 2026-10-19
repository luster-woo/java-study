package com.ssafy.fw_06_rest.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestController 가 붙은 클래스에서 난 예외만 처리 → JSON(ProblemDetail) 으로 응답
@Slf4j
@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler {

    @ExceptionHandler(MemberNotFoundException.class)
    public ProblemDetail handleNotFound(MemberNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());   // 404
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleRestException(Exception e) {
        log.error("[@RestController에서 발생한 오류] ", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());   // 500
    }
}
