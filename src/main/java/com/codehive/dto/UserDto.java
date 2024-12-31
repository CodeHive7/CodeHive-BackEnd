package com.codehive.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String location;
    private String phoneNumber;
}
