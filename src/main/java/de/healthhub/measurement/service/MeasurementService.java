package de.healthhub.measurement.service;

import de.healthhub.auth.model.RoleName;
import de.healthhub.auth.model.User;
import de.healthhub.auth.repository.UserRepository;
import de.healthhub.measurement.model.MeasurementCreateRequest;
import de.healthhub.measurement.model.MeasurementMeResponse;
import de.healthhub.measurement.model.Patient;
import de.healthhub.measurement.repository.PatientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;
import java.util.List;

@ApplicationScoped
public class MeasurementService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private PatientRepository patientRepository;

    /**
     * Extrahiert den Keycloak Subject (sub) aus dem SecurityContext.
     * Achtung: je nach WildFly/Keycloak Config kann das auch username sein!
     */
    public String extractSubject(SecurityContext securityContext, HttpServletRequest httpRequest) {

        if (securityContext != null) {
            Principal principal = securityContext.getUserPrincipal();
            if (principal != null && principal.getName() != null && !principal.getName().isBlank()) {
                System.out.println("HealthHub DEBUG principal = " + principal.getName());
                return principal.getName();
            }
        }

        if (httpRequest != null) {
            Principal principal = httpRequest.getUserPrincipal();
            if (principal != null && principal.getName() != null && !principal.getName().isBlank()) {
                System.out.println("HealthHub DEBUG request principal = " + principal.getName());
                return principal.getName();
            }
        }

        throw new IllegalStateException("No authenticated user");
    }

    public MeasurementMeResponse getCurrentPatientView(String subject) {

        User user = loadEnabledUserBySubject(subject);

        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("No patient assigned"));

        return MeasurementMeResponse.success(
                user.getUsername(),
                user.getId(),
                patient.getId(),
                List.of("PATIENT")
        );
    }

    public void createMeasurement(String subject, MeasurementCreateRequest request) {

        User user = loadEnabledUserBySubject(subject);

        boolean isPatient = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName() == RoleName.PATIENT);

        if (!isPatient) {
            throw new IllegalArgumentException("PATIENT role required");
        }

        patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("No patient assigned"));

        if (request == null) {
            throw new IllegalArgumentException("Measurement request is required");
        }

        if (request.type() == null || request.type().isBlank()) {
            throw new IllegalArgumentException("Measurement type is required");
        }

        if (request.value() == null) {
            throw new IllegalArgumentException("Measurement value is required");
        }

        System.out.println("HealthHub measurement accepted: subject=" + subject
                + ", type=" + request.type()
                + ", value=" + request.value());

        // TODO:
        // - inbox_submission speichern
        // - audit_log schreiben
        // - Normalisierung starten
    }

    private User loadEnabledUserBySubject(String subject) {

        User user = userRepository.findByKeycloakSubject(subject)
                .orElseThrow(() -> new IllegalStateException("User not found for subject=" + subject));

        if (!user.isEnabled()) {
            throw new IllegalStateException("User disabled");
        }

        return user;
    }
}