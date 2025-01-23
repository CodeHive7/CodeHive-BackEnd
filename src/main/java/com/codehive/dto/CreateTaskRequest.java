package com.codehive.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTaskRequest {
    private String title;
    private String description;
    protected LocalDate dueDate;
    private Long assignedToUserId;
}
