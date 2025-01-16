package com.codehive.service;

import com.codehive.dto.CreateProjectRequest;
import com.codehive.dto.ProjectResponseDto;

public interface ProjectService {
    ProjectResponseDto createProject(CreateProjectRequest request, String creatorUsername);

}
