import client from './client';
import type { ApiResponse, PageResponse } from '../types/api';
import type { DestinatarioRequest, DestinatarioResponse } from '../types/destinatario';

export const getDestinatarios = (params?: {
    nombre?: string;
    page?: number;
    size?: number;
}) => client.get<PageResponse<DestinatarioResponse>>('/destinatarios/consultar-destinatarios', { params });

export const crearDestinatario = (data: DestinatarioRequest) =>
    client.post<ApiResponse<DestinatarioResponse>>('/destinatarios/registrar-destinatario', data);

export const editarDestinatario = (data: DestinatarioRequest) =>
    client.post<ApiResponse<DestinatarioResponse>>('/destinatarios/editar-destinatario', data);
