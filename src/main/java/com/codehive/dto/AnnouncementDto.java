package com.codehive.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnnouncementDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
}
