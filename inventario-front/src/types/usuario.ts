export interface UsuarioRequest {
    idUsuario?: number;
    username: string;
    password?: string;
    idRol: number;
}

export interface UsuarioResponse {
    idUsuario: number;
    username: string;
    rol: string;
    enabled: boolean;
}