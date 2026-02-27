import { createContext, useContext, useState, useEffect} from 'react';
import type { ReactNode } from 'react';
import type { AuthContextType, Usuario } from '../types/auth';
import { setAuthHeader, clearAuthHeader, loadAuthFromStorage } from '../api/client';
import client from '../api/client';

const AuthContext = createContext<AuthContextType | null>(null);
export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [usuario, setUsuario] = useState<Usuario | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadAuthFromStorage();
        const stored = localStorage.getItem('usuario');
        if (stored) {
            setUsuario(JSON.parse(stored));
        }
        setLoading(false);

        const handleBeforeUnload = () => {
            clearAuthHeader();
            localStorage.removeItem('auth');
            localStorage.removeItem('usuario');
        };

        window.addEventListener('beforeunload', handleBeforeUnload);
        return () => window.removeEventListener('beforeunload', handleBeforeUnload);
    }, []);

    const login = async (username: string, password: string) => {
        setAuthHeader(username, password);
        try {
            const response = await client.get('/usuarios/me');
            const user: Usuario = response.data.data;
            setUsuario(user);
            localStorage.setItem('usuario', JSON.stringify(user));
        } catch (error) {
            clearAuthHeader();
            throw error;
        }
    };

    const logout = () => {
        clearAuthHeader();
        localStorage.removeItem('usuario');
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