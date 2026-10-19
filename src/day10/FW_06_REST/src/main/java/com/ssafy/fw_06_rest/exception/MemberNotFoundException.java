package com.ssafy.fw_06_rest.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(int mno) {
        super(mno + "번 회원이 없습니다.");
    }
}
