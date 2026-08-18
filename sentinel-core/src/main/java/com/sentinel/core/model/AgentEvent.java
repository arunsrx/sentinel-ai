package com.sentinel.core.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record AgentEvent(
        UUID eventId,
        String agentId,
        String agentName,
        EventType eventType,
        String inputPayload,
        String outputPayload,
        int inputTokens,
        int outputTokens,
        int totalTokens,
        long latencyMs,
        String modelUsed,
        String sourceIp,
        Map<String, String> metadata,
        Instant timestamp) {

    public enum EventType {
        LLM_CALL,
        TOOL_INVOCATION,
        AGENT_TO_AGENT,
        API_CALL
    }

    public AgentEvent {
        Objects.requireNonNull(eventId, "eventId cannot be null");
        Objects.requireNonNull(eventType, "eventType cannot be null");
        Objects.requireNonNull(timestamp, "timestamp cannot be null");
    }

}
