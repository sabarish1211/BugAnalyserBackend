package com.example.BugAnalyser.repository;

import com.example.BugAnalyser.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByTeam_TeamId(Long teamId);
    List<User> findByTeamTeamId(Long teamId);
    
}
