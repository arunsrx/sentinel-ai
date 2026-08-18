package com.sentinel.core.model;

import java.util.Map;
import java.util.Objects;

public record DetectorResult(
        String detectorName,
        ThreatCategory category,
        boolean triggered,
        double confidence,
        String evidence,
        Map<String, Object> details) {

    public DetectorResult {
        Objects.requireNonNull(detectorName, "detectorName cannot be null");
        Objects.requireNonNull(category, "category cannot be null");

        if (confidence < 0.0 || confidence > 1.0) {
            throw new IllegalArgumentException("confidence must be between 0.0 and 1.0");
        }

        details = details == null ? Map.of() : Map.copyOf(details);
    }

    public static DetectorResult safe(String detectorName, ThreatCategory category) {
        return new DetectorResult(detectorName, category, false, 0, null, null);
    }

}
