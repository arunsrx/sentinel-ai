package com.sentinel.core.model;

import java.time.Instant;
import java.util.Objects;

public record InvestigationFinding(
        String source, // The tool or agent that created the finding
        FindingSeverity severity, // e.g., LOW, MEDIUM, HIGH, CRITICAL
        String findingDetails, // The actual text/data of the finding
        Instant timestamp) {
    // Compact constructor to enforce invariants
    public InvestigationFinding {
        Objects.requireNonNull(source, "Source/Tool name cannot be null");
        Objects.requireNonNull(severity, "Finding severity is required");
        Objects.requireNonNull(timestamp, "Timestamp must be provided");

        if (findingDetails == null || findingDetails.strip().isEmpty()) {
            throw new IllegalArgumentException("Finding details cannot be blank");
        }

        if (timestamp.isAfter(Instant.now())) {
            throw new IllegalArgumentException("Finding timestamp cannot be in the future");
        }
    }
}
