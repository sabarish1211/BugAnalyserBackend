package com.example.BugAnalyser.controller;

import com.example.BugAnalyser.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/admin")
    public ResponseEntity<Map<String, Object>> getAdminAnalytics() {
        return ResponseEntity.ok(analyticsService.getAdminAnalytics());
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<Map<String, Object>> getTeamAnalytics(@PathVariable Long teamId) {
        return ResponseEntity.ok(analyticsService.getTeamAnalytics(teamId));
    }

    @GetMapping("/developer/{userId}")
    public ResponseEntity<Map<String, Object>> getDeveloperAnalytics(@PathVariable Long userId) {
        return ResponseEntity.ok(analyticsService.getDeveloperAnalytics(userId));
    }
}