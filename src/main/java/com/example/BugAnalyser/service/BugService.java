package com.example.BugAnalyser.service;

import com.example.BugAnalyser.dto.BugDTO;
import com.example.BugAnalyser.model.Bug;
import com.example.BugAnalyser.model.Project;
import com.example.BugAnalyser.model.User;
import com.example.BugAnalyser.repository.BugRepository;
import com.example.BugAnalyser.repository.ProjectRepository;
import com.example.BugAnalyser.repository.UserProjectAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BugService {

    @Autowired
    private BugRepository bugRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProjectAssignmentRepository userProjectAssignmentRepository;

    public List<Bug> getBugsByStatus(String status) {
        return bugRepository.findByStatus(status);
    }

    public List<Bug> getResolvedBugsByDeveloper(String username) {
        User developer = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bugRepository.findByResolverAndStatus(developer, "resolved");
    }

    public Bug createBug(BugDTO bugDTO) {
        Project project = projectRepository.findById(bugDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User reporter = userService.getUserById(bugDTO.getReporterId())
                .orElseThrow(() -> new RuntimeException("Reporter not found"));

        Bug bug = new Bug();
        bug.setTitle(bugDTO.getTitle());
        bug.setDescription(bugDTO.getDescription());
        bug.setPriority(bugDTO.getPriority() != null ? bugDTO.getPriority() : "Low");
        bug.setStatus("Open");
        bug.setProject(project);
        bug.setReporter(reporter);
        bug.setCreatedDate(LocalDate.now());

        return bugRepository.save(bug);
    }

    public List<Bug> getAllBugs() {
        return bugRepository.findAll();
    }

    public Optional<Bug> getBugById(Long id) {
        return bugRepository.findById(id);
    }

    public List<Bug> getBugsByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return bugRepository.findByProject(project);
    }

    public Bug updateBugStatusAndResolver(Long id, String status, Long resolverId) {
        Bug existing = bugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug not found"));

        existing.setStatus(status);

        if (resolverId != null) {
            User resolver = userService.getUserById(resolverId)
                    .orElseThrow(() -> new RuntimeException("Resolver not found"));
            existing.setResolver(resolver);
            existing.setResolvedDate(LocalDateTime.now());
        }

        return bugRepository.save(existing);
    }

    public List<Bug> getBugsByReporter(Long reporterId) {
        User reporter = userService.getUserById(reporterId)
                .orElseThrow(() -> new RuntimeException("Reporter not found"));
        return bugRepository.findByReporter(reporter);
    }

    public void deleteBug(Long id) {
        bugRepository.deleteById(id);
    }

    public List<Bug> getBugsByTeam(Long teamId) {
        return bugRepository.findByTeamId(teamId);
    }

    public List<Bug> getOpenBugsForProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return bugRepository.findByProjectAndStatus(project, "Open");
    }
    public List<Bug> getBugsForDeveloper(String username) {
    User dev = userService.getUserByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // Get all projects assigned to the user
    List<Project> assignedProjects = userProjectAssignmentRepository.findProjectsByUserId(dev.getUserId());
    
    // If no direct project assignments, fall back to team projects
    if (assignedProjects.isEmpty() && dev.getTeam() != null) {
        assignedProjects = dev.getTeam().getProjects();
    }

    // Collect all bugs from assigned projects
    List<Bug> bugs = assignedProjects.stream()
            .flatMap(project -> project.getBugs().stream())
            .collect(Collectors.toList());

    return bugs;
}
}
