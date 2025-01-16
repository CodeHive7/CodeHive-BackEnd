package com.codehive.service.impl;

import com.codehive.dto.CreateUserRequest;
import com.codehive.dto.UserDto;
import com.codehive.entity.Permissions;
import com.codehive.entity.Role;
import com.codehive.entity.User;
import com.codehive.mapper.UserMapper;
import com.codehive.repository.PermissionsRepository;
import com.codehive.repository.RoleRepository;
import com.codehive.repository.UserRepository;
import com.codehive.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
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

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
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
}
