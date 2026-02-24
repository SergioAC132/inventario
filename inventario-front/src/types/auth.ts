export interface Usuario {
    idUsuario: number;
    username: string;
    rol: string;
    enabled: boolean;
}

export interface AuthContextType {
    usuario: Usuario | null;
    login: (username: string, password: string) => Promise<void>;
    logout: () => void;
    isAuthenticated: boolean;
    loading: boolean;
}