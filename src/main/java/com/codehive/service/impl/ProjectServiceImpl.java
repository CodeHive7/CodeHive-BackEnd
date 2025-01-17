package com.codehive.service.impl;

import com.codehive.Enum.ProjectStage;
import com.codehive.dto.CreateProjectRequest;
import com.codehive.dto.ProjectResponseDto;
import com.codehive.entity.Project;
import com.codehive.entity.ProjectPosition;
import com.codehive.entity.User;
import com.codehive.mapper.ProjectMapper;
import com.codehive.mapper.ProjectPositionMapper;
import com.codehive.repository.ProjectRepository;
import com.codehive.repository.UserRepository;
import com.codehive.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final ProjectPositionMapper positionMapper;

    @Override
    public ProjectResponseDto createProject(CreateProjectRequest request, String creatorUsername) {
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectMapper.toEntity(request);
        project.setCreator(creator);

        if(request.getPositions() != null) {
            request.getPositions().forEach(posReq -> {
                ProjectPosition posEntity = positionMapper.toEntity(posReq);
                posEntity.setProject(project);
                project.getPositions().add(posEntity);
            });
        }
        Project saved = projectRepository.save(project);
        return projectMapper.toDto(saved);
    }

    @Override
    public List<ProjectResponseDto> getMyProjects(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Project> projects = projectRepository.findByCreatorWithPositions(user);

        return projects.stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    public ProjectResponseDto updateProject(Long projectId, CreateProjectRequest request, String username) {
        Project project = projectRepository.findByIdWithPositions(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if(!project.getCreator().getUsername().equals(username)) {
            throw new RuntimeException("You are not allowed to update this project");
        }
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStage(ProjectStage.valueOf(request.getStage()));
        project.setCategory(request.getCategory());
        project.setWebsiteUrl(request.getWebsiteUrl());
        project.setProblemToFix(request.getProblemToFix());

        project.getPositions().clear();
        if(request.getPositions() != null) {
            request.getPositions().forEach(posReq -> {
                ProjectPosition posEntity = positionMapper.toEntity(posReq);
                posEntity.setProject(project);
                project.getPositions().add(posEntity);
            });
        }
        Project saved = projectRepository.save(project);
        return projectMapper.toDto(saved);
    }

    @Override
    public void deleteProject(Long projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if(!project.getCreator().getUsername().equals(username)) {
            throw new RuntimeException("You are not allowed to delete this project");
        }
        projectRepository.delete(project);
    }
}
