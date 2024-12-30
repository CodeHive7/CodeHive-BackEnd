package com.codehive.service;

import com.codehive.dto.LoginRequest;
import com.codehive.dto.RegisterRequest;
import com.codehive.entity.User;

import java.util.Optional;

public interface AuthService {
    String register(RegisterRequest registerRequest);
    Optional<User> findByUsername(String username);
    String authenticate(LoginRequest loginRequest);
}
