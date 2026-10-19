package com.ssafy.fw_06_rest.tx;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class TxAddressDao {

    private final JdbcTemplate jdbc;

    public int insert(int mno, String title) {
        return jdbc.update("INSERT INTO tx_address (mno, title) VALUES (?, ?)", mno, title);
    }

    public int deleteByMno(int mno) {
        return jdbc.update("DELETE FROM tx_address WHERE mno = ?", mno);
    }

    public int countByMno(int mno) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM tx_address WHERE mno = ?", Integer.class, mno);
    }

    public List<Map<String, Object>> selectAll() {
        return jdbc.queryForList("SELECT * FROM tx_address ORDER BY ano");
    }
}
