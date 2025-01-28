package com.codehive.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;
    private String assignedTo;
}
