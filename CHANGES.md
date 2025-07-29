# Changes Made to Implement Multi-Depth JSON Pointer Support for X-Encrypted-Fields

## Overview
This document describes the changes made to implement multi-depth JSON Pointer support for the X-Encrypted-Fields header in both the Spring Boot backend and Vue.js frontend. This allows for encryption and decryption of fields at any level of nesting in JSON structures, including arrays.

## What is JSON Pointer?
JSON Pointer is a string syntax for identifying a specific value within a JSON document, defined in [RFC 6901](https://tools.ietf.org/html/rfc6901). For example, "/name" would point to the "name" field in a JSON object, while "/result/info/name" would point to a nested field, and "/result/0/name" would point to a field in the first element of an array.

## Libraries Added

### Backend (Spring Boot)
- Added Jackson's `jackson-databind` library for JSON Pointer support

### Frontend (Vue.js)
- Added `json-pointer` library (version 0.6.2) for JSON Pointer operations

## Changes Made

### Backend (Spring Boot)

1. Added Jackson dependency to pom.xml:
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

2. Added new endpoints to SampleController.java to demonstrate multi-depth support:

- `/data/nested` - Returns a nested object with encrypted fields inside the "info" object:
```json
{
    "result": {
        "info": {
            "name": "암호화 대상",
            "phone": "암호화 대상"
        },
        "otherInfo": "1"
    }
}
```

- `/data/array` - Returns an array of objects with encrypted fields:
```json
{
    "result":[
        {
            "name": "암호화 대상",
            "phone": "암호화 대상"
        },
        {
            "name": "암호화 대상",
            "phone": "암호화 대상"
        },
        {
            "name": "암호화 대상",
            "phone": "암호화 대상"
        }
    ]
}
```

- `/data/array-nested` - Returns an array of objects with nested objects containing encrypted fields:
```json
{
    "result": [
        {
            "info": {
                "name": "암호화 대상",
                "phone": "암호화 대상"
            },
            "otherInfo": "1"
        },
        {
            "info": {
                "name": "암호화 대상",
                "phone": "암호화 대상"
            },
            "otherInfo": "1"
        },
        {
            "info": {
                "name": "암호화 대상",
                "phone": "암호화 대상"
            },
            "otherInfo": "1"
        }
    ]
}
```

3. Updated the X-Encrypted-Fields header to include JSON Pointers for nested fields:
```java
// For nested object
headers.add("X-Encrypted-Fields", "/result/info/name,/result/info/phone");

// For array of objects
headers.add("X-Encrypted-Fields", "/result/0/name,/result/0/phone,/result/1/name,/result/1/phone,/result/2/name,/result/2/phone");

// For array of objects with nested objects
headers.add("X-Encrypted-Fields", "/result/0/info/name,/result/0/info/phone,/result/1/info/name,/result/1/info/phone,/result/2/info/name,/result/2/info/phone");
```

### Frontend (Vue.js)

1. Added json-pointer library to package.json:
```json
"dependencies": {
  "axios": "^1.11.0",
  "json-pointer": "^0.6.2",
  "vue": "^3.5.18"
}
```

2. Updated the decryption logic in api.js to handle nested fields:
```javascript
import axios from 'axios';
import jsonpointer from 'json-pointer';

// ...

const decryptItem = async (item) => {
  const decryptedItem = JSON.parse(JSON.stringify(item)); // Deep clone to avoid modifying the original
  
  for (const pointer of fields) {
    try {
      // Check if the pointer exists in the object
      if (jsonpointer.has(decryptedItem, pointer)) {
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
```

3. Simplified the array handling in the response:
```javascript
// Process the data
const decryptedData = await decryptItem(response.data);

// Update the response data with the decrypted data
response.data = {
  data: decryptedData,
  encryptedData: encryptedData
};
```

## Testing
To test these changes:

1. Run the backend application:
   ```
   cd back
   ./mvnw spring-boot:run
   ```

2. Run the frontend application:
   ```
   cd front
   npm install
   npm run serve
   ```

3. Open the frontend application in a browser (usually at http://localhost:8081).

4. Test the different endpoints:
   - `/data` - Basic endpoint with top-level encrypted fields
   - `/data/nested` - Endpoint with nested encrypted fields
   - `/data/array` - Endpoint with an array of objects containing encrypted fields
   - `/data/array-nested` - Endpoint with an array of objects containing nested encrypted fields

5. Verify that all encrypted fields are correctly decrypted, regardless of their nesting level.