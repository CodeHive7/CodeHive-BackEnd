package com.codehive.controller.chat;

import com.codehive.Enum.ApplicationStatus;
import com.codehive.dto.chat.ChatMessageDto;
import com.codehive.entity.Project;
import com.codehive.repository.ApplicationRepository;
import com.codehive.repository.ProjectRepository;
import com.codehive.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ProjectRepository projectRepository;
    private final ApplicationRepository applicationRepository;

    @PostMapping("/{projectId}/messages")
    public ResponseEntity<ChatMessageDto> sendMessage(
            @PathVariable Long projectId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal User user) {
        String content = request.get("content");

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getCreator().getUsername().equals(user.getUsername())) {
            boolean isAccepted = applicationRepository.existsByApplicantAndPosition_ProjectAndStatus(
                    new com.codehive.entity.User() {{
                        setUsername(user.getUsername());
                    }},
                    project,
                    ApplicationStatus.ACCEPTED);
            if(!isAccepted) {
                throw new RuntimeException("You are not allowed to send message to this project");
            }
        }
        ChatMessageDto message = chatService.sendMessage(projectId, user.getUsername(), content);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{projectId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getMessages(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User user) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if(!project.getCreator().getUsername().equals(user.getUsername())) {
            boolean isAccepted = applicationRepository.existsByApplicantAndPosition_ProjectAndStatus(
                    new com.codehive.entity.User() {{
                        setUsername(user.getUsername());
                    }},
                    project,
                    ApplicationStatus.ACCEPTED);
            if(!isAccepted) {
                throw new RuntimeException("You are not allowed to view messages of this project");
            }
        }
        List<ChatMessageDto> messages = chatService.getProjectMessages(projectId, user.getUsername());
        return ResponseEntity.ok(messages);
    }
}
