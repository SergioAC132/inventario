import axios from 'axios';

const client = axios.create({
    baseURL: 'http://localhost:8080/api',
});

export const setAuthToken = (token: string) => {
    sessionStorage.setItem('token', token);
    client.defaults.headers.common['Authorization'] = `Bearer ${token}`;
};

export const clearAuthToken = () => {
    delete client.defaults.headers.common['Authorization'];
    sessionStorage.removeItem('token');
};

export const loadAuthFromStorage = () => {
    const token = sessionStorage.getItem('token');
    if (token) {
        client.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }
};

client.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401 && window.location.pathname !== '/login') {
            clearAuthToken();
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default client;
