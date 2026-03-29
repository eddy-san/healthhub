package de.healthhub.measurement.service;

import de.healthhub.auth.model.User;
import de.healthhub.auth.service.UserProvisioningService;
import de.healthhub.measurement.model.MeasurementCreateRequest;
import de.healthhub.measurement.model.MeasurementMeResponse;
import de.healthhub.measurement.model.Patient;
import de.healthhub.measurement.repository.PatientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.SecurityContext;

@ApplicationScoped
public class MeasurementService {

    @Inject
    private UserProvisioningService userProvisioningService;

    @Inject
    private PatientRepository patientRepository;

    public MeasurementMeResponse getCurrentPatientView(SecurityContext securityContext, HttpServletRequest httpRequest) {
        User user = userProvisioningService.getOrCreateUser(securityContext, httpRequest);

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

    public void createMeasurement(SecurityContext securityContext,
                                  HttpServletRequest httpRequest,
                                  MeasurementCreateRequest request) {

        User user = userProvisioningService.getOrCreateUser(securityContext, httpRequest);

        if (!user.isEnabled()) {
            throw new IllegalStateException("User disabled");
        }

        patientRepository.findByUserId(user.getId())
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
}