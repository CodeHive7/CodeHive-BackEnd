package com.codehive.dto;

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
    private List<PositionResponseDto> positions;
    private String applicationStatus;
    private String feedback;

}
