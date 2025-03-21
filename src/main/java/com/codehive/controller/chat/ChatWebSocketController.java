package com.codehive.controller.chat;

import com.codehive.dto.chat.ChatMessageDto;
import com.codehive.dto.chat.ChatMessageRequest;
import com.codehive.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessageRequest request, Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("User {} sent message to project: {}", username, request.getProjectId());
            ChatMessageDto savedMessage = chatService.sendMessage(request.getProjectId(), username, request.getContent());
            messagingTemplate.convertAndSend("/topic/project/" + request.getProjectId(), savedMessage);
        } catch (Exception e) {
            log.error("Error in sendMessage:", e);
            throw e;
        }
    }

    @MessageMapping("/chat.fetchMessages")
    public void fetchMessages(ChatMessageRequest request, Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("Fetching messages for user {} in project {}", username, request.getProjectId());
            List<ChatMessageDto> messages = chatService.getProjectMessages(request.getProjectId(), username);
            messagingTemplate.convertAndSendToUser(username, "/queue/messages", messages);
        } catch (Exception e) {
            log.error("Error in fetchMessages:", e);
            messagingTemplate.convertAndSendToUser(authentication.getName(), "/queue/messages", List.of());
        }
    }

    @MessageMapping("/chat.join")
    public void joinChat(ChatMessageRequest request, Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("User {} joined the chat for project {}", username, request.getProjectId());
            ChatMessageDto joinMessage = new ChatMessageDto();
            joinMessage.setSenderUsername(username);
            joinMessage.setContent(username + " joined the conversation");
            joinMessage.setProjectId(request.getProjectId());
            messagingTemplate.convertAndSend("/topic/project/" + request.getProjectId(), joinMessage);
        } catch (Exception e) {
            log.error("Error in joinChat:", e);
            throw e;
        }
    }
}
