package com.codehive.controller;

import com.codehive.dto.CreateProjectRequest;
import com.codehive.dto.ProjectResponseDto;
import com.codehive.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(
            @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal User principal) {

        String username = principal.getUsername();
        ProjectResponseDto dto = projectService.createProject(request, username);
        return ResponseEntity.ok(dto);
    }
}
