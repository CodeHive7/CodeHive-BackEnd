package com.codehive.controller;

import com.codehive.dto.CreateUserRequest;
import com.codehive.dto.PermissionRequest;
import com.codehive.dto.RoleRequest;
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

    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<String> assignRolesToUser(@PathVariable Long userId, @RequestBody RoleRequest roleRequest) {
        adminService.assignRolesToUser(userId, roleRequest.getRoleNames());
        return ResponseEntity.ok("Roles assigned successfully");
    }

    @DeleteMapping("/users/{userId}/roles")
    public ResponseEntity<String> removeRolesFromUser(@PathVariable Long userId, @RequestBody RoleRequest roleRequest) {
        adminService.removeRolesFromUser(userId, roleRequest.getRoleNames());
        return ResponseEntity.ok("Roles removed successfully");
    }

    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<String> assignPermissions(@PathVariable Long roleId, @RequestBody PermissionRequest request){
        adminService.assignPermissionsToRole(roleId, request.getPermissionNames());
        return ResponseEntity.ok("Permissions assigned successfully");
    }

    @DeleteMapping("/roles/{roleId}/permissions")
    public ResponseEntity<String> removePermissions(@PathVariable Long roleId, @RequestBody PermissionRequest request){
        adminService.removePermissionsFromRole(roleId, request.getPermissionNames());
        return ResponseEntity.ok("Permissions removed successfully");
    }
}
