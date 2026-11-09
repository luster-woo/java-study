package com.ssafy.security.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원 정보
 * - refresh : 로그인 시 발급한 refresh token (재발급 요청 때 대조, logout 시 null)
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;      // 로그인 id (username)

    private String name;

    private String password;   // 암호화된 비밀번호

    private String role;       // USER, ADMIN (ROLE_ 접두사 없이 저장)

    @Column(length = 1000)
    private String refresh;    // refresh token

    public Member(String email, String name, String password, String role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }
}
