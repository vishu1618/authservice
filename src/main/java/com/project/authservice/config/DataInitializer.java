package com.project.authservice.config;

import com.project.authservice.entity.Role;
import com.project.authservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        seedRoleIfAbsent(Role.RoleName.ROLE_USER);
        seedRoleIfAbsent(Role.RoleName.ROLE_ADMIN);
        log.info("Role initialization complete. Total roles: {}", roleRepository.count());
    }

    private void seedRoleIfAbsent(Role.RoleName roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            roleRepository.save(Role.builder().name(roleName).build());
            log.info("Created role: {}", roleName);
        }
    }
}