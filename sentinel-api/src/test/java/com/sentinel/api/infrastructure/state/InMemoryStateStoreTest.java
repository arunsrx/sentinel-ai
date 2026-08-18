package com.sentinel.api.infrastructure.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class InMemoryStateStoreTest {

    private InMemoryStateStore stateStore;

    @BeforeEach
    void setUp() {
        stateStore = new InMemoryStateStore();
    }

    // Counter Tests
    @Test
    @DisplayName("Should increment counter and return new value")
    void testIncrementCounter() {
        long result = stateStore.incrementCounter("requests", 5);
        assertEquals(5, result);
    }

    @Test
    @DisplayName("Should accumulate multiple increments")
    void testMultipleIncrements() {
        stateStore.incrementCounter("requests", 3);
        stateStore.incrementCounter("requests", 2);
        long result = stateStore.incrementCounter("requests", 1);
        assertEquals(6, result);
    }

    @Test
    @DisplayName("Should return 0 for non-existent counter")
    void testGetNonExistentCounter() {
        long result = stateStore.getCounter("missing");
        assertEquals(0, result);
    }

    @ParameterizedTest(name = "Increment by {0} should result in {1}")
    @CsvSource({
            "1, 1",
            "10, 10",
            "100, 100",
            "-5, -5"
    })
    @DisplayName("Should handle various increment amounts")
    void testIncrementVariousAmounts(long amount, long expected) {
        long result = stateStore.incrementCounter("counter", amount);
        assertEquals(expected, result);
    }

    // Key-Value with Expiry Tests
    @Test
    @DisplayName("Should set and retrieve value")
    void testSetAndGetValue() {
        stateStore.setWithExpiry("key1", "value1", Duration.ofHours(1));
        Optional<String> result = stateStore.get("key1");
        assertTrue(result.isPresent());
        assertEquals("value1", result.get());
    }

    @Test
    @DisplayName("Should return empty optional for non-existent key")
    void testGetNonExistentKey() {
        Optional<String> result = stateStore.get("missing");
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return empty for expired value")
    void testExpiredValueReturnsEmpty() throws InterruptedException {
        stateStore.setWithExpiry("tempKey", "tempValue", Duration.ofMillis(100));
        Thread.sleep(150);
        Optional<String> result = stateStore.get("tempKey");
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should overwrite existing value")
    void testOverwriteValue() {
        stateStore.setWithExpiry("key", "value1", Duration.ofHours(1));
        stateStore.setWithExpiry("key", "value2", Duration.ofHours(1));
        assertEquals("value2", stateStore.get("key").get());
    }

    @Test
    @DisplayName("Should update TTL on expire call")
    void testExpireUpdatesExpiry() throws InterruptedException {
        stateStore.setWithExpiry("key", "value", Duration.ofMillis(200));
        Thread.sleep(150);
        stateStore.expire("key", Duration.ofSeconds(10));
        Optional<String> result = stateStore.get("key");
        assertTrue(result.isPresent());
        assertEquals("value", result.get());
    }

    @Test
    @DisplayName("Should not expire call on non-existent key")
    void testExpireNonExistentKeyDoesNothing() {
        stateStore.expire("missing", Duration.ofSeconds(1));
        assertFalse(stateStore.get("missing").isPresent());
    }

    // List Tests
    @Test
    @DisplayName("Should append value to list")
    void testAppendToList() {
        stateStore.appendToList("myList", "item1", 10);
        List<String> result = stateStore.getList("myList");
        assertEquals(1, result.size());
        assertEquals("item1", result.get(0));
    }

    @Test
    @DisplayName("Should return empty list for non-existent key")
    void testGetNonExistentList() {
        List<String> result = stateStore.getList("missing");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should maintain insertion order")
    void testListMaintainsOrder() {
        stateStore.appendToList("ordered", "first", 10);
        stateStore.appendToList("ordered", "second", 10);
        stateStore.appendToList("ordered", "third", 10);
        List<String> result = stateStore.getList("ordered");
        assertEquals(List.of("first", "second", "third"), result);
    }

    @Test
    @DisplayName("Should trim list to maxSize by removing oldest")
    void testListTrimsToMaxSize() {
        int maxSize = 3;
        stateStore.appendToList("bounded", "item1", maxSize);
        stateStore.appendToList("bounded", "item2", maxSize);
        stateStore.appendToList("bounded", "item3", maxSize);
        stateStore.appendToList("bounded", "item4", maxSize);

        List<String> result = stateStore.getList("bounded");
        assertEquals(3, result.size());
        assertEquals(List.of("item2", "item3", "item4"), result);
    }

    @Test
    @DisplayName("Should handle single item list")
    void testSingleItemList() {
        stateStore.appendToList("single", "only", 5);
        List<String> result = stateStore.getList("single");
        assertEquals(1, result.size());
        assertEquals("only", result.get(0));
    }

    @ParameterizedTest(name = "Max size {0} should retain exactly {0} items")
    @CsvSource({
            "1, 1",
            "5, 5",
            "10, 10"
    })
    @DisplayName("Should respect various maxSize limits")
    void testVariousMaxSizes(int maxSize, int expectedSize) {
        for (int i = 0; i < 20; i++) {
            stateStore.appendToList("list", "item" + i, maxSize);
        }
        assertEquals(expectedSize, stateStore.getList("list").size());
    }

    // Integration Tests
    @Test
    @DisplayName("Should isolate different data contexts")
    void testDataContextIsolation() {
        stateStore.incrementCounter("counter1", 5);
        stateStore.setWithExpiry("key1", "value1", Duration.ofHours(1));
        stateStore.appendToList("list1", "item1", 10);

        assertEquals(5, stateStore.getCounter("counter1"));
        assertEquals("value1", stateStore.get("key1").get());
        assertEquals(1, stateStore.getList("list1").size());
    }
}
