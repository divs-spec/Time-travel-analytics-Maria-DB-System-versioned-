package com.example.timetravel.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private JdbcTemplate jdbc;

    // Only allow specific audit tables for safety; we only created inventory_audit
    @GetMapping("/{table}/{id}")
    public List<Map<String,Object>> audit(@PathVariable String table, @PathVariable int id) {
        if (!"inventory_audit".equals(table)) {
            throw new IllegalArgumentException("table not allowed");
        }
        String sql = String.format("SELECT * FROM %s WHERE inventory_id = ? ORDER BY changed_at DESC", table);
        return jdbc.queryForList(sql, id);
    }
}
