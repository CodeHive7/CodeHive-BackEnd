package com.codehive;

import com.codehive.entity.Permissions;
import com.codehive.entity.Role;
import com.codehive.entity.User;
import com.codehive.repository.PermissionsRepository;
import com.codehive.repository.RoleRepository;
import com.codehive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class DefaultAdmin implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionsRepository permissionsRepository;

    @Value("${app.default-admin.username}")
    private String username;

    @Value("${app.default-admin.password}")
    private String password;

    @Value("${app.default-admin.email}")
    private String email;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.findByName("SUPER_ADMIN")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        List<Permissions> allPermissions = permissionsRepository.findAll();
        adminRole.getPermissions().addAll(allPermissions);
        roleRepository.save(adminRole);

        var optionalUser = userRepository.findByUsername(username);
        User admin;
        if(optionalUser.isEmpty()) {
            admin = new User();
            admin.setFullName("Default Admin");
            admin.setUsername(username);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setActive(true);
        } else {
            admin = optionalUser.get();
        }
        admin.getRoles().add(adminRole);
        userRepository.save(admin);
    }
}
