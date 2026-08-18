package com.sentinel.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AgentEvent Tests")
public class AgentEventTest {

    private UUID eventId = UUID.randomUUID();
    private UUID agentId = UUID.randomUUID();
    private String agentName = "TestAgent";
    private AgentEvent.EventType eventType = AgentEvent.EventType.LLM_CALL;
    private String inputPayload = "inputPayload";
    private String outputPayload = "outputPayload";
    private int inputTokens = 10;
    private int outputTokens = 20;
    private int totalTokens = 30;
    private long latencyMs = 100L;
    private String modelUsed = "TestModel";
    private String sourceIp = "192.168.1.1";
    private Map<String, String> metadata = Map.of("key1", "value1", "key2", "value2");
    private Instant timestamp = Instant.now();

    @Test
    @DisplayName("Should create valid AgentEvent with all parameters")
    void testCompactConstructor() {
        AgentEvent agentEvent = new AgentEvent(
                eventId,
                agentId,
                agentName,
                eventType,
                inputPayload,
                outputPayload,
                inputTokens,
                outputTokens,
                totalTokens,
                latencyMs,
                modelUsed,
                sourceIp,
                metadata,
                timestamp);

        assertNotNull(agentEvent);
        assertNotNull(agentEvent.eventId());
        assertNotNull(agentEvent.eventType());
        assertNotNull(agentEvent.timestamp());
    }


    @Test
    @DisplayName("Should support all EventType enum values")
    void testAllEventTypes() {
        for (AgentEvent.EventType type : AgentEvent.EventType.values()) {
            AgentEvent agentEvent = new AgentEvent(
                    eventId,
                    agentId,
                    agentName,
                    type,
                    inputPayload,
                    outputPayload,
                    inputTokens,
                    outputTokens,
                    totalTokens,
                    latencyMs,
                    modelUsed,
                    sourceIp,
                    metadata,
                    timestamp);

            assertEquals(type, agentEvent.eventType());
        }
    }

    @Test
    @DisplayName("Should provide accessor methods for all fields")
    void testRecordAccessors() {
        AgentEvent agentEvent = new AgentEvent(
                eventId, agentId, agentName, eventType,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                latencyMs, modelUsed, sourceIp,
                metadata, timestamp);

        assertEquals(eventId, agentEvent.eventId());
        assertEquals(agentId, agentEvent.agentId());
        assertEquals(agentName, agentEvent.agentName());
        assertEquals(eventType, agentEvent.eventType());
        assertEquals(inputPayload, agentEvent.inputPayload());
        assertEquals(outputPayload, agentEvent.outputPayload());
        assertEquals(inputTokens, agentEvent.inputTokens());
        assertEquals(outputTokens, agentEvent.outputTokens());
        assertEquals(totalTokens, agentEvent.totalTokens());
        assertEquals(latencyMs, agentEvent.latencyMs());
        assertEquals(modelUsed, agentEvent.modelUsed());
        assertEquals(sourceIp, agentEvent.sourceIp());
        assertEquals(metadata, agentEvent.metadata());
        assertEquals(timestamp, agentEvent.timestamp());
    }

    @Test
    @DisplayName("Should correctly implement equals for identical records")
    void testEqualsForIdenticalRecords() {
        AgentEvent event1 = new AgentEvent(
                eventId, agentId, agentName, eventType,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                latencyMs, modelUsed, sourceIp,
                metadata, timestamp);

        AgentEvent event2 = new AgentEvent(
                eventId, agentId, agentName, eventType,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                latencyMs, modelUsed, sourceIp,
                metadata, timestamp);

        assertEquals(event1, event2);
    }

    @Test
    @DisplayName("Should correctly implement equals when records differ")
    void testEqualsForDifferentRecords() {
        AgentEvent event1 = new AgentEvent(
                eventId, agentId, agentName, eventType,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                latencyMs, modelUsed, sourceIp,
                metadata, timestamp);

        AgentEvent event2 = new AgentEvent(
                UUID.randomUUID(), agentId, agentName, eventType,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                latencyMs, modelUsed, sourceIp,
                metadata, timestamp);

        assertNotEquals(event1, event2);
    }

