package com.codehive.service;

import com.codehive.dto.CreateTaskRequest;
import com.codehive.dto.TaskDto;

import java.util.List;

public interface TaskService {
    TaskDto createTask(Long projectId, CreateTaskRequest request , String ownerUsername);
    List<TaskDto> getProjectTasks(Long projectId, String username);
    List<TaskDto> getUserTasks(String username);
    TaskDto updateTaskStatus(Long taskId, String username , String status);
}
