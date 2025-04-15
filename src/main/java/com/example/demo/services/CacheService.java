package com.example.demo.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Service class for managing data caching operations.
 * Implements caching functionality using Spring's caching annotations
 * and includes resilience patterns using Resilience4j.
 */
@Service
@Slf4j
@CacheConfig(cacheNames = "data")
public class CacheService {

    // In-memory data store to simulate database
    private final Map<String, String> dataStore = new HashMap<>();

    /**
     * Retrieves data from cache if available, otherwise fetches from data store.
     * Implements circuit breaker and retry patterns for resilience.
     * 
     * @param id The identifier for the data to retrieve
     * @return The cached data or null if not found
     */
    @Cacheable(key = "#id", unless = "#result == null")
    @CircuitBreaker(name = "cacheService", fallbackMethod = "getDataFallback")
    @Retry(name = "cacheService", fallbackMethod = "getDataFallback")
    public String getData(String id) {
        log.info("Cache miss for id: {}", id);
        simulateDatabaseCall();
        return dataStore.get(id);
    }

    /**
     * Retrieves data directly from data store without using cache.
     * 
     * @param id The identifier for the data to retrieve
     * @return The data or null if not found
     */
    public String getDataWithoutCache(String id) {
        log.info("No cache for id: {}", id);
        simulateDatabaseCall();
        return dataStore.get(id);
    }

    /**
     * Updates data in both cache and data store.
     * 
     * @param id The identifier for the data to update
     * @param data The new data value
     * @return The updated data
     */
    @CachePut(key = "#id")
    public String updateData(String id, String data) {
        log.info("Updating data for id: {}", id);
        dataStore.put(id, data);
        return data;
    }

    /**
     * Removes data from both cache and data store.
     * 
     * @param id The identifier for the data to delete
     */
    @CacheEvict(key = "#id")
    public void deleteData(String id) {
        log.info("Deleting data for id: {}", id);
        dataStore.remove(id);
    }

    /**
     * Clears all entries from both cache and data store.
     */
    @CacheEvict(allEntries = true)
    public void clearAllCache() {
        log.info("Clearing all cache entries");
        dataStore.clear();
    }

    /**
     * Fallback method for getData when circuit breaker or retry fails.
     * 
     * @param id The identifier for the data
     * @param e The exception that triggered the fallback
     * @return A fallback data value
     */
    public String getDataFallback(String id, Exception e) {
        log.warn("Fallback triggered for id: {}, error: {}", id, e.getMessage());
        return "Fallback data for " + id;
    }

    /**
     * Simulates database latency by adding a small delay.
     */
    private void simulateDatabaseCall() {
        try {
            // Simulate database latency
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 