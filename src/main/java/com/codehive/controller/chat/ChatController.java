package com.codehive.controller.chat;

import com.codehive.Enum.ApplicationStatus;
import com.codehive.dto.chat.ChatMessageDto;
import com.codehive.entity.Project;
import com.codehive.repository.ApplicationRepository;
import com.codehive.repository.ProjectRepository;
import com.codehive.repository.UserRepository;
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
    private final UserRepository userRepository;

    @PostMapping("/{projectId}/messages")
    public ResponseEntity<ChatMessageDto> sendMessage(
            @PathVariable Long projectId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal User userPrincipal) {
        String content = request.get("content");

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        com.codehive.entity.User sender = userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // If not the project creator , verify that the user is accepted in the project
        if (!project.getCreator().getUsername().equals(sender.getUsername())) {
            boolean isAccepted = applicationRepository.existsByApplicantAndPosition_ProjectAndStatus(
                    sender,
                    project,
                    ApplicationStatus.ACCEPTED);
            if(!isAccepted) {
                throw new RuntimeException("You are not allowed to send message to this project");
            }
        }
        ChatMessageDto message = chatService.sendMessage(projectId, sender.getUsername(), content);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{projectId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getMessages(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User userPrincipal) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        com.codehive.entity.User currentUser = userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!project.getCreator().getUsername().equals(currentUser.getUsername())) {
            boolean isAccepted = applicationRepository.existsByApplicantAndPosition_ProjectAndStatus(
                    currentUser,
                    project,
                    ApplicationStatus.ACCEPTED);
            if(!isAccepted) {
                throw new RuntimeException("You are not allowed to view messages of this project");
            }
        }
        List<ChatMessageDto> messages = chatService.getProjectMessages(projectId, userPrincipal.getUsername());
        return ResponseEntity.ok(messages);
    }
}
