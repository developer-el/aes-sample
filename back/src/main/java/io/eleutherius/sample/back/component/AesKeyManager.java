package io.eleutherius.sample.back.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class AesKeyManager {
    private final Map<String, String> keyMap = new ConcurrentHashMap<>();

    public String generateKey() {
        String uuid = UUID.randomUUID().toString();
        SecretKey key = generateAesKey();
        keyMap.put(uuid, Base64.getEncoder().encodeToString(key.getEncoded()));
        // 실제 운영환경에서는 Redis 등에 저장하고 TTL 적용
        return uuid;
    }

    public SecretKey getKey(String uuid) {
        final String encodedKey = keyMap.get(uuid);

        return new SecretKeySpec(Base64.getDecoder().decode(encodedKey), "AES");
    }

    private SecretKey generateAesKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES key generation failed", e);
        }
    }

    public String encrypt(String data, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = new byte[12];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            byte[] cipherText = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            byte[] combined = ByteBuffer.allocate(iv.length + cipherText.length)
                    .put(iv).put(cipherText).array();
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String getKeyBase64(String uuid) {
        SecretKey key = getKey(uuid);
        return key != null ? Base64.getEncoder().encodeToString(key.getEncoded()) : null;
    }
}
