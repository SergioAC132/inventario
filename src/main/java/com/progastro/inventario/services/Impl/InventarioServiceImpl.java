package com.progastro.inventario.services.Impl;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.progastro.inventario.exceptions.ResourceNotFoundException;
import com.progastro.inventario.mappers.InventarioMapper;
import com.progastro.inventario.models.DTO.CompraProductoRequestDTO;
import com.progastro.inventario.models.DTO.InventarioResponseDTO;
import com.progastro.inventario.models.Entities.CompraProductos;
import com.progastro.inventario.models.Entities.Inventario;
import com.progastro.inventario.models.Entities.Producto;
import com.progastro.inventario.repositories.InventarioRepository;
import com.progastro.inventario.repositories.ProductoRepository;
import com.progastro.inventario.services.InventarioServiceBridge;
import static com.progastro.inventario.util.Constantes.PRODUCTO_NO_ENCONTRADO_ID;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioServiceBridge {

    private final InventarioMapper inventarioMapper;
    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public Inventario obtenerOCrearInventario(CompraProductoRequestDTO dto) {

        Producto producto = obtenerProducto(dto.getProductoId());

        Optional<Inventario> existente = inventarioRepository.findByProductoAndLoteAndFechaCaducidad(producto, dto.getLote(), dto.getFechaCaducidad());

        if (existente.isPresent()) {
            Inventario inv = existente.get();
            inv.setCantidadDisponible(inv.getCantidadDisponible() + dto.getCantidad());
            return inventarioRepository.save(inv);
        }

        Inventario nuevo = new Inventario();
        nuevo.setProducto(producto);
        nuevo.setLote(dto.getLote());
        nuevo.setCantidadDisponible(dto.getCantidad());
        nuevo.setFechaCaducidad(dto.getFechaCaducidad());

        return inventarioRepository.save(nuevo);
    }
    
    @Override
    @Transactional
    public void revertirIngresoPorCompra(CompraProductos cp) {
        Inventario inv = cp.getInventario();

        inv.setCantidadDisponible(inv.getCantidadDisponible() - cp.getCantidad());

        if(inv.getCantidadDisponible() <= 0) {
            inv.setActive(false);
        }

        inventarioRepository.save(inv);
    }

    @Override
    @Transactional
    public Page<InventarioResponseDTO> listarInventarios(Long idProducto, String lote, LocalDate fechaInicio, LocalDate fechaFin,
                                                            int page, int size) {
        
        PageRequest pageable = PageRequest.of(page, size, Sort.by("fechaCaducidad", "cantidadDisponible").ascending());
        
        Producto producto = obtenerProducto(idProducto);
        Page<Inventario> inventarios = inventarioRepository.findByFiltros(producto, lote, fechaInicio, fechaFin, pageable);

        return inventarios.map(inventarioMapper::toResponse);
    }

    @Override
    @Transactional
    public void modificarStock(Long idInventario, Boolean modificacion) {
        Inventario inventario = inventarioRepository.findById(idInventario).orElseThrow(() ->
            new ResourceNotFoundException("Inventario no encontrado con id:" + idInventario)
        );

        inventario.setCantidadDisponible(modificacion ? inventario.getCantidadDisponible()+1 : inventario.getCantidadDisponible()-1);
    }

    private Producto obtenerProducto(Long idProducto) {
        return productoRepository.findById(idProducto).orElseThrow(() ->
            new ResourceNotFoundException(PRODUCTO_NO_ENCONTRADO_ID + idProducto)
        );
    }

}
