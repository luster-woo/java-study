package com.ssafy.fw_06_rest.tx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

// [05, 06] 주소 → 회원 삭제가 "한 묶음" 으로 처리되는지 확인
// 주의: 테스트 클래스에 @Transactional 을 붙이지 않음 (붙이면 테스트 자체가 하나의 트랜잭션이 되어 결과가 달라짐)
@SpringBootTest
class TxMemberServiceTest {

    @Autowired TxMemberService service;
    @Autowired TxMemberDao mDao;
    @Autowired TxAddressDao aDao;
    @Autowired JdbcTemplate jdbc;

    final int MNO = 1;

    @BeforeEach
    void setUp() {   // 매 테스트마다 회원 1명 + 주소 1개로 시작
        jdbc.update("DELETE FROM tx_address");
        jdbc.update("DELETE FROM tx_log");
        jdbc.update("DELETE FROM tx_member");
        mDao.insert(MNO, "hong");
        aDao.insert(MNO, "서울시 강남구");
    }

    void assertBothDeleted() {
        assertEquals(0, aDao.countByMno(MNO), "주소 삭제됨");
        assertEquals(0, mDao.count(MNO), "회원 삭제됨");
    }

    void assertBothRemain() {   // rollback 성공 = 둘 다 그대로
        assertEquals(1, aDao.countByMno(MNO), "주소 남아 있음");
        assertEquals(1, mDao.count(MNO), "회원 남아 있음");
    }

    void assertHalfDone() {     // 반만 성공 = 주소만 지워지고 회원은 남음
        assertEquals(0, aDao.countByMno(MNO), "주소는 삭제됨");
        assertEquals(1, mDao.count(MNO), "회원은 남아 있음");
    }

    @Test
    @DisplayName("[05] 예외가 없으면 주소, 회원 모두 삭제")
    void deleteSuccess() {
        service.delete(MNO, false);
        assertBothDeleted();
    }

    @Test
    @DisplayName("[05] @Transactional 없음 + 중간 예외 → 주소만 지워짐 (반만 성공)")
    void deleteWithoutTx() {
        assertThrows(ArithmeticException.class, () -> service.deleteWithoutTx(MNO, true));
        assertHalfDone();
    }

    @Test
    @DisplayName("[05] @Transactional + 중간 예외(RuntimeException) → 전부 rollback")
    void deleteWithTx() {
        assertThrows(ArithmeticException.class, () -> service.delete(MNO, true));
        assertBothRemain();
    }

    @Test
    @DisplayName("[06] Checked 예외는 기본적으로 rollback 안 함 → 주소 삭제가 commit 됨")
    void checkedExceptionCommits() {
        assertThrows(BusinessException.class, () -> service.deleteThrowChecked(MNO));
        assertHalfDone();
    }

    @Test
    @DisplayName("[06] rollbackFor 지정 → Checked 예외도 rollback")
    void checkedExceptionWithRollbackFor() {
        assertThrows(BusinessException.class, () -> service.deleteThrowCheckedWithRollbackFor(MNO));
        assertBothRemain();
    }

    @Test
    @DisplayName("[06] 같은 클래스 안에서 호출(self-invocation) → @Transactional 적용 안 됨")
    void selfInvocation() {
        assertThrows(ArithmeticException.class, () -> service.deleteBySelfInvocation(MNO));
        assertHalfDone();   // delete() 에 @Transactional 이 있는데도 rollback 안 됨!
    }

    @Test
    @DisplayName("[06] 명시적 T.X(PlatformTransactionManager) + 중간 예외 → rollback")
    void programmaticTx() {
        assertThrows(ArithmeticException.class, () -> service.deleteProgrammatic(MNO, true));
        assertBothRemain();
    }
}
