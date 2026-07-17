import client from './client';
import type { ApiResponse, PageResponse } from '../types/api';
import type { DoctorRequest, DoctorResponse } from '../types/doctor';

export const getDoctores = (params?: {
    nombre?: string;
    page?: number;
    size?: number;
}) => client.get<PageResponse<DoctorResponse>>('/doctores/consultar-doctores', { params });

export const crearDoctor = (data: DoctorRequest) =>
    client.post<ApiResponse<DoctorResponse>>('/doctores/registrar-doctor', data);

export const editarDoctor = (data: DoctorRequest) =>
    client.post<ApiResponse<DoctorResponse>>('/doctores/editar-doctor', data);
