export interface InventarioResponse {
    idInventario: number;
    lote: string;
    cantidadDisponible: number;
    fechaCaducidad: string;
    active: boolean;
}