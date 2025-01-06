package com.codehive.controller;

import com.codehive.dto.CreateUserRequest;
import com.codehive.dto.UserDto;
import com.codehive.repository.RoleRepository;
import com.codehive.repository.UserRepository;
import com.codehive.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @GetMapping("users")
    public ResponseEntity<List<UserDto>> listAllUsers() {
        List<UserDto> allUsers = adminService.findAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest request) {
        UserDto savedUser = adminService.createUser(request);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/users/{userId}/block")
    public ResponseEntity<String> blockUser(@PathVariable Long userId) {
        adminService.blockUser(userId);
        return ResponseEntity.ok("User blocked successfully");
    }

    @PostMapping("/users/{userId}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable Long userId) {
        adminService.unblockUser(userId);
        return ResponseEntity.ok("User unblocked successfully");
    }


}
