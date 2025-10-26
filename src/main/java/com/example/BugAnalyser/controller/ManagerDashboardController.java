package com.example.BugAnalyser.controller;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.BugAnalyser.model.Bug;
import com.example.BugAnalyser.model.Project;
import com.example.BugAnalyser.model.User;
import com.example.BugAnalyser.service.ManagerDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/manager/dashboard")
@RequiredArgsConstructor
public class ManagerDashboardController {

    private final ManagerDashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(@RequestAttribute("username") String Username) {
        Map<String, Object> stats = dashboardService.getDashboardStats(Username);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getProjects(@AuthenticationPrincipal User manager) {
        return ResponseEntity.ok(dashboardService.getTeamProjects(manager));
    }

    @GetMapping("/bugs")
    public ResponseEntity<List<Bug>> getBugs(@AuthenticationPrincipal User manager) {
        return ResponseEntity.ok(dashboardService.getTeamBugs(manager));
    }
}
