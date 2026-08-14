package com.sentinel.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class RiskScoreTest {

    private static final String INVALID_RISK_SCORE_MESSAGE = "RiskScore value must be between 0.0 and 1.0 inclusive";

    // Compact constructor tests
    @Test
    void testConstructorWithMinBoundary() {
        RiskScore riskScore = new RiskScore(0.0);
        assertEquals(0.0, riskScore.value());
    }

    @Test
    void testConstructorWithMaxBoundary() {
        RiskScore riskScore = new RiskScore(1.0);
        assertEquals(1.0, riskScore.value());
    }

    @ParameterizedTest
    @ValueSource(doubles = { 0.25, 0.5, 0.75, 0.9 })
    void testConstructorWithValidValue(double value) {
        RiskScore riskScore = new RiskScore(value);
        assertEquals(value, riskScore.value());
    }

    @Test
    void testConstructorWithInvalidValueBelowMin() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new RiskScore(-0.1);
        });
        assertTrue(exception.getMessage().contains(INVALID_RISK_SCORE_MESSAGE));
    }

    @Test
    void testConstructorWithInvalidValueAboveMax() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new RiskScore(1.1);
        });
        assertTrue(exception.getMessage().contains(INVALID_RISK_SCORE_MESSAGE));
    }

    // test toRiskLevel method
    @Test
    void testToRiskLevel() {
        assertEquals(RiskLevel.SAFE, new RiskScore(0.0).toRiskLevel());
        assertEquals(RiskLevel.LOW, new RiskScore(0.2).toRiskLevel());
        assertEquals(RiskLevel.MEDIUM, new RiskScore(0.49).toRiskLevel());
        assertEquals(RiskLevel.HIGH, new RiskScore(0.7).toRiskLevel());
        assertEquals(RiskLevel.CRITICAL, new RiskScore(0.75).toRiskLevel());
    }

    @Test
    void testToRiskLevelBoundaryLowToMedium() {
        RiskScore lowBoundary = new RiskScore(0.25);
        assertEquals(RiskLevel.MEDIUM, lowBoundary.toRiskLevel());

        RiskScore justBelowMedium = new RiskScore(0.249);
        assertEquals(RiskLevel.LOW, justBelowMedium.toRiskLevel());
    }

    @Test
    void testToRiskLevelBoundaryMediumToHigh() {
        RiskScore highBoundary = new RiskScore(0.50);
        assertEquals(RiskLevel.HIGH, highBoundary.toRiskLevel());

        RiskScore justBelowHigh = new RiskScore(0.49);
        assertEquals(RiskLevel.MEDIUM, justBelowHigh.toRiskLevel());
    }

    @Test
    void testToRiskLevelBoundaryHighToCritical() {
        RiskScore criticalBoundary = new RiskScore(0.75);
        assertEquals(RiskLevel.CRITICAL, criticalBoundary.toRiskLevel());

        RiskScore justBelowCritical = new RiskScore(0.749);
        assertEquals(RiskLevel.HIGH, justBelowCritical.toRiskLevel());
    }

    @Test
    void testEqualsAndHashCode() {
        RiskScore score1 = new RiskScore(0.5);
        RiskScore score2 = new RiskScore(0.5);
        RiskScore score3 = new RiskScore(0.7);

        assertEquals(score1, score2);
        assertEquals(score1.hashCode(), score2.hashCode());
        assertNotEquals(score1, score3);
        assertEquals(score1, score1);
    }

    // Test accessor method
    @Test
    void testValueAccessor() {
        RiskScore riskScore = new RiskScore(0.3);
        assertEquals(0.3, riskScore.value());
    }
}
