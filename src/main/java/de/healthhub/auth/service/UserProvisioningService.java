package de.healthhub.auth.service;

import de.healthhub.auth.model.User;
import de.healthhub.auth.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;

@ApplicationScoped
public class UserProvisioningService {

    @Inject
    UserRepository userRepository;

    @Transactional
    public User getOrCreateUser(SecurityContext securityContext, HttpServletRequest request) {
        String subject = extractSubject(securityContext, request);

        User user = userRepository.findByKeycloakSubject(subject).orElse(null);

        if (user == null) {
            user = new User();
            user.setKeycloakSubject(subject);
            user.setUsername(extractUsername(securityContext, request));
            user.setEmail(null);
            user.setEnabled(true);
            return userRepository.save(user);
        }

        String username = extractUsername(securityContext, request);
        boolean changed = false;

        if (username != null && !username.equals(user.getUsername())) {
            user.setUsername(username);
            changed = true;
        }

        if (changed) {
            user = userRepository.update(user);
        }

        return user;
    }

    public String extractSubject(SecurityContext securityContext, HttpServletRequest request) {
        if (securityContext != null) {
            Principal principal = securityContext.getUserPrincipal();
            if (principal != null && principal.getName() != null && !principal.getName().isBlank()) {
                return principal.getName();
            }
        }

        if (request != null) {
            Principal principal = request.getUserPrincipal();
            if (principal != null && principal.getName() != null && !principal.getName().isBlank()) {
                return principal.getName();
            }
        }

        throw new IllegalStateException("No authenticated user");
    }

    public String extractUsername(SecurityContext securityContext, HttpServletRequest request) {
        return extractSubject(securityContext, request);
    }
}