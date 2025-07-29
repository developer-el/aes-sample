<template>
  <main>
    <button @click="fetchSecureUser()">사용자 정보 가져오기</button>
    <div v-if="responses.encrypted">
      <h3>암호화된 응답:</h3>
      <pre>{{ responses.encrypted }}</pre>
    </div>
    <div v-if="responses.decrypted">
      <h3>복호화된 응답:</h3>
      <pre>{{ responses.decrypted }}</pre>
    </div>
  </main>
</template>

<script setup>
import axios from 'axios';
import {reactive} from 'vue';

const responses = reactive({
  encrypted: null,
  decrypted: null
});

async function decryptAES(encryptedBase64, base64Key) {
  // Base64 디코딩
  const combined = Uint8Array.from(atob(encryptedBase64), c => c.charCodeAt(0));
  const iv = combined.slice(0, 12);
  const data = combined.slice(12);

  // Key 가져오기
  const keyBytes = Uint8Array.from(atob(base64Key), c => c.charCodeAt(0));
  const cryptoKey = await window.crypto.subtle.importKey(
      'raw',
      keyBytes,
      { name: 'AES-GCM' },
      false,
      ['decrypt']
  );

  // 복호화
  const decrypted = await window.crypto.subtle.decrypt(
      { name: 'AES-GCM', iv: iv },
      cryptoKey,
      data
  );

  return new TextDecoder().decode(decrypted);
}

async function fetchSecureUser() {
  try {
    const res = await axios.get('http://localhost:8080/data');
    const uuid = res.headers['x-aes-uuid'];
    const encFields = res.headers['x-encrypted-fields'].split(',');

    // Spring Boot 서버에서 AES key 요청
    const keyRes = await axios.get(`http://localhost:8080/aes-key/${uuid}`);
    const aesKey = keyRes.data;

    responses.encrypted = {...res.data};
    const decryptedData = {...res.data};
    for (const field of encFields) {
      decryptedData[field] = await decryptAES(res.data[field], aesKey);
    }

    responses.decrypted = decryptedData;
  } catch (error) {
    console.error('데이터 fetch 오류:', error);
  }
}
</script>

<style scoped>
header {
  line-height: 1.5;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }
}
</style>
