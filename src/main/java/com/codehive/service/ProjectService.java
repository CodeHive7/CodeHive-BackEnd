package com.codehive.service;

import com.codehive.dto.ApplicantResponseDto;
import com.codehive.dto.ApplyForPositionRequest;
import com.codehive.dto.CreateProjectRequest;
import com.codehive.dto.ProjectResponseDto;

import java.util.List;

public interface ProjectService {
    ProjectResponseDto createProject(CreateProjectRequest request, String creatorUsername);
    List<ProjectResponseDto> getMyProjects(String username);
    ProjectResponseDto updateProject(Long projectId, CreateProjectRequest request , String username);
    void deleteProject(Long projectId, String username);
    void applyForPosition(Long projectId, Long positionId, String username, ApplyForPositionRequest request);
    List<ProjectResponseDto> getProjectsUserAppliedTo(String username);
    List<ApplicantResponseDto> getApplicantsForProject(Long projectId, String username);

}
