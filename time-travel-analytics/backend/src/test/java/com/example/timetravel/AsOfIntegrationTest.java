package com.example.timetravel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AsOfIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void testAsOfSnapshot() {
        ResponseEntity<List> resp = rest.getForEntity("/api/asof?table=inventory&timestamp=2025-09-01T12:00:00", List.class);
        assertThat(resp.getBody()).isNotNull();
    }

    @Test
    void testDiff() {
        ResponseEntity<Map> resp = rest.getForEntity("/api/diff?table=inventory&from=2025-09-01T12:00:00&to=2025-09-02T12:00:00", Map.class);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody()).containsKey("snapshotA");
        assertThat(resp.getBody()).containsKey("snapshotB");
    }
}

