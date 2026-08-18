package com.sentinel.core.port;

import com.sentinel.core.model.AgentEvent;

public interface EventConsumer {
    // Implemented by SentinelAutonomousLoop
    void onEvent(AgentEvent event);
}
