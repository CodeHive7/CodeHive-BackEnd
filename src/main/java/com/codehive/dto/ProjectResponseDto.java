package com.codehive.dto;

import com.codehive.Enum.ProjectStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private String stage;
    private String category;
    private String websiteUrl;
    private String problemToFix;
    private String question1;
    private String question2;
    private ProjectStatus status;
    private List<PositionResponseDto> positions;
    private String applicationStatus;
    private String feedback;

}
