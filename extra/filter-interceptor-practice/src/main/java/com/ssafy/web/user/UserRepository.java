package com.ssafy.web.user;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/** 실습용 메모리 저장소 — 계정 2개 고정 */
@Repository
public class UserRepository {

    private final Map<String, UserDto> users = Map.of(
            "admin", new UserDto("admin", "관리자", "1234", "ADMIN"),
            "user",  new UserDto("user",  "일반사용자", "1234", "USER"));

    public UserDto findById(String id) {
        return users.get(id);
    }

    public List<UserDto> findAll() {
        return List.copyOf(users.values());
    }
}
