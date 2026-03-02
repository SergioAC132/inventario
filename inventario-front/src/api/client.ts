import axios from 'axios';

const client = axios.create({
    baseURL: 'http://192.168.100.65:8080/api',
});

export const setAuthHeader = (username: string, password: string) => {
    const token = btoa(`${username}:${password}`);
    client.defaults.headers.common['Authorization'] = `Basic ${token}`;
    localStorage.setItem('auth', token);
};

export const clearAuthHeader = () => {
    delete client.defaults.headers.common['Authorization'];
    localStorage.removeItem('auth');
};

export const loadAuthFromStorage = () => {
    const token = localStorage.getItem('auth');
    if (token) {
        client.defaults.headers.common['Authorization'] = `Basic ${token}`;
    }
};

client.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401 && window.location.pathname !== '/login') {
            clearAuthHeader();
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default client;