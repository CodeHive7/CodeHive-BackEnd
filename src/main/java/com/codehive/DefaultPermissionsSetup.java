package com.codehive;

import com.codehive.entity.Permissions;
import com.codehive.entity.Role;
import com.codehive.repository.PermissionsRepository;
import com.codehive.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultPermissionsSetup implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionsRepository permissionsRepository;


    @Override
    @Transactional
    public void run(String... args) throws Exception {

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        Role superAdminRole = roleRepository.findByName("SUPER_ADMIN")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        List<Permissions> allPermissions = permissionsRepository.findAll();

        List<String> userPermissions = List.of(
                "CREATE_PROJECT", "READ_PROJECT", "UPDATE_PROJECT", "DELETE_PROJECT",
                "CREATE_TASK", "READ_TASK", "UPDATE_TASK", "DELETE_TASK",
                "COMMENT_PROJECT", "SUBMIT_TASK", "APPLY_FOR_POSITION" , "UPDATE_APPLICATION_STATUS"
        );

        List<String> adminPermissions = List.of(
                "CREATE_PROJECT", "READ_PROJECT", "UPDATE_PROJECT", "DELETE_PROJECT",
                "CREATE_TASK", "READ_TASK", "UPDATE_TASK", "DELETE_TASK",
                "COMMENT_PROJECT", "SUBMIT_TASK",
                "BLOCK_USER", "UNBLOCK_USER","CREATE_CATEGORY" , "UPDATE_APPLICATION_STATUS"
        );


        List<Permissions> userPerms = allPermissions.stream()
                .filter(p -> userPermissions.contains(p.getName()))
                .toList();

        List<Permissions> adminPerms = allPermissions.stream()
                .filter(p -> adminPermissions.contains(p.getName()))
                .toList();


        userRole.getPermissions().addAll(userPerms);
        userPerms.forEach(p -> p.getRoles().add(userRole));

        adminRole.getPermissions().addAll(adminPerms);
        adminPerms.forEach(p -> p.getRoles().add(adminRole));

        superAdminRole.getPermissions().addAll(allPermissions);
        allPermissions.forEach(p -> p.getRoles().add(superAdminRole));

        roleRepository.save(userRole);
        roleRepository.save(adminRole);
        roleRepository.save(superAdminRole);

    }
}
