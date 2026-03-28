package de.healthhub.bootstrap;

import de.healthhub.auth.model.Role;
import de.healthhub.auth.model.RoleName;
import de.healthhub.auth.model.User;
import de.healthhub.auth.repository.RoleRepository;
import de.healthhub.auth.repository.UserRepository;
import de.healthhub.auth.security.PasswordHasher;
import de.healthhub.measurement.model.Patient;
import de.healthhub.measurement.repository.PatientRepository;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@Singleton
@Startup
public class AdminUserBootstrap {

    @Inject
    private UserRepository userRepository;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private PatientRepository patientRepository;

    @Inject
    private PasswordHasher passwordHasher;

    @PostConstruct
    @Transactional
    public void init() {
        createAdmin();
        createPatient();
    }

    private void createAdmin() {
        String username = getenvOrDefault("ADMIN_USERNAME", "eddy.admin");
        String email = getenvOrDefault("ADMIN_EMAIL", "admin@healthhub.local");

        if (userRepository.existsByUsername(username)) {
            System.out.println("HealthHub bootstrap: admin already exists");
            return;
        }

        Role adminRole = roleRepository.findByRoleName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

        User admin = new User();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setEnabled(true);
        admin.setPasswordHash(passwordHasher.hash("KEYCLOAK_MANAGED_ACCOUNT"));
        admin.addRole(adminRole);

        userRepository.save(admin);

        System.out.println("HealthHub bootstrap: admin user created");
    }

    private void createPatient() {
        String username = getenvOrDefault("PATIENT_USERNAME", "eddy.patient");
        String email = getenvOrDefault("PATIENT_EMAIL", "patient@healthhub.local");
        String firstName = getenvOrDefault("PATIENT_FIRST_NAME", "Eduard");
        String lastName = getenvOrDefault("PATIENT_LAST_NAME", "Roth");

        if (userRepository.existsByUsername(username)) {
            System.out.println("HealthHub bootstrap: patient user already exists");
            return;
        }

        Role patientRole = roleRepository.findByRoleName(RoleName.PATIENT)
                .orElseThrow(() -> new IllegalStateException("PATIENT role not found"));

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);
        user.setPasswordHash(passwordHasher.hash("KEYCLOAK_MANAGED_ACCOUNT"));
        user.addRole(patientRole);

        userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(user);
        patient.setPatientNumber("P-" + System.currentTimeMillis());
        patient.setFirstName(firstName);
        patient.setLastName(lastName);

        patientRepository.save(patient);

        System.out.println("HealthHub bootstrap: patient user created (Keycloak-managed)");
    }

    private String getenvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}