package edu.espe.springlab.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AppInfoController {

    @Value("${app.version:unknown}")
    private String appVersion;

    @GetMapping("/version")
    public ResponseEntity<Map<String, Object>> getVersion() {
        Map<String, Object> versionInfo = new HashMap<>();
        versionInfo.put("version", appVersion);
        versionInfo.put("timestamp", LocalDateTime.now());
        versionInfo.put("application", "Spring Lab");
        return ResponseEntity.ok(versionInfo);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("version", appVersion);
        healthInfo.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(healthInfo);
    }
}
