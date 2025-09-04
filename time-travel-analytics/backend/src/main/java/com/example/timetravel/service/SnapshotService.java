package com.example.timetravel.service;

import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.sql.Timestamp;
import java.util.stream.Collectors;

@Service
public class SnapshotService {
    @Autowired
    private JdbcTemplate jdbc;

    private final List<String> allowed = List.of("inventory","users","orders");

    private void validateTable(String table) {
        if (!allowed.contains(table)) {
            throw new IllegalArgumentException("table not allowed");
        }
    }

    public List<Map<String,Object>> snapshotAsOf(String table, String timestamp) {
        validateTable(table);
        String ts = timestamp.replace('T',' ');
        String sql = String.format("SELECT * FROM %s FOR SYSTEM_TIME AS OF TIMESTAMP '%s'", table, ts);
        List<Map<String,Object>> rows = jdbc.queryForList(sql);
        // Optionally mask PII older than cutoff (policy example: mask older than 90 days)
        if ("users".equals(table)) {
            // example: no automatic masking here; caller can request masking logic if required
        }
        return rows;
    }

    public Map<String,List<Map<String,Object>>> diff(String table, String from, String to) {
        validateTable(table);
        List<Map<String,Object>> a = snapshotAsOf(table, from);
        List<Map<String,Object>> b = snapshotAsOf(table, to);
        return Map.of("snapshotA", a, "snapshotB", b);
    }

    // Example masking method that the controller could call if needed
    public List<Map<String,Object>> applyMaskingIfOld(String table, List<Map<String,Object>> rows, String cutoffTs) {
        if (!"users".equals(table) || cutoffTs == null) return rows;
        Timestamp cutoff = Timestamp.valueOf(cutoffTs.replace('T',' '));
        return rows.stream().map(r -> {
            Object rs = r.get("row_start");
            if (rs instanceof Timestamp) {
                Timestamp start = (Timestamp) rs;
                if (start.before(cutoff)) {
                    r.put("email","***@***");
                    r.put("address","[REDACTED]");
                }
            }
            return r;
        }).collect(Collectors.toList());
    }
}
