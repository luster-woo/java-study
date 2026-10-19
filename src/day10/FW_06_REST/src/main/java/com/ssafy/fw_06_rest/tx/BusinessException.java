package com.ssafy.fw_06_rest.tx;

// [06] Checked Exception (Exception 을 상속, RuntimeException 아님)
// → @Transactional 기본 규칙에서는 이 예외가 나도 rollback 하지 않고 commit 함!
public class BusinessException extends Exception {
    public BusinessException(String message) {
        super(message);
    }
}
