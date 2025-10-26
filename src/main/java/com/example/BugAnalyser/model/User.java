package com.example.BugAnalyser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String email;
    private String password;
    private String role; // Developer, Tester, Manager
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Bug> reportedBugs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserProjectAssignment> projectAssignments;

    @Transient
    private Integer projectCount = 0;

    // Custom constructor without ID
    public User(String username, String email, String role, Boolean isActive, Team team) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.team = team;
    }
}
