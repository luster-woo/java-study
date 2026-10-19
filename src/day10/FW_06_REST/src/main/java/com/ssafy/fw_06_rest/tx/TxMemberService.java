package com.ssafy.fw_06_rest.tx;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

// [05] Service 는 트랜잭션의 시작점
// 요구사항: 회원을 지우려면 주소부터 지워야 함 (주소 삭제 → 회원 삭제를 "한 묶음" 으로)
// fail = true 이면 두 작업 "사이" 에서 일부러 예외를 냄 (교재의 1 / new Random().nextInt(2) 를 확실하게 바꾼 것)
@Slf4j
@Service
@RequiredArgsConstructor
public class TxMemberService {

    private final TxMemberDao mDao;
    private final TxAddressDao aDao;
    private final PlatformTransactionManager txManager;   // [06-7] 명시적 T.X 처리용

    // ① @Transactional 없음 → DAO 작업마다 각자 바로 저장(auto commit)
    //    fail = true: 주소만 지워지고 회원은 남음 (반만 성공)
    public void deleteWithoutTx(int mno, boolean fail) {
        aDao.deleteByMno(mno);
        if (fail) { int i = 1 / 0; }        // ArithmeticException (RuntimeException)
        mDao.delete(mno);
    }

    // ② @Transactional → 프록시(비서)가 앞뒤로 begin / commit / rollback 처리
    //    fail = true: RuntimeException → 주소 삭제까지 rollback → 둘 다 남아 있음
    @Transactional
    public void delete(int mno, boolean fail) {
        aDao.deleteByMno(mno);
        if (fail) { int i = 1 / 0; }
        mDao.delete(mno);
    }

    // ③ [06-2] Checked Exception 은 기본적으로 rollback 안 함 → 주소 삭제가 commit 됨!
    @Transactional
    public void deleteThrowChecked(int mno) throws BusinessException {
        aDao.deleteByMno(mno);
        throw new BusinessException("Checked 예외 발생");
    }

    // ④ [06-2] rollbackFor 로 Checked Exception 도 rollback 대상에 추가
    @Transactional(rollbackFor = BusinessException.class)
    public void deleteThrowCheckedWithRollbackFor(int mno) throws BusinessException {
        aDao.deleteByMno(mno);
        throw new BusinessException("Checked 예외 발생 (rollbackFor 지정)");
    }

    // ⑤ [06-6] 같은 클래스 안에서 호출 (self-invocation)
    //    this.delete(...) 는 프록시를 거치지 않음 → @Transactional 이 붙어 있어도 적용 안 됨
    //    결과는 ① 과 똑같이 "반만 성공"
    public void deleteBySelfInvocation(int mno) {
        this.delete(mno, true);
    }

    // ⑥ [06-7] 명시적 T.X 처리: 어노테이션 대신 PlatformTransactionManager 를 직접 사용
    public void deleteProgrammatic(int mno, boolean fail) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();   // T.X 속성 설정
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = txManager.getTransaction(def);              // T.X 시작
        try {
            aDao.deleteByMno(mno);
            if (fail) { int i = 1 / 0; }
            mDao.delete(mno);
            txManager.commit(status);                                           // 성공 → commit
        } catch (RuntimeException e) {
            txManager.rollback(status);                                         // 실패 → rollback
            throw e;   // 교재 코드는 예외를 삼키지만, 호출한 쪽도 실패를 알 수 있게 다시 던짐
        }
    }
}
