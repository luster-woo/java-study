package com.ssafy.fw_06_rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// @RequestBody 로 JSON → 객체 변환하려면 기본 생성자 + setter(또는 필드 접근)가 필요
// 객체 → JSON 응답에는 getter 가 사용됨
@Data                 // getter, setter, toString, equals, hashCode
@NoArgsConstructor    // Jackson 이 객체를 만들 때 사용
@AllArgsConstructor
@Builder
@Schema(title = "Member(회원)", description = "회원 정보")
public class Member {

    @Schema(description = "회원 번호, 서버가 자동 부여", accessMode = AccessMode.READ_ONLY, example = "1")
    private Integer mno;

    @Schema(description = "이메일", requiredMode = RequiredMode.REQUIRED, example = "hong@ssafy.com")
    private String email;

    @Schema(description = "이름", requiredMode = RequiredMode.REQUIRED, example = "홍길동")
    private String name;

    @Schema(description = "비밀번호", requiredMode = RequiredMode.REQUIRED, example = "1234")
    private String password;
}
