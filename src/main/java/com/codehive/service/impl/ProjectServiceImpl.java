package com.codehive.service.impl;

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
}
