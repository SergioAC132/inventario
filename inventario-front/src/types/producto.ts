import type { InventarioResponse } from './inventario';

export interface ProductoResponse {
    idProducto: number;
    codigo: string;
    nombre: string;
    nombreMarca: string;
    stockTotal: number;
    inventarios: InventarioResponse[];
}

export interface ProductoRequest {
    idProducto?: number;
    codigo: string;
    nombre: string;
    idMarca: number;
}