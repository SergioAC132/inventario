package com.progastro.inventario.services;

import java.time.LocalDate;

import org.springframework.data.domain.Page;

import com.progastro.inventario.models.DTO.CompraProductoRequestDTO;
import com.progastro.inventario.models.DTO.InventarioResponseDTO;
import com.progastro.inventario.models.DTO.SalidaProductoRequestDTO;
import com.progastro.inventario.models.Entities.CompraProductos;
import com.progastro.inventario.models.Entities.Inventario;
import com.progastro.inventario.models.Entities.SalidaProductos;

public interface InventarioServiceBridge {
    Inventario obtenerOCrearInventario(CompraProductoRequestDTO dto);

    void revertirIngresoPorCompra(CompraProductos cp);

    Page<InventarioResponseDTO> listarInventarios(Long idProducto, String lote, LocalDate fechaInicio, LocalDate fechaFin,
                                                    int page, int size);

    void modificarStock(Long idInventario, Boolean modificacion);

    Inventario restarStock(SalidaProductoRequestDTO dto);

    void revertirSalida(SalidaProductos sp);

    void ajustarStockPorEdicionSalida(Inventario inventario, int diferencia);

    void ajustarStockPorEdicionCompra(Inventario inventario, int diferencia);

    Inventario obtenerInventarioParaSalida(SalidaProductoRequestDTO dto);
}
