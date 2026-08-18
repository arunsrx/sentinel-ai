package com.sentinel.core.model;

public record EnforcementResult(boolean isAccepted, String reason) {

    /**
     * Factory method for a successful enforcement result.
     */
    public static EnforcementResult accepted() {
        return new EnforcementResult(true, "Accepted");
    }

    /**
     * Factory method for a rejected enforcement result with a reason.
     */
    public static EnforcementResult rejected(String reason) {
        java.util.Objects.requireNonNull(reason, "reason cannot be null");
        return new EnforcementResult(false, reason);
    }
}
