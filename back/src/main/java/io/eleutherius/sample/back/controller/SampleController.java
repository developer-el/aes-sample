package io.eleutherius.sample.back.controller;

import io.eleutherius.sample.back.component.AesKeyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
@RestController
public class SampleController {
    private final AesKeyManager aesKeyManager;

    private static final String[] FAMILY_NAMES = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "임"};
    private static final String[] GIVEN_NAMES = {"민준", "서준", "도윤", "예준", "시우", "지호", "주원", "지훈", "도현", "지민",
            "서연", "서윤", "지우", "하은", "하윤", "민서", "지유", "채원", "수아", "지민"};

    private String generateRandomPhoneNumber() {
        Random random = new SecureRandom();
        return String.format("010-%04d-%04d",
                random.nextInt(10000),
                random.nextInt(10000));
    }

    private String generateRandomName() {
        Random random = new SecureRandom();
        String familyName = FAMILY_NAMES[random.nextInt(FAMILY_NAMES.length)];
        String givenName = GIVEN_NAMES[random.nextInt(GIVEN_NAMES.length)];
        return familyName + givenName;
    }

    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        String uuid = aesKeyManager.generateKey();
        SecretKey key = aesKeyManager.getKey(uuid);

        Map<String, Object> body = new HashMap<>();
        body.put("name", aesKeyManager.encrypt(generateRandomName(), key));
        body.put("phone", aesKeyManager.encrypt(generateRandomPhoneNumber(), key));
        body.put("otherInfo", "이건 암호화 안 함");

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AES-UUID", uuid);
        headers.add("X-Encrypted-Fields", "/name,/phone");

        return ResponseEntity.ok().headers(headers).body(body);
    }
    
    // Example 1: Nested object with encrypted fields
    @GetMapping("/data/nested")
    public ResponseEntity<Map<String, Object>> getNestedUserInfo() {
        String uuid = aesKeyManager.generateKey();
        SecretKey key = aesKeyManager.getKey(uuid);

        Map<String, Object> info = new HashMap<>();
        info.put("name", aesKeyManager.encrypt(generateRandomName(), key));
        info.put("phone", aesKeyManager.encrypt(generateRandomPhoneNumber(), key));
        
        Map<String, Object> body = new HashMap<>();
        body.put("result", Map.of(
            "info", info,
            "otherInfo", "1"
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AES-UUID", uuid);
        headers.add("X-Encrypted-Fields", "/result/info/name,/result/info/phone");

        return ResponseEntity.ok().headers(headers).body(body);
    }
    
    // Example 2: Array of objects with encrypted fields
    @GetMapping("/data/array")
    public ResponseEntity<Map<String, Object>> getArrayUserInfo() {
        String uuid = aesKeyManager.generateKey();
        SecretKey key = aesKeyManager.getKey(uuid);

        List<Map<String, Object>> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Map<String, Object> user = new HashMap<>();
            user.put("name", aesKeyManager.encrypt(generateRandomName(), key));
            user.put("phone", aesKeyManager.encrypt(generateRandomPhoneNumber(), key));
            users.add(user);
        }
        
        Map<String, Object> body = new HashMap<>();
        body.put("result", users);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AES-UUID", uuid);
        headers.add("X-Encrypted-Fields", "/result/0/name,/result/0/phone,/result/1/name,/result/1/phone,/result/2/name,/result/2/phone");

        return ResponseEntity.ok().headers(headers).body(body);
    }
    
    // Example 3: Array of objects with nested objects containing encrypted fields
    @GetMapping("/data/array-nested")
    public ResponseEntity<Map<String, Object>> getArrayNestedUserInfo() {
        String uuid = aesKeyManager.generateKey();
        SecretKey key = aesKeyManager.getKey(uuid);

        List<Map<String, Object>> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Map<String, Object> info = new HashMap<>();
            info.put("name", aesKeyManager.encrypt(generateRandomName(), key));
            info.put("phone", aesKeyManager.encrypt(generateRandomPhoneNumber(), key));
            
            Map<String, Object> user = new HashMap<>();
            user.put("info", info);
            user.put("otherInfo", "1");
            users.add(user);
        }
        
        Map<String, Object> body = new HashMap<>();
        body.put("result", users);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AES-UUID", uuid);
        headers.add("X-Encrypted-Fields", "/result/0/info/name,/result/0/info/phone,/result/1/info/name,/result/1/info/phone,/result/2/info/name,/result/2/info/phone");

        return ResponseEntity.ok().headers(headers).body(body);
    }

    @GetMapping("/aes-key/{uuid}")
    public ResponseEntity<Map<String, Object>> getAesKey(@PathVariable String uuid) {
        String key = aesKeyManager.getKeyBase64(uuid);
        return key != null ? ResponseEntity.ok(Map.of("key", key)) : ResponseEntity.notFound().build();
    }
}
