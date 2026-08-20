package com.sentinel.api.infrastructure.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sentinel.core.model.AgentEvent;
import com.sentinel.core.port.EventPublisher;

@Component
@Profile("local")
public class InMemoryEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher springEvents;

    // Required for Spring to inject the event publisher bean
    public InMemoryEventPublisher(ApplicationEventPublisher springEvents) {
        this.springEvents = springEvents;
    }

    @Override
    public void publish(AgentEvent event) {
        // Wraps the raw event and fires it into Spring's memory pipeline
        springEvents.publishEvent(new AgentEventWrapper(event));
    }
}
