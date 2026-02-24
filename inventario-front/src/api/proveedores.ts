import client from './client';
import type { ApiResponse, PageResponse } from '../types/api';
import type { ProveedorRequest, ProveedorResponse } from '../types/proveedor';

export const getProveedores = (params?: {
    nombre?: string;
    rfc?: string;
    page?: number;
    size?: number;
}) => client.get<PageResponse<ProveedorResponse>>('/proveedores/consultar-proveedores', { params });

export const crearProveedor = (data: ProveedorRequest) =>
    client.post<ApiResponse<ProveedorResponse>>('/proveedores/registrar-proveedor', data);

export const editarProveedor = (data: ProveedorRequest) =>
    client.post<ApiResponse<ProveedorResponse>>('/proveedores/editar-proveedor', data);