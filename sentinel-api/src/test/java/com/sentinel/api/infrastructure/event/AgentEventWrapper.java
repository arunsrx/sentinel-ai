package com.sentinel.api.infrastructure.event;

import java.time.Instant;
import java.util.UUID;

import com.sentinel.core.model.AgentEvent;

public record AgentEventWrapper(AgentEvent event, UUID id, Instant timestamp) {
    public AgentEventWrapper(AgentEvent event) {
        this(event, UUID.randomUUID(), Instant.now());
    }

}
