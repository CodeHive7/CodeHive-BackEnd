package com.codehive.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateUserRequest {

    private String fullName;
    private String username;
    private String email;
    private String password;
    private List<String> roles;
}
