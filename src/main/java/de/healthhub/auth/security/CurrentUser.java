package de.healthhub.auth.security;

import de.healthhub.auth.model.User;
import de.healthhub.auth.repository.UserRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
public class CurrentUser {

    @Inject
    JsonWebToken jwt;

    @Inject
    UserRepository userRepository;

    public User getOrCreate() {
        String subject = jwt.getSubject();
        String username = jwt.getClaim("preferred_username");
        String email = jwt.getClaim("email");

        if (subject == null || subject.isBlank()) {
            throw new IllegalStateException("Missing JWT subject");
        }

        User user = userRepository.findByKeycloakSubject(subject).orElse(null);

        if (user == null) {
            user = new User();
            user.setKeycloakSubject(subject);
            user.setUsername(blankToNull(username));
            user.setEmail(blankToNull(email));
            user.setEnabled(true);
            userRepository.save(user);
            return user;
        }

        boolean changed = false;

        String normalizedUsername = blankToNull(username);
        String normalizedEmail = blankToNull(email);

        if (!equalsNullable(normalizedUsername, user.getUsername())) {
            user.setUsername(normalizedUsername);
            changed = true;
        }

        if (!equalsNullable(normalizedEmail, user.getEmail())) {
            user.setEmail(normalizedEmail);
            changed = true;
        }

        if (changed) {
            user = userRepository.update(user);
        }

        return user;
    }

    public String getSubject() {
        return jwt.getSubject();
    }

    public String getUsername() {
        return blankToNull(jwt.getClaim("preferred_username"));
    }

    public String getEmail() {
        return blankToNull(jwt.getClaim("email"));
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    private boolean equalsNullable(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }
}