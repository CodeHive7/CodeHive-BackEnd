package com.codehive.controller;

import com.codehive.dto.*;
import com.codehive.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    @PreAuthorize("hasAuthority('CREATE_PROJECT')")
    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(
            @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal User principal) {

        String username = principal.getUsername();
        ProjectResponseDto dto = projectService.createProject(request, username);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAuthority('READ_PROJECT')")
    @GetMapping("/my-projects")
    public ResponseEntity<List<ProjectResponseDto>> getMyProjects(@AuthenticationPrincipal User principal) {
        String username = principal.getUsername();
        List<ProjectResponseDto> projects = projectService.getMyProjects(username);
        return ResponseEntity.ok(projects);
    }

    @PreAuthorize("hasAuthority('READ_PROJECT')")
    @GetMapping("/applied-projects")
    public ResponseEntity<List<ProjectResponseDto>> getProjectsUserAppliedTo(@AuthenticationPrincipal User principal) {
        String username = principal.getUsername();
        List<ProjectResponseDto> projects = projectService.getProjectsUserAppliedTo(username);
        return ResponseEntity.ok(projects);
    }

    @PreAuthorize("hasAuthority('UPDATE_PROJECT')")
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @PathVariable Long projectId,
            @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal User principal
    ) {
        String username = principal.getUsername();
        ProjectResponseDto updated = projectService.updateProject(projectId, request, username);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAuthority('DELETE_PROJECT')")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable Long projectId , @AuthenticationPrincipal User principal) {
        String username = principal.getUsername();
        projectService.deleteProject(projectId, username);
        return ResponseEntity.ok("Project deleted successfully");
    }

//    @PreAuthorize("hasAuthority('APPLY_FOR_POSITION')")
    @PostMapping("/{projectId}/positions/{positionId}/apply")
    public ResponseEntity<String> applyForPosition(@PathVariable Long projectId,
                                                   @PathVariable Long positionId,
                                                   @RequestBody(required = false) ApplyForPositionRequest request,
                                                   @AuthenticationPrincipal User principal
    ) {
        if(request == null) {
            request = new ApplyForPositionRequest();
        }

        String username = principal.getUsername();
        projectService.applyForPosition(projectId, positionId, username, request);
        return ResponseEntity.ok("Applied successfully");

    }

    @PreAuthorize("hasAuthority('READ_PROJECT')")
    @GetMapping("/{projectId}/applicants")
    public ResponseEntity<List<ApplicantResponseDto>> getApplicantsForProject(@PathVariable Long projectId, @AuthenticationPrincipal User principal) {
        String username = principal.getUsername();
        List<ApplicantResponseDto> applicants = projectService.getApplicantsForProject(projectId, username);
        return ResponseEntity.ok(applicants);
    }

    @PreAuthorize("hasAuthority('UPDATE_APPLICATION_STATUS')")
    @PutMapping("/{projectId}/applications")
    public ResponseEntity<String> updateApplications(@PathVariable Long projectId,
                                                     @RequestBody ApplicationUpdateRequest request,
                                                     @AuthenticationPrincipal User principal) {
        String username = principal.getUsername();
        projectService.updateApplicationStatus(username, request);
        return ResponseEntity.ok("Application updated successfully");
    }

    @PreAuthorize("hasAuthority('READ_PROJECT')")
    @GetMapping("/accepted")
    public ResponseEntity<List<ProjectResponseDto>> getAcceptedProjects() {
        List<ProjectResponseDto> projects = projectService.getAcceptedProjects();
        return ResponseEntity.ok(projects);
    }

    @PreAuthorize("hasAuthority('READ_PROJECT')")
    @GetMapping("/rejected")
    public ResponseEntity<List<ProjectResponseDto>> getRejectedProjects() {
        List<ProjectResponseDto> projects = projectService.getRejectedProjects();
        return ResponseEntity.ok(projects);
    }

}
