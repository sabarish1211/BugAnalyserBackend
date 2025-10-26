package com.example.BugAnalyser.service;

import com.example.BugAnalyser.model.Project;
import com.example.BugAnalyser.model.User;
import com.example.BugAnalyser.model.UserProjectAssignment;
import com.example.BugAnalyser.repository.UserProjectAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserProjectAssignmentService {

    @Autowired
    private UserProjectAssignmentRepository assignmentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    public UserProjectAssignment assignUserToProject(Long userId, Long projectId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Project project = projectService.getProjectEntityById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Check if assignment already exists
        Optional<UserProjectAssignment> existing = assignmentRepository.findByUserAndProject(user, project);
        if (existing.isPresent()) {
            return existing.get();
        }

        UserProjectAssignment assignment = new UserProjectAssignment(user, project);
        return assignmentRepository.save(assignment);
    }

    public void removeUserFromProject(Long userId, Long projectId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Project project = projectService.getProjectEntityById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Optional<UserProjectAssignment> assignment = assignmentRepository.findByUserAndProject(user, project);
        assignment.ifPresent(assignmentRepository::delete);
    }

    public List<Project> getProjectsForUser(Long userId) {
        return assignmentRepository.findProjectsByUserId(userId);
    }

    public List<User> getUsersForProject(Long projectId) {
        return assignmentRepository.findUsersByProjectId(projectId);
    }

    public List<UserProjectAssignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }
}