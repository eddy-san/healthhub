package de.healthhub.auth.service;

import de.healthhub.auth.model.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;

@RequestScoped
public class OidcWebIdentityService {

    @Inject
    UserProvisioningService userProvisioningService;

    public User getOrCreateFromRequest(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalStateException("HTTP request is missing");
        }

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            throw new IllegalStateException("No authenticated OIDC user");
        }

        String subject = normalize(principal.getName());
        if (subject == null) {
            throw new IllegalStateException("OIDC principal name is empty");
        }

        String username = normalize(request.getRemoteUser());

        if (username == null) {
            username = subject;
        }

        String email = null;

        System.out.println("HealthHub OIDC subject=" + subject);
        System.out.println("HealthHub OIDC username=" + username);
        System.out.println("HealthHub OIDC email=" + email);

        return userProvisioningService.getOrCreateExternalUser(subject, username, email);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}