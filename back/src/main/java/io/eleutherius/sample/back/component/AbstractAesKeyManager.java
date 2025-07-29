package io.eleutherius.sample.back.component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * Abstract base class for AesKeyManager implementations.
 * Contains common encryption logic to avoid duplication in concrete implementations.
 */
public abstract class AbstractAesKeyManager implements AesKeyManager {

    /**
     * Template method for generating a new key.
     * This method handles the common logic of generating a UUID and AES key,
     * while delegating the storage to concrete implementations.
     *
     * @return UUID string for the generated key
     */
    @Override
    public String generateKey() {
        String uuid = UUID.randomUUID().toString();
        SecretKey key = generateAesKey();
        putKey(uuid, key);
        return uuid;
    }

    /**
     * Abstract method to be implemented by concrete classes for storing a key.
     *
     * @param uuid The UUID to associate with the key
     * @param key The SecretKey to store
     */
    protected abstract void putKey(String uuid, SecretKey key);

    /**
     * Abstract method to be implemented by concrete classes for retrieving a key.
     *
     * @param uuid The UUID of the key to retrieve
     * @return The SecretKey, or null if not found
     */
    @Override
    public abstract SecretKey getKey(String uuid);

    /**
     * Generates a new AES key.
     *
     * @return A new SecretKey for AES encryption
     */
    protected SecretKey generateAesKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES key generation failed", e);
        }
    }

    /**
     * Encrypts data using the specified key.
     * This implementation uses AES/GCM/NoPadding for secure encryption.
     *
     * @param data The data to encrypt
     * @param key The key to use for encryption
     * @return Base64-encoded encrypted data
     */
    @Override
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

    /**
     * Gets the Base64-encoded string representation of a key by its UUID.
     *
     * @param uuid The UUID of the key
     * @return Base64-encoded key, or null if not found
     */
    @Override
    public String getKeyBase64(String uuid) {
        SecretKey key = getKey(uuid);
        return key != null ? Base64.getEncoder().encodeToString(key.getEncoded()) : null;
    }
}