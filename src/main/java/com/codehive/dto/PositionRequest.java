package com.codehive.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PositionRequest {
    private String roleName;
    private boolean paid;
    private int quantity;
}
