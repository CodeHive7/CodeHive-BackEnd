package com.codehive.service;


import com.codehive.dto.UserDto;
import com.codehive.vm.UserVM;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserDto updateUserProfile(String username , UserVM vm);
    UserDto getUserProfile(String username);
}
