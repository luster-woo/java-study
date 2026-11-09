package com.ssafy.security.member;

/** 응답용 - password, refresh 같은 민감 정보는 내보내지 않음 */
public record MemberDto(String email, String name, String role) {

    public static MemberDto from(Member m) {
        return new MemberDto(m.getEmail(), m.getName(), m.getRole());
    }
}
