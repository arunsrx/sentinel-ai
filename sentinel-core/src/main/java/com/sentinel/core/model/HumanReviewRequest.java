package com.sentinel.core.model;

import java.time.Instant;
import java.util.Objects;

public class HumanReviewRequest {

    private final String requestId;
    private final String agentId;
    private final String triggerReason;
    private final String findingsSummary;
    private final Instant createdAt;

    // Mutable lifecycle state
    private ReviewStatus status;
    private String reviewerNotes;
    private Instant resolvedAt;

    // Factory method driven by your @Tool flagForHumanReview requirement
    public static HumanReviewRequest flagForHumanReview(String requestId, String agentId, String reason,
            String findings) {
        return new HumanReviewRequest(requestId, agentId, reason, findings);
    }

    private HumanReviewRequest(String requestId, String agentId, String reason, String findings) {
        this.requestId = Objects.requireNonNull(requestId, "Request ID is mandatory");
        this.agentId = Objects.requireNonNull(agentId, "Agent ID is mandatory");

        if (reason == null || reason.strip().isEmpty()) {
            throw new IllegalArgumentException("A clear trigger reason must be provided for human review");
        }
        this.triggerReason = reason;
        this.findingsSummary = findings != null ? findings : "";
        this.status = ReviewStatus.PENDING;
        this.createdAt = Instant.now();
    }

    // Business Operation: Handle state mutations while protecting constraints
    public void resolve(ReviewStatus targetStatus, String notes) {
        Objects.requireNonNull(targetStatus, "Target resolution status cannot be null");
        if (targetStatus == ReviewStatus.PENDING) {
            throw new IllegalArgumentException("Cannot transition a resolved request back to PENDING status");
        }
        if (this.status != ReviewStatus.PENDING) {
            throw new IllegalStateException("This review request has already been finalized");
        }
        if (notes == null || notes.strip().isEmpty()) {
            throw new IllegalArgumentException("Reviewer notes/conclusions are mandatory upon resolution");
        }

        this.status = targetStatus;
        this.reviewerNotes = notes;
        this.resolvedAt = Instant.now();
    }

    // Getters
    public String getRequestId() {
        return requestId;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getTriggerReason() {
        return triggerReason;
    }

    public String getFindingsSummary() {
        return findingsSummary;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public String getReviewerNotes() {
        return reviewerNotes;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }
}
