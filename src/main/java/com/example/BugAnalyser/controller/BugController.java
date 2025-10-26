package com.example.BugAnalyser.controller;

import com.example.BugAnalyser.dto.BugDTO;
import com.example.BugAnalyser.model.Bug;
import com.example.BugAnalyser.model.User;
import com.example.BugAnalyser.service.BugService;
import com.example.BugAnalyser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/bugs")
@CrossOrigin(origins = "http://localhost:5173")
public class BugController {

    @Autowired
    private BugService bugService;

    @Autowired
    private UserService userService;
    @GetMapping("/developer")
public ResponseEntity<List<Bug>> getBugsForDeveloper(@RequestParam String username) {
    return ResponseEntity.ok(
        bugService.getBugsForDeveloper(username)
    );
}

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Bug>> getBugsForUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(
            bugService.getBugsForDeveloper(user.getUsername())
        );
    }


    @GetMapping
    public ResponseEntity<List<Bug>> getAllBugs() {
        return ResponseEntity.ok(bugService.getAllBugs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bug> getBugById(@PathVariable Long id) {
        return bugService.getBugById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Bug> createBug(@RequestAttribute("username") String username,
                                         @RequestBody BugDTO bugDTO) {
        Long reporterId = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserId();
        bugDTO.setReporterId(reporterId);
        Bug saved = bugService.createBug(bugDTO);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bug> updateBugStatus(@RequestAttribute("username") String username,
                                               @PathVariable Long id,
                                               @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isEmpty()) return ResponseEntity.badRequest().build();

        Long resolverId = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserId();

        Bug updated = bugService.updateBugStatusAndResolver(id, status, resolverId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBug(@PathVariable Long id) {
        bugService.deleteBug(id);
        return ResponseEntity.noContent().build();
    }
}
