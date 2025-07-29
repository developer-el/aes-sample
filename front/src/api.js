import axios from 'axios';
import jsonpointer from 'jsonpointer';

// Create a custom axios instance
const api = axios.create({
  baseURL: 'http://localhost:8080',
});

// Decryption function
async function decryptAES(encryptedBase64, base64Key) {
  const combined = Uint8Array.from(atob(encryptedBase64), c => c.charCodeAt(0));
  const iv = combined.slice(0, 12);
  const data = combined.slice(12);

  const keyBytes = Uint8Array.from(atob(base64Key), c => c.charCodeAt(0));
  const cryptoKey = await window.crypto.subtle.importKey(
    'raw',
    keyBytes,
    {name: 'AES-GCM'},
    false,
    ['decrypt']
  );

  const decrypted = await window.crypto.subtle.decrypt(
    {name: 'AES-GCM', iv: iv},
    cryptoKey,
    data
  );

  return new TextDecoder().decode(decrypted);
}

// Response interceptor for decryption
api.interceptors.response.use(async (response) => {
  try {
    // Check if the response has the necessary headers for decryption
    const uuid = response.headers['x-aes-uuid'];
    const encryptedFields = response.headers['x-encrypted-fields'];

    const encryptedData = {...response.data}

    if (uuid && encryptedFields) {
      // Get the list of encrypted fields
      const fields = encryptedFields.split(',');

      // Get the AES key using the baseURL from our api instance
      // Using axios directly to avoid triggering this interceptor again
      const keyResponse = await axios.get(`${api.defaults.baseURL}/aes-key/${uuid}`);
      const aesKey = keyResponse.data.key;

      const decryptItem = async (item) => {
        const decryptedItem = JSON.parse(JSON.stringify(item)); // Deep clone to avoid modifying the original
        
        for (const pointer of fields) {
          try {
            // Check if the pointer exists in the object
            if (jsonpointer.get(decryptedItem, pointer) !== undefined) {
              // Get the encrypted value
              const encryptedValue = jsonpointer.get(decryptedItem, pointer);
              
              // Decrypt the value
              const decryptedValue = await decryptAES(encryptedValue, aesKey);
              
              // Set the decrypted value back in the object
              jsonpointer.set(decryptedItem, pointer, decryptedValue);
            }
          } catch (error) {
            console.error(`Failed to process field with pointer '${pointer}':`, error);
          }
        }
        
        return decryptedItem;
      };

// Process the data
      const decryptedData = await decryptItem(response.data);

      // Update the response data with the decrypted data
      response.data = {
        data: decryptedData,
        encryptedData: encryptedData
      };
    }
  } catch (error) {
    console.error('Error in decryption interceptor:', error);
    // Return the original response even if decryption fails
  }

  return response;
}, (error) => {
  return Promise.reject(error);
});

export default api;