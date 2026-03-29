package de.healthhub.auth.service;

import de.healthhub.auth.model.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;

@RequestScoped
public class ApiIdentityService {

    @Inject
    UserProvisioningService userProvisioningService;

    public User getOrCreateFromRequest(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalStateException("HTTP request is missing");
        }

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            throw new IllegalStateException("No authenticated API user");
        }

        String subject = principal.getName();
        if (subject == null || subject.isBlank()) {
            throw new IllegalStateException("API principal name is empty");
        }

        String username = request.getRemoteUser();
        String email = null;

        return userProvisioningService.getOrCreateExternalUser(subject, username, email);
    }
}