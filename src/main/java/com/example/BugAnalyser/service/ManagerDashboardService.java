package com.example.BugAnalyser.service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.BugAnalyser.model.Bug;
import com.example.BugAnalyser.model.Project;
import com.example.BugAnalyser.model.Team;
import com.example.BugAnalyser.model.User;
import com.example.BugAnalyser.repository.BugRepository;
import com.example.BugAnalyser.repository.ProjectRepository;
import com.example.BugAnalyser.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagerDashboardService {

    private final BugRepository bugRepo;
    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;

    public Map<String, Object> getDashboardStats(String Username) {
        User manager = userRepo.findByUsername(Username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        Team team = manager.getTeam(); // manager's assigned team
        if (team == null) throw new RuntimeException("Manager has no assigned team");

        Long teamMembers = (long) userRepo.findByTeam_TeamId(team.getTeamId()).size();
        Long activeProjects = (long) projectRepo.findByTeam_TeamId(team.getTeamId())
                .stream().filter(p -> "Active".equalsIgnoreCase(p.getStatus())).count();
        Long openBugs = bugRepo.countOpenBugsByTeam(team.getTeamId());

        LocalDateTime startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        Long resolvedThisWeek = bugRepo.countResolvedBugsThisWeek(team.getTeamId(), startOfWeek);

        Map<String, Object> stats = new HashMap<>();
        stats.put("teamMembers", teamMembers);
        stats.put("activeProjects", activeProjects);
        stats.put("openBugs", openBugs);
        stats.put("resolvedThisWeek", resolvedThisWeek);

        return stats;
    }

    public List<Project> getTeamProjects(User manager) {
        Team team = manager.getTeam();
        return projectRepo.findByTeam_TeamId(team.getTeamId());
    }

    public List<Bug> getTeamBugs(User manager) {
        Team team = manager.getTeam();
        return bugRepo.findByProject_Team_TeamId(team.getTeamId());
    }
}
