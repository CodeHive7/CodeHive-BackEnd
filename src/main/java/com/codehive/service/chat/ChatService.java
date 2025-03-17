package com.codehive.service.chat;

import com.codehive.dto.chat.ChatMessageDto;

import java.util.List;

public interface ChatService {
    ChatMessageDto sendMessage(Long projectId, String username, String content);
    List<ChatMessageDto> getProjectMessages(Long projectId, String username);
}
