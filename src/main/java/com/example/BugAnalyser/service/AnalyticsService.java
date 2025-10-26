package com.example.BugAnalyser.service;

import com.example.BugAnalyser.repository.BugRepository;
import com.example.BugAnalyser.repository.UserRepository;
import com.example.BugAnalyser.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AnalyticsService {

    @Autowired
    private BugRepository bugRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    public Map<String, Object> getAdminAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        long totalBugs = bugRepository.count();
        long resolvedBugs = bugRepository.countByStatus("Resolved");
        long openBugs = bugRepository.countByStatus("Open");
        long inProgressBugs = bugRepository.countByStatus("In Progress");
        
        analytics.put("totalBugs", totalBugs);
        analytics.put("resolvedBugs", resolvedBugs);
        analytics.put("openBugs", openBugs);
        analytics.put("inProgressBugs", inProgressBugs);
        
        // Priority distribution
        Map<String, Long> bugsByPriority = new HashMap<>();
        bugsByPriority.put("high", bugRepository.countByPriority("High"));
        bugsByPriority.put("medium", bugRepository.countByPriority("Medium"));
        bugsByPriority.put("low", bugRepository.countByPriority("Low"));
        analytics.put("bugsByPriority", bugsByPriority);
        
        // Team performance
        List<Map<String, Object>> bugsByTeam = new ArrayList<>();
        teamRepository.findAll().forEach(team -> {
            Map<String, Object> teamData = new HashMap<>();
            teamData.put("name", team.getTeamName());
            teamData.put("bugs", bugRepository.countByTeamId(team.getTeamId()));
            teamData.put("resolved", bugRepository.countByTeamIdAndStatus(team.getTeamId(), "Resolved"));
            bugsByTeam.add(teamData);
        });
        analytics.put("bugsByTeam", bugsByTeam);
        
        // Average resolution time
        analytics.put("avgResolutionTime", calculateAvgResolutionTime());
        
        return analytics;
    }

    public Map<String, Object> getTeamAnalytics(Long teamId) {
        Map<String, Object> analytics = new HashMap<>();
        
        long teamBugs = bugRepository.countByTeamId(teamId);
        long resolvedBugs = bugRepository.countByTeamIdAndStatus(teamId, "Resolved");
        long openBugs = bugRepository.countByTeamIdAndStatus(teamId, "Open");
        long inProgressBugs = bugRepository.countByTeamIdAndStatus(teamId, "In Progress");
        
        analytics.put("teamBugs", teamBugs);
        analytics.put("resolvedBugs", resolvedBugs);
        analytics.put("openBugs", openBugs);
        analytics.put("inProgressBugs", inProgressBugs);
        
        // Team members performance
        List<Map<String, Object>> teamMembers = new ArrayList<>();
        userRepository.findByTeamTeamId(teamId).forEach(user -> {
            Map<String, Object> memberData = new HashMap<>();
            memberData.put("name", user.getUsername());
            memberData.put("role", user.getRole());
            memberData.put("resolved", bugRepository.countByResolverUserId(user.getUserId()));
            memberData.put("assigned", bugRepository.countAssignedToUser(user.getUserId()));
            teamMembers.add(memberData);
        });
        analytics.put("teamMembers", teamMembers);
        
        analytics.put("avgResolutionTime", calculateTeamAvgResolutionTime(teamId));
        
        return analytics;
    }

    public Map<String, Object> getDeveloperAnalytics(Long userId) {
        Map<String, Object> analytics = new HashMap<>();
        
        long assignedBugs = bugRepository.countAssignedToUser(userId);
        long resolvedBugs = bugRepository.countByResolverUserId(userId);
        long inProgressBugs = bugRepository.countInProgressByUser(userId);
        
        analytics.put("assignedBugs", assignedBugs);
        analytics.put("resolvedBugs", resolvedBugs);
        analytics.put("inProgressBugs", inProgressBugs);
        analytics.put("avgResolutionTime", calculateUserAvgResolutionTime(userId));
        
        return analytics;
    }

    private double calculateAvgResolutionTime() {
        return bugRepository.findByStatusAndResolvedDateIsNotNull("Resolved")
                .stream()
                .mapToLong(bug -> ChronoUnit.DAYS.between(bug.getCreatedDate().atStartOfDay(), bug.getResolvedDate()))
                .average()
                .orElse(0.0);
    }

    private double calculateTeamAvgResolutionTime(Long teamId) {
        return bugRepository.findResolvedBugsByTeam(teamId)
                .stream()
                .mapToLong(bug -> ChronoUnit.DAYS.between(bug.getCreatedDate().atStartOfDay(), bug.getResolvedDate()))
                .average()
                .orElse(0.0);
    }

    private double calculateUserAvgResolutionTime(Long userId) {
        return bugRepository.findByResolverUserIdAndResolvedDateIsNotNull(userId)
                .stream()
                .mapToLong(bug -> ChronoUnit.DAYS.between(bug.getCreatedDate().atStartOfDay(), bug.getResolvedDate()))
                .average()
                .orElse(0.0);
    }
}