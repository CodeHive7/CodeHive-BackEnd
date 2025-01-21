package com.codehive.controller;

import com.codehive.dto.CreateTaskRequest;
import com.codehive.dto.TaskDto;
import com.codehive.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PreAuthorize("hasAuthority('READ_TASK')")
    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskDto>> getUserTasks(@AuthenticationPrincipal User principal) {
        String username = principal.getUsername();
        List<TaskDto> tasks = taskService.getUserTasks(username);
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("hasAuthority('UPDATE_TASK_STATUS')")
    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskDto> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody Map<String,String> requestBody,
            @AuthenticationPrincipal User principal) {
        String username = principal.getUsername();
        String status = requestBody.get("status");
        TaskDto dto = taskService.updateTaskStatus(taskId, username, status);
        return ResponseEntity.ok(dto);
    }
}
