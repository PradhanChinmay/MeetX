import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: `${API_URL}/api`,
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const errorMessage = error.response?.data?.message || error.message || 'An error occurred';
    return Promise.reject(new Error(errorMessage));
  }
);

export const roomAPI = {
  createRoom: async (roomName) => {
    return api.post('/rooms', { name: roomName });
  },

  joinRoom: async (roomId) => {
    return api.post('/rooms/join', { roomId });
  },

  getRoomInfo: async (roomId) => {
    return api.get(`/rooms/${roomId}`);
  },

  getHealth: async () => {
    return api.get('/health');
  }
};

export default api;