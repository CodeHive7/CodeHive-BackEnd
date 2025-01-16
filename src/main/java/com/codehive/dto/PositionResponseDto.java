package com.codehive.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PositionResponseDto {
    private Long id;
    private String roleName;
    private boolean paid;
    private int quantity;
}
