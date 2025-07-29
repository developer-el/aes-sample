package io.eleutherius.sample.back.component;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test that verifies MapAesKeyManager is used with the default profile.
 */
@SpringBootTest
public class AesKeyManagerProfileTest {
    @Autowired
    private AesKeyManager aesKeyManager;

    @Test
    public void shouldUseMapAesKeyManagerWithDefaultProfile() {
        assertTrue(aesKeyManager instanceof MapAesKeyManager,
                "Default profile should use MapAesKeyManager, but was using " + aesKeyManager.getClass().getSimpleName());
    }
}