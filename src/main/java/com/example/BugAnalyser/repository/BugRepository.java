package com.example.BugAnalyser.repository;

import com.example.BugAnalyser.model.Bug;
import com.example.BugAnalyser.model.Project;
import com.example.BugAnalyser.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BugRepository extends JpaRepository<Bug, Long> {
    List<Bug> findByProject(Project project);
    List<Bug> findByStatus(String status);
    List<Bug> findByResolverAndStatus(User resolver, String status);
    List<Bug> findByProjectAndStatus(Project project, String status);
    List<Bug> findByReporter(User reporter);

    @Query("SELECT b FROM Bug b WHERE b.project.team.teamId = :teamId")
    List<Bug> findByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT COUNT(b) FROM Bug b WHERE b.project.team.teamId = :teamId AND b.status = 'Open'")
    Long countOpenBugsByTeam(@Param("teamId") Long teamId);

    @Query("SELECT COUNT(b) FROM Bug b WHERE b.project.team.teamId = :teamId AND b.status = 'Resolved' AND b.resolvedDate >= :startOfWeek")
    Long countResolvedBugsThisWeek(@Param("teamId") Long teamId, @Param("startOfWeek") LocalDateTime startOfWeek);

    List<Bug> findByProject_Team_TeamId(Long teamId);
    
    // Analytics queries
    Long countByStatus(String status);
    Long countByPriority(String priority);
    
    @Query("SELECT COUNT(b) FROM Bug b WHERE b.project.team.teamId = :teamId")
    Long countByTeamId(@Param("teamId") Long teamId);
    
    @Query("SELECT COUNT(b) FROM Bug b WHERE b.project.team.teamId = :teamId AND b.status = :status")
    Long countByTeamIdAndStatus(@Param("teamId") Long teamId, @Param("status") String status);
    
    @Query("SELECT COUNT(b) FROM Bug b WHERE b.resolver.userId = :userId")
    Long countByResolverUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(b) FROM Bug b WHERE b.resolver.userId = :userId OR b.reporter.userId = :userId")
    Long countAssignedToUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(b) FROM Bug b WHERE (b.resolver.userId = :userId OR b.reporter.userId = :userId) AND b.status = 'In Progress'")
    Long countInProgressByUser(@Param("userId") Long userId);
    
    List<Bug> findByStatusAndResolvedDateIsNotNull(String status);
    
    @Query("SELECT b FROM Bug b WHERE b.project.team.teamId = :teamId AND b.status = 'Resolved' AND b.resolvedDate IS NOT NULL")
    List<Bug> findResolvedBugsByTeam(@Param("teamId") Long teamId);
    
    List<Bug> findByResolverUserIdAndResolvedDateIsNotNull(Long userId);
}
