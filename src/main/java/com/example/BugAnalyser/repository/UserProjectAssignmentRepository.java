package com.example.BugAnalyser.repository;

import com.example.BugAnalyser.model.Project;
import com.example.BugAnalyser.model.User;
import com.example.BugAnalyser.model.UserProjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProjectAssignmentRepository extends JpaRepository<UserProjectAssignment, Long> {
    List<UserProjectAssignment> findByUser(User user);
    List<UserProjectAssignment> findByProject(Project project);
    Optional<UserProjectAssignment> findByUserAndProject(User user, Project project);
    
    @Query("SELECT upa.project FROM UserProjectAssignment upa WHERE upa.user.userId = :userId")
    List<Project> findProjectsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT upa.user FROM UserProjectAssignment upa WHERE upa.project.projectId = :projectId")
    List<User> findUsersByProjectId(@Param("projectId") Long projectId);
}