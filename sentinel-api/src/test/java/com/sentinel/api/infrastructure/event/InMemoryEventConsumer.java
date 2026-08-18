package com.sentinel.api.infrastructure.event;

import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.sentinel.core.port.EventConsumer;

@Component
@Profile("local")
public class InMemoryEventConsumer {

    private final EventConsumer consumer;

    public InMemoryEventConsumer(EventConsumer consumer) {
        this.consumer = consumer;
    }

    @EventListener
    @Async("eventProcessorExecutor")
    public void onApplicationEvent(AgentEventWrapper wrapper) {
        consumer.onEvent(wrapper.event());
    }
}
