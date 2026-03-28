package de.healthhub.auth.service;

import de.healthhub.auth.model.Role;
import de.healthhub.auth.model.RoleName;
import de.healthhub.auth.model.User;
import de.healthhub.auth.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthenticationService {

    @Inject
    private UserRepository userRepository;

    public boolean isReady() {
        try {
            String adminUsername = getenvOrDefault("ADMIN_USERNAME", "eddy.admin");

            User admin = userRepository.findByUsername(adminUsername).orElse(null);

            if (admin == null) {
                return false;
            }

            if (!admin.isEnabled()) {
                return false;
            }

            return admin.getRoles().stream()
                    .map(Role::getRoleName)
                    .anyMatch(role -> role == RoleName.ADMIN);

        } catch (Exception e) {
            return false;
        }
    }

    private String getenvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}