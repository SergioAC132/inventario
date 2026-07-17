import type { NotaResponse } from './nota';

export type TipoSalida = 'CIRUGIA' | 'ESTUDIO' | 'VENTA';
export type DestinoSalida = 'PARTICULAR' | 'ASEGURADORA';

export interface SalidaProductoRequest {
    idInventario: number;
    cantidad: number;
    total?: number;
}

export interface SalidaProductoResponse {
    idSalidaProducto: number;
    idInventario: number;
    idProducto: number;
    nombreProducto: string;
    marca: string;
    lote: string;
    fechaCaducidad: string;
    cantidad: number;
    cantidadRestante: number;
    costoUnitarioCompra: number;
    costoTotal: number;
}

export interface SalidaRequest {
    idSalida?: number;
    fecha: string;
    tipo: TipoSalida;
    destino: DestinoSalida;
    folio?: number;
    destinatarioId?: number;
    productos: SalidaProductoRequest[];
    nota?: string;
}

export interface SalidaResponse {
    idSalida: number;
    fecha: string;
    tipo: string;
    destino: string;
    folio?: number;
    destinatario?: string;
    total: number;
    productos: SalidaProductoResponse[];
    notas: NotaResponse[];
}