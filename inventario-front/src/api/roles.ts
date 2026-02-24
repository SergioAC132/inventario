import client from './client';
import type { ApiResponse } from '../types/api';
import type { RolResponse } from '../types/rol';

export const getRoles = () =>
    client.get<ApiResponse<RolResponse[]>>('/roles/consultar-roles');