export interface ProveedorResponse {
    idProveedor: number;
    nombre: string;
    rfc: string;
    tipoPersona: number;
    telefono: string;
    correo: string;
    codigoPostal: number;
}

export interface ProveedorRequest {
    idProveedor?: number;
    nombre: string;
    rfc: string;
    tipoPersona: number;
    telefono: string;
    correo?: string;
    codigoPostal?: number;
}