    @Test
    @DisplayName("Should correctly implement hashCode")
    void testHashCode() {
        AgentEvent event1 = new AgentEvent(
                eventId, agentId, agentName, eventType,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                latencyMs, modelUsed, sourceIp,
                metadata, timestamp);

        AgentEvent event2 = new AgentEvent(
                eventId, agentId, agentName, eventType,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                latencyMs, modelUsed, sourceIp,
                metadata, timestamp);

        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    @DisplayName("Should handle empty metadata")
    void testEmptyMetadata() {
        AgentEvent agentEvent = new AgentEvent(
                eventId, agentId, agentName, eventType,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                latencyMs, modelUsed, sourceIp,
                Map.of(), timestamp);

        assertNotNull(agentEvent.metadata());
        assertTrue(agentEvent.metadata().isEmpty());
    }

    @Test
    @DisplayName("Should handle various token counts")
    void testVariousTokenCounts() {
        AgentEvent agentEvent1 = new AgentEvent(
                eventId, agentId, agentName, eventType,
                inputPayload, outputPayload,
                0, 0, 0,
                latencyMs, modelUsed, sourceIp,
                metadata, timestamp);
        assertEquals(0, agentEvent1.inputTokens());

        AgentEvent agentEvent2 = new AgentEvent(
                eventId, agentId, agentName, eventType,
                inputPayload, outputPayload,
                1000, 2000, 3000,
                latencyMs, modelUsed, sourceIp,
                metadata, timestamp);
        assertEquals(1000, agentEvent2.inputTokens());
        assertEquals(2000, agentEvent2.outputTokens());
        assertEquals(3000, agentEvent2.totalTokens());
    }

    @Test
    @DisplayName("Should handle various latency values")
    void testVariousLatencyValues() {
        AgentEvent agentEvent1 = new AgentEvent(
                eventId, agentId, agentName, eventType,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                0L, modelUsed, sourceIp,
                metadata, timestamp);
        assertEquals(0L, agentEvent1.latencyMs());

        AgentEvent agentEvent2 = new AgentEvent(
                eventId, agentId, agentName, eventType,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                Long.MAX_VALUE, modelUsed, sourceIp,
                metadata, timestamp);
        assertEquals(Long.MAX_VALUE, agentEvent2.latencyMs());
    }

    @Test
    @DisplayName("Should handle null model")
    void testNullModel() {
        AgentEvent agentEvent = new AgentEvent(
                eventId, agentId, agentName, eventType,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                latencyMs, null, sourceIp,
                Map.of(), timestamp);

        assertNull(agentEvent.modelUsed());
    }

    @Test
    @DisplayName("Should be immutable record - field values cannot change after construction")
    void testRecordIsImmutable() {
        // Create an AgentEvent with specific values
        UUID testEventId = UUID.randomUUID();
        UUID testAgentId = UUID.randomUUID();
        String testAgentName = "ImmutableAgent";
        AgentEvent.EventType testEventType = AgentEvent.EventType.LLM_CALL;
        String testInputPayload = "immutableInput";
        String testOutputPayload = "immutableOutput";
        int testInputTokens = 100;
        int testOutputTokens = 200;
        int testTotalTokens = 300;
        long testLatencyMs = 5000L;
        String testModelUsed = "ImmutableModel";
        String testSourceIp = "10.0.0.1";
        Map<String, String> testMetadata = Map.of("immutableKey", "immutableValue");
        Instant testTimestamp = Instant.now();

        AgentEvent agentEvent = new AgentEvent(
                testEventId, testAgentId, testAgentName, testEventType,
                testInputPayload, testOutputPayload,
                testInputTokens, testOutputTokens, testTotalTokens,
                testLatencyMs, testModelUsed, testSourceIp,
                testMetadata, testTimestamp);

        // Verify all field values remain constant after construction
        assertEquals(testEventId, agentEvent.eventId(), "Event ID should remain immutable");
        assertEquals(testAgentId, agentEvent.agentId(), "Agent ID should remain immutable");
        assertEquals(testAgentName, agentEvent.agentName(), "Agent name should remain immutable");
        assertEquals(testEventType, agentEvent.eventType(), "Event type should remain immutable");
        assertEquals(testInputPayload, agentEvent.inputPayload(), "Input payload should remain immutable");
        assertEquals(testOutputPayload, agentEvent.outputPayload(), "Output payload should remain immutable");
        assertEquals(testInputTokens, agentEvent.inputTokens(), "Input tokens should remain immutable");
        assertEquals(testOutputTokens, agentEvent.outputTokens(), "Output tokens should remain immutable");
        assertEquals(testTotalTokens, agentEvent.totalTokens(), "Total tokens should remain immutable");
        assertEquals(testLatencyMs, agentEvent.latencyMs(), "Latency should remain immutable");
        assertEquals(testModelUsed, agentEvent.modelUsed(), "Model used should remain immutable");
        assertEquals(testSourceIp, agentEvent.sourceIp(), "Source IP should remain immutable");
        assertEquals(testMetadata, agentEvent.metadata(), "Metadata should remain immutable");
        assertEquals(testTimestamp, agentEvent.timestamp(), "Timestamp should remain immutable");

        // Verify multiple calls return the same values (no side effects)
        assertEquals(agentEvent.eventId(), agentEvent.eventId(), "Repeated access should return same eventId");
        assertEquals(agentEvent.timestamp(), agentEvent.timestamp(), "Repeated access should return same timestamp");
        assertEquals(agentEvent.latencyMs(), agentEvent.latencyMs(), "Repeated access should return same latencyMs");

        // Verify that records with identical field values are equal (immutable value
        // semantics)
        AgentEvent identicalEvent = new AgentEvent(
                testEventId, testAgentId, testAgentName, testEventType,
                testInputPayload, testOutputPayload,
                testInputTokens, testOutputTokens, testTotalTokens,
                testLatencyMs, testModelUsed, testSourceIp,
                testMetadata, testTimestamp);

        assertEquals(agentEvent, identicalEvent,
                "Records with identical field values should be equal (immutable semantics)");
        assertEquals(agentEvent.hashCode(), identicalEvent.hashCode(),
                "Identical immutable records should have equal hashCodes");
    }

    @Test
    @DisplayName("Should distinguish events with different agentIds")
    void testDifferentAgentIds() {
        UUID agentId2 = UUID.randomUUID();
        AgentEvent event1 = new AgentEvent(
                eventId, agentId, agentName, eventType,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                latencyMs, modelUsed, sourceIp,
                metadata, timestamp);

        AgentEvent event2 = new AgentEvent(
                eventId, agentId2, agentName, eventType,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                latencyMs, modelUsed, sourceIp,
                metadata, timestamp);

        assertNotEquals(event1, event2);
    }

    @Test
    void testNullEventId() {
        assertThrows(NullPointerException.class,
    () -> new AgentEvent(
                null, agentId, agentName, eventType,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                latencyMs, modelUsed, sourceIp,
                metadata, timestamp));
    }

    @Test
    void testNullEventType() {
        assertThrows(NullPointerException.class,
    () -> new AgentEvent(
                eventId, agentId, agentName, null,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                latencyMs, modelUsed, sourceIp,
                metadata, timestamp));
    }

    @Test
    void testNullTimeStamp() {
        assertThrows(NullPointerException.class,
    () -> new AgentEvent(
                eventId, agentId, agentName, AgentEvent.EventType.AGENT_TO_AGENT,
                inputPayload, outputPayload,
                inputTokens, outputTokens, totalTokens,
                latencyMs, modelUsed, sourceIp,
                metadata, null));
    }

}
