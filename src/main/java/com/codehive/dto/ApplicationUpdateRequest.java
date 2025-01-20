package com.codehive.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApplicationUpdateRequest {
    private List<Long> applicationIds;
    private boolean accept;
    private String feedback;
}
