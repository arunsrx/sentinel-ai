package com.sentinel.core.model;

import java.time.Instant;
import java.util.Objects;

public class LongTermMemoryEntry {

    private final String memoryId;
    private final MemoryType type;
    private final String relatedAgentId;
    private final String content;
    private final double importance; // Bounds: 0.0 to 1.0
    private final Instant createdAt;

    // Mutable usage tracking parameters
    private Instant lastAccessed;
    private int accessCount;

    public LongTermMemoryEntry(String memoryId, MemoryType type, String relatedAgentId, String content,
            double importance) {
        this.memoryId = Objects.requireNonNull(memoryId, "Memory entry ID is required");
        this.type = Objects.requireNonNull(type, "Memory type must be specified");
        this.relatedAgentId = Objects.requireNonNull(relatedAgentId, "Related agent ID is mandatory");

        if (content == null || content.strip().isEmpty()) {
            throw new IllegalArgumentException("Memory payload content cannot be empty");
        }
        this.content = content;

        if (importance < 0.0 || importance > 1.0) {
            throw new IllegalArgumentException(
                    "Memory importance must be bounded between 0.0 and 1.0. Given: " + importance);
        }
        this.importance = importance;

        this.createdAt = Instant.now();
        this.lastAccessed = this.createdAt;
        this.accessCount = 0; // Starts unread upon immediate insertion
    }

    // Business Operation: Explicitly records memory access and retrieval trends
    public void trackAccess() {
        this.accessCount++;
        this.lastAccessed = Instant.now();
    }

    // Getters
    public String getMemoryId() {
        return memoryId;
    }

    public MemoryType getType() {
        return type;
    }

    public String getRelatedAgentId() {
        return relatedAgentId;
    }

    public String getContent() {
        return content;
    }

    public double getImportance() {
        return importance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastAccessed() {
        return lastAccessed;
    }

    public int getAccessCount() {
        return accessCount;
    }
}
