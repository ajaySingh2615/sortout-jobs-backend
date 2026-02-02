package com.cadt.sortoutjobbackend.common.ratelimit;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    // Store: key -> [requestCount, windowStartTime]
    private final Map<String, long[]> requestCounts = new ConcurrentHashMap<>();

    /**
     * Check if request is allowed.
     *
     * @param key           - Unique identifier (e.g., "login:192.168.1.1" or "otp:+919876543210")
     * @param maxRequests   - Max allowed requests in window
     * @param windowSeconds - Time window in seconds
     * @return true if allowed, false if rate limited
     */
    public boolean isAllowed(String key, int maxRequests, int windowSeconds) {
        long now = Instant.now().getEpochSecond();

        requestCounts.compute(key, (k, data) -> {
            if (data == null) {
                // First request - create new window
                return new long[]{1, now};
            }

            long count = data[0];
            long windowStart = data[1];

            // check if window has expired
            if (now - windowStart >= windowSeconds) {
                // new window - reset count
                return new long[]{1, now};
            }

            // Same window - increment count
            return new long[]{count + 1, windowStart};
        });

        long[] data = requestCounts.get(key);
        return data[0] <= maxRequests;
    }

    /**
     * Get remaining requests in current window
     */
    public int getRemainingRequests(String key, int maxRequests, int windowSeconds) {
        long[] data = requestCounts.get(key);
        if (data == null) return maxRequests;

        long now = Instant.now().getEpochSecond();
        if (now - data[1] >= windowSeconds) return maxRequests;

        return Math.max(0, maxRequests - (int) data[0]);
    }

    /**
     * Get seconds until window resets
     */
    public long getSecondsUntilReset(String key, int windowSeconds) {
        long[] data = requestCounts.get(key);

        if (data == null) return 0;

        long windowStart = data[1];
        long now = Instant.now().getEpochSecond();
        long elapsed = now - windowStart;

        return Math.max(0, windowSeconds - elapsed);
    }

    /**
     * Clear old entries (call periodically to prevent memory leak)
     */
    public void cleanup(int windowSeconds) {
        long now = Instant.now().getEpochSecond();
        requestCounts.entrySet().removeIf(entry ->
                now - entry.getValue()[1] > windowSeconds * 2L
        );
    }
}
