package com.ssafy.fw_06_rest.controller;

import com.ssafy.fw_06_rest.dto.Member;
import com.ssafy.fw_06_rest.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// @RestController = @Controller + @ResponseBody
// → 모든 메서드의 반환값이 view 이름이 아니라 데이터(JSON)로 응답됨
@RestController
@RequestMapping("/api/v1/members")   // URL 은 자원(명사), 동작은 HTTP 메서드로
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Member API", description = "회원 등록, 조회, 수정, 삭제")
public class MemberRestController {

    private final MemberService mService;

    // 목록 조회: GET /api/v1/members
    @GetMapping
    @Operation(summary = "회원 목록 조회", description = "등록된 모든 회원을 반환한다.")
    public List<Member> getMembers() {
        return mService.getMembers();                 // List → JSON 배열
    }

    // 하나 조회: GET /api/v1/members/1
    @GetMapping("/{mno}")
    @Operation(summary = "회원 한 명 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "없는 회원 번호"),
    })
    public Member getMember(@Parameter(description = "회원 번호", example = "1") @PathVariable int mno) {  // URL 의 {mno} → int 로 자동 형 변환
        return mService.getMember(mno);               // 객체 → JSON
    }

    // 등록: POST /api/v1/members  (body: JSON)
    @PostMapping
    @Operation(summary = "회원 등록", description = "회원 번호(mno)는 서버가 자동으로 부여한다.")
    @ApiResponse(responseCode = "201", description = "등록 성공")
    public ResponseEntity<Map<String, Object>> registMember(@RequestBody Member member) {
        log.debug("요청 body → 객체: {}", member);    // JSON → Member 로 변환된 결과
        mService.registMember(member);
        return ResponseEntity.status(HttpStatus.CREATED)   // 201 Created
                             .body(Map.of("member", member));
    }

    // 전체 수정: PUT /api/v1/members/1  (body: JSON)
    @PutMapping("/{mno}")
    @Operation(summary = "회원 정보 수정", description = "보낸 값으로 회원 정보를 전부 교체한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "없는 회원 번호"),
    })
    public Member updateMember(@Parameter(description = "회원 번호", example = "1") @PathVariable int mno, @RequestBody Member member) {
        return mService.updateMember(mno, member);
    }

    // 삭제: DELETE /api/v1/members/1
    @DeleteMapping("/{mno}")
    @Operation(summary = "회원 삭제")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공 (body 없음)"),
        @ApiResponse(responseCode = "404", description = "없는 회원 번호"),
    })
    public ResponseEntity<Void> deleteMember(@Parameter(description = "회원 번호", example = "1") @PathVariable int mno) {
        mService.deleteMember(mno);
        return ResponseEntity.noContent().build();         // 204 No Content (body 없음)
    }
}
