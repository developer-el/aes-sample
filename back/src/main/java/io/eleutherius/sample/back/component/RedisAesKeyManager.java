package io.eleutherius.sample.back.component;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

/**
 * Redis-based implementation of AesKeyManager.
 * This is a placeholder for the future Redis implementation.
 * Currently, it has minimal implementation but will be updated
 * to use Redis for key storage in the future.
 */
@Slf4j
@Component
@Profile("redis")
public class RedisAesKeyManager extends AbstractAesKeyManager {
    // TODO: Replace with Redis client/template

    @PreDestroy
    public void shutdown() {
    }

    @Override
    protected void putKey(String uuid, SecretKey key) {
        // TODO: Store key in Redis with expiration
        // For now, just log that this method was called
        log.debug("RedisAesKeyManager.putKey called with UUID: {}", uuid);
    }

    @Override
    public SecretKey getKey(String uuid) {
        // TODO: Retrieve key from Redis
        // For now, return null to indicate key not found
        log.debug("RedisAesKeyManager.getKey called with UUID: {}", uuid);
        return null;
    }
}