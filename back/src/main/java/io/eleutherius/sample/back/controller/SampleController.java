package io.eleutherius.sample.back.controller;

import io.eleutherius.sample.back.component.AesKeyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class SampleController {
    private final AesKeyManager aesKeyManager;

    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        String uuid = aesKeyManager.generateKey();
        SecretKey key = aesKeyManager.getKey(uuid);

        Map<String, Object> body = new HashMap<>();
        body.put("name", aesKeyManager.encrypt("홍길동", key));
        body.put("phone", aesKeyManager.encrypt("010-1234-5678", key));
        body.put("otherInfo", "이건 암호화 안 함");

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AES-UUID", uuid);
        headers.add("X-Encrypted-Fields", "name,phone");

        return ResponseEntity.ok().headers(headers).body(body);
    }

    @GetMapping("/aes-key/{uuid}")
    public ResponseEntity<String> getAesKey(@PathVariable String uuid) {
        String key = aesKeyManager.getKeyBase64(uuid);
        return key != null ? ResponseEntity.ok(key) : ResponseEntity.notFound().build();
    }
}
