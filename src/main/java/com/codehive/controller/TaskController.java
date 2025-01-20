package com.codehive.controller;

import com.codehive.dto.CreateTaskRequest;
import com.codehive.dto.TaskDto;
import com.codehive.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PreAuthorize("hasAuthority('CREATE_TASK')")
    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<TaskDto> createTask(
            @PathVariable Long projectId,
            @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal User principal) {
        String username = principal.getUsername();
        TaskDto dto = taskService.createTask(projectId, request, username);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAuthority('READ_TASK')")
    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<TaskDto>> getProjectTasks(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User principal) {
        String username = principal.getUsername();
        List<TaskDto> tasks = taskService.getProjectTasks(projectId, username);
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("hasAuthority('UPDATE_TASK_STATUS')")
    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<TaskDto> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam String status,
            @AuthenticationPrincipal User principal) {
        String username = principal.getUsername();
        TaskDto dto = taskService.updateTaskStatus(taskId, username, status);
        return ResponseEntity.ok(dto);
    }
}
