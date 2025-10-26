package com.example.BugAnalyser.dto;

import com.example.BugAnalyser.model.Team;

public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private String role;
    private Boolean isActive;
    private Team team;

    public UserDTO(Long userId, String username, String email, String role, Boolean isActive, Team team) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.team = team;
    }
    // getters
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public Boolean getIsActive() { return isActive; }
    public Team getTeam() { return team; }
}
