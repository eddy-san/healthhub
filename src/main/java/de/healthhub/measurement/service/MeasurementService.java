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
import jakarta.servlet.http.HttpServletRequest;
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

    public String extractUsername(SecurityContext securityContext, HttpServletRequest httpRequest) {
        if (securityContext != null) {
            Principal principal = securityContext.getUserPrincipal();
            if (principal != null) {
                System.out.println("HealthHub debug securityContext principal = " + principal.getName());
                if (principal.getName() != null && !principal.getName().isBlank()) {
                    return principal.getName();
                }
            } else {
                System.out.println("HealthHub debug securityContext principal = null");
            }
        } else {
            System.out.println("HealthHub debug securityContext = null");
        }

        if (httpRequest != null) {
            Principal principal = httpRequest.getUserPrincipal();
            if (principal != null) {
                System.out.println("HealthHub debug httpRequest principal = " + principal.getName());
                if (principal.getName() != null && !principal.getName().isBlank()) {
                    return principal.getName();
                }
            } else {
                System.out.println("HealthHub debug httpRequest principal = null");
            }
        } else {
            System.out.println("HealthHub debug httpRequest = null");
        }

        throw new IllegalStateException("No authenticated user");
    }

    public MeasurementMeResponse getCurrentPatientView(String username) {
        System.out.println("HealthHub debug getCurrentPatientView username = " + username);

        User user = loadEnabledUser(username);

        Set<RoleName> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());

        System.out.println("HealthHub debug user found id = " + user.getId());
        System.out.println("HealthHub debug role names = " + roleNames);

        if (!roleNames.contains(RoleName.PATIENT)) {
            throw new IllegalArgumentException("PATIENT role required");
        }

        Patient patient = patientRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("No patient assigned to user"));

        System.out.println("HealthHub debug patient found id = " + patient.getId());

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

        patientRepository.findByUsername(username)
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