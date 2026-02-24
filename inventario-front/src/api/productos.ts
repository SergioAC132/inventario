import client from './client';
import type { ApiResponse, PageResponse } from '../types/api';
import type { ProductoRequest, ProductoResponse } from '../types/producto';

export const getProductos = (params: {
    marca?: string;
    nombre?: string;
    codigo?: string;
    page?: number;
    size?: number;
}) => client.get<PageResponse<ProductoResponse>>('/productos/consultar-productos', { params });

export const crearProducto = (data: ProductoRequest) =>
    client.post<ApiResponse<ProductoResponse>>('/productos/registrar-producto', data);

export const editarProducto = (data: ProductoRequest) =>
    client.post<ApiResponse<ProductoResponse>>('/productos/editar-producto', data);