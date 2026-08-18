package com.sentinel.core.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Investigation {
    // Immutable identity and initial context
    private final String investigationId;
    private final String agentId;
    private final String triggerReason;
    private final Instant startedAt;

    // Mutable lifecycle fields
    private InvestigationStatus status;
    private final List<InvestigationFinding> findings;
    private String conclusion;
    private RecommendedAction finalAction;
    private Instant closedAt;

    // Corrected Constructor (Creation Invariants)
    public Investigation(String investigationId, String agentId, String triggerReason, Instant startedAt) {
        this.investigationId = Objects.requireNonNull(investigationId, "Investigation ID is mandatory");
        this.agentId = Objects.requireNonNull(agentId, "Agent ID is mandatory");
        this.startedAt = Objects.requireNonNull(startedAt, "Start timestamp is mandatory");

        if (triggerReason == null || triggerReason.strip().isEmpty()) {
            throw new IllegalArgumentException("Trigger reason cannot be blank");
        }
        this.triggerReason = triggerReason;

        this.status = InvestigationStatus.OPEN;
        this.findings = new ArrayList<>();
    }

    // Business Method: Adding point-in-time audit findings
    public void addFinding(InvestigationFinding finding) {
        Objects.requireNonNull(finding, "Finding cannot be null");
        if (this.status == InvestigationStatus.CLOSED) {
            throw new IllegalStateException("Cannot add findings to a closed investigation");
        }
        this.findings.add(finding);
    }

    // Business Method: Safe Closure & Lifecycle Transition
    public void close(String conclusion, RecommendedAction finalAction, Instant closedAt) {
        Objects.requireNonNull(finalAction, "A final mitigation action must be specified upon closing");
        Objects.requireNonNull(closedAt, "Closure timestamp is mandatory");

        if (this.status == InvestigationStatus.CLOSED) {
            throw new IllegalStateException("Investigation is already closed");
        }
        if (closedAt.isBefore(this.startedAt)) {
            throw new IllegalArgumentException("Closure time cannot be before the start time");
        }
        if (conclusion == null || conclusion.strip().isEmpty()) {
            throw new IllegalArgumentException("A closing conclusion summary is required");
        }

        this.status = InvestigationStatus.CLOSED;
        this.conclusion = conclusion;
        this.finalAction = finalAction;
        this.closedAt = closedAt;
    }

    // Getters
    public String getInvestigationId() {
        return investigationId;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getTriggerReason() {
        return triggerReason;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public InvestigationStatus getStatus() {
        return status;
    }

    public String getConclusion() {
        return conclusion;
    }

    public RecommendedAction getFinalAction() {
        return finalAction;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    // Encapsulation protection for shared list
    public List<InvestigationFinding> getFindings() {
        return Collections.unmodifiableList(findings);
    }
}
