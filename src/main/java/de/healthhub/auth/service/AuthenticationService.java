package de.healthhub.auth.service;

import de.healthhub.auth.model.Role;
import de.healthhub.auth.model.RoleName;
import de.healthhub.auth.model.User;
import de.healthhub.auth.repository.UserRepository;
import de.healthhub.auth.security.LoggedInUser;
import de.healthhub.auth.security.PasswordHasher;
import de.healthhub.auth.security.UserSession;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class AuthenticationService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private PasswordHasher passwordHasher;

    @Inject
    private UserSession userSession;

    public boolean loginAdmin(String username, String clearTextPassword) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || !user.isEnabled()) {
            return false;
        }

        if (!passwordHasher.matches(clearTextPassword, user.getPasswordHash())) {
            return false;
        }

        boolean isAdmin = user.getRoles().stream()
                .map(Role::getRoleName)
                .anyMatch(role -> role == RoleName.ADMIN);

        if (!isAdmin) {
            return false;
        }

        Set<RoleName> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());

        LoggedInUser loggedInUser =
                new LoggedInUser(user.getId(), user.getUsername(), roleNames, null);

        userSession.login(loggedInUser);

        return true;
    }

    public void logout() {
        userSession.logout();
    }

    public boolean isReady() {
        try {
            User admin = userRepository.findByUsername("admin").orElse(null);

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
}