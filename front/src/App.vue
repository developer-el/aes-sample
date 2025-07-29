<template>
  <main>
    <button @click="fetchSecureUser()" style="padding: 10px 20px; font-size: 16px; margin: 10px;">sample 1</button>
    <button @click="fetchNestedData()" style="padding: 10px 20px; font-size: 16px; margin: 10px;">sample 2</button>
    <button @click="fetchArrayData()" style="padding: 10px 20px; font-size: 16px; margin: 10px;">sample 3</button>
    <button @click="fetchArrayNestedData()" style="padding: 10px 20px; font-size: 16px; margin: 10px;">sample 4</button>
    <div style="height: 500px; width: 1000px; overflow-y: auto; margin-bottom: 20px; margin-top: 20px; border: 1px solid white;">
      <h3>암호화된 응답:</h3>
      <pre>{{ responses.encrypted }}</pre>
    </div>
    <div style="height: 500px; width: 1000px; overflow-y: auto; border: 1px solid white;">
      <h3>복호화된 응답:</h3>
      <pre>{{ responses.decrypted }}</pre>
    </div>
  </main>
</template>

<script setup>
import api from './api';
import {reactive} from 'vue';

const responses = reactive({
  encrypted: {},
  decrypted: {}
});

async function fetchData(endpoint) {
  try {
    const res = await api.get(endpoint);
    responses.encrypted = {...res.data.encryptedData};
    responses.decrypted = res.data;
    delete responses.decrypted.encryptedData;
  } catch (error) {
    console.error('데이터 fetch 오류:', error);
  }
}

const fetchSecureUser = () => fetchData('/data');
const fetchNestedData = () => fetchData('/data/nested');
const fetchArrayData = () => fetchData('/data/array');
const fetchArrayNestedData = () => fetchData('/data/array-nested');

fetchSecureUser()
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