package com.example.BugAnalyser.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    private String projectName;
    private String projectCode;
    private String status; // Active, Completed, OnHold

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Bug> bugs = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserProjectAssignment> userAssignments = new ArrayList<>();

    public Project(String projectName, String projectCode, String status, Team team) {
        this.projectName = projectName;
        this.projectCode = projectCode;
        this.status = status;
        this.team = team;
    }

    public void addBug(Bug bug) {
        bugs.add(bug);
        bug.setProject(this);
    }

    public void removeBug(Bug bug) {
        bugs.remove(bug);
        bug.setProject(null);
    }
}
