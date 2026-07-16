export interface DoctorResponse {
    idDoctor: number;
    nombre: string;
    telefono: string;
}

export interface DoctorRequest {
    nombre: string;
    telefono?: string;
}
