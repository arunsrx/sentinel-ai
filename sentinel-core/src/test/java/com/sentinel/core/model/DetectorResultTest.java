package com.sentinel.core.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class DetectorResultTest {

    // Common valid test arguments
    private final String validName = "TokenAbuseDetector";
    private final ThreatCategory validCategory = ThreatCategory.TOKEN_ABUSE;

    @Test
    @DisplayName("Should successfully create DetectorResult with valid parameters")
    void testValidConstruction() {
        Map<String, Object> details = Map.of("key", "value");

        DetectorResult result = new DetectorResult(
                validName, validCategory, true, 0.85, "High token usage line 4", details);

        assertAll("Valid instance properties",
                () -> assertEquals(validName, result.detectorName()),
                () -> assertEquals(validCategory, result.category()),
                () -> assertTrue(result.triggered()),
                () -> assertEquals(0.85, result.confidence()),
                () -> assertEquals("High token usage line 4", result.evidence()),
                () -> assertEquals(details, result.details()));
    }

    @Test
    @DisplayName("Should throw NullPointerException when detectorName is null")
    void testNullDetectorNameThrowsException() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new DetectorResult(null, validCategory, true, 0.5, "evidence", Map.of()));
        assertEquals("detectorName cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NullPointerException when category is null")
    void testNullCategoryThrowsException() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new DetectorResult(validName, null, true, 0.5, "evidence", Map.of()));
        assertEquals("category cannot be null", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(doubles = { 0.0, 0.5, 1.0 })
    @DisplayName("Should allow confidence boundary values [0.0, 1.0]")
    void testValidConfidenceBoundaries(double validConfidence) {
        assertDoesNotThrow(
                () -> new DetectorResult(validName, validCategory, true, validConfidence, "evidence", Map.of()));
    }

    @ParameterizedTest
    @ValueSource(doubles = { -0.1, -10.0, 1.01, 5.0 })
    @DisplayName("Should throw IllegalArgumentException for invalid confidence levels")
    void testInvalidConfidenceThrowsException(double invalidConfidence) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new DetectorResult(validName, validCategory, true, invalidConfidence, "evidence", Map.of()));
        assertEquals("confidence must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    @DisplayName("Should normalize null details map to an empty immutable map")
    void testNullDetailsNormalizedToEmptyMap() {
        DetectorResult result = new DetectorResult(
                validName, validCategory, false, 0.0, null, null);

        assertNotNull(result.details(), "Details map should not be null");
        assertTrue(result.details().isEmpty(), "Details map should be empty");

        // Verify it is immutable
        assertThrows(UnsupportedOperationException.class, () -> result.details().put("newKey", "newValue"));
    }

    @Test
    @DisplayName("Should create defensive copy of details map to preserve immutability")
    void testDetailsMapIsDefensivelyCopied() {
        Map<String, Object> mutableMap = new HashMap<>();
        mutableMap.put("ip", "192.168.1.1");

        DetectorResult result = new DetectorResult(
                validName, validCategory, true, 0.9, "evidence", mutableMap);

        // 1. Modifying the original map should not mutate the internal state of the
        // record
        mutableMap.put("ip", "hacked");
        assertEquals("192.168.1.1", result.details().get("ip"),
                "Record state should not change when external map is modified");

        // 2. The returned map should be unmodifiable directly
        assertThrows(UnsupportedOperationException.class, () -> result.details().put("newKey", "newValue"));
    }

    @Test
    void testStaticFactory() {
        DetectorResult result = DetectorResult.safe(validName, validCategory);
        assertAll("Valid instance properties",
                () -> assertEquals(validName, result.detectorName()),
                () -> assertEquals(validCategory, result.category()),
                () -> assertFalse(result.triggered()),
                () -> assertEquals(0, result.confidence()),
                () -> assertNull(result.evidence()),
                () -> assertNotNull(result.details()));
    }

    @Test
    void testStaticFactoryNullDetectorNameThrowsException() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> DetectorResult.safe(null, validCategory));
        assertEquals("detectorName cannot be null", exception.getMessage());
    }
}
