import client from './client';
import type { PageResponse } from '../types/api';
import type { MarcaResponse } from '../types/marca';

export const getMarcas = (params?: {
    nombre?: string;
    page?: number;
    size?: number;
}) => client.get<PageResponse<MarcaResponse>>('/marcas/consultar-marcas', { params });