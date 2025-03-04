package com.codehive.service;

import com.codehive.dto.*;

import java.util.List;

public interface ProjectService {
    ProjectResponseDto createProject(CreateProjectRequest request, String creatorUsername);
    List<ProjectResponseDto> getMyProjects(String username);
    ProjectResponseDto updateProject(Long projectId, CreateProjectRequest request , String username);
    void deleteProject(Long projectId, String username);
    void applyForPosition(Long projectId, Long positionId, String username, ApplyForPositionRequest request);
    List<ProjectResponseDto> getProjectsUserAppliedTo(String username);
    List<ApplicantResponseDto> getApplicantsForUserProjects(String username);
    List<ApplicantResponseDto> getApplicantsForProject(Long projectId, String username);
    void updateApplicationStatus(String username , ApplicationUpdateRequest request);
    void acceptProject(Long projectId, String adminUsername);
    void rejectProject(Long projectId, String adminUsername, String feedback);
    List<ProjectResponseDto> getAcceptedProjects();
    List<ProjectResponseDto> getRejectedProjects();
    List<ProjectResponseDto> getPendingProjects();
    List<ProjectResponseDto> getAllProjects();
    ProjectResponseDto getProjectById(Long projectId);
    List<AcceptedApplicantDto> getAcceptedApplicants(Long projectId , String username);


}
