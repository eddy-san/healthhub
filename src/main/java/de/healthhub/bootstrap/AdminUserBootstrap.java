package de.healthhub.bootstrap;

import de.healthhub.auth.security.PasswordHasher;
import de.healthhub.auth.model.Role;
import de.healthhub.auth.model.RoleName;
import de.healthhub.auth.model.User;
import de.healthhub.auth.repository.RoleRepository;
import de.healthhub.auth.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@Singleton
@Startup
public class AdminUserBootstrap {

    @Inject
    private UserRepository userRepository;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private PasswordHasher passwordHasher;

    @PostConstruct
    @Transactional
    public void init() {
        String username = getenvOrDefault("ADMIN_USERNAME", "admin");
        String password = getenvOrDefault("ADMIN_PASSWORD", "admin123!");
        String email = getenvOrDefault("ADMIN_EMAIL", "admin@healthhub.local");

        if (userRepository.existsByUsername(username)) {
            System.out.println("HealthHub bootstrap: admin already exists");
            return;
        }

        Role adminRole = roleRepository.findByRoleName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

        User admin = new User();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setEnabled(true);
        admin.setPasswordHash(passwordHasher.hash(password));
        admin.addRole(adminRole);

        userRepository.save(admin);

        System.out.println("HealthHub bootstrap: admin user created");
    }

    private String getenvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}