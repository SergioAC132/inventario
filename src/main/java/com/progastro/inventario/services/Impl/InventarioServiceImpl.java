package com.progastro.inventario.services.Impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.progastro.inventario.exceptions.ResourceNotFoundException;
import com.progastro.inventario.exceptions.ValidationException;
import com.progastro.inventario.mappers.InventarioMapper;
import com.progastro.inventario.models.DTO.CompraProductoRequestDTO;
import com.progastro.inventario.models.DTO.InventarioResponseDTO;
import com.progastro.inventario.models.DTO.SalidaProductoRequestDTO;
import com.progastro.inventario.models.Entities.CompraProductos;
import com.progastro.inventario.models.Entities.Inventario;
import com.progastro.inventario.models.Entities.Producto;
import com.progastro.inventario.models.Entities.SalidaProductos;
import com.progastro.inventario.repositories.InventarioRepository;
import com.progastro.inventario.repositories.ProductoRepository;
import com.progastro.inventario.services.InventarioServiceBridge;
import com.progastro.inventario.services.LoginService;

import static com.progastro.inventario.util.Constantes.INVENTARIO_NO_ENCONTRADO_ID;
import static com.progastro.inventario.util.Constantes.PRODUCTO_NO_ENCONTRADO_ID;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioServiceBridge {

    private final InventarioMapper inventarioMapper;
    private final InventarioRepository inventarioRepository;
    private final LoginService loginService;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public Inventario obtenerOCrearInventario(CompraProductoRequestDTO dto) {

        Producto producto = obtenerProducto(dto.getProductoId());

        Optional<Inventario> existente = inventarioRepository.findByProductoAndLoteAndFechaCaducidad(producto, dto.getLote(), dto.getFechaCaducidad());
        BigDecimal costoUnitario = dto.getCostoTotal().divide(new java.math.BigDecimal(dto.getCantidad()), 2, java.math.RoundingMode.HALF_UP);

        if (existente.isPresent()) {
            Inventario inv = existente.get();
            inv.setCantidadDisponible(inv.getCantidadDisponible() + dto.getCantidad());
            if (inv.getCostoUnitario().compareTo(costoUnitario) < 0) {
                inv.setCostoUnitario(costoUnitario);
            }
            return inventarioRepository.save(inv);
        }

        Inventario nuevo = new Inventario();
        nuevo.setProducto(producto);
        nuevo.setLote(dto.getLote());
        nuevo.setCantidadDisponible(dto.getCantidad());
        nuevo.setFechaCaducidad(dto.getFechaCaducidad());
        nuevo.setCostoUnitario(costoUnitario);
        return inventarioRepository.save(nuevo);
    }
    
    @Override
    @Transactional
    public void revertirIngresoPorCompra(CompraProductos cp) {
        Inventario inv = cp.getInventario();

        int nuevaCantidad = inv.getCantidadDisponible() - cp.getCantidad();

        if (nuevaCantidad < 0) {
            throw new ValidationException(
                "No se puede cancelar la compra porque el inventario del producto '" +
                inv.getProducto().getNombre() + "' lote '" + inv.getLote() +
                "' ya no tiene suficiente stock para revertir"
            );
        }

        inv.setCantidadDisponible(nuevaCantidad);

        if (nuevaCantidad == 0) {
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
            new ResourceNotFoundException(INVENTARIO_NO_ENCONTRADO_ID + idInventario)
        );

        loginService.settearUsuario();

        inventario.setCantidadDisponible(modificacion ? inventario.getCantidadDisponible()+1 : inventario.getCantidadDisponible()-1);
        if (inventario.getCantidadDisponible() == 0) inventario.setActive(false);
        inventarioRepository.save(inventario);
    }

    private Producto obtenerProducto(Long idProducto) {
        return productoRepository.findById(idProducto).orElseThrow(() ->
            new ResourceNotFoundException(PRODUCTO_NO_ENCONTRADO_ID + idProducto)
        );
    }

    @Override
    @Transactional
    public Inventario restarStock(SalidaProductoRequestDTO request) {
        Inventario inventario = inventarioRepository.findById(request.getIdInventario()).orElseThrow(() ->
                new ResourceNotFoundException(INVENTARIO_NO_ENCONTRADO_ID + request.getIdInventario())
            );

        inventario.setCantidadDisponible(inventario.getCantidadDisponible() - request.getCantidad());

        if (inventario.getCantidadDisponible() == 0) {
            inventario.setActive(false);
        }
        return inventarioRepository.save(inventario);
    }

    @Override
    @Transactional
    public void revertirSalida(SalidaProductos sp) {
        Inventario inv = sp.getInventario();
        inv.setCantidadDisponible(inv.getCantidadDisponible() + sp.getCantidad());
        inventarioRepository.save(inv);
    }

    @Override
    @Transactional
    public void ajustarStockPorEdicionSalida(Inventario inventario, int diferencia) {
        inventario.setCantidadDisponible(inventario.getCantidadDisponible() - diferencia);
        inventarioRepository.save(inventario);
    }

    @Override
    @Transactional
    public void ajustarStockPorEdicionCompra(Inventario inventario, int diferencia) {
        int nuevaCantidad = inventario.getCantidadDisponible() + diferencia;
        if (nuevaCantidad < 0) {
            throw new com.progastro.inventario.exceptions.ValidationException(
                "No se puede reducir la cantidad de la compra porque el inventario del producto '" +
                inventario.getProducto().getNombre() + "' lote '" + inventario.getLote() +
                "' no tiene suficiente stock disponible"
            );
        }
        inventario.setCantidadDisponible(nuevaCantidad);
        if (nuevaCantidad == 0) {
            inventario.setActive(false);
        } else {
            inventario.setActive(true);
        }
        inventarioRepository.save(inventario);
    }

    @Override
    @Transactional
    public void ajustarCostoUnitarioPorEdicionCompra(Inventario inventario, BigDecimal costoTotal, Integer cantidad) {
        BigDecimal nuevoCostoUnitario = costoTotal.divide(new BigDecimal(cantidad), 2, java.math.RoundingMode.HALF_UP);
        inventario.setCostoUnitario(nuevoCostoUnitario);
        inventarioRepository.save(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public Inventario obtenerInventarioParaSalida(SalidaProductoRequestDTO dto) {
        return inventarioRepository.findById(dto.getIdInventario()).orElseThrow(() ->
            new ResourceNotFoundException(INVENTARIO_NO_ENCONTRADO_ID + dto.getIdInventario())
        );
    }
}
