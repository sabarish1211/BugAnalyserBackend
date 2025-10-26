package com.example.BugAnalyser.service;

import com.example.BugAnalyser.dto.ProjectDTO;
import com.example.BugAnalyser.model.Project;
import com.example.BugAnalyser.model.Team;
import com.example.BugAnalyser.repository.ProjectRepository;
import com.example.BugAnalyser.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;

    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectName(projectDTO.projectName());
        project.setProjectCode(projectDTO.projectCode());
        project.setStatus(projectDTO.status());

        if (projectDTO.teamId() != null) {
            Team team = teamRepository.findById(projectDTO.teamId())
                    .orElseThrow(() -> new RuntimeException("Team not found"));
            project.setTeam(team);
        }

        Project saved = projectRepository.save(project);
        return mapToDTO(saved);
    }

    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public Optional<ProjectDTO> getProjectById(Long id) {
        return projectRepository.findById(id).map(this::mapToDTO);
    }

    public Optional<Project> getProjectEntityById(Long id) {
        return projectRepository.findById(id);
    }

    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        return projectRepository.findById(id).map(existing -> {
            existing.setProjectName(projectDTO.projectName());
            existing.setProjectCode(projectDTO.projectCode());
            existing.setStatus(projectDTO.status());

            if (projectDTO.teamId() != null) {
                Team team = teamRepository.findById(projectDTO.teamId())
                        .orElseThrow(() -> new RuntimeException("Team not found"));
                existing.setTeam(team);
            }

            Project saved = projectRepository.save(existing);
            return mapToDTO(saved);
        }).orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    private ProjectDTO mapToDTO(Project project) {
        return new ProjectDTO(
                project.getProjectId(),
                project.getProjectName(),
                project.getProjectCode(),
                project.getStatus(),
                project.getTeam() != null ? project.getTeam().getTeamId() : null,
                project.getTeam() != null ? project.getTeam().getTeamName() : null
        );
    }
}
