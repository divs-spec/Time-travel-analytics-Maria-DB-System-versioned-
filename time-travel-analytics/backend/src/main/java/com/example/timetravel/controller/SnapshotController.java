package com.example.timetravel.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.timetravel.service.SnapshotService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SnapshotController {
    @Autowired
    private SnapshotService service;

    @GetMapping("/asof")
    public List<Map<String,Object>> asOf(@RequestParam String table,
                                         @RequestParam String timestamp,
                                         @RequestParam(required = false) String maskCutoff) {
        var rows = service.snapshotAsOf(table, timestamp);
        if (maskCutoff != null) {
            rows = service.applyMaskingIfOld(table, rows, maskCutoff);
        }
        return rows;
    }

    @GetMapping("/diff")
    public Map<String, List<Map<String,Object>>> diff(@RequestParam String table,
                                                      @RequestParam String from,
                                                      @RequestParam String to) {
        return service.diff(table, from, to);
    }
}
