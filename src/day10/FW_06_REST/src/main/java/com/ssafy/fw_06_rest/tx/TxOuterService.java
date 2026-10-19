package com.ssafy.fw_06_rest.tx;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// [06-3] 전파 속성 실습: 교재의 BeanB (바깥에서 호출하는 쪽)
// 시나리오: 회원 가입(회원 저장) + 가입 로그 저장
@Slf4j
@Service
@RequiredArgsConstructor
public class TxOuterService {

    private final TxMemberDao mDao;
    private final TxLogService logService;   // 다른 빈 → 프록시를 거쳐서 호출됨

    // 안쪽 REQUIRED 가 실패 → 예외를 잡아서 무시해도 전체 rollback
    // 끝날 때 commit 하려는 순간 UnexpectedRollbackException 발생 → 회원도 저장 안 됨
    @Transactional
    public void joinWithRequired(int mno, String name) {
        mDao.insert(mno, name);
        try {
            logService.saveRequired(name + " 가입", true);
        } catch (RuntimeException e) {
            log.debug("안쪽 예외를 잡았다! 괜찮을까? → {}", e.getMessage());
        }
    }

    // 안쪽 REQUIRES_NEW 가 실패 → 안쪽(로그)만 rollback, 바깥(회원)은 commit
    @Transactional
    public void joinWithRequiresNew(int mno, String name) {
        mDao.insert(mno, name);
        try {
            logService.saveRequiresNew(name + " 가입", true);
        } catch (RuntimeException e) {
            log.debug("안쪽 예외를 잡았다 → {}", e.getMessage());
        }
    }

    // 안쪽 REQUIRES_NEW 는 성공(이미 commit) 후 바깥이 실패 → 회원은 rollback, 로그는 남음
    // 활용 예: "가입 시도" 기록은 가입이 실패해도 꼭 남기고 싶을 때
    @Transactional
    public void joinFailAfterLog(int mno, String name) {
        logService.saveRequiresNew(name + " 가입 시도", false);
        mDao.insert(mno, name);
        throw new IllegalStateException("가입 처리 중 오류");
    }
}
