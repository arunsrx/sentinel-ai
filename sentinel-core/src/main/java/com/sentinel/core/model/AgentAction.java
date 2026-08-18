package com.sentinel.core.model;

import java.time.Instant;
import java.util.Objects;

public record AgentAction(String agentId,
        ActionType actionType,
        String reason,
        Instant timestamp) {
    // Compact constructor to enforce the structural invariants
    public AgentAction {
        Objects.requireNonNull(agentId, "Agent ID cannot be null");
        Objects.requireNonNull(actionType, "Action type cannot be null");
        Objects.requireNonNull(reason, "Reason cannot be null");
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");
    }

}
