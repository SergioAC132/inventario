import client from './client';
import type { ApiResponse, PageResponse } from '../types/api';
import type { InventarioResponse } from '../types/inventario';

export const modificarStock = (idInventario: number, modificacion: boolean) =>
    client.patch<ApiResponse<void>>(`/inventarios/${idInventario}/${modificacion}`);

export const getInventariosPorProducto = (idProducto: number) =>
    client.get<PageResponse<InventarioResponse>>(`/inventarios/${idProducto}/inventarios`, {
        params: { size: 100 }
    });