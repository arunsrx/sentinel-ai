package com.sentinel.api.infrastructure.state;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.sentinel.core.port.StateStore;

/**
 * In-memory implementation of the StateStore interface.
 * Single State Store Instance that provides unified access,
 * but separates data logically underneath using specific ConcurrentHashMap
 * instances.
 * Detector 1 can be reading a window for agent_A from the window map.
 * Detector 2 can be incrementing a counter for agent_B in the counter map.
 * 
 */
public class InMemoryStateStore implements StateStore {

    // Internal data structures
    // Context 1: Counter tracking
    private final ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();
    // Context 2: TTL Key-Value tracking
    private final ConcurrentHashMap<String, ExpiringValue> keyValueStore = new ConcurrentHashMap<>();
    // Context 3: Rolling Window tracking
    private final ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> listStore = new ConcurrentHashMap<>();

    // TTL management
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "state-store-expiry-worker");
        thread.setDaemon(true);
        return thread;
    });

    public InMemoryStateStore() {
        // Run expiry cleanup every 30 seconds
        this.scheduler.scheduleAtFixedRate(this::evictExpiredEntries, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public long incrementCounter(String key, long amount) {
        return counters.computeIfAbsent(key, k -> new AtomicLong(0)).addAndGet(amount);
    }

    @Override
    public long getCounter(String key) {
        AtomicLong counter = counters.get(key);
        return counter != null ? counter.get() : 0L;
    }

    @Override
    public void setWithExpiry(String key, String value, Duration ttl) {
        Instant expiresAt = Instant.now().plus(ttl);
        keyValueStore.put(key, new ExpiringValue(value, expiresAt));
    }

    @Override
    public Optional<String> get(String key) {
        ExpiringValue expiringValue = keyValueStore.get(key);
        if (expiringValue == null) {
            return Optional.empty();
        }
        if (expiringValue.isExpired()) {
            keyValueStore.remove(key); // Proactive removal
            return Optional.empty();
        }
        return Optional.of(expiringValue.value());
    }

    @Override
    public void appendToList(String key, String value, int maxSize) {
        ConcurrentLinkedDeque<String> deque = listStore.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());
        deque.addLast(value);

        // Trim to maxSize
        while (deque.size() > maxSize) {
            deque.pollFirst();
        }
    }

    @Override
    public List<String> getList(String key) {
        ConcurrentLinkedDeque<String> deque = listStore.get(key);
        if (deque == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(deque);
    }

    @Override
    public void expire(String key, Duration ttl) {
        ExpiringValue existing = keyValueStore.get(key);
        if (existing != null) {
            setWithExpiry(key, existing.value(), ttl);
        }
    }

    /**
     * Periodic background task to evict stale entries.
     */
    private void evictExpiredEntries() {
        keyValueStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * Inner record representing an expiring value wrapper.
     */
    private record ExpiringValue(String value, Instant expiresAt) {
        public boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
