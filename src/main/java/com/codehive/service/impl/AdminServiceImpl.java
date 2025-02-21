package com.codehive.service.impl;

import com.codehive.dto.CategoryDto;
import com.codehive.dto.CreateUserRequest;
import com.codehive.dto.RoleDto;
import com.codehive.dto.UserDto;
import com.codehive.entity.Category;
import com.codehive.entity.Permissions;
import com.codehive.entity.Role;
import com.codehive.entity.User;
import com.codehive.mapper.UserMapper;
import com.codehive.repository.*;
import com.codehive.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PermissionsRepository permissionsRepository;
    private final CategoryRepository categoryRepository;
    private final ProjectRepository projectRepository;

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    UserDto dto = new UserDto();
                    dto.setId(user.getId());
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    Set<String> roleNames = user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet());
                    dto.setRoles(roleNames);
                    dto.setStatus(user.isActive() ? "Active": "Banned");
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(CreateUserRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        List<Role> rolesToAssign = request.getRoles().stream()
                        .map(roleName -> roleRepository.findByName(roleName)
                                .orElseThrow(() -> new RuntimeException("Role not found"))
                        )
                                .toList();
        user.setRoles(new HashSet<>(rolesToAssign));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    public void assignRolesToUser(Long userId, List<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not Found"));

        for(String roleName : roleNames){
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.getRoles().add(role);
        }
        userRepository.save(user);
    }

    @Override
    public void removeRolesFromUser(Long userId, List<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not Found"));

        for(String roleName : roleNames){
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.getRoles().remove(role);
            role.getUsers().remove(user);
        }
        userRepository.save(user);
    }

    @Override
    public void assignPermissionsToRole(Long roleId, List<String> permissionNames) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        for (String permName : permissionNames){
            Permissions permissions = permissionsRepository.findByName(permName)
                    .orElseThrow(() -> new RuntimeException("Permission not found"));

            role.getPermissions().add(permissions);
            permissions.getRoles().add(role);
        }
        roleRepository.save(role);
    }

    @Override
    public void removePermissionsFromRole(Long roleId, List<String> permissionNames) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found: " +roleId));

        for(String permName : permissionNames) {
            Permissions permission = permissionsRepository.findByName(permName)
                    .orElseThrow(() -> new RuntimeException("Permission not found: " + permName));

            role.getPermissions().remove(permission);
            permission.getRoles().remove(role);
        }
        roleRepository.save(role);
    }

    @Override
    public void createCategory(String categoryName) {
        if(categoryRepository.findByName(categoryName).isPresent()) {
            throw new RuntimeException("Category already exists");
        }
        Category category = new Category();
        category.setName(categoryName);
        categoryRepository.save(category);
    }

    @Override
    public List<CategoryDto> listCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryDto(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public void updateCategory(Long categoryId, String newName) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(newName);
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        //Check if category is assigned to any project
        long projectCount = projectRepository.countByCategory(category);
        if(projectCount > 0) {
            throw new RuntimeException("Cannot delete category. It is currently is use");
        }

        categoryRepository.delete(category);
    }

    @Override
    public void assignPermissionsToUser(Long userId, List<String> permissionNames) {

    }

    @Override
    public void removePermissionsFromUser(Long userId, List<String> permissionNames) {

    }

    @Override
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(role -> new RoleDto(role.getId(), role.getName(), role.getPermissions()
                        .stream().map(Permissions::getName).collect(Collectors.toList())))
                .toList();
    }

    @Override
    public List<String> getAllPermissions() {
        return permissionsRepository.findAll().stream()
                .map(Permissions::getName)
                .collect(Collectors.toList());
    }

    @Override
    public void createRole(String roleName) {
        if (roleRepository.findByName(roleName).isPresent()){
            throw new RuntimeException("Role already exists");
        }
        Role newRole = new Role();
        newRole.setName(roleName);
        roleRepository.save(newRole);
    }

    @Override
    public void updateRole(Long roleId, String newName) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        role.setName(newName);
        roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        roleRepository.delete(role);
    }
}
