package com.codehive.service.impl;

import com.codehive.Enum.ProjectStage;
import com.codehive.dto.ApplyForPositionRequest;
import com.codehive.dto.CreateProjectRequest;
import com.codehive.dto.ProjectResponseDto;
import com.codehive.entity.*;
import com.codehive.mapper.ProjectMapper;
import com.codehive.mapper.ProjectPositionMapper;
import com.codehive.repository.*;
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
    private final ProjectPositionRepository projectPositionRepository;
    private final ApplicationRepository applicationRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProjectResponseDto createProject(CreateProjectRequest request, String creatorUsername) {
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = resolveCategory(request);
        ProjectStage stage = resolveStage(request);

        Project project = projectMapper.toEntity(request);
        project.setCreator(creator);
        project.setCategory(category);
        project.setStage(stage);
        project.setQuestion1(request.getQuestion1());
        project.setQuestion2(request.getQuestion2());


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
        Category category = resolveCategory(request);

        ProjectStage stage = resolveStage(request);

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setCategory(category);
        project.setStage(stage);
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

    private Category resolveCategory(CreateProjectRequest request) {
        if (request.getCustomCategory() != null && !request.getCustomCategory().isBlank()) {
            return categoryRepository.findByName(request.getCustomCategory())
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setName(request.getCustomCategory());
                        return categoryRepository.save(newCategory);
                    });
        } else if (request.getSelectedCategory() != null && !request.getSelectedCategory().isBlank()) {
            return categoryRepository.findByName(request.getSelectedCategory())
                    .orElseThrow(() -> new RuntimeException("Selected category does not exist"));
        } else {
            throw new RuntimeException("A category must be selected or provided");
        }
    }

    private ProjectStage resolveStage(CreateProjectRequest request) {
        if (request.getStage() == null || request.getStage().isBlank()) {
            throw new RuntimeException("Project stage must be selected");
        }
        try {
            return ProjectStage.valueOf(request.getStage().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid project stage. Accepted values: " + List.of(ProjectStage.values()));
        }
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

    @Override
    public void applyForPosition(Long projectId, Long positionId, String username, ApplyForPositionRequest request) {
        User applicant = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ProjectPosition position = projectPositionRepository.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        if(!position.getProject().getId().equals(projectId)) {
            throw new RuntimeException("Position does not belong to this project");
        }

        if(project.getCreator().getId().equals(applicant.getId())) {
            throw new RuntimeException("You cannot apply for your own project");
        }

        if(position.getQuantity() <= 0) {
            throw new RuntimeException("Position is already filled");
        }
        if(project.getQuestion1() != null && !project.getQuestion1().isBlank()) {
            if(request.getAnswer1() == null || request.getAnswer1().isBlank()) {
                throw new RuntimeException("You must provide an answer for question1");
            }
        }
        if(project.getQuestion2() != null && !project.getQuestion2().isBlank()) {
            if(request.getAnswer2() == null || request.getAnswer2().isBlank()) {
                throw new RuntimeException("You must provide an answer for question2");
            }
        }
        if(applicationRepository.existsByApplicantAndPosition(applicant, position)) {
            throw new RuntimeException("You have already applied for this position");
        }

        PositionApplication application = new PositionApplication();
        application.setApplicant(applicant);
        application.setPosition(position);
        application.setAnswer1(request.getAnswer1());
        application.setAnswer2(request.getAnswer2());

        position.setQuantity(position.getQuantity() - 1);

        applicationRepository.save(application);
        projectPositionRepository.save(position);

    }
}
