import client from './client';
import type { ApiResponse, PageResponse } from '../types/api';
import type { UsuarioRequest, UsuarioResponse } from '../types/usuario';

export const getUsuarios = (params: {
    username?: string;
    rol?: string;
    enabled?: boolean;
    page?: number;
    size?: number;
}) => client.get<PageResponse<UsuarioResponse>>('/usuarios/consultar-usuarios', { params });

export const crearUsuario = (data: UsuarioRequest) =>
    client.post<ApiResponse<UsuarioResponse>>('/usuarios/registrar-usuario', data);

export const editarUsuario = (data: UsuarioRequest) =>
    client.post<ApiResponse<UsuarioResponse>>('/usuarios/editar-usuario', data);

export const cambiarEstadoUsuario = (idUsuario: number) =>
    client.patch<ApiResponse<void>>(`/usuarios/${idUsuario}/cambio-estado`);