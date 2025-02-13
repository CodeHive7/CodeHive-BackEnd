package com.codehive.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserDto {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String location;
    private String phoneNumber;
    private Set<String> roles ;
    private String status;
}
