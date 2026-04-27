import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  timeout: 30000
});

export async function analyzeCode({ code, language, file }) {
  if (file) {
    const formData = new FormData();
    formData.append('file', file);
    if (code) {
      formData.append('code', code);
    }
    if (language) {
      formData.append('language', language);
    }

    const response = await api.post('/api/analyze', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    return response.data;
  }

  const response = await api.post('/api/analyze', {
    code,
    language
  });

  return response.data;
}

export async function fetchHistory() {
  const response = await api.get('/api/history');
  return response.data;
}
