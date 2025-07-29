package io.eleutherius.sample.back.component;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test that verifies RedisAesKeyManager is used with the "redis" profile.
 */
@SpringBootTest
@ActiveProfiles("redis")
public class AesKeyManagerRedisProfileTest {
    @Autowired
    private AesKeyManager aesKeyManager;

    @Test
    public void shouldUseRedisAesKeyManagerWithRedisProfile() {
        assertTrue(aesKeyManager instanceof RedisAesKeyManager,
                "Redis profile should use RedisAesKeyManager, but was using " + aesKeyManager.getClass().getSimpleName());
    }
}