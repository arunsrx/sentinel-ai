package com.sentinel.core.model;

public enum ThreatCategory {
    TOKEN_ABUSE("Excessive token consumption through loops or stuffing"),
    TOKEN_BUDGET_EXCEEDED("Exceeding allocated daily or hourly token budgets"),
    RUNAWAY_LOOP("Agent stuck producing identical outputs repeatedly"),
    PROMPT_INJECTION("Inputs designed to hijack other agents"),
    DATA_EXFILTRATION("Leaking sensitive data such as PII or keys in outputs"),
    TOXIC_OUTPUT("Harmful, biased, or unsafe content generated"),
    RATE_ANOMALY("Abnormal API call frequency"),
    BURST_ATTACK("Sudden spike in request volume"),
    IDENTITY_SPOOFING("Impersonating another agent or user"),
    GOAL_DEVIATION("Behavior drifting from declared purpose");

    private final String description;

    ThreatCategory(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

}
