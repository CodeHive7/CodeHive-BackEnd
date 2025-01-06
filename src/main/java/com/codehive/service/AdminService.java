package com.codehive.service;

import com.codehive.dto.CreateUserRequest;
import com.codehive.dto.UserDto;

import java.util.List;

public interface AdminService {
    List<UserDto> findAllUsers();
    UserDto createUser(CreateUserRequest request);
    void blockUser(Long userId);
    void unblockUser(Long userId);

}
