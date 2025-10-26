package com.example.BugAnalyser.controller;

import com.example.BugAnalyser.model.Project;
import com.example.BugAnalyser.model.User;
import com.example.BugAnalyser.model.UserProjectAssignment;
import com.example.BugAnalyser.service.UserProjectAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "http://localhost:5173")
public class UserProjectAssignmentController {

    @Autowired
    private UserProjectAssignmentService assignmentService;

    @PostMapping("/assign")
    public ResponseEntity<UserProjectAssignment> assignUserToProject(@RequestBody Map<String, Long> request) {
        System.out.println("Assign request received: " + request);
        Long userId = request.get("userId");
        Long projectId = request.get("projectId");
        System.out.println("Assigning user " + userId + " to project " + projectId);
        UserProjectAssignment assignment = assignmentService.assignUserToProject(userId, projectId);
        return ResponseEntity.ok(assignment);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeUserFromProject(@RequestBody Map<String, Long> request) {
        System.out.println("Remove request received: " + request);
        Long userId = request.get("userId");
        Long projectId = request.get("projectId");
        System.out.println("Removing user " + userId + " from project " + projectId);
        assignmentService.removeUserFromProject(userId, projectId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/projects")
    public ResponseEntity<List<Project>> getProjectsForUser(@PathVariable Long userId) {
        List<Project> projects = assignmentService.getProjectsForUser(userId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/project/{projectId}/users")
    public ResponseEntity<List<User>> getUsersForProject(@PathVariable Long projectId) {
        List<User> users = assignmentService.getUsersForProject(projectId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserProjectAssignment>> getAllAssignments() {
        List<UserProjectAssignment> assignments = assignmentService.getAllAssignments();
        return ResponseEntity.ok(assignments);
    }
}