package com.codehive.service;

import com.codehive.dto.CreateUserRequest;
import com.codehive.dto.UserDto;

import java.util.List;
import java.util.Stack;

public interface AdminService {
    List<UserDto> findAllUsers();
    UserDto createUser(CreateUserRequest request);
    void blockUser(Long userId);
    void unblockUser(Long userId);
    void assignRolesToUser(Long userId, List<String> roleNames);
    void removeRolesFromUser(Long userId, List<String> roleNames);
    void assignPermissionsToRole(Long roleId, List<String> permissionNames);
    void removePermissionsFromRole(Long roleId, List<String> permissionNames);
    void createCategory(String categoryName);
    List<String> listCategories();
    void assignPermissionsToUser(Long userId, List<String> permissionNames);
    void removePermissionsFromUser(Long userId, List<String> permissionNames);
}