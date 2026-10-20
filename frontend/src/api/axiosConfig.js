import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
});

// Attach JWT token automatically to every request if available
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('certifolio_token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle authentication expiration
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      const currentPath = window.location.pathname;
      if (!currentPath.includes('/login') && !currentPath.includes('/portfolio')) {
        localStorage.removeItem('certifolio_token');
        localStorage.removeItem('certifolio_user');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;
