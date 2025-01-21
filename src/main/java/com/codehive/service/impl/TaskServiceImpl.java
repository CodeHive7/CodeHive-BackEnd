package com.codehive.service.impl;

import com.codehive.Enum.ApplicationStatus;
import com.codehive.dto.CreateTaskRequest;
import com.codehive.dto.TaskDto;
import com.codehive.entity.Project;
import com.codehive.entity.Task;
import com.codehive.entity.User;
import com.codehive.mapper.TaskMapper;
import com.codehive.repository.ApplicationRepository;
import com.codehive.repository.ProjectRepository;
import com.codehive.repository.TaskRepository;
import com.codehive.repository.UserRepository;
import com.codehive.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ApplicationRepository applicationRepository;

    @Override
    public TaskDto createTask(Long projectId, CreateTaskRequest request, String ownerUsername) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if(!project.getCreator().getUsername().equals(ownerUsername)) {
            throw new RuntimeException("You are not authorized to create task for this project");
        }
        User assignedTo = userRepository.findById(request.getAssignedToUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isUserAccepted = applicationRepository.existsByApplicantAndPosition_ProjectAndStatus(
                assignedTo , project , ApplicationStatus.ACCEPTED );
        if(!isUserAccepted) {
            throw new RuntimeException("The user must have an accepted application for this project to be assigned a task");
        }

        Task task = new Task();
        task.setProject(project);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setAssignedTo(assignedTo);
        task.setStatus("TODO");
        taskRepository.save(task);

        return taskMapper.toDto(task);
    }

    @Override
    public List<TaskDto> getProjectTasks(Long projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(project.getCreator().getId().equals(user.getId())) {
            return taskRepository.findByProject(project)
                    .stream()
                    .map(taskMapper::toDto)
                    .collect(Collectors.toList());
        }
        boolean isPartOfTeam = applicationRepository.existsByApplicantAndPosition_ProjectAndStatus(
                user, project , ApplicationStatus.ACCEPTED);
        if(!isPartOfTeam) {
            throw new RuntimeException("You are not authorized to view tasks for this project");
        }
        return taskRepository.findByProject(project)
                .stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getUserTasks(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByAssignedTo(user)
                .stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDto updateTaskStatus(Long taskId, String username, String status) {
        List<String> validStatuses = List.of("TODO", "DOING", "DONE");
        if(!validStatuses.contains(status.toUpperCase())) {
            throw new IllegalArgumentException("Invalid status. Allowed values: TODO, DOING, DONE");
        }

        Task task = taskRepository.findByIdWithAssignedUser(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getAssignedTo().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to update the status of this task");
        }

        task.setStatus(status.toUpperCase());
        taskRepository.save(task);

        return taskMapper.toDto(task);
    }
}
