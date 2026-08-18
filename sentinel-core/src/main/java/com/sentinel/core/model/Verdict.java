package com.sentinel.core.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record Verdict(
        String verdictId,
        String agentId,
        RiskLevel riskLevel,
        RiskScore compositeScore,
        List<ThreatCategory> threats,
        List<DetectorResult> detectorResults,
        String explanation,
        RecommendedAction action,
        Instant timestamp) {
    // Validate intrinsic invariants and normalize immutable collections.
    public Verdict {
        Objects.requireNonNull(verdictId, "verdictId cannot be null");
        Objects.requireNonNull(agentId, "agentId cannot be null");
        Objects.requireNonNull(riskLevel, "riskLevel cannot be null");
        Objects.requireNonNull(compositeScore, "compositeScore cannot be null");
        Objects.requireNonNull(threats, "threats cannot be null");
        Objects.requireNonNull(detectorResults, "detectorResults cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
        Objects.requireNonNull(timestamp, "timestamp cannot be null");

        threats = List.copyOf(threats);
        detectorResults = List.copyOf(detectorResults);

        riskLevel = compositeScore.toRiskLevel();

        // if (explanation == null || explanation.strip().isEmpty()) {
        // throw new IllegalArgumentException("An explanation summary must be provided
        // for audit tracking");
        // }

        // if (timestamp.isAfter(Instant.now())) {
        // throw new IllegalArgumentException("Verdict timestamp cannot be set in the
        // future");
        // }
    }
}
