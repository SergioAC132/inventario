export interface DoctorResponse {
    idDoctor: number;
    nombre: string;
    telefono: string;
}

export interface DoctorRequest {
    idDoctor?: number;
    nombre: string;
    telefono?: string;
}
