package de.healthhub.measurement.service;

import de.healthhub.auth.model.Role;
import de.healthhub.auth.model.RoleName;
import de.healthhub.auth.model.User;
import de.healthhub.auth.service.ApiIdentityService;
import de.healthhub.measurement.model.MeasurementCreateRequest;
import de.healthhub.measurement.model.MeasurementMeResponse;
import de.healthhub.measurement.model.Patient;
import de.healthhub.measurement.repository.PatientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

@ApplicationScoped
public class MeasurementService {

    @Inject
    ApiIdentityService apiIdentityService;

    @Inject
    PatientRepository patientRepository;

    public MeasurementMeResponse getCurrentPatientView(HttpServletRequest request) {
        User user = apiIdentityService.getOrCreateFromRequest(request);

        if (!user.isEnabled()) {
            throw new IllegalStateException("User disabled");
        }

        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("No patient assigned to user"));

        return MeasurementMeResponse.success(
                user.getUsername(),
                user.getId(),
                patient.getId(),
                java.util.List.of("PATIENT")
        );
    }

    public void createMeasurement(HttpServletRequest request, MeasurementCreateRequest req) {
        User user = apiIdentityService.getOrCreateFromRequest(request);

        if (!user.isEnabled()) {
            throw new IllegalStateException("User disabled");
        }

        boolean isPatient = user.getRoles().stream()
                .map(Role::getRoleName)
                .anyMatch(role -> role == RoleName.PATIENT);

        if (!isPatient) {
            throw new IllegalArgumentException("PATIENT role required");
        }

        patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("No patient assigned to user"));

        if (req == null) {
            throw new IllegalArgumentException("Measurement request is required");
        }

        if (req.type() == null || req.type().isBlank()) {
            throw new IllegalArgumentException("Measurement type is required");
        }

        if (req.value() == null) {
            throw new IllegalArgumentException("Measurement value is required");
        }
    }
}