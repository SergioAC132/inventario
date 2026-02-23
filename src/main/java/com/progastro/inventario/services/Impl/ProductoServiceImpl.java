package com.progastro.inventario.services.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.progastro.inventario.exceptions.ResourceNotFoundException;
import com.progastro.inventario.mappers.ProductoMapper;
import com.progastro.inventario.models.DTO.ProductoRequestDTO;
import com.progastro.inventario.models.DTO.ProductoResponseDTO;
import com.progastro.inventario.models.Entities.Marca;
import com.progastro.inventario.models.Entities.Producto;
import com.progastro.inventario.repositories.MarcaRepository;
import com.progastro.inventario.repositories.ProductoRepository;
import com.progastro.inventario.services.ProductoServiceBridge;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoServiceBridge {
    
    private final ProductoMapper productoMapper;
    private final MarcaRepository marcaRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public ProductoResponseDTO registrarProducto(ProductoRequestDTO request) {
        
        Marca marca = validarDatosMarca(request.getIdMarca(), request.getCodigo(), null);
    
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setMarca(marca);
        producto.setCodigo(request.getCodigo());

        productoRepository.save(producto);
        return productoMapper.toResponse(producto);
    }

    private Marca validarDatosMarca(Long idMarca, String codigo, Long idProducto) {
        Marca marca = marcaRepository.findById(idMarca).orElseThrow(() -> 
                    new ResourceNotFoundException(("Marca no encontrada con el id" + idMarca))
        );
        
        boolean productoDuplicado;

        if (idProducto == null) {
            productoDuplicado = productoRepository.existsByCodigoAndMarca(codigo, marca);
        } else {
            productoDuplicado = productoRepository.existsByCodigoAndMarcaAndIdProductoNot(codigo, marca, idProducto);
        }

        if (productoDuplicado) {
            throw new ValidationException(
                "Ya existe un producto con ese código para esta marca"
            );
        }

        return marca;
        
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listarProductos(String marca, String nombre, String codigo, int page, int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").descending());
        Page<Producto> productos = productoRepository.findByFiltros(marca, nombre, codigo, pageable);

        return productos.map(productoMapper::toResponse);
    
    }

    @Override
    @Transactional
    public ProductoResponseDTO editarProducto(ProductoRequestDTO request) {
        Marca marca = validarDatosMarca(request.getIdMarca(), request.getCodigo(), request.getIdProducto());

        Producto producto = productoRepository.findById(request.getIdProducto()).orElseThrow(() ->
            new ResourceNotFoundException(("Producto no encontrado con el id:") + request.getIdProducto())
        );

        producto.setNombre(request.getNombre());
        producto.setMarca(marca);
        producto.setCodigo(request.getCodigo());

        productoRepository.save(producto);
        return productoMapper.toResponse(producto);

    }

}
