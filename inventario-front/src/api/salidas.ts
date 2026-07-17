import client from './client';
import type { ApiResponse, PageResponse } from '../types/api';
import type { SalidaRequest, SalidaResponse } from '../types/salida';

export const getSalidas = (params?: {
    producto?: string;
    destino?: string;
    tipo?: string;
    fechaInicio?: string;
    fechaFin?: string;
    folio?: number;
    destinatario?: string;
    page?: number;
    size?: number;
}) => client.get<PageResponse<SalidaResponse>>('/salidas/consultar-salidas', { params });

export const registrarSalida = (data: SalidaRequest) =>
    client.post<ApiResponse<SalidaResponse>>('/salidas/registrar-salida', data);

export const editarSalida = (data: SalidaRequest) =>
    client.post<ApiResponse<SalidaResponse>>('/salidas/editar-salida', data);

export const devolverProductoSalida = (idSalidaProducto: number) =>
    client.post<ApiResponse<SalidaResponse>>(`/salidas/devolver-producto/${idSalidaProducto}`);

export const eliminarSalida = (idSalida: number, password: string) =>
    client.post<ApiResponse<void>>(`/salidas/eliminar-salida/${idSalida}`, { password });