package de.healthhub.measurement.model;

import java.util.List;

public record MeasurementMeResponse(String error, String username, Long userId, Long patientId, List<String> roles) {

    public static MeasurementMeResponse error(String error) {
        return new MeasurementMeResponse(error, null, null, null, null);
    }

    public static MeasurementMeResponse success(String username, Long userId, Long patientId, List<String> roles) {
        return new MeasurementMeResponse(null, username, userId, patientId, roles);
    }
}
