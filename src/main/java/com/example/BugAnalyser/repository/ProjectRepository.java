package com.example.BugAnalyser.repository;

import com.example.BugAnalyser.model.Project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
     List<Project> findByTeam_TeamId(Long teamId);
}
