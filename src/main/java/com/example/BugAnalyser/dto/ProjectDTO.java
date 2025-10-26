package com.example.BugAnalyser.dto;

public record ProjectDTO(
        Long projectId,
        String projectName,
        String projectCode,
        String status,
        Long teamId,
        String teamName
) {}
