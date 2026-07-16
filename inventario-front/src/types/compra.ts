export type EstatusCompra = 'REGISTRADA' | 'FACTURADA' | 'CANCELADA';

export interface CompraProductoRequest {
    productoId: number;
    lote: string;
    fechaCaducidad: string;
    cantidad: number;
    subtotal: number;
    costoTotal: number;
}

export interface CompraProductoResponse {
    idCompraProducto: number;
    idProducto: number;
    nombreProducto: string;
    marca: string;
    lote: string;
    fechaCaducidad: string;
    cantidad: number;
    costoTotal: number;
}

export interface CompraRequest {
    idCompra?: number;
    proveedorId?: number;
    doctorId?: number;
    fecha: string;
    numeroFactura?: string;
    productos: CompraProductoRequest[];
    estatus: EstatusCompra;
}

export interface CompraResponse {
    idCompra: number;
    fecha: string;
    proveedor: string | null;
    doctor: string | null;
    numeroFactura: string;
    estatus: EstatusCompra;
    total: number;
    productos: CompraProductoResponse[];
}