package com.codehive.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicantResponseDto {
    private String applicantName;
    private String applicantUsername;
    private String positionName;
    private String applicationStatus;
    private String projectName;
}
