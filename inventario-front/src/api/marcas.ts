import client from './client';
import type { ApiResponse, PageResponse } from '../types/api';
import type { MarcaResponse } from '../types/marca';

export interface MarcaRequest {
    idMarca?: number;
    nombre: string;
}

export const getMarcas = (params?: {
    nombre?: string;
    page?: number;
    size?: number;
}) => client.get<PageResponse<MarcaResponse>>('/marcas/consultar-marcas', { params });

export const crearMarca = (data: MarcaRequest) =>
    client.post<ApiResponse<MarcaResponse>>('/marcas/registrar-marca', data);

export const editarMarca = (data: MarcaRequest) =>
    client.post<ApiResponse<MarcaResponse>>('/marcas/editar-marca', data);