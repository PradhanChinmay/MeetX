package com.videoapp.meetx.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.videoapp.meetx.service.RoomService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        health.put("service", "WebRTC Signaling Server");
        health.put("version", "1.0.0");
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        stats.putAll(roomService.getServerStats());
        
        return ResponseEntity.ok(stats);
    }
}
