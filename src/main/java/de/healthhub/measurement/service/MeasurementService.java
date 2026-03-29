package de.healthhub.measurement.service;

import de.healthhub.auth.model.User;
import de.healthhub.auth.security.CurrentUser;
import de.healthhub.measurement.model.MeasurementCreateRequest;
import de.healthhub.measurement.model.MeasurementMeResponse;
import de.healthhub.measurement.model.Patient;
import de.healthhub.measurement.repository.PatientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class MeasurementService {

    @Inject
    private PatientRepository patientRepository;

    @Inject
    private CurrentUser currentUser;

    public MeasurementMeResponse getCurrentPatientView() {
        User user = currentUser.getOrCreate();

        if (!user.isEnabled()) {
            throw new IllegalStateException("User disabled");
        }

        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("No patient assigned to user"));

        return MeasurementMeResponse.success(
                user.getUsername(),
                user.getId(),
                patient.getId(),
                List.of("PATIENT")
        );
    }

    public void createMeasurement(MeasurementCreateRequest request) {
        User user = currentUser.getOrCreate();

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

        // Hier später:
        // inbox_submission speichern
        // audit log schreiben
        // Normalisierung anstoßen
    }
}