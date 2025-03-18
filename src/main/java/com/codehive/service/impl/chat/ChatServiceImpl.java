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
        if(!isCreator) {
            hasAcceptedPosition = applicationRepository.existsByApplicantAndPosition_ProjectAndStatus(
                    sender, project, ApplicationStatus.ACCEPTED);
        }
        // only allow sending messages if user is creator or has accepted position
        if(!isCreator && !hasAcceptedPosition) {
            throw new RuntimeException("User is not authorized to send messages in this project");
        }
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setSender(sender);
        chatMessage.setProject(project);

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        return mapToDto(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDto> getProjectMessages(Long projectId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<ChatMessage> messages = chatMessageRepository.findByProjectIdOrderByTimestampAsc(projectId);
        return messages.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
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
