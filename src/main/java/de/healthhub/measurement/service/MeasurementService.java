package de.healthhub.measurement.service;

import de.healthhub.auth.model.Role;
import de.healthhub.auth.model.RoleName;
import de.healthhub.auth.model.User;
import de.healthhub.auth.repository.UserRepository;
import de.healthhub.measurement.model.MeasurementCreateRequest;
import de.healthhub.measurement.model.MeasurementMeResponse;
import de.healthhub.measurement.model.Patient;
import de.healthhub.measurement.repository.PatientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class MeasurementService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private PatientRepository patientRepository;

    public String extractUsername(SecurityContext securityContext) {
        if (securityContext == null) {
            throw new IllegalStateException("No security context");
        }

        Principal principal = securityContext.getUserPrincipal();

        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new IllegalStateException("No authenticated user");
        }

        return principal.getName();
    }

    public MeasurementMeResponse getCurrentPatientView(String username) {
        User user = loadEnabledUser(username);

        Set<RoleName> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());

        if (!roleNames.contains(RoleName.PATIENT)) {
            throw new IllegalArgumentException("PATIENT role required");
        }

        Patient patient = patientRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("No patient assigned to user"));

        return MeasurementMeResponse.success(
                user.getUsername(),
                user.getId(),
                patient.getId(),
                roleNames.stream().map(Enum::name).sorted().toList()
        );
    }

    public void createMeasurement(String username, MeasurementCreateRequest request) {
        User user = loadEnabledUser(username);

        boolean isPatient = user.getRoles().stream()
                .map(Role::getRoleName)
                .anyMatch(role -> role == RoleName.PATIENT);

        if (!isPatient) {
            throw new IllegalArgumentException("PATIENT role required");
        }

        Patient patient = patientRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("No patient assigned to user"));

        if (request == null) {
            throw new IllegalArgumentException("Measurement request is required");
        }

        if (request.type() == null || request.type().isBlank()) {
            throw new IllegalArgumentException("Measurement type is required");
        }

        if (request.value() == null) {
            throw new IllegalArgumentException("Measurement value is required");
        }

        // TODO:
        // Persistierung ergänzen:
        // - MeasurementEntity erzeugen
        // - patient.getId() setzen
        // - request.type(), request.value(), request.unit(), request.timestamp() speichern
    }

    private User loadEnabledUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!user.isEnabled()) {
            throw new IllegalStateException("User disabled");
        }

        return user;
    }
}