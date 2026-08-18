package com.sentinel.core.port;

import com.sentinel.core.model.AgentEvent;

public interface EventPublisher {
    public void publish(AgentEvent event);
}
