package com.codehive.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateProjectRequest {
    private String name;
    private String description;
    private String stage;
    private String category;
    private String websiteUrl;
    private String problemToFix;

    private List<PositionRequest> positions;
}
