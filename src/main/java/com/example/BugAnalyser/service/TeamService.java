package com.example.BugAnalyser.service;

import com.example.BugAnalyser.model.Team;
import com.example.BugAnalyser.repository.TeamRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }

    public Team updateTeam(Long id, Team updatedTeam) {
        return teamRepository.findById(id).map(team -> {
            team.setTeamName(updatedTeam.getTeamName());
            return teamRepository.save(team);
        }).orElseThrow(() -> new RuntimeException("Team not found with ID: " + id));
    }

    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new RuntimeException("Team not found with ID: " + id);
        }
        teamRepository.deleteById(id);
    }
}
