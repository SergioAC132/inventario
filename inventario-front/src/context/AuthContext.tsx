import { createContext, useContext, useState, useEffect} from 'react';
import type { ReactNode } from 'react';
import type { AuthContextType, Usuario } from '../types/auth';
import { setAuthToken, clearAuthToken, loadAuthFromStorage } from '../api/client';
import client from '../api/client';

const AuthContext = createContext<AuthContextType | null>(null);
export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [usuario, setUsuario] = useState<Usuario | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadAuthFromStorage();
        const stored = sessionStorage.getItem('usuario');
        if (stored) {
            setUsuario(JSON.parse(stored));
        }
        setLoading(false);
    }, []);

    const login = async (username: string, password: string) => {
        const response = await client.post('/auth/login', { username, password });
        const { token, usuario: user } = response.data.data;
        setAuthToken(token);
        setUsuario(user);
        sessionStorage.setItem('usuario', JSON.stringify(user));
    };

    const logout = () => {
        clearAuthToken();
        sessionStorage.removeItem('usuario');
        setUsuario(null);
    };

    if (loading) return null;

    return (
        <AuthContext.Provider value={{ usuario, login, logout, isAuthenticated: !!usuario, loading }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) throw new Error('useAuth debe usarse dentro de AuthProvider');
    return context;
};
