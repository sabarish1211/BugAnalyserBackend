package com.example.BugAnalyser.service;

import com.example.BugAnalyser.model.Team;
import com.example.BugAnalyser.model.User;
import com.example.BugAnalyser.repository.TeamRepository;
import com.example.BugAnalyser.repository.UserRepository;
import com.example.BugAnalyser.repository.UserProjectAssignmentRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserProjectAssignmentRepository userProjectAssignmentRepository;

    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() { 
        List<User> users = userRepository.findAll();
        // Add project count to each user
        users.forEach(user -> {
            int projectCount = userProjectAssignmentRepository.findProjectsByUserId(user.getUserId()).size();
            user.setProjectCount(projectCount);
        });
        return users;
    }

    public Optional<User> getUserById(Long id) { return userRepository.findById(id); }

    @Transactional
    public Optional<User> getUserByUsername(String username) { return userRepository.findByUsername(username); }

    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            user.setRole(updatedUser.getRole());
            user.setIsActive(updatedUser.getIsActive());
            user.setTeam(updatedUser.getTeam());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    public User assignTeam(Long userId, Long teamId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("Team not found"));

    user.setTeam(team);
    return userRepository.save(user);
}


    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
}
