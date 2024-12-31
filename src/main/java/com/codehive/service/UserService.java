package com.codehive.service;


import com.codehive.dto.UserDto;
import com.codehive.vm.UserVM;

public interface UserService {

    UserDto updateUserProfile(String username , UserVM vm);
    UserDto getUserProfile(String username);
}
