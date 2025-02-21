package com.codehive.controller;

import com.codehive.dto.*;
import com.codehive.repository.RoleRepository;
import com.codehive.repository.UserRepository;
import com.codehive.service.AdminService;
import com.codehive.service.ProjectService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestScope;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProjectService projectService;

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')  and hasAuthority('READ_USER')")
    @GetMapping("users")
    public ResponseEntity<List<UserDto>> listAllUsers() {
        List<UserDto> allUsers = adminService.findAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN') and hasAuthority('CREATE_USER')")
    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest request) {
        UserDto savedUser = adminService.createUser(request);
        return ResponseEntity.ok(savedUser);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN') and hasAuthority('BLOCK_USER')")
    @PostMapping("/users/{userId}/block")
    public ResponseEntity<String> blockUser(@PathVariable Long userId) {
        adminService.blockUser(userId);
        return ResponseEntity.ok("User blocked successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN') and hasAuthority('UNBLOCK_USER')")
    @PostMapping("/users/{userId}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable Long userId) {
        adminService.unblockUser(userId);
        return ResponseEntity.ok("User unblocked successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN') and hasAuthority('ASSIGN_ROLE')")
    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<String> assignRolesToUser(@PathVariable Long userId, @RequestBody RoleRequest roleRequest) {
        adminService.assignRolesToUser(userId, roleRequest.getRoleNames());
        return ResponseEntity.ok("Roles assigned successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN') and hasAuthority('REMOVE_ROLE')")
    @DeleteMapping("/users/{userId}/roles")
    public ResponseEntity<String> removeRolesFromUser(@PathVariable Long userId, @RequestBody RoleRequest roleRequest) {
        adminService.removeRolesFromUser(userId, roleRequest.getRoleNames());
        return ResponseEntity.ok("Roles removed successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN') and hasAuthority('CREATE_ROLE')")
    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<String> assignPermissions(@PathVariable Long roleId, @RequestBody PermissionRequest request){
        adminService.assignPermissionsToRole(roleId, request.getPermissionNames());
        return ResponseEntity.ok("Permissions assigned successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN') and hasAuthority('REMOVE_PERMISSION')")
    @DeleteMapping("/roles/{roleId}/permissions")
    public ResponseEntity<String> removePermissions(@PathVariable Long roleId, @RequestBody PermissionRequest request){
        adminService.removePermissionsFromRole(roleId, request.getPermissionNames());
        return ResponseEntity.ok("Permissions removed successfully");
    }

    @PreAuthorize("hasAuthority('CREATE_CATEGORY')")
    @PostMapping("/categories")
    public ResponseEntity<String> createCategory(@RequestBody CategoryRequest categoryRequest) {
        adminService.createCategory(categoryRequest.getName());
        return ResponseEntity.ok("Category created successfully");
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryRequest request) {
        adminService.updateCategory(categoryId, request.getName());
        return ResponseEntity.ok("Category updated successfully");
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        try {
            adminService.deleteCategory(categoryId);
            return ResponseEntity.ok("Category deleted successfully");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> listCategories() {
        List<CategoryDto> categories = adminService.listCategories();
        return ResponseEntity.ok(categories);
    }

    @PreAuthorize("hasAuthority('ACCEPT_PROJECT')")
    @PostMapping("/projects/{projectId}/accept")
    public ResponseEntity<String> acceptProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User principal
            ) {
        String adminUsername = principal.getUsername();
        projectService.acceptProject(projectId, adminUsername);
        return ResponseEntity.ok("Project accepted successfully");
    }

    @PreAuthorize("hasAuthority('REJECT_PROJECT')")
    @PostMapping("/projects/{projectId}/reject")
    public ResponseEntity<String> rejectProject(
            @PathVariable Long projectId,
            @RequestBody(required = false) RejectProjectRequest request,
            @AuthenticationPrincipal User principal
    ) {
        String adminUsername = principal.getUsername();
        String feedback = request != null ? request.getFeedback() : null;
        projectService.rejectProject(projectId, adminUsername, feedback);
        return ResponseEntity.ok("Project rejected successfully");
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/roles")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(adminService.getAllRoles());
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/permissions")
    public ResponseEntity<List<String>> getAllPermissions() {
        return ResponseEntity.ok(adminService.getAllPermissions());
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/roles")
    public ResponseEntity<String> createRole(@RequestBody CreateRole request) {
        adminService.createRole(request.getName());
        return ResponseEntity.ok("Role created successfully");
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/roles/{roleId}")
    public ResponseEntity<String> updateRole(@PathVariable Long roleId, @RequestBody CreateRole request) {
        adminService.updateRole(roleId, request.getName());
        return ResponseEntity.ok("Role updated successfully");
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable Long roleId) {
        adminService.deleteRole(roleId);
        return ResponseEntity.ok("Role deleted successfully");
    }
    @PreAuthorize("hasAuthority('READ_PROJECT')")
    @GetMapping("/allProjects")
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {
        List<ProjectResponseDto> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }
}
