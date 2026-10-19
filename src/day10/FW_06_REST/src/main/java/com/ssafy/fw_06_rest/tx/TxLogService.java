package com.ssafy.fw_06_rest.tx;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// [06-3] 전파 속성 실습: 교재의 BeanA (안쪽에서 호출되는 쪽)
// 반드시 TxOuterService 와 "다른 빈" 이어야 함 → 같은 클래스면 프록시를 안 거쳐서 전파 속성 자체가 무시됨
@Service
@RequiredArgsConstructor
public class TxLogService {

    private final TxLogDao logDao;

    // REQUIRED(기본값): 바깥 트랜잭션이 있으면 "같은 배" 에 탑승
    @Transactional
    public void saveRequired(String msg, boolean fail) {
        logDao.insert(msg);
        if (fail) throw new IllegalStateException("로그 저장 실패 (REQUIRED)");
    }

    // REQUIRES_NEW: 바깥 트랜잭션은 잠깐 멈추고 "새 배" 를 띄움 → 서로 영향 없음
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRequiresNew(String msg, boolean fail) {
        logDao.insert(msg);
        if (fail) throw new IllegalStateException("로그 저장 실패 (REQUIRES_NEW)");
    }
}
