package com.apic.minilinks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    private static final String REDIS_BUCKET_KEY = "ratelimiter:";
    private static final int BUCKET_CAPACITY = 5;
    private static final int REFILL_RATE = 1; // Tokens per second

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean allowRequest(String ipAddress) {
        long currentTime = System.currentTimeMillis();

        // Get the current number of tokens in the bucket and the timestamp
        String tokensStr = redisTemplate.opsForValue().get(REDIS_BUCKET_KEY + ipAddress);
        if (tokensStr == null) {
            // If no entry exists in Redis for this IP, set the initial token count and timestamp
            tokensStr = BUCKET_CAPACITY + ":" + currentTime;
            redisTemplate.opsForValue().set(REDIS_BUCKET_KEY + ipAddress, tokensStr);
        }

        // Parse the stored tokens and timestamp from the Redis value
        long storedTokens = Long.parseLong(tokensStr.split(":")[0]);
        long storedTimestamp = Long.parseLong(tokensStr.split(":")[1]);

        // Calculate the time elapsed since the last update
        long timeElapsedSeconds = (currentTime / 1000) - (storedTimestamp / 1000);

        // Calculate the number of tokens to refill since the last update
        long refillTokens = Math.min(REFILL_RATE * timeElapsedSeconds, BUCKET_CAPACITY);

        // Refill the bucket if necessary
        long currentTokens = Math.min(storedTokens + refillTokens, BUCKET_CAPACITY);

        System.out.println("currentTokens:" + String.valueOf(currentTokens));
        if (currentTokens > 0) {
            // Allow the request and deduct one token from the bucket
            currentTokens -= 1;

            // Update the bucket with the new token count and timestamp
            String newTokensStr = currentTokens + ":" + currentTime;
            redisTemplate.opsForValue().set(REDIS_BUCKET_KEY + ipAddress, newTokensStr);
            return true;
        } else {
            // Reject the request due to rate-limit exceeded
            return false;
        }
    }
}
