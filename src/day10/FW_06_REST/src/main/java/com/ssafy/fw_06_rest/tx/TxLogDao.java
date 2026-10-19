package com.ssafy.fw_06_rest.tx;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class TxLogDao {

    private final JdbcTemplate jdbc;

    public int insert(String msg) {
        return jdbc.update("INSERT INTO tx_log (msg) VALUES (?)", msg);
    }

    public int count() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM tx_log", Integer.class);
    }

    public List<Map<String, Object>> selectAll() {
        return jdbc.queryForList("SELECT * FROM tx_log ORDER BY id");
    }
}
