package com.ssafy.web.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class UserDto {
    private String id;
    private String name;
    private String password;
    private String role;   // "USER" or "ADMIN"

    public boolean isAdmin() {
        // 상수를 앞에 두면 role이 null이어도 NPE 없이 false
        return "ADMIN".equals(this.role);
    }
}
