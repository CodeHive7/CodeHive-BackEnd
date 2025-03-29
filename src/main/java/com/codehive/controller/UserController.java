package com.codehive.controller;

import com.codehive.dto.UserDto;
import com.codehive.entity.User;
import com.codehive.mapper.UserMapper;
import com.codehive.repository.UserRepository;
import com.codehive.service.UserService;
import com.codehive.vm.UserVM;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        UserDto userDto = userService.getUserProfile(username);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserVM userVM) {
        String username = userDetails.getUsername();
        UserDto userDto = userService.updateUserProfile(username, userVM);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<UserDto> getUserProfileByUsername(@PathVariable String username) {
        UserDto userDto = userService.getUserProfile(username);
        return ResponseEntity.ok(userDto);
    }

}
