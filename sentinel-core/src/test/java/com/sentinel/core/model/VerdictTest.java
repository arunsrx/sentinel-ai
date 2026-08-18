package com.sentinel.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class VerdictTest {

    @Test
    void shouldCreateVerdictSuccessfully() {
        // Arrange
        RiskScore mockScore = mock(RiskScore.class);
        when(mockScore.toRiskLevel()).thenReturn(RiskLevel.HIGH);

        List<ThreatCategory> threats = List.of(mock(ThreatCategory.class));
        List<DetectorResult> detectors = List.of(mock(DetectorResult.class));
        Instant now = Instant.now();

        // Act
        Verdict verdict = new Verdict(
                "v-123", "agent-456", RiskLevel.LOW, mockScore,
                threats, detectors, "Looks suspicious", RecommendedAction.BLOCK, now);

        // Assert
        assertEquals("v-123", verdict.verdictId());
        assertEquals("agent-456", verdict.agentId());
        assertEquals(mockScore, verdict.compositeScore());
        assertEquals("Looks suspicious", verdict.explanation());
        assertEquals(RecommendedAction.BLOCK, verdict.action());
        assertEquals(now, verdict.timestamp());
    }

    @Test
    void shouldOverwriteRiskLevelUsingCompositeScore() {
        // Arrange
        RiskScore mockScore = mock(RiskScore.class);
        when(mockScore.toRiskLevel()).thenReturn(RiskLevel.CRITICAL);

        // Act
        // We pass RiskLevel.LOW, but the constructor should overwrite it with CRITICAL
        Verdict verdict = new Verdict(
                "v-123", "agent-456", RiskLevel.LOW, mockScore,
                List.of(), List.of(), "Test", RecommendedAction.NONE, Instant.now());

        // Assert
        assertEquals(RiskLevel.CRITICAL, verdict.riskLevel());
    }

    @Test
    void shouldThrowExceptionWhenRequiredFieldIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> new Verdict(
                null, "agent-456", RiskLevel.LOW, mock(RiskScore.class),
                List.of(), List.of(), "Test", RecommendedAction.NONE, Instant.now()), "verdictId cannot be null");
    }

    @Test
    void shouldMakeListsImmutable() {
        // Arrange
        List<ThreatCategory> mutableThreats = new ArrayList<>();
        mutableThreats.add(mock(ThreatCategory.class));

        Verdict verdict = new Verdict(
                "v-123", "agent-456", RiskLevel.LOW, mock(RiskScore.class),
                mutableThreats, List.of(), "Test", RecommendedAction.NONE, Instant.now());

        // Act & Assert
        // The record should hold a copy, so changes to the original list won't affect
        // it
        int initialSize = verdict.threats().size();
        mutableThreats.add(mock(ThreatCategory.class));
        assertEquals(initialSize, verdict.threats().size());

        // The returned list should be unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> verdict.threats().add(mock(ThreatCategory.class)));
    }
}
