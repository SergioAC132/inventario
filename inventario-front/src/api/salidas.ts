import client from './client';
import type { ApiResponse, PageResponse } from '../types/api';
import type { SalidaRequest, SalidaResponse } from '../types/salida';

export const getSalidas = (params?: {
    producto?: string;
    destino?: string;
    tipo?: string;
    fechaInicio?: string;
    fechaFin?: string;
    page?: number;
    size?: number;
}) => client.get<PageResponse<SalidaResponse>>('/salidas/consultar-salidas', { params });

export const registrarSalida = (data: SalidaRequest) =>
    client.post<ApiResponse<SalidaResponse>>('/salidas/registrar-salida', data);

export const editarSalida = (data: SalidaRequest) =>
    client.post<ApiResponse<SalidaResponse>>('/salidas/editar-salida', data);