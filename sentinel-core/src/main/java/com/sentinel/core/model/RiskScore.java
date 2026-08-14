package com.sentinel.core.model;

public record RiskScore(double value) {
    public RiskScore {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("RiskScore value must be between 0.0 and 1.0 inclusive");
        }
    }

    public RiskLevel toRiskLevel() {
        if (value == 0.0) {
            return RiskLevel.SAFE;
        } else if (value > 0.0 && value < 0.25) {
            return RiskLevel.LOW;
        } else if (value >= 0.25 && value < 0.50) {
            return RiskLevel.MEDIUM;
        } else if (value >= 0.50 && value < 0.75) {
            return RiskLevel.HIGH;
        } else {
            return RiskLevel.CRITICAL;
        }
    }
}
