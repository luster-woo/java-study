package com.ssafy.fw_06_rest.tx;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

// [05] Repository(DAO): Connection 을 직접 다루지 않음
// → JdbcTemplate 이 "지금 진행 중인 트랜잭션의 Connection" 을 알아서 가져다 씀
@Repository
@RequiredArgsConstructor
public class TxMemberDao {

    private final JdbcTemplate jdbc;

    public int insert(int mno, String name) {
        return jdbc.update("INSERT INTO tx_member (mno, name) VALUES (?, ?)", mno, name);
    }

    public int delete(int mno) {
        return jdbc.update("DELETE FROM tx_member WHERE mno = ?", mno);
    }

    public int count(int mno) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM tx_member WHERE mno = ?", Integer.class, mno);
    }

    public List<Map<String, Object>> selectAll() {
        return jdbc.queryForList("SELECT * FROM tx_member ORDER BY mno");
    }
}
