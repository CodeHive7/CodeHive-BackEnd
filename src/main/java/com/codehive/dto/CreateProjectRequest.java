package com.codehive.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateProjectRequest {
    private String name;
    private String description;
    @NotNull(message = "Project stage is required")
    private String stage;
    private String selectedCategory;
    private String customCategory;
    private String websiteUrl;
    private String problemToFix;
    private String question1;
    private String question2;

    private List<PositionRequest> positions;
}
