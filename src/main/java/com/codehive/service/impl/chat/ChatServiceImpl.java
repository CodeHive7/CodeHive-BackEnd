package com.codehive.service.impl.chat;

import com.codehive.Enum.ApplicationStatus;
import com.codehive.dto.chat.ChatMessageDto;
import com.codehive.entity.Project;
import com.codehive.entity.User;
import com.codehive.entity.chat.ChatMessage;
import com.codehive.repository.ApplicationRepository;
import com.codehive.repository.ProjectRepository;
import com.codehive.repository.UserRepository;
import com.codehive.repository.chat.ChatMessageRepository;
import com.codehive.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    @Transactional
    public ChatMessageDto sendMessage(Long projectId, String username, String content) {
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Check if the user is the project creator
        boolean isCreator = project.getCreator().getId().equals(sender.getId());
        // Check if the user has an accepted application for this project
        boolean hasAcceptedPosition = false;
        if (!isCreator) {
            hasAcceptedPosition = applicationRepository.existsByApplicantAndPosition_ProjectAndStatus(
                    sender, project, ApplicationStatus.ACCEPTED);
        }

        System.out.println("DEBUG: In sendMessage - User " + username + " isCreator: " + isCreator);
        System.out.println("DEBUG: In sendMessage - User " + username + " hasAcceptedPosition: " + hasAcceptedPosition);

        if (!isCreator && !hasAcceptedPosition) {
            throw new RuntimeException("User is not authorized to send messages in this project");
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setSender(sender);
        chatMessage.setProject(project);
        chatMessage.setTimestamp(LocalDateTime.now());

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        System.out.println("DEBUG: Message saved with ID " + savedMessage.getId() +
                ", content: " + savedMessage.getContent() +
                ", timestamp: " + savedMessage.getTimestamp());

        return mapToDto(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDto> getProjectMessages(Long projectId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Check if the user is authorized to view messages
        boolean isCreator = project.getCreator().getId().equals(user.getId());
        boolean hasAcceptedPosition = false;
        if (!isCreator) {
            hasAcceptedPosition = applicationRepository.existsByApplicantAndPosition_ProjectAndStatus(
                    user, project, ApplicationStatus.ACCEPTED);
        }

        System.out.println("DEBUG: User " + username + " isCreator: " + isCreator);
        System.out.println("DEBUG: User " + username + " hasAcceptedPosition: " + hasAcceptedPosition);

        if (!isCreator && !hasAcceptedPosition) {
            System.out.println("DEBUG: User " + username + " is NOT authorized to view messages for project " + projectId);
            return List.of();
        }

        List<ChatMessage> messages = chatMessageRepository.findByProjectIdOrderByTimestampAsc(projectId);
        System.out.println("DEBUG: Found " + messages.size() + " chat messages for project " + projectId);
        for (ChatMessage msg : messages) {
            System.out.println("DEBUG: Message ID " + msg.getId() + ", content: " + msg.getContent() +
                    ", timestamp: " + msg.getTimestamp());
        }

        List<ChatMessageDto> dtos = messages.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        System.out.println("DEBUG: Converted messages to DTOs: " + dtos);
        return dtos;
    }

    @Override
    @Transactional
    public boolean addUserToProject(Long projectId, String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private ChatMessageDto mapToDto(ChatMessage chatMessage) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(chatMessage.getId());
        dto.setContent(chatMessage.getContent());
        dto.setSenderId(chatMessage.getSender().getId());
        dto.setSenderUsername(chatMessage.getSender().getUsername());
        dto.setProjectId(chatMessage.getProject().getId());
        dto.setTimestamp(chatMessage.getTimestamp());
        return dto;
    }
}
