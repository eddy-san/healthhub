package de.healthhub.auth.service;

import de.healthhub.auth.model.User;
import de.healthhub.auth.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import java.security.Principal;
import java.util.Objects;

@ApplicationScoped
public class UserProvisioningService {

    @Inject
    private UserRepository userRepository;

    @Transactional
    public User getOrCreateFromRequest(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalStateException("HttpServletRequest is null");
        }

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            throw new IllegalStateException("No authenticated principal found");
        }

        String subject = extractSubject(request, principal);
        String username = extractUsername(request, principal);

        if (username == null || username.isBlank()) {
            username = subject;
        }

        String email = extractEmail(request);

        return getOrCreateExternalUser(subject, username, email);
    }

    @Transactional
    public User getOrCreateExternalUser(String subject, String username, String email) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalStateException("Subject is required");
        }

        System.out.println("HealthHub provisioning subject=" + subject);
        System.out.println("HealthHub provisioning username=" + username);
        System.out.println("HealthHub provisioning email=" + email);

        User user = userRepository.findByKeycloakSubject(subject).orElse(null);

        if (user == null) {
            System.out.println("HealthHub provisioning: creating new app_user");

            user = new User();
            user.setKeycloakSubject(subject);
            user.setUsername(username);
            user.setEmail(email);
            user.setEnabled(true);

            userRepository.save(user);

            System.out.println("HealthHub provisioning: created app_user for subject=" + subject);
            return user;
        }

        boolean changed = false;

        if (!Objects.equals(user.getUsername(), username)) {
            user.setUsername(username);
            changed = true;
        }

        if (!Objects.equals(user.getEmail(), email)) {
            user.setEmail(email);
            changed = true;
        }

        if (!user.isEnabled()) {
            user.setEnabled(true);
            changed = true;
        }

        if (changed) {
            user = userRepository.update(user);
            System.out.println("HealthHub provisioning: updated app_user id=" + user.getId());
        } else {
            System.out.println("HealthHub provisioning: existing app_user reused id=" + user.getId());
        }

        return user;
    }

    private String extractSubject(HttpServletRequest request, Principal principal) {
        Object subAttr = request.getAttribute("sub");
        if (subAttr != null && !subAttr.toString().isBlank()) {
            return subAttr.toString();
        }

        Object oidcSub = request.getAttribute("OIDC_CLAIM_sub");
        if (oidcSub != null && !oidcSub.toString().isBlank()) {
            return oidcSub.toString();
        }

        String principalName = principal.getName();
        if (principalName != null && !principalName.isBlank()) {
            return principalName;
        }

        throw new IllegalStateException("No subject found in authenticated request");
    }

    private String extractUsername(HttpServletRequest request, Principal principal) {
        Object preferredUsername = request.getAttribute("preferred_username");
        if (preferredUsername != null && !preferredUsername.toString().isBlank()) {
            return preferredUsername.toString();
        }

        Object oidcPreferredUsername = request.getAttribute("OIDC_CLAIM_preferred_username");
        if (oidcPreferredUsername != null && !oidcPreferredUsername.toString().isBlank()) {
            return oidcPreferredUsername.toString();
        }

        String remoteUser = request.getRemoteUser();
        if (remoteUser != null && !remoteUser.isBlank()) {
            return remoteUser;
        }

        String principalName = principal.getName();
        if (principalName != null && !principalName.isBlank()) {
            return principalName;
        }

        return null;
    }

    private String extractEmail(HttpServletRequest request) {
        Object email = request.getAttribute("email");
        if (email != null && !email.toString().isBlank()) {
            return email.toString();
        }

        Object oidcEmail = request.getAttribute("OIDC_CLAIM_email");
        if (oidcEmail != null && !oidcEmail.toString().isBlank()) {
            return oidcEmail.toString();
        }

        return null;
    }
}