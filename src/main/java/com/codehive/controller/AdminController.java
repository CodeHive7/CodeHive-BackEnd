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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('READ_USER')")
    @GetMapping("users")
    public ResponseEntity<List<UserDto>> listAllUsers() {
        List<UserDto> allUsers = adminService.findAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_USER')")
    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest request) {
        UserDto savedUser = adminService.createUser(request);
        return ResponseEntity.ok(savedUser);
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('BLOCK_USER')")
    @PostMapping("/users/{userId}/block")
    public ResponseEntity<String> blockUser(@PathVariable Long userId) {
        adminService.blockUser(userId);
        return ResponseEntity.ok("User blocked successfully");
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('UNBLOCK_USER')")
    @PostMapping("/users/{userId}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable Long userId) {
        adminService.unblockUser(userId);
        return ResponseEntity.ok("User unblocked successfully");
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('ASSIGN_ROLE')")
    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<String> assignRolesToUser(@PathVariable Long userId, @RequestBody RoleRequest roleRequest) {
        adminService.assignRolesToUser(userId, roleRequest.getRoleNames());
        return ResponseEntity.ok("Roles assigned successfully");
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('REMOVE_ROLE')")
    @DeleteMapping("/users/{userId}/roles")
    public ResponseEntity<String> removeRolesFromUser(@PathVariable Long userId, @RequestBody RoleRequest roleRequest) {
        adminService.removeRolesFromUser(userId, roleRequest.getRoleNames());
        return ResponseEntity.ok("Roles removed successfully");
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_ROLE')")
    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<String> assignPermissions(@PathVariable Long roleId, @RequestBody PermissionRequest request){
        adminService.assignPermissionsToRole(roleId, request.getPermissionNames());
        return ResponseEntity.ok("Permissions assigned successfully");
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('REMOVE_PERMISSION')")
    @DeleteMapping("/roles/{roleId}/permissions")
    public ResponseEntity<String> removePermissions(@PathVariable Long roleId, @RequestBody PermissionRequest request){
        adminService.removePermissionsFromRole(roleId, request.getPermissionNames());
        return ResponseEntity.ok("Permissions removed successfully");
    }
}
