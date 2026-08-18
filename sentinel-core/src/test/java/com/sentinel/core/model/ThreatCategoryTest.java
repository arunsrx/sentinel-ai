package com.sentinel.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ThreatCategoryTest {

    @ParameterizedTest(name = "Constant {0} should have description: \"{1}\"")
    // Use a pipe (|) as the delimiter so commas don't break the parsing
    @CsvSource(delimiter = '|', value = {
            "TOKEN_ABUSE | Excessive token consumption through loops or stuffing",
            "TOKEN_BUDGET_EXCEEDED | Exceeding allocated daily or hourly token budgets",
            "RUNAWAY_LOOP | Agent stuck producing identical outputs repeatedly",
            "PROMPT_INJECTION | Inputs designed to hijack other agents",
            "DATA_EXFILTRATION | Leaking sensitive data such as PII or keys in outputs",
            "TOXIC_OUTPUT | Harmful, biased, or unsafe content generated",
            "RATE_ANOMALY | Abnormal API call frequency",
            "BURST_ATTACK | Sudden spike in request volume",
            "IDENTITY_SPOOFING | Impersonating another agent or user",
            "GOAL_DEVIATION | Behavior drifting from declared purpose"
    })
    @DisplayName("Should verify each enum constant maps to its correct description")
    void testEnumDescriptions(String constantName, String expectedDescription) {
        // Look up the enum constant dynamically by its string name
        ThreatCategory category = ThreatCategory.valueOf(constantName);

        assertEquals(expectedDescription, category.description(),
                "The description for " + constantName + " does not match!");
    }

    @Test
    @DisplayName("Should contain exactly 10 threat categories")
    void testEnumSize() {
        // Guarantees no one accidentally deletes or adds a category without updating
        // the tests
        assertEquals(10, ThreatCategory.values().length,
                "The number of defined ThreatCategory values has changed");
    }
}
