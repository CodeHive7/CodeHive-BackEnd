package com.codehive.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    private String content;
    private Long senderId;
    private String senderUsername;
    private Long projectId;
    private LocalDateTime timestamp;
}
