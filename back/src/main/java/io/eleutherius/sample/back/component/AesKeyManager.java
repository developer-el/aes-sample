package io.eleutherius.sample.back.component;

import javax.crypto.SecretKey;

/**
 * Interface for managing AES encryption keys.
 * This facade provides methods for generating, retrieving, and using encryption keys.
 */
public interface AesKeyManager {
    
    /**
     * Generates a new AES key and returns its UUID.
     * 
     * @return UUID string for the generated key
     */
    String generateKey();
    
    /**
     * Retrieves a SecretKey by its UUID.
     * 
     * @param uuid The UUID of the key to retrieve
     * @return The SecretKey, or null if not found
     */
    SecretKey getKey(String uuid);
    
    /**
     * Encrypts data using the specified key.
     * 
     * @param data The data to encrypt
     * @param key The key to use for encryption
     * @return Base64-encoded encrypted data
     */
    String encrypt(String data, SecretKey key);
    
    /**
     * Gets the Base64-encoded string representation of a key by its UUID.
     * 
     * @param uuid The UUID of the key
     * @return Base64-encoded key, or null if not found
     */
    String getKeyBase64(String uuid);
}