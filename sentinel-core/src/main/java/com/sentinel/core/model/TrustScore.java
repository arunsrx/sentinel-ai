package com.sentinel.core.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TrustScore {
    // Identity is tied permanently to the Agent
    private final String agentId;

    // Mutable state variables
    private double value; // Must be 0.0 - 1.0
    private TrustLevel trustLevel;
    private int totalIncidents; // Tracks bad/suspicious events
    private int totalGood; // Tracks positive behaviors
    private final List<TrustHistoryEntry> history; // Holds the point-in-time snapshots
    private Instant lastUpdated;

    // Constructor enforces creation invariants
    public TrustScore(String agentId, double initialValue, String eventType, String reason) {
        this.agentId = Objects.requireNonNull(agentId, "Agent ID cannot be null");
        validateScoreBounds(initialValue);
        this.value = initialValue;
        this.trustLevel = deriveTrustLevel(initialValue);
        this.totalIncidents = 0;
        this.totalGood = 0;
        this.history = new ArrayList<>();
        this.lastUpdated = Instant.now(); // Set initial creation time

        // Add an initial baseline entry to history
        // this.history.add(new TrustHistoryEntry(
        // agentId,
        // initialValue,
        // TrustEvent.INITIALIZED,
        // reason != null ? reason :
        // "Initial trust score baseline set",
        // this.lastUpdated));
    }

    // Encapsulated mutation logic ensures invariants can never be breached
    public void adjustScore(double delta, boolean isViolation, String reason, String eventType) {
        Objects.requireNonNull(eventType, "Event type cannot be null");

        double newValue = this.value + delta;

        // Clamp values
        if (newValue > 1.0)
            newValue = 1.0;
        if (newValue < 0.0)
            newValue = 0.0;

        this.value = newValue;
        this.trustLevel = deriveTrustLevel(newValue);
        this.lastUpdated = Instant.now();

        if (isViolation) {
            this.totalIncidents++;
        } else {
            this.totalGood++;
        }

        // Pass eventType.name() if your TrustHistoryEntry record expects a String
        // TrustHistoryEntry historyEntry = new TrustHistoryEntry(
        // this.agentId,
        // this.value,
        // eventType,
        // reason,
        // this.lastUpdated);
        // this.history.add(historyEntry);
    }

    private void validateScoreBounds(double score) {
        if (score < 0.0 || score > 1.0) {
            throw new IllegalArgumentException("Trust score must be between 0.0 and 1.0. Given: " + score);
        }
    }

    private TrustLevel deriveTrustLevel(double score) {
        if (score >= 0.8)
            return TrustLevel.HIGHLY_TRUSTED;
        if (score >= 0.6)
            return TrustLevel.TRUSTED;
        if (score >= 0.4)
            return TrustLevel.NEUTRAL;
        if (score >= 0.2)
            return TrustLevel.SUSPICIOUS;
        return TrustLevel.UNTRUSTED;
    }

    // Getters
    public String getAgentId() {
        return agentId;
    }

    public double getValue() {
        return value;
    }

    public TrustLevel getTrustLevel() {
        return trustLevel;
    }

    public int getTotalIncidents() {
        return totalIncidents;
    }

    public int getTotalGood() {
        return totalGood;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    // Protect the internal list from being modified directly from outside
    public List<TrustHistoryEntry> getHistory() {
        return Collections.unmodifiableList(history);
    }
}
