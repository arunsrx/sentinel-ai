package com.sentinel.core.port;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface StateStore {
    // Supports negative values for decrementing and changed to delta from amount
    // argument name for clarity
    long incrementCounter(String key, long delta);

    long getCounter(String key);

    // Changed long ttl to Duration for explicit time safety
    void setWithExpiry(String key, String value, Duration ttl);

    Optional<String> get(String key);

    void appendToList(String key, String value, int maxSize);

    List<String> getList(String key);

    // Changed long ttl to Duration
    void expire(String key, Duration ttl);
}
