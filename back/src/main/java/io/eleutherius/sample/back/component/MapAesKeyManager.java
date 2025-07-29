package io.eleutherius.sample.back.component;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Map-based implementation of AesKeyManager.
 * Uses a ConcurrentHashMap to store keys in memory.
 */
@Component
@Profile({"default", "map"})
public class MapAesKeyManager extends AbstractAesKeyManager {
    private final Map<String, String> keyMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }

    @Override
    protected void putKey(String uuid, SecretKey key) {
        keyMap.put(uuid, Base64.getEncoder().encodeToString(key.getEncoded()));
        scheduler.schedule(() -> keyMap.remove(uuid), 600, TimeUnit.SECONDS);
    }

    @Override
    public SecretKey getKey(String uuid) {
        final String encodedKey = keyMap.get(uuid);
        if (encodedKey == null) {
            return null;
        }
        return new SecretKeySpec(Base64.getDecoder().decode(encodedKey), "AES");
    }
}