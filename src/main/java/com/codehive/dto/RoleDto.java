package com.codehive.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class RoleDto {
    private Long id;
    private String name;
    private List<String> permissions;
}
