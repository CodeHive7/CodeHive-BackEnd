package com.codehive.controller;

import com.codehive.dto.*;
import com.codehive.repository.RoleRepository;
import com.codehive.repository.UserRepository;
import com.codehive.service.AdminService;
import com.codehive.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listAllUsers_ShouldReturnUserList() {
        List<UserDto> mockUsers = Arrays.asList(new UserDto(), new UserDto());
        when(adminService.findAllUsers()).thenReturn(mockUsers);

        ResponseEntity<List<UserDto>> response = adminController.listAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUsers, response.getBody());
        verify(adminService).findAllUsers();
    }

    @Test
    void createUser_ValidRequest_ShouldReturnCreatedUser() {
        CreateUserRequest request = new CreateUserRequest();
        UserDto mockUser = new UserDto();
        when(adminService.createUser(any(CreateUserRequest.class))).thenReturn(mockUser);

        ResponseEntity<UserDto> response = adminController.createUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
        verify(adminService).createUser(request);
    }

    @Test
    void blockUser_ValidUserId_ShouldReturnSuccessMessage() {
        Long userId = 1L;
        doNothing().when(adminService).blockUser(userId);

        ResponseEntity<String> response = adminController.blockUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User blocked successfully", response.getBody());
        verify(adminService).blockUser(userId);
    }

    @Test
    void unblockUser_ValidUserId_ShouldReturnSuccessMessage() {
        Long userId = 1L;
        doNothing().when(adminService).unblockUser(userId);

        ResponseEntity<String> response = adminController.unblockUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User unblocked successfully", response.getBody());
        verify(adminService).unblockUser(userId);
    }

    @Test
    void assignRolesToUser_ValidRequest_ShouldReturnSuccessMessage() {
        Long userId = 1L;
        RoleRequest roleRequest = new RoleRequest();
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");
        roleRequest.setRoleNames(roles);

        doNothing().when(adminService).assignRolesToUser(userId, roles);

        ResponseEntity<String> response = adminController.assignRolesToUser(userId, roleRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Roles assigned successfully", response.getBody());
        verify(adminService).assignRolesToUser(userId, roles);
    }

    @Test
    void removeRolesFromUser_ValidRequest_ShouldReturnSuccessMessage() {
        Long userId = 1L;
        RoleRequest roleRequest = new RoleRequest();
        List<String> roles = List.of("ROLE_USER");
        roleRequest.setRoleNames(roles);

        doNothing().when(adminService).removeRolesFromUser(userId, roles);

        ResponseEntity<String> response = adminController.removeRolesFromUser(userId, roleRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Roles removed successfully", response.getBody());
        verify(adminService).removeRolesFromUser(userId, roles);
    }

    @Test
    void assignPermissions_ValidRequest_ShouldReturnSuccessMessage() {
        Long roleId = 1L;
        PermissionRequest request = new PermissionRequest();
        List<String> permissions = List.of("READ_USER", "CREATE_USER");
        request.setPermissionNames(permissions);

        doNothing().when(adminService).assignPermissionsToRole(roleId, permissions);

        ResponseEntity<String> response = adminController.assignPermissions(roleId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Permissions assigned successfully", response.getBody());
        verify(adminService).assignPermissionsToRole(roleId, permissions);
    }

    @Test
    void removePermissions_ValidRequest_ShouldReturnSuccessMessage() {
        Long roleId = 1L;
        PermissionRequest request = new PermissionRequest();
        List<String> permissions = List.of("READ_USER");
        request.setPermissionNames(permissions);

        doNothing().when(adminService).removePermissionsFromRole(roleId, permissions);

        ResponseEntity<String> response = adminController.removePermissions(roleId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Permissions removed successfully", response.getBody());
        verify(adminService).removePermissionsFromRole(roleId, permissions);
    }

    @Test
    void createCategory_ValidRequest_ShouldReturnSuccessMessage() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Test Category");

        doNothing().when(adminService).createCategory("Test Category");

        ResponseEntity<String> response = adminController.createCategory(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Category created successfully", response.getBody());
        verify(adminService).createCategory("Test Category");
    }

    @Test
    void updateCategory_ValidRequest_ShouldReturnSuccessMessage() {
        Long categoryId = 1L;
        CategoryRequest request = new CategoryRequest();
        request.setName("Updated Category");

        doNothing().when(adminService).updateCategory(categoryId, "Updated Category");

        ResponseEntity<String> response = adminController.updateCategory(categoryId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Category updated successfully", response.getBody());
        verify(adminService).updateCategory(categoryId, "Updated Category");
    }

    @Test
    void deleteCategory_Success_ShouldReturnSuccessMessage() {
        Long categoryId = 1L;
        doNothing().when(adminService).deleteCategory(categoryId);

        ResponseEntity<String> response = adminController.deleteCategory(categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Category deleted successfully", response.getBody());
        verify(adminService).deleteCategory(categoryId);
    }

    @Test
    void deleteCategory_Exception_ShouldReturnErrorMessage() {
        Long categoryId = 1L;
        String errorMessage = "Cannot delete category with associated projects";
        doThrow(new RuntimeException(errorMessage)).when(adminService).deleteCategory(categoryId);

        ResponseEntity<String> response = adminController.deleteCategory(categoryId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(adminService).deleteCategory(categoryId);
    }

    @Test
    void listCategories_ShouldReturnCategoryList() {
        List<CategoryDto> mockCategories = Arrays.asList(
                new CategoryDto(1L, "Category 1"),
                new CategoryDto(2L, "Category 2")
        );
        when(adminService.listCategories()).thenReturn(mockCategories);

        ResponseEntity<List<CategoryDto>> response = adminController.listCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCategories, response.getBody());
        verify(adminService).listCategories();
    }

    @Test
    void acceptProject_ValidProjectId_ShouldReturnSuccessMessage() {
        Long projectId = 1L;
        String adminUsername = "admin";

        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn(adminUsername);

        doNothing().when(projectService).acceptProject(projectId, adminUsername);

        ResponseEntity<String> response = adminController.acceptProject(projectId, mockUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Project accepted successfully", response.getBody());
        verify(projectService).acceptProject(projectId, adminUsername);
    }

    @Test
    void rejectProject_WithFeedback_ShouldReturnSuccessMessage() {
        Long projectId = 1L;
        String adminUsername = "admin";
        String feedback = "Not meeting requirements";

        RejectProjectRequest request = new RejectProjectRequest();
        request.setFeedback(feedback);

        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn(adminUsername);

        doNothing().when(projectService).rejectProject(projectId, adminUsername, feedback);

        ResponseEntity<String> response = adminController.rejectProject(projectId, request, mockUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Project rejected successfully", response.getBody());
        verify(projectService).rejectProject(projectId, adminUsername, feedback);
    }

    @Test
    void rejectProject_WithoutFeedback_ShouldReturnSuccessMessage() {
        Long projectId = 1L;
        String adminUsername = "admin";

        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn(adminUsername);

        doNothing().when(projectService).rejectProject(projectId, adminUsername, null);

        ResponseEntity<String> response = adminController.rejectProject(projectId, null, mockUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Project rejected successfully", response.getBody());
        verify(projectService).rejectProject(projectId, adminUsername, null);
    }

    @Test
    void getAllRoles_ShouldReturnRolesList() {
        List<RoleDto> mockRoles = Arrays.asList(
                new RoleDto(1L, "ROLE_USER", Arrays.asList("READ_USER", "WRITE_USER")),
                new RoleDto(2L, "ROLE_ADMIN", Arrays.asList("READ_USER", "WRITE_USER", "DELETE_USER")));
        when(adminService.getAllRoles()).thenReturn(mockRoles);

        ResponseEntity<List<RoleDto>> response = adminController.getAllRoles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRoles, response.getBody());
        verify(adminService).getAllRoles();
    }

    @Test
    void getAllPermissions_ShouldReturnPermissionsList() {
        List<String> mockPermissions = List.of("READ_USER", "CREATE_USER");
        when(adminService.getAllPermissions()).thenReturn(mockPermissions);

        ResponseEntity<List<String>> response = adminController.getAllPermissions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPermissions, response.getBody());
        verify(adminService).getAllPermissions();
    }

    @Test
    void createRole_ValidRequest_ShouldReturnSuccessMessage() {
        CreateRole request = new CreateRole();
        request.setName("ROLE_TEST");

        doNothing().when(adminService).createRole("ROLE_TEST");

        ResponseEntity<String> response = adminController.createRole(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Role created successfully", response.getBody());
        verify(adminService).createRole("ROLE_TEST");
    }

    @Test
    void updateRole_ValidRequest_ShouldReturnSuccessMessage() {
        Long roleId = 1L;
        CreateRole request = new CreateRole();
        request.setName("ROLE_UPDATED");

        doNothing().when(adminService).updateRole(roleId, "ROLE_UPDATED");

        ResponseEntity<String> response = adminController.updateRole(roleId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Role updated successfully", response.getBody());
        verify(adminService).updateRole(roleId, "ROLE_UPDATED");
    }

    @Test
    void deleteRole_ValidRoleId_ShouldReturnSuccessMessage() {
        Long roleId = 1L;

        doNothing().when(adminService).deleteRole(roleId);

        ResponseEntity<String> response = adminController.deleteRole(roleId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Role deleted successfully", response.getBody());
        verify(adminService).deleteRole(roleId);
    }

    @Test
    void getAllProjects_ShouldReturnProjectsList() {
        List<ProjectResponseDto> mockProjects = Arrays.asList(
                new ProjectResponseDto(),
                new ProjectResponseDto()
        );
        when(projectService.getAllProjects()).thenReturn(mockProjects);

        ResponseEntity<List<ProjectResponseDto>> response = adminController.getAllProjects();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProjects, response.getBody());
        verify(projectService).getAllProjects();
    }

    @Test
    void getDashboardStats_ShouldReturnStatsMap() {
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("userCount", 10);
        mockStats.put("projectCount", 25);

        when(adminService.getDashboardStats()).thenReturn(mockStats);

        ResponseEntity<Map<String, Object>> response = adminController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockStats, response.getBody());
        verify(adminService).getDashboardStats();
    }
}