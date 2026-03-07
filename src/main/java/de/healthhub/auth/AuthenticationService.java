package de.healthhub.auth;

import de.healthhub.domain.auth.RoleEntity;
import de.healthhub.domain.auth.RoleName;
import de.healthhub.domain.auth.User;
import de.healthhub.domain.patient.Patient;
import de.healthhub.persistence.PatientRepository;
import de.healthhub.persistence.UserRepository;
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

    public boolean login(String username, String clearTextPassword) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !user.isEnabled()) {
            return false;
        }

        if (!passwordHasher.matches(clearTextPassword, user.getPasswordHash())) {
            return false;
        }

        Set<RoleName> roleNames = user.getRoles().stream()
                .map(RoleEntity::getRoleName)
                .collect(Collectors.toSet());

        Long patientId = patientRepository.findByUserId(user.getId())
                .map(Patient::getId)
                .orElse(null);

        LoggedInUser loggedInUser = new LoggedInUser(user.getId(), user.getUsername(), roleNames, patientId);
        userSession.login(loggedInUser);
        return true;
    }

    public void logout() {
        userSession.logout();
    }
}
