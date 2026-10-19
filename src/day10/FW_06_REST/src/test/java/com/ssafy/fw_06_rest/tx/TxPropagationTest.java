package com.ssafy.fw_06_rest.tx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.junit.jupiter.api.Assertions.*;

// [06-3] 전파 속성: 바깥(TxOuterService, 회원 저장) → 안쪽(TxLogService, 로그 저장)
@SpringBootTest
class TxPropagationTest {

    @Autowired TxOuterService outerService;
    @Autowired TxMemberDao mDao;
    @Autowired TxLogDao logDao;
    @Autowired JdbcTemplate jdbc;

    final int MNO = 10;

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM tx_address");
        jdbc.update("DELETE FROM tx_log");
        jdbc.update("DELETE FROM tx_member");
    }

    @Test
    @DisplayName("REQUIRED: 안쪽 실패 → 바깥에서 예외를 잡아도 전체 rollback + UnexpectedRollbackException")
    void required() {
        assertThrows(UnexpectedRollbackException.class, () -> outerService.joinWithRequired(MNO, "hong"));
        assertEquals(0, mDao.count(MNO), "같은 배 → 회원도 rollback");
        assertEquals(0, logDao.count(), "로그도 rollback");
    }

    @Test
    @DisplayName("REQUIRES_NEW: 안쪽 실패 → 안쪽(로그)만 rollback, 바깥(회원)은 commit")
    void requiresNew() {
        assertDoesNotThrow(() -> outerService.joinWithRequiresNew(MNO, "hong"));
        assertEquals(1, mDao.count(MNO), "다른 배 → 회원은 저장됨");
        assertEquals(0, logDao.count(), "로그만 rollback");
    }

    @Test
    @DisplayName("REQUIRES_NEW: 안쪽은 이미 commit, 바깥 실패 → 회원은 rollback, 로그는 남음")
    void outerFailAfterRequiresNew() {
        assertThrows(IllegalStateException.class, () -> outerService.joinFailAfterLog(MNO, "hong"));
        assertEquals(0, mDao.count(MNO), "회원은 rollback");
        assertEquals(1, logDao.count(), "가입 시도 로그는 남음");
    }
}
