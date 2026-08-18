package com.sentinel.core.model;

import java.time.Instant;
import java.util.Objects;

public record TrustHistoryEntry(
        Instant timestamp,
        double value,
        String reason,
        TrustEvent eventType) {
    public TrustHistoryEntry {
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        Objects.requireNonNull(eventType, "Event type cannot be null");
        Objects.requireNonNull(reason, "Reason cannot be null");

        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("Historical trust score must be between 0.0 and 1.0. Found: " + value);
        }
    }
}
