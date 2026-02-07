package com.progastro.inventario.services;

import com.progastro.inventario.models.DTO.CompraProductoRequestDTO;
import com.progastro.inventario.models.Entities.CompraProductos;
import com.progastro.inventario.models.Entities.Inventario;

public interface InventarioServiceBridge {
    Inventario obtenerOCrearInventario(CompraProductoRequestDTO dto);

    void revertirIngresoPorCompra(CompraProductos cp);
}
