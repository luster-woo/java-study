package com.ssafy.fw_06_rest.tx;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

// [05, 06] 브라우저(Swagger) 나 requests.http 로 트랜잭션 결과를 눈으로 확인하는 컨트롤러
// 사용법: POST /reset 으로 데이터 초기화 → 실험 API 호출 → 응답의 "after" 를 보고 무엇이 남았는지 확인
@RestController
@RequestMapping("/api/v1/tx")
@RequiredArgsConstructor
@Tag(name = "Transaction 실습", description = "05 Service Layer와 TX, 06 @Transactional")
public class TxDemoController {

    private final TxMemberService memberService;
    private final TxOuterService outerService;
    private final TxMemberDao mDao;
    private final TxAddressDao aDao;
    private final TxLogDao logDao;
    private final JdbcTemplate jdbc;

    public enum DeleteMode { NO_TX, TX, CHECKED, CHECKED_ROLLBACK_FOR, SELF_INVOCATION, PROGRAMMATIC }

    public enum JoinMode { REQUIRED, REQUIRES_NEW, OUTER_FAIL }

    @PostMapping("/reset")
    @Operation(summary = "데이터 초기화", description = "회원 1~3번과 각각의 주소, 빈 로그 테이블로 되돌린다.")
    @Transactional
    public Map<String, Object> reset() {
        jdbc.update("DELETE FROM tx_address");
        jdbc.update("DELETE FROM tx_log");
        jdbc.update("DELETE FROM tx_member");
        for (int mno = 1; mno <= 3; mno++) {
            mDao.insert(mno, "회원" + mno);
            aDao.insert(mno, "회원" + mno + "의 주소");
        }
        return status();
    }

    @GetMapping("/status")
    @Operation(summary = "현재 테이블 상태 조회")
    public Map<String, Object> status() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("members", mDao.selectAll());
        map.put("addresses", aDao.selectAll());
        map.put("logs", logDao.selectAll());
        return map;
    }

    @DeleteMapping("/members/{mno}")
    @Operation(summary = "[05] 회원 삭제 (주소 → 회원)", description = """
            mode
            - NO_TX: @Transactional 없음 → fail=true 면 주소만 지워짐
            - TX: @Transactional → fail=true 면 둘 다 rollback
            - CHECKED: Checked 예외 → rollback 안 됨, 주소 삭제가 commit
            - CHECKED_ROLLBACK_FOR: rollbackFor 지정 → rollback
            - SELF_INVOCATION: 같은 클래스 안에서 호출 → @Transactional 무시
            - PROGRAMMATIC: PlatformTransactionManager 직접 사용""")
    public Map<String, Object> deleteMember(
            @Parameter(example = "1") @PathVariable int mno,
            @RequestParam(defaultValue = "TX") DeleteMode mode,
            @Parameter(description = "true 면 주소 삭제 후 일부러 예외 (NO_TX, TX, PROGRAMMATIC 에서 사용)")
            @RequestParam(defaultValue = "true") boolean fail) {
        return run(() -> {
            switch (mode) {
                case NO_TX -> memberService.deleteWithoutTx(mno, fail);
                case TX -> memberService.delete(mno, fail);
                case CHECKED -> memberService.deleteThrowChecked(mno);
                case CHECKED_ROLLBACK_FOR -> memberService.deleteThrowCheckedWithRollbackFor(mno);
                case SELF_INVOCATION -> memberService.deleteBySelfInvocation(mno);
                case PROGRAMMATIC -> memberService.deleteProgrammatic(mno, fail);
            }
        });
    }

    @PostMapping("/join/{mno}")
    @Operation(summary = "[06] 전파 속성 실습 (회원 가입 + 로그)", description = """
            mode
            - REQUIRED: 안쪽 로그 저장 실패 → 예외를 잡아도 전체 rollback (UnexpectedRollbackException)
            - REQUIRES_NEW: 안쪽 로그 저장 실패 → 로그만 rollback, 회원은 저장
            - OUTER_FAIL: 로그는 먼저 저장(REQUIRES_NEW), 바깥이 실패 → 회원은 rollback, 로그는 남음""")
    public Map<String, Object> join(
            @Parameter(example = "10") @PathVariable int mno,
            @RequestParam(defaultValue = "REQUIRED") JoinMode mode) {
        return run(() -> {
            switch (mode) {
                case REQUIRED -> outerService.joinWithRequired(mno, "회원" + mno);
                case REQUIRES_NEW -> outerService.joinWithRequiresNew(mno, "회원" + mno);
                case OUTER_FAIL -> outerService.joinFailAfterLog(mno, "회원" + mno);
            }
        });
    }

    // 실험을 실행하고 "예외가 났는지" + "실행 후 테이블 상태" 를 같이 돌려줌
    private Map<String, Object> run(Action action) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            action.run();
            result.put("exception", "없음");
        } catch (Exception e) {
            result.put("exception", e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        result.put("after", status());
        return result;
    }

    @FunctionalInterface
    private interface Action {
        void run() throws Exception;   // Checked 예외(BusinessException)도 던질 수 있게
    }
}
