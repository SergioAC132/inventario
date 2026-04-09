export interface InventarioResponse {
    idInventario: number;
    lote: string;
    cantidadDisponible: number;
    fechaCaducidad: string;
    costoUnitario: number;
    active: boolean;
}