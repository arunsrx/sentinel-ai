package com.sentinel.core.model;

import java.time.Instant;
import java.util.Objects;

public record Alert(
        AlertSeverity severity,
        String message,
        String source,
        Instant timestamp) {
    // Compact constructor for validation
    public Alert {
        Objects.requireNonNull(severity, "severity cannot be null");
        Objects.requireNonNull(message, "message cannot be null");
        Objects.requireNonNull(source, "source cannot be null");
        Objects.requireNonNull(timestamp, "timestamp cannot be null");
    }
}
