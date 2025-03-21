package com.codehive.controller.chat;

import com.codehive.dto.chat.ChatMessageDto;
import com.codehive.dto.chat.ChatMessageRequest;
import com.codehive.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ChatService chatService;

    @GetMapping("/{projectId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getProjectMessages(
            @PathVariable Long projectId) {
        List<ChatMessageDto> messages = chatService.getProjectMessages(projectId,
                // Get username from security context
                SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{projectId}/messages")
    public ResponseEntity<ChatMessageDto> sendMessage(
            @PathVariable Long projectId,
            @RequestBody ChatMessageRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ChatMessageDto message = chatService.sendMessage(projectId, username, request.getContent());
        return ResponseEntity.ok(message);
    }
}