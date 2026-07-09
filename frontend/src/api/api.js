import axios from 'axios';

const apiUrl = import.meta.env.VITE_API_URL || '';
const baseURL = `${apiUrl}/api`;
export const api = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem('url_shortener_token');

  if (token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});