package de.healthhub.measurement.service;

import de.healthhub.auth.model.RoleName;
import de.healthhub.auth.security.ApiRequestUser;
import de.healthhub.auth.security.LoggedInUser;
import de.healthhub.measurement.model.MeasurementCreateRequest;
import de.healthhub.measurement.model.MeasurementMeResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MeasurementService {

    @Inject
    private ApiRequestUser apiRequestUser;

    public MeasurementMeResponse getCurrentPatientView() {
        LoggedInUser user = apiRequestUser.getLoggedInUser();

        if (user == null) {
            return MeasurementMeResponse.error("No authenticated user");
        }

        if (!user.getRoles().contains(RoleName.PATIENT)) {
            return MeasurementMeResponse.error("PATIENT role required");
        }

        if (user.getPatientId() == null) {
            return MeasurementMeResponse.error("No patient assigned to user");
        }

        return MeasurementMeResponse.success(
                user.getUsername(),
                user.getUserId(),
                user.getPatientId(),
                user.getRoles().stream().map(Enum::name).toList()
        );
    }

    public void createMeasurement(MeasurementCreateRequest request) {
        LoggedInUser user = apiRequestUser.getLoggedInUser();

        if (user == null) {
            throw new IllegalStateException("No authenticated user");
        }

        if (!user.getRoles().contains(RoleName.PATIENT)) {
            throw new IllegalStateException("PATIENT role required");
        }

        if (user.getPatientId() == null) {
            throw new IllegalStateException("No patient assigned to user");
        }

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
        // Hier später Persistierung einbauen, z. B.:
        // - MeasurementEntity erzeugen
        // - patientId aus LoggedInUser setzen
        // - Repository.save(...)
    }
}