package com.sentinel.core.model;

import java.time.Instant;
import java.util.Objects;

public class AgentProfile {
    // Identity - Note: Remove 'final' ONLY if you are using Hibernate/JPA
    private final String agentId;

    private String agentName;
    private String owner;
    private int dailyTokenBudget;
    private int maxRequestsPerMinute; // Renamed to match request.maxRequestsPerMinute()
    private Instant registeredAt; // Added from controller
    private Status status; // Matches AgentProfile.Status
    private String sharedSecret;
    private String quarantineReason;
    private Instant quarantinedAt;

    // Enforced Enum Status
    public enum Status {
        ACTIVE, THROTTLED, QUARANTINED, BLOCKED
    }

    // No-Args Constructor (Required only if using JPA/Hibernate)
    protected AgentProfile() {
        this.agentId = null;
    }

    // Explicit Constructor used for the Registration flow
    public AgentProfile(String agentId, String agentName, String owner,
            int dailyTokenBudget, int maxRequestsPerMinute,
            Instant registeredAt, Status status) {
        this.agentId = Objects.requireNonNull(agentId, "Agent ID cannot be null");
        this.agentName = agentName;
        this.owner = owner;
        this.dailyTokenBudget = dailyTokenBudget;
        this.maxRequestsPerMinute = maxRequestsPerMinute;
        this.registeredAt = registeredAt != null ? registeredAt : Instant.now();
        this.status = status != null ? status : Status.ACTIVE;
    }

    /**
     * Attaches the generated shared secret to this agent profile.
     * This is called during the registration lifecycle before saving to the DB.
     */
    public void updateSharedSecret(String sharedSecret) {
        if (this.sharedSecret != null) {
            throw new IllegalStateException("Shared secret has already been initialized for this agent.");
        }
        this.sharedSecret = Objects.requireNonNull(sharedSecret, "Shared secret cannot be null");
    }

    // --- Domain Logic Methods (Invariants) ---

    public void throttle() {
        if (this.status == Status.BLOCKED) {
            throw new IllegalStateException("Cannot throttle a blocked agent.");
        }
        // A security quarantine takes priority over a simple volumetric throttle
        if (this.status != Status.QUARANTINED) {
            this.status = Status.THROTTLED;
        }
    }

    public void block() {
        this.status = Status.BLOCKED;
        // Clear out quarantine data since BLOCKED is a higher terminal severity
        this.quarantineReason = null;
        this.quarantinedAt = null;
    }

    public void quarantine(String reason) {
        if (this.status == Status.BLOCKED) {
            throw new IllegalStateException("Cannot quarantine a permanently blocked agent.");
        }
        this.status = Status.QUARANTINED;
        this.quarantineReason = Objects.requireNonNull(reason, "Quarantine reason is required");
        this.quarantinedAt = Instant.now();
    }

    /**
     * Escales an already quarantined agent to a reinforced state (Hard Block)
     * due to persistent violations inside the window.
     */
    public void reinforceQuarantine() {
        if (this.status != Status.QUARANTINED) {
            throw new IllegalStateException("An agent must be in QUARANTINED status before it can be REINFORCED.");
        }
        // Culturally/Logically maps to your terminal BLOCKED status in the DB
        this.status = Status.BLOCKED;
    }

    /**
     * Recovers an agent back to an ACTIVE state from being Throttled or Blocked.
     */
    public void activate() {
        this.status = Status.ACTIVE;
        this.quarantineReason = null;
        this.quarantinedAt = null;
    }

    // --- Standard Identity-Based Equals/HashCode ---

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AgentProfile))
            return false;
        AgentProfile that = (AgentProfile) o;
        return Objects.equals(agentId, that.agentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentId);
    }

    // --- Getters ---
    public String getAgentId() {
        return agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getOwner() {
        return owner;
    }

    public int getDailyTokenBudget() {
        return dailyTokenBudget;
    }

    public int getMaxRequestsPerMinute() {
        return maxRequestsPerMinute;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public Status getStatus() {
        return status;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public String getQuarantineReason() {
        return quarantineReason;
    }

    public Instant getQuarantinedAt() {
        return quarantinedAt;
    }
}
