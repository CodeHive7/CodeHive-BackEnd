package com.codehive.controller;
//
//import com.codehive.dto.*;
//import com.codehive.service.AdminService;
//import com.codehive.service.ProjectService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.userdetails.User;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//class AdminControllerTest {
//
//    @Mock
//    private AdminService adminService;
//
//    @Mock
//    private ProjectService projectService;
//
//    @InjectMocks
//    private AdminController adminController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void listAllUsers_ShouldReturnUserList() {
//        List<UserDto> mockUsers = Arrays.asList(new UserDto(), new UserDto());
//        when(adminService.findAllUsers()).thenReturn(mockUsers);
//
//        ResponseEntity<List<UserDto>> response = adminController.listAllUsers();
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(2, response.getBody().size());
//    }
//
//    @Test
//    void createUser_ValidRequest_ShouldReturnCreatedUser() {
//        CreateUserRequest request = new CreateUserRequest();
//        UserDto mockUser = new UserDto();
//        when(adminService.createUser(any(CreateUserRequest.class))).thenReturn(mockUser);
//
//        ResponseEntity<UserDto> response = adminController.createUser(request);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//    }
//
//    @Test
//    void blockUser_ValidUserId_ShouldReturnSuccessMessage() {
//        doNothing().when(adminService).blockUser(anyLong());
//
//        ResponseEntity<String> response = adminController.blockUser(1L);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("User blocked successfully", response.getBody());
//    }
//
//    @Test
//    void unblockUser_ValidUserId_ShouldReturnSuccessMessage() {
//        doNothing().when(adminService).unblockUser(anyLong());
//
//        ResponseEntity<String> response = adminController.unblockUser(1L);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("User unblocked successfully", response.getBody());
//    }
//
//    @Test
//    void assignRolesToUser_ValidRequest_ShouldReturnSuccessMessage() {
//        RoleRequest roleRequest = new RoleRequest();
//        roleRequest.setRoleNames(List.of("ROLE_USER"));
//
//        doNothing().when(adminService).assignRolesToUser(anyLong(), anyList());
//
//        ResponseEntity<String> response = adminController.assignRolesToUser(1L, roleRequest);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Roles assigned successfully", response.getBody());
//    }
//
//    @Test
//    void removeRolesFromUser_ValidRequest_ShouldReturnSuccessMessage() {
//        RoleRequest roleRequest = new RoleRequest();
//        roleRequest.setRoleNames(List.of("ROLE_USER"));
//
//        doNothing().when(adminService).removeRolesFromUser(anyLong(), anyList());
//
//        ResponseEntity<String> response = adminController.removeRolesFromUser(1L, roleRequest);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Roles removed successfully", response.getBody());
//    }
//
//    @Test
//    void assignPermissions_ValidRequest_ShouldReturnSuccessMessage() {
//        PermissionRequest permissionRequest = new PermissionRequest();
//        permissionRequest.setPermissionNames(List.of("PERMISSION_1"));
//
//        doNothing().when(adminService).assignPermissionsToRole(anyLong(), anyList());
//
//        ResponseEntity<String> response = adminController.assignPermissions(1L, permissionRequest);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Permissions assigned successfully", response.getBody());
//    }
//
//    @Test
//    void removePermissions_ValidRequest_ShouldReturnSuccessMessage() {
//        PermissionRequest permissionRequest = new PermissionRequest();
//        permissionRequest.setPermissionNames(List.of("PERMISSION_1"));
//
//        doNothing().when(adminService).removePermissionsFromRole(anyLong(), anyList());
//
//        ResponseEntity<String> response = adminController.removePermissions(1L, permissionRequest);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Permissions removed successfully", response.getBody());
//    }
//
//    @Test
//    void createCategory_ValidRequest_ShouldReturnSuccessMessage() {
//        CategoryRequest categoryRequest = new CategoryRequest();
//        categoryRequest.setName("New Category");
//
//        doNothing().when(adminService).createCategory(anyString());
//
//        ResponseEntity<String> response = adminController.createCategory(categoryRequest);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Category created successfully", response.getBody());
//    }
//
//    @Test
//    void listCategories_ShouldReturnCategoryList() {
//        List<String> mockCategories = List.of("Category 1", "Category 2");
//        when(adminService.listCategories()).thenReturn(mockCategories);
//
//        ResponseEntity<List<String>> response = adminController.listCategories();
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(2, response.getBody().size());
//    }
//
//    @Test
//    void acceptProject_ValidProjectId_ShouldReturnSuccessMessage() {
//        String adminUsername = "admin";
//
//        doNothing().when(projectService).acceptProject(anyLong(), eq(adminUsername));
//
//        User mockUser = mock(User.class);
//        when(mockUser.getUsername()).thenReturn(adminUsername);
//
//        ResponseEntity<String> response = adminController.acceptProject(1L, mockUser);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Project accepted successfully", response.getBody());
//    }
//
//    @Test
//    void rejectProject_ValidProjectId_ShouldReturnSuccessMessage() {
//        String adminUsername = "admin";
//        RejectProjectRequest request = new RejectProjectRequest();
//        request.setFeedback("Not a good fit");
//
//        doNothing().when(projectService).rejectProject(anyLong(), eq(adminUsername), anyString());
//
//        User mockUser = mock(User.class);
//        when(mockUser.getUsername()).thenReturn(adminUsername);
//
//        ResponseEntity<String> response = adminController.rejectProject(1L, request, mockUser);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Project rejected successfully", response.getBody());
//    }
//}
