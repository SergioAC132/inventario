export type TipoSalida = 'CIRUGIA' | 'ESTUDIO' | 'VENTA';
export type DestinoSalida = 'PARTICULAR' | 'ASEGURADORA';

export interface SalidaProductoRequest {
    idInventario: number;
    cantidad: number;
    subtotal?: number;
    total?: number;
}

export interface SalidaProductoResponse {
    idSalidaProducto: number;
    idProducto: number;
    nombreProducto: string;
    marca: string;
    lote: string;
    fechaCaducidad: string;
    cantidad: number;
    cantidadRestante: number;
    costoTotal: number;
}

export interface SalidaRequest {
    idSalida?: number;
    fecha: string;
    tipo: TipoSalida;
    destino: DestinoSalida;
    productos: SalidaProductoRequest[];
}

export interface SalidaResponse {
    idSalida: number;
    fecha: string;
    tipo: string;
    destino: string;
    total: number;
    productos: SalidaProductoResponse[];
}