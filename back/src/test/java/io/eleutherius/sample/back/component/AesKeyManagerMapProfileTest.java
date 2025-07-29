package io.eleutherius.sample.back.component;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test that verifies MapAesKeyManager is used with the "map" profile.
 */
@SpringBootTest
@ActiveProfiles("map")
public class AesKeyManagerMapProfileTest {
    @Autowired
    private AesKeyManager aesKeyManager;

    @Test
    public void shouldUseMapAesKeyManagerWithMapProfile() {
        assertTrue(aesKeyManager instanceof MapAesKeyManager,
                "Map profile should use MapAesKeyManager, but was using " + aesKeyManager.getClass().getSimpleName());
    }
}