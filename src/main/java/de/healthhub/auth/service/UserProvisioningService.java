package de.healthhub.auth.service;

import de.healthhub.auth.model.User;
import de.healthhub.auth.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class UserProvisioningService {

    @Inject
    UserRepository userRepository;

    @Inject
    JsonWebToken jwt;

    @Transactional
    public User getOrCreateUserFromToken() {
        String subject = jwt.getSubject();
        String username = jwt.getClaim("preferred_username");
        String email = jwt.getClaim("email");

        if (subject == null || subject.isBlank()) {
            throw new IllegalStateException("JWT subject is missing");
        }

        User user = userRepository.findByKeycloakSubject(subject).orElse(null);

        if (user == null) {
            user = new User();
            user.setKeycloakSubject(subject);
            user.setUsername(username);
            user.setEmail(email);
            user.setEnabled(true);
            return userRepository.save(user);
        }

        boolean changed = false;

        if (username != null && !username.equals(user.getUsername())) {
            user.setUsername(username);
            changed = true;
        }

        if (email != null && !email.equals(user.getEmail())) {
            user.setEmail(email);
            changed = true;
        }

        if (changed) {
            user = userRepository.update(user);
        }

        return user;
    }
}