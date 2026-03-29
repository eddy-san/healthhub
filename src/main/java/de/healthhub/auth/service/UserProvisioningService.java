package de.healthhub.auth.service;

import de.healthhub.auth.model.User;
import de.healthhub.auth.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class UserProvisioningService {

    @Inject
    UserRepository userRepository;

    public User getOrCreateExternalUser(String subject, String username, String email) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Missing external subject");
        }

        User user = userRepository.findByKeycloakSubject(subject).orElse(null);

        if (user == null) {
            user = new User();
            user.setKeycloakSubject(subject);
            user.setUsername(username);
            user.setEmail(email);
            user.setEnabled(true);
            userRepository.save(user);
            return user;
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