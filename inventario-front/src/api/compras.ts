import client from './client';
import type { ApiResponse, PageResponse } from '../types/api';
import type { CompraRequest, CompraResponse } from '../types/compra';

export const getCompras = (params?: {
    proveedor?: string;
    estatus?: string;
    fechaInicio?: string;
    fechaFin?: string;
    page?: number;
    size?: number;
}) => client.get<PageResponse<CompraResponse>>('/compras/consultar-compras', { params });

export const registrarCompra = (data: CompraRequest) =>
    client.post<ApiResponse<CompraResponse>>('/compras/registrar-compra', data);

export const editarCompra = (data: CompraRequest) =>
    client.post<ApiResponse<CompraResponse>>('/compras/editar-compra', data);

export const cancelarCompra = (idCompra: number) =>
    client.patch<ApiResponse<void>>(`/compras/${idCompra}/cancelar`);