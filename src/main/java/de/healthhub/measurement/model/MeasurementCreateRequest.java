package de.healthhub.measurement.model;

import java.time.Instant;

public record MeasurementCreateRequest(
        String type,
        Double value,
        String unit,
        Instant timestamp
) {}