package com.codehive;

import com.codehive.entity.Role;
import com.codehive.entity.User;
import com.codehive.repository.RoleRepository;
import com.codehive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultAdmin implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin.username}")
    private String username;

    @Value("${app.default-admin.password}")
    private String password;

    @Value("${app.default-admin.email}")
    private String email;

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.findByUsername(username).isEmpty()){
            User admin = new User();
            admin.setFullName("Default admin");
            admin.setUsername(username);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode("adminStrongPassword"));
            admin.setActive(true);

            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            admin.getRoles().add(adminRole);
            userRepository.save(admin);
        }
    }
}
