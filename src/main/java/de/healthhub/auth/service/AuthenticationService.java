package de.healthhub.auth.service;

import de.healthhub.auth.api.LoginResponse;
import de.healthhub.auth.security.JwtService;
import de.healthhub.auth.security.LoggedInUser;
import de.healthhub.auth.security.PasswordHasher;
import de.healthhub.auth.security.UserSession;
import de.healthhub.measurement.model.Patient;
import de.healthhub.auth.model.Role;
import de.healthhub.auth.model.RoleName;
import de.healthhub.auth.model.User;
import de.healthhub.measurement.repository.PatientRepository;
import de.healthhub.auth.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class AuthenticationService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private PatientRepository patientRepository;

    @Inject
    private PasswordHasher passwordHasher;

    @Inject
    private UserSession userSession;

    @Inject
    private JwtService jwtService;

    /**
     * Admin Web Login (JSF)
     */
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

    /**
     * Web Logout
     */
    public void logout() {
        userSession.logout();
    }

    /**
     * API Login (JWT) for Patients
     */
    public LoginResponse loginApi(String username, String password) {

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("User disabled");
        }

        if (!passwordHasher.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Password mismatch");
        }

        boolean isPatient = user.getRoles().stream()
                .map(Role::getRoleName)
                .anyMatch(role -> role == RoleName.PATIENT);

        if (!isPatient) {
            throw new IllegalArgumentException("API login allowed for PATIENT only");
        }

        String token = jwtService.createToken(user);

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds()
        );
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

            boolean isAdmin = admin.getRoles().stream()
                    .map(Role::getRoleName)
                    .anyMatch(role -> role == RoleName.ADMIN);

            if (!isAdmin) {
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